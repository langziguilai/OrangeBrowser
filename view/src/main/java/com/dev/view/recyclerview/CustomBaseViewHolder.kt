package com.dev.view.recyclerview

import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import com.amulyakhare.textdrawable.TextDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.Headers
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.dev.view.R
import com.dev.view.recyclerview.adapter.base.BaseViewHolder
import java.io.File
import java.util.*

fun getGlideUrlWithReferer(url: String, referer: String): GlideUrl {
    return GlideUrl(url, object : Headers {
        override fun getHeaders(): MutableMap<String, String> {
            val header = HashMap<String, String>()
            header["Referer"] = referer
            return header
        }

    })
}

open class CustomBaseViewHolder(view: View) : BaseViewHolder(view) {
    fun setTextToAppCompatTextView(@IdRes viewId: Int, value: CharSequence): CustomBaseViewHolder {
        val view = getView<AppCompatTextView>(viewId)
        view.setTextFuture(
            PrecomputedTextCompat.getTextFuture(value, view.textMetricsParamsCompat, null)
        )
        return this
    }

    fun setTextToAppCompatTextView(@IdRes viewId: Int, @StringRes strId: Int): CustomBaseViewHolder {
        val view = getView<AppCompatTextView>(viewId)
        view.setTextFuture(
            PrecomputedTextCompat.getTextFuture(
                view.context.resources.getString(strId),
                view.textMetricsParamsCompat,
                null
            )
        )
        return this
    }
    companion object{
        const val FIT_CENTER=1
        const val CENTER_INSIDE=1
        const val CENTER_OUTSIDE=1
    }
    fun loadImage(
        @IdRes viewId: Int, url: String,
        referer: String,
        strategy: Int = FIT_CENTER
    ): CustomBaseViewHolder {
        val view = getView<ImageView>(viewId)
        var mRequestOptions = RequestOptions.fitCenterTransform()
        when(strategy){
            CENTER_INSIDE->mRequestOptions=RequestOptions.centerInsideTransform()
            CENTER_OUTSIDE->mRequestOptions=RequestOptions.centerCropTransform()
        }
        Glide.with(view.context).load(getGlideUrlWithReferer(url, referer))
            .placeholder(ColorDrawable(view.context.resources.getColor(R.color.color_7A7A7A)))
            .transition(DrawableTransitionOptions().crossFade(300))
            .apply(mRequestOptions).into(view)
        return this
    }

    fun loadImage(@IdRes viewId: Int, url: String,strategy: Int = FIT_CENTER): CustomBaseViewHolder {
        val view = getView<ImageView>(viewId)
        var mRequestOptions = RequestOptions.circleCropTransform()
        when(strategy){
            CENTER_INSIDE->mRequestOptions=RequestOptions.centerInsideTransform()
            CENTER_OUTSIDE->mRequestOptions=RequestOptions.centerCropTransform()
        }
        Glide.with(view.context).load(url)
            .apply(mRequestOptions).into(view)
        return this
    }

    //加载不被裁剪的图片
    fun loadNoCropImage(@IdRes viewId: Int, url: String, referer: String): CustomBaseViewHolder {
        val view = getView<ImageView>(viewId)
        Glide.with(view.context)
            .applyDefaultRequestOptions(RequestOptions().centerInside())
            .load(getGlideUrlWithReferer(url, referer))
            .placeholder(ColorDrawable(view.context.resources.getColor(R.color.color_7A7A7A)))
            .transition(DrawableTransitionOptions().crossFade(300))
            .into(view)
        return this
    }

    fun loadLocalImage(@IdRes viewId: Int, path: String): CustomBaseViewHolder {
        val view = getView<ImageView>(viewId)
        val mRequestOptions = RequestOptions.centerCropTransform()
        Glide.with(view.context).load(File(path))
            .apply(mRequestOptions).into(view)
        return this
    }

    fun loadBitmapToImageView(@IdRes viewId: Int, bitmap: Bitmap): CustomBaseViewHolder {
        getView<ImageView>(viewId).setImageBitmap(bitmap)
        return this
    }

    fun loadResImage(@IdRes viewId: Int, resId: Int): CustomBaseViewHolder {
        val view = getView<ImageView>(viewId)
        val mRequestOptions = RequestOptions.circleCropTransform()
            .centerCrop()
        Glide.with(view.context).load(resId)
            .apply(mRequestOptions).into(view)
        return this
    }

    fun loadCircleImage(@IdRes viewId: Int, url: String, referer: String): CustomBaseViewHolder {
        val view = getView<ImageView>(viewId)
        val mRequestOptions = RequestOptions.circleCropTransform()
            .circleCrop()
        Glide.with(view.context).load(getGlideUrlWithReferer(url, referer))
            .placeholder(ColorDrawable(view.context.resources.getColor(R.color.color_E6E6E5)))
            //.transition(DrawableTransitionOptions().crossFade(1000))
            .apply(mRequestOptions).into(view)
        return this
    }

    fun loadResCircleImage(@IdRes viewId: Int, resId: Int): CustomBaseViewHolder {
        val view = getView<ImageView>(viewId)
        val mRequestOptions = RequestOptions.circleCropTransform()
            .circleCrop()
        Glide.with(view.context).load(resId)
            .apply(mRequestOptions).into(view)
        return this
    }

    fun loadTextAsImage(
        @IdRes viewId: Int, text: String?,
        fontSize: Int = 42,
        textColor: Int? = 0x111111,
        borderRadius: Int = 0,
        borderSize: Int = 0,
        backgroundColor: Int? = 0xDDDDDD
    ): CustomBaseViewHolder {
        val view = getView<ImageView>(viewId)
        val textDrawable = TextDrawable.builder()
            .beginConfig()
            .fontSize(fontSize)
            .textColor(textColor!!)
            .withBorder(borderSize)
            .endConfig()
            .buildRoundRect(text?.substring(0, 1), backgroundColor!!, borderRadius) // radius in px
        Glide.with(view.context).load(textDrawable)
            .into(view)
        return this
    }
}