package com.dev.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

public class CommonUtil {
    @Nullable
    public static Activity findActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            ContextWrapper wrapper = (ContextWrapper) context;
            return findActivity(wrapper.getBaseContext());
        } else {
            return null;
        }
    }

    public static Bitmap getResizedBitmap(Bitmap bm, float newHeight, float newWidth) {

        int width = bm.getWidth();

        int height = bm.getHeight();

        float scaleWidth = newWidth / width;

        float scaleHeight = newHeight / height;

        // CREATE A MATRIX FOR THE MANIPULATION

        Matrix matrix = new Matrix();

        // RESIZE THE BIT MAP

        matrix.postScale(scaleWidth, scaleHeight);

        // RECREATE THE NEW BITMAP

        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

    }
}

