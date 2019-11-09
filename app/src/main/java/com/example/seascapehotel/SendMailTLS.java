package com.example.seascapehotel;
/*
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Demo app that shows how to construct and send an RFC822
 * (singlepart) message.
 *
 * XXX - allow more than one recipient on the command line
 *
 * This is just a variant of msgsend.java that demonstrates use of
 * some SMTP-specific features.
 *
 * @author Max Spivak
 * @author Bill Shannon
 */

public class SendMailTLS {

    /**
     * Example of how to extend the SMTPTransport class.
     * This example illustrates how to issue the XACT
     * command before the SMTPTransport issues the DATA
     * command.
     * <p>
     * public static class SMTPExtension extends SMTPTransport {
     * public SMTPExtension(Session session, URLName url) {
     * super(session, url);
     * // to check that we're being used
     * System.out.println("SMTPExtension: constructed");
     * }
     * <p>
     * protected synchronized OutputStream data() throws MessagingException {
     * if (supportsExtension("XACCOUNTING"))
     * issueCommand("XACT", 250);
     * return super.data();
     * }
     * }
     */

    static Session mailSession;

    private static MimeMessage draftEmailMessage(String email, String Code) throws AddressException, MessagingException
    {
        String[] toEmails = { email };
        String emailSubject = "Your Code for Resetting Password";
        String emailBody = "Your code is : " + Code;
        MimeMessage emailMessage = new MimeMessage(mailSession);
        /**
         * Set the mail recipients
         * */
        for (int i = 0; i < toEmails.length; i++)
        {
            emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmails[i]));
        }
        emailMessage.setSubject(emailSubject);
        /**
         * If sending HTML mail
         * */
        emailMessage.setContent(emailBody, "text/html");
        /**
         * If sending only text mail
         * */
        return emailMessage;
    }


    public static void sendEmail(String email, String code) throws AddressException, MessagingException
    {
        /**
         * Sender's credentials
         * */
        final String fromUser = "seascapehotelspa@gmail.com";
        final String fromUserEmailPassword = "Csis4175##";
        Properties emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", "587");

        emailProperties.put("mail.smtp.auth", "true");
        emailProperties.put("mail.smtp.starttls.enable", "true");
        mailSession = javax.mail.Session.getDefaultInstance(emailProperties, null);

        final String emailHost = "smtp.gmail.com";

        /**
         * Draft the message
         * */
        final MimeMessage emailMessage = draftEmailMessage(email,code);
        /**
         * Send the mail
         * */

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Transport transport = mailSession.getTransport("smtp");
                    transport.connect(emailHost, fromUser, fromUserEmailPassword);
                    transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
                    transport.close();
                }catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }
}