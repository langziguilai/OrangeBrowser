package com.dev.view;

import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class GlideHelper {
    public static void loadImage(ImageView imageView, Uri uri){
        Glide.with(imageView.getContext()).load(uri).into(imageView);
    }
}
