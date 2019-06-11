package com.dev.view.extension

import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.File

fun ImageView.loadLocalImage(path:String){
    val mRequestOptions = RequestOptions.centerCropTransform()
    Glide.with(context).load(File(path))
        .apply(mRequestOptions).into(this)
}
fun ImageView.loadBitmap(bitmap: Bitmap){
    val mRequestOptions = RequestOptions.centerCropTransform()
    Glide.with(context).load(bitmap)
        .apply(mRequestOptions).into(this)
}
fun ImageView.loadRemoteImage(url:String){
    val mRequestOptions = RequestOptions.centerCropTransform()
    Glide.with(context).load(url)
        .apply(mRequestOptions).into(this)
}