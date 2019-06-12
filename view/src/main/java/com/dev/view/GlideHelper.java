package com.dev.view;

import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

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
}
