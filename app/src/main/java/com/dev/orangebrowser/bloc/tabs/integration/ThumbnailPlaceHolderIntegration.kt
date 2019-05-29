package com.dev.orangebrowser.bloc.tabs.integration

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import com.dev.base.extension.*
import com.dev.browser.session.Session
import com.dev.orangebrowser.bloc.tabs.TabFragment
import com.dev.orangebrowser.databinding.FragmentTabBinding
import com.dev.view.extension.loadBitmap
import com.dev.view.extension.loadLocalImage
import kotlinx.coroutines.*
import java.io.File
import java.lang.Runnable
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext

class ThumbnailPlaceHolderIntegration(var binding: FragmentTabBinding, session: Session, val fragment: TabFragment) {

    init {
        if (session.tmpThumbnail != null) {
            if (session.tmpThumbnail!!.get() != null) {
                binding.thumbnailPlaceHolder.setImageBitmap(session.tmpThumbnail!!.get())
            }
        } else if (session.thumbnailPath != null) {
            binding.thumbnailPlaceHolder.loadLocalImage(session.thumbnailPath!!)
        }
    }

    fun setImage(session: Session) {
        if (session.tmpThumbnail != null && session.tmpThumbnail!!.get() != null) {
            binding.thumbnailPlaceHolder.setImageBitmap(session.tmpThumbnail!!.get())
        } else if (session.thumbnailPath != null) {
            binding.thumbnailPlaceHolder.loadLocalImage(session.thumbnailPath!!)
        }
    }

    fun show(runnable: Runnable, selectSession: Session) {
        val it = binding.thumbnailPlaceHolder
        it.show()
        it.animate().setDuration(NORMAL_ANIMATION)
            .scaleX(1f)
            .scaleY(1f)
            .setInterpolator(DEFAULT_INTERPOLATOR).withEndAction {
                //保存起来，因为它可能被回收掉了
                selectSession.tmpThumbnail = SoftReference(it.getBitmap())
                runnable.run()
            }.start()
    }

    fun hide(runnable: Runnable) {
        binding.thumbnailPlaceHolder.onGlobalLayoutComplete {
            //以中心点开始缩放
            val targetScale = fragment.cardWidth.toFloat() / it.width.toFloat()
            it.animate()
                .setDuration(NORMAL_ANIMATION)
                .scaleX(targetScale)
                .scaleY(targetScale)
                .setInterpolator(DEFAULT_INTERPOLATOR)
                .withEndAction {
                    it.scaleX = targetScale
                    it.scaleY = targetScale
                    it.hide()
                    runnable.run()
                }.start()
        }
    }

}