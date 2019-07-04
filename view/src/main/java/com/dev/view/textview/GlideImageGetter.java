package com.dev.view.textview;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dev.util.Keep;
import com.dev.util.KeepMemberIfNecessary;
import com.dev.util.KeepNameIfNecessary;
import com.dev.view.R;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: Allen.
 * @date: 2018/6/26
 * @description: glide 加载图片
 */
@KeepNameIfNecessary
public class GlideImageGetter implements Html.ImageGetter, Drawable.Callback {
    private final Context mContext;

    private final TextView mTextView;

    private final Set<ImageGetterViewTarget> mTargets;
    @KeepMemberIfNecessary
    public static GlideImageGetter get(View view) {
        return (GlideImageGetter) view.getTag(R.id.drawable_tag);
    }
    @KeepMemberIfNecessary
    public void clear() {
        GlideImageGetter prev = get(mTextView);
        if (prev == null) return;

        for (ImageGetterViewTarget target : prev.mTargets) {
            Glide.with(mContext).clear(target);
        }
    }
    @KeepMemberIfNecessary
    public GlideImageGetter(Context context, TextView textView) {
        this.mContext = context;
        this.mTextView = textView;

//        clear(); //屏蔽掉这句在TextView中可以加载多张图片
        mTargets = new HashSet<>();
        mTextView.setTag(R.id.drawable_tag, this);
    }
    @KeepMemberIfNecessary
    @Override
    public Drawable getDrawable(String url) {
        final UrlDrawableGlide urlDrawable = new UrlDrawableGlide();
        Glide.with(mContext)
                .load(url)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new ImageGetterViewTarget(mTextView, urlDrawable));
        return urlDrawable;
    }
    @KeepMemberIfNecessary
    @Override
    public void invalidateDrawable(Drawable who) {
        mTextView.invalidate();
    }
    @KeepMemberIfNecessary
    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {

    }
    @KeepMemberIfNecessary
    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {

    }

    private class ImageGetterViewTarget extends ViewTarget<TextView, Drawable> {

        private final UrlDrawableGlide mDrawable;

        private ImageGetterViewTarget(TextView view, UrlDrawableGlide drawable) {
            super(view);
            mTargets.add(this);
            this.mDrawable = drawable;
        }

        @Override
        public void onResourceReady(Drawable resource, Transition<? super Drawable> glideAnimation) {
            Rect rect;
            if (resource.getIntrinsicWidth() > 100) {
                float width;
                float height;
                if (resource.getIntrinsicWidth() >= getView().getWidth()) {
                    float downScale = (float) resource.getIntrinsicWidth() / getView().getWidth();
                    width = (float) resource.getIntrinsicWidth() / (float) downScale;
                    height = (float) resource.getIntrinsicHeight() / (float) downScale;
                } else {
                    float multiplier = (float) getView().getWidth() / resource.getIntrinsicWidth();
                    width = (float) resource.getIntrinsicWidth() * (float) multiplier;
                    height = (float) resource.getIntrinsicHeight() * (float) multiplier;
                }
                rect = new Rect(0, 0, Math.round(width), Math.round(height));
            } else {
                rect = new Rect(0, 0, resource.getIntrinsicWidth() * 2, resource.getIntrinsicHeight() * 2);
            }
            resource.setBounds(rect);

            mDrawable.setBounds(rect);
            mDrawable.setDrawable(resource);


            if (resource instanceof Animatable) {
                mDrawable.setCallback(get(getView()));
                ((Animatable)resource).start();
            }

            getView().setText(getView().getText());
            getView().invalidate();
        }

        private Request request;

        @Override
        public Request getRequest() {
            return request;
        }

        @Override
        public void setRequest(Request request) {
            this.request = request;
        }
    }
}