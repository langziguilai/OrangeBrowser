package com.dev.orangebrowser.bloc.browser.integration

import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.orangebrowser.extension.getSpBool
import com.dev.orangebrowser.extension.getSpInt
import com.dev.orangebrowser.extension.getSpString

//设置WebView的Setting
class WebViewSettingIntegration(
    var binding: FragmentBrowserBinding,
    var fragment: BrowserFragment,
    var sessionManager: SessionManager,
    var session: Session
) :
    LifecycleAwareFeature {

    override fun start() {
        val engineSession = sessionManager.getOrCreateEngineSession(session)
        //设置字体大小
        engineSession.setFontSize(fragment.getSpInt(R.string.pref_setting_font_size, 100))
        //设置追踪
        if (fragment.getSpBool(R.string.pref_setting_refuse_track, true)) {
            engineSession.disableTrackingProtection()
        } else {
            engineSession.enableTrackingProtection()
        }
        //设置UserAgent并且隐藏设备信息
        engineSession.setUserAgent(fragment.getSpString(R.string.pref_setting_ua))
        //设置是否是禁止图片模式
        engineSession.forbidLoadingImage(session.forbidImageMode,false)
        //设置是否是桌面模式
        if (session.desktopMode){
            engineSession.setUserAgent(fragment.requireContext().getString(R.string.user_agent_pc))
        }else{
            val ua=fragment.getSpString(R.string.pref_setting_ua)
            if (ua!=fragment.requireContext().getString(R.string.user_agent_pc)){
                engineSession.setUserAgent(ua)
            }else{
                engineSession.setUserAgent(fragment.requireContext().getString(R.string.user_agent_android))
            }
        }
    }

    override fun stop() {

    }
}
