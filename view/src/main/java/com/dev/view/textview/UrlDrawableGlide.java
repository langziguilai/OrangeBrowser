package com.dev.view.textview;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import com.dev.util.Keep;
import com.dev.util.KeepMemberIfNecessary;
import com.dev.util.KeepNameIfNecessary;


/**
 * @author: Allen.
 * @date: 2018/6/26
 * @description: glide 加载图片
 */
@KeepNameIfNecessary
public class UrlDrawableGlide extends Drawable implements Drawable.Callback {
    private Drawable mDrawable;

    @KeepMemberIfNecessary
    @Override
    public void draw(Canvas canvas) {
        if (mDrawable != null) {
            mDrawable.draw(canvas);
        }
    }

    @KeepMemberIfNecessary
    @Override
    public void setAlpha(int alpha) {
        if (mDrawable != null) {
            mDrawable.setAlpha(alpha);
        }
    }

    @KeepMemberIfNecessary
    @Override
    public void setColorFilter(ColorFilter cf) {
        if (mDrawable != null) {
            mDrawable.setColorFilter(cf);
        }
    }

    @KeepMemberIfNecessary
    @SuppressLint("WrongConstant")
    @Override
    public int getOpacity() {
        if (mDrawable != null) {
            return mDrawable.getOpacity();
        }
        return 0;
    }
    @KeepMemberIfNecessary
    public void setDrawable(Drawable drawable) {
        if (this.mDrawable != null) {
            this.mDrawable.setCallback(null);
        }
        drawable.setCallback(this);
        this.mDrawable = drawable;
    }
    @KeepMemberIfNecessary
    @Override
    public void invalidateDrawable(Drawable who) {
        if (getCallback() != null) {
            getCallback().invalidateDrawable(who);
        }
    }
    @KeepMemberIfNecessary
    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if (getCallback() != null) {
            getCallback().scheduleDrawable(who, what, when);
        }
    }
    @KeepMemberIfNecessary
    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        if (getCallback() != null) {
            getCallback().unscheduleDrawable(who, what);
        }
    }
}