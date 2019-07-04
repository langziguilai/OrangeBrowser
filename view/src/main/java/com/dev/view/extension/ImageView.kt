package com.dev.view.extension

import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev.util.Keep
import java.io.File

@Keep
fun ImageView.loadLocalImage(path:String){
    val mRequestOptions = RequestOptions.centerCropTransform()
    Glide.with(context).load(File(path))
        .apply(mRequestOptions).into(this)
}
@Keep
fun ImageView.loadBitmap(bitmap: Bitmap){
    val mRequestOptions = RequestOptions.centerCropTransform()
    Glide.with(context).load(bitmap)
        .apply(mRequestOptions).into(this)
}
@Keep
fun ImageView.loadRemoteImage(url:String){
    val mRequestOptions = RequestOptions.centerCropTransform()
    Glide.with(context).load(url)
        .apply(mRequestOptions).into(this)
}