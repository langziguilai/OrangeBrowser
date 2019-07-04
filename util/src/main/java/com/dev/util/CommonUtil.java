package com.dev.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
@KeepNameIfNecessary
public class CommonUtil {
    @Nullable
    @KeepMemberIfNecessary
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
    @KeepMemberIfNecessary
    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
//
//        int width = bm.getWidth();
//
//        int height = bm.getHeight();
//
//        float scaleWidth = newWidth / width;
//
//        float scaleHeight = newHeight / height;
//
//        // CREATE A MATRIX FOR THE MANIPULATION
//
//        Matrix matrix = new Matrix();
//
//        // RESIZE THE BIT MAP
//
//        matrix.postScale(scaleWidth, scaleHeight);
//
//        // RECREATE THE NEW BITMAP
//
//        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float ratioX = newWidth / (float) bm.getWidth();
        float ratioY = newHeight / (float) bm.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bm, middleX - bm.getWidth() / 2, middleY - bm.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;

    }
}

