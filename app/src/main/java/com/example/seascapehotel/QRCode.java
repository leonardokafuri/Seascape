package com.example.seascapehotel;

import android.content.Context;
import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QRCode {
    public static Bitmap generate(String text, int QRcodeWidth, Context c) {

        Bitmap bitmap = null ;
        BitMatrix bitMatrix;

        try {
            bitMatrix = new MultiFormatWriter().encode(
                    text,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        } catch (WriterException e) {
            return null;
        }

        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        0x247cff :
                        0xFFFFFFFF;
            }
        }
        bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.RGB_565);

        bitmap.setPixels(pixels, 0, QRcodeWidth, 0, 0, bitMatrixWidth, bitMatrixHeight);

        return bitmap;
    }
}
