package com.dev.orangebrowser.bloc.tabs.integration

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

class ThumbnailPlaceHolderIntegration(var binding: FragmentTabBinding, session: Session, val fragment: TabFragment) {
    init {
        if (session.tmpThumbnail != null) {
            binding.thumbnailPlaceHolder.setImageBitmap(session.tmpThumbnail!!)
        } else if (session.thumbnailPath != null) {
            binding.thumbnailPlaceHolder.loadLocalImage(session.thumbnailPath!!)
        }
    }
    fun setImage(session:Session){
        if (session.tmpThumbnail != null) {
            binding.thumbnailPlaceHolder.setImageBitmap(session.tmpThumbnail!!)
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
}