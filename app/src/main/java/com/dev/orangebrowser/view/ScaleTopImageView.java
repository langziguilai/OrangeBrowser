package com.dev.orangebrowser.view;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class ScaleTopImageView extends AppCompatImageView {

    public ScaleTopImageView(Context context) {
        super(context);
    }

    public ScaleTopImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleTopImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        Matrix matrix=getImageMatrix();
        float scaleFactor=getWidth()/(float)getDrawable().getIntrinsicHeight();
        matrix.setScale(scaleFactor,scaleFactor,0,0);
        setImageMatrix(matrix);
        return super.setFrame(l, t, r, b);
    }
}
