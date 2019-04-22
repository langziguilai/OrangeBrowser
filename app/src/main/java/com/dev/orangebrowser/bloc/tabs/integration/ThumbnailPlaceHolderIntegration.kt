package com.dev.orangebrowser.bloc.tabs.integration

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import com.dev.base.extension.DEFAULT_INTERPOLATOR
import com.dev.base.extension.NORMAL_ANIMATION
import com.dev.base.extension.hide
import com.dev.base.extension.onGlobalLayoutComplete
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

class ThumbnailPlaceHolderIntegration(var binding: FragmentTabBinding, session: Session, val fragment: TabFragment) :
    CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

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

    fun show(runnable: Runnable) {
        val it = binding.thumbnailPlaceHolder
        it.visibility = View.VISIBLE
        it.animate().setDuration(NORMAL_ANIMATION)
            .scaleX(1f)
            .scaleY(1f)
            .setInterpolator(DEFAULT_INTERPOLATOR).withEndAction {
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
                    it.visibility = View.GONE
                    runnable.run()
                }.start()
        }
    }

    fun loadLocalImageToSelectSession(context: Context, selectSession: Session) {
        if ((selectSession.tmpThumbnail == null || selectSession.tmpThumbnail!!.get() == null) && selectSession.thumbnailPath != null) {
            val t1 = System.currentTimeMillis()
            launch(Dispatchers.IO) {
                val imagePath = context.filesDir.path + selectSession.thumbnailPath
                val bitmap = BitmapFactory.decodeFile(imagePath)
                launch(Dispatchers.Main) {
                    selectSession.tmpThumbnail = SoftReference(bitmap)
                    val t2 = System.currentTimeMillis()
                    Log.i("load local image cost :", " " + (t2 - t1) + " ms")
                    coroutineContext.cancelChildren()
                }
            }

        }
    }
}