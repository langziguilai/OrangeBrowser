package com.dev.orangebrowser.bloc.home.intergration

import android.widget.FrameLayout
import android.widget.ImageView
import com.dev.browser.session.Session
import com.dev.orangebrowser.bloc.home.HomeFragment
import com.dev.orangebrowser.databinding.FragmentHomeBinding

class ContentBlinkFixIntegration(binding: FragmentHomeBinding, fragment: HomeFragment, session: Session) {
    init {
        session.mainPageThumbnailRef?.apply {
            this.get()?.apply {
                val imageView = ImageView(fragment.requireContext())
                imageView.setBackgroundColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                val params2 = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                binding.contentContainer.addView(
                    imageView,
                    params2
                )
                imageView.setImageBitmap(this)
                //用完清理
                imageView.postDelayed({
                    binding.contentContainer.removeView(imageView)
                }, 500)
            }
        }
        if (session.webPageThumbnailRef != null && session.webPageThumbnailRef!!.get() != null) {

        }

    }
}
