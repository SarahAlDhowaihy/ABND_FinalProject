package com.example.android.inventoryapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by sarahaldowihy on 10/11/2017 AD.
 */

/**
 * Setup upload image
 * Several helpful multi resources :
 * http://stackoverflow.com/questions/5309190/android-pick-images-from-gallery
 * http://stackoverflow.com/questions/10618325/how-to-create-a-blob-from-bitmap-in-android-activity
 */
public class SetupImage {
    public static final byte[] convertBitmapToBlob(Bitmap bitmapImage) {
        byte[] byteArray;

        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, byteOutputStream);
            byteArray = byteOutputStream.toByteArray();
        } catch (Exception e) {
            Log.e("DetailsActivity", "convertBitmapToBlob exception: " + e);
            return null;
        }

        return byteArray;
    }

    public static final Bitmap convertBlobToBitmap(byte[] imageByteArray) {
        if (imageByteArray == null) {
            return null;
        }

        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
    }
}
