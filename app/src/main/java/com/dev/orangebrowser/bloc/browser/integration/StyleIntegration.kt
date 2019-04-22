package com.dev.orangebrowser.bloc.browser.integration


import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.annotation.ColorInt
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.engine.SystemEngineSession
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.util.ColorKitUtil
import com.dev.util.CommonUtil
import com.dev.view.StatusBarUtil

//根据webview的头部来更新显示的头部
class StyleIntegration(
    var binding: FragmentBrowserBinding,
    var fragment: BrowserFragment,
    var sessionManager: SessionManager,
    var session: Session
) :
    LifecycleAwareFeature {
    private var sessionObserver: Session.Observer

    init {
        if (session.themeColorMap.containsKey(session.url)) {
            updateStyle(session.themeColorMap[session.url]!!)
        } else {
            val color = fragment.activityViewModel.theme.value!!.colorPrimary
            updateStyle(color)
        }
        sessionObserver = object : Session.Observer {
            override fun onThumbnailCapture(session: Session, bitmap: Bitmap?) {
                bitmap?.apply {
                    val engineSession = sessionManager.getOrCreateEngineSession(session) as SystemEngineSession
                    val dy = engineSession.webView.scrollY
                    //在尚未滑动的时候，可以通过截图来获取颜色，否则，不改变style
                    if (dy == 0) {
                        if (session.themeColorMap.containsKey(session.url)) {
                            updateStyle(session.themeColorMap[session.url]!!)
                        } else {
                            bitmap.apply {
                                val color = bitmap.getPixel(5, 5)
                                updateStyle(color)
                                session.themeColorMap[session.url] = color
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateStyle(@ColorInt color: Int) {

        binding.topBar.background = ColorDrawable(color)
        binding.topMenuPanel.background = ColorDrawable(color)
        StatusBarUtil.setStatusBarBackGroundColorAndIconColor(fragment.requireActivity(), color)
        setTextColor(color)
    }

    private fun setTextColor(color: Int) {
        //如果时亮色背景，就设置字体颜色为暗色
        if (ColorKitUtil.isBackGroundLightMode(color)) {
            setTextDarkMode()
        } else {
            setTextLightMode()
        }
    }

    private fun setTextLightMode() {
        session.isStatusBarDarkMode = true
        val whiteColor = fragment.resources.getColor(R.color.colorWhite)
        binding.searchText.setTextColor(whiteColor)
        binding.searchText.setHintTextColor(whiteColor)
        binding.topMenu.setTextColor(whiteColor)
        binding.progress.progressDrawable = fragment.context?.getDrawable(R.drawable.bg_progressbar_light)
        binding.progress.animate().alpha(0f).setDuration(1000).withEndAction {
            binding.progress.alpha = 1.0f
        }.start()
        val adapter = binding.topMenuPanel.adapter
        if (adapter is TopMenuPanelAdapter) {
            adapter.color = R.color.colorWhite
            adapter.notifyDataSetChanged()
        }
    }

    private fun setTextDarkMode() {
        session.isStatusBarDarkMode = false
        val blackColor = fragment.resources.getColor(R.color.colorBlack)
        binding.searchText.setTextColor(blackColor)
        binding.searchText.setHintTextColor(blackColor)
        binding.topMenu.setTextColor(blackColor)
        binding.progress.progressDrawable = fragment.context?.getDrawable(R.drawable.bg_progressbar_dark)
        binding.progress.animate().alpha(0f).setDuration(1000).withEndAction {
            binding.progress.alpha = 1.0f
        }.start()
        val adapter = binding.topMenuPanel.adapter
        if (adapter is TopMenuPanelAdapter) {
            adapter.color = R.color.colorBlack
            adapter.notifyDataSetChanged()
        }
    }
    override fun start() {
        session.register(sessionObserver)
    }

    override fun stop() {
        session.unregister(sessionObserver)
    }
}
