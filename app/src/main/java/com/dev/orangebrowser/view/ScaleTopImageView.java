package com.dev.orangebrowser.view;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import com.dev.util.KeepMemberIfNecessary;
import com.dev.util.KeepNameIfNecessary;

@KeepNameIfNecessary
public class ScaleTopImageView extends AppCompatImageView {
    @KeepMemberIfNecessary
    public ScaleTopImageView(Context context) {
        super(context);
    }
    @KeepMemberIfNecessary
    public ScaleTopImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    @KeepMemberIfNecessary
    public ScaleTopImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    @KeepMemberIfNecessary
    protected boolean setFrame(int l, int t, int r, int b) {
        Matrix matrix=getImageMatrix();
        float scaleFactor=getWidth()/(float)getDrawable().getIntrinsicHeight();
        matrix.setScale(scaleFactor,scaleFactor,0,0);
        setImageMatrix(matrix);
        return super.setFrame(l, t, r, b);
    }
}
