package com.dev.view;

import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.dev.util.Keep;
import com.dev.util.KeepMemberIfNecessary;
import com.dev.util.KeepNameIfNecessary;

import java.io.File;
@KeepNameIfNecessary
public class GlideHelper {
    @KeepMemberIfNecessary
    public static void loadLocalImage(ImageView imageView, String path){
        Glide.with(imageView.getContext())
                .load(Uri.fromFile(new File(path)))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.failure).into(imageView);
    }
    @KeepMemberIfNecessary
    public static void loadRemoteImage(ImageView imageView, String url,String referer){
        if (url.trim().length()<=0){
            imageView.setImageDrawable(new ColorDrawable(0xff777777));
        }else{
            LazyHeaders.Builder builder=new LazyHeaders.Builder();
            builder.addHeader("Referer",referer);
            GlideUrl glideUrl=new GlideUrl(url,builder.build());
            Glide.with(imageView.getContext()).load(glideUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.failure).into(imageView);
        }
    }
}
