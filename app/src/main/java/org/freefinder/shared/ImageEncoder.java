package org.freefinder.shared;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by rade on 23.8.17..
 */

public class ImageEncoder {
    public static String encodeImage(Bitmap imageBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap decodeImage(String encodedImage) {
        String imageDataBytes = encodedImage.substring(encodedImage.indexOf(",") + 1);
        byte[] imageBytes = Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}
