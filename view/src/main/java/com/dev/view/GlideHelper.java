package com.dev.view;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.HashMap;

public class GlideHelper {
    public static void loadLocalImage(ImageView imageView, String path){
        Glide.with(imageView.getContext()).load(Uri.fromFile(new File(path))).into(imageView);
    }
    public static void loadRemoteImage(ImageView imageView, String url,String referer){
        LazyHeaders.Builder builder=new LazyHeaders.Builder();
        builder.addHeader("Referer",referer);
        GlideUrl glideUrl=new GlideUrl(url,builder.build());
        Glide.with(imageView.getContext()).load(glideUrl).into(imageView);
    }
    public static void loadRemoteImageWithShareElement(ImageView imageView,String url,String referer){
        LazyHeaders.Builder builder=new LazyHeaders.Builder();
        builder.addHeader("Referer",referer);
        GlideUrl glideUrl=new GlideUrl(url,builder.build());
        Glide.with(imageView)
                .load(glideUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .transform(new FitCenter())
                        .skipMemoryCache(true)
                        .placeholder(new ColorDrawable(Color.GRAY)))
                .into(imageView);
    }

}
