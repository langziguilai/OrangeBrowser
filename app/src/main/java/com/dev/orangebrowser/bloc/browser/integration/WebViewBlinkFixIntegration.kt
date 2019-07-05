package com.dev.orangebrowser.bloc.browser.integration

import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateLayoutParams
import com.dev.base.extension.onGlobalLayoutComplete
import com.dev.browser.session.Session
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.orangebrowser.extension.getSpInt
import com.dev.util.DensityUtil

class WebViewBlinkFixIntegration(binding: FragmentBrowserBinding,fragment:BrowserFragment,session:Session){
    init {
        session.webPageThumbnailRef?.apply {
            this.get()?.apply {
                val imageView= ImageView(fragment.requireContext())
                imageView.setBackgroundColor(fragment.activityViewModel.theme.value!!.colorPrimaryDisable)
                val params2 = CoordinatorLayout.LayoutParams(
                    this.width,
                    this.height
                )
                params2.setMargins(0,DensityUtil.dip2px(fragment.requireContext(),44f),0,0)
                binding.fragmentContainer.addView(
                    imageView,
                    params2
                )
                imageView.setImageBitmap(this)
                //用完清理
                imageView.postDelayed({
                    binding.fragmentContainer.removeView(imageView)
                },500)
            }
        }
    }
}
