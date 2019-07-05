package com.dev.orangebrowser.bloc.browser.integration

import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.dev.browser.session.Session
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.util.DensityUtil
//NOTE:此处需要根据TopBar的高度来设置ImageView的位置，所以，如果TopBar的高度修改了，那么，这里也需要修改
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
                val topBarHeight=DensityUtil.dip2px(fragment.requireContext(),44f)
                params2.setMargins(0,topBarHeight,0,0)
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
