package com.dev.orangebrowser.bloc.browser.integration

import android.widget.FrameLayout
import android.widget.ImageView
import com.dev.browser.session.Session
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.databinding.FragmentBrowserBinding

class WebViewBlinkFixIntegration(binding: FragmentBrowserBinding,fragment:BrowserFragment,session:Session){
    init {
        session.tmpThumbnail?.apply {
            this.get()?.apply {
                val imageView= ImageView(fragment.requireContext())
                imageView.setBackgroundColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                val params2 = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                binding.webViewContainer.addView(
                    imageView,
                    params2
                )
                imageView.setImageBitmap(this)
                //用完清理
                imageView.postDelayed({
                    binding.webViewContainer.removeView(imageView)
                },300)
            }
        }
        if (session.tmpThumbnail!=null && session.tmpThumbnail!!.get()!=null){

        }

    }
}
