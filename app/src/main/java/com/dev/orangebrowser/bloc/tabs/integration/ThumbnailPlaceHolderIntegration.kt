package com.dev.orangebrowser.bloc.tabs.integration

import com.dev.base.extension.*
import com.dev.browser.session.Session
import com.dev.orangebrowser.bloc.tabs.TabFragment
import com.dev.orangebrowser.databinding.FragmentTabBinding
import com.dev.view.extension.loadLocalImage
import java.lang.Runnable
import java.lang.ref.SoftReference

class ThumbnailPlaceHolderIntegration(var binding: FragmentTabBinding, session: Session, val fragment: TabFragment) {

    init {
        if (session.screenNumber==Session.HOME_SCREEN){
            if (session.mainPageThumbnailRef != null) {
                if (session.mainPageThumbnailRef!!.get() != null) {
                    binding.thumbnailPlaceHolder.setImageBitmap(session.mainPageThumbnailRef!!.get())
                }
            } else if (session.mainPageThumbnailPath != null) {
                binding.thumbnailPlaceHolder.loadLocalImage(session.mainPageThumbnailPath!!)
            }
        }else{
            if (session.webPageThumbnailRef != null) {
                if (session.webPageThumbnailRef!!.get() != null) {
                    binding.thumbnailPlaceHolder.setImageBitmap(session.webPageThumbnailRef!!.get())
                }
            } else if (session.webPageThumbnailPath != null) {
                binding.thumbnailPlaceHolder.loadLocalImage(session.webPageThumbnailPath!!)
            }
        }
    }

    fun setImage(session: Session) {
        if (session.screenNumber==Session.HOME_SCREEN){
            if (session.mainPageThumbnailRef != null) {
                if (session.mainPageThumbnailRef!!.get() != null) {
                    binding.thumbnailPlaceHolder.setImageBitmap(session.mainPageThumbnailRef!!.get())
                }
            } else if (session.mainPageThumbnailPath != null) {
                binding.thumbnailPlaceHolder.loadLocalImage(session.mainPageThumbnailPath!!)
            }
        }else{
            if (session.webPageThumbnailRef != null) {
                if (session.webPageThumbnailRef!!.get() != null) {
                    binding.thumbnailPlaceHolder.setImageBitmap(session.webPageThumbnailRef!!.get())
                }
            } else if (session.webPageThumbnailPath != null) {
                binding.thumbnailPlaceHolder.loadLocalImage(session.webPageThumbnailPath!!)
            }
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
                if (selectSession.screenNumber==Session.HOME_SCREEN){
                    selectSession.mainPageThumbnailRef = SoftReference(it.getBitmap())
                }else{
                    selectSession.webPageThumbnailRef = SoftReference(it.getBitmap())
                }
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