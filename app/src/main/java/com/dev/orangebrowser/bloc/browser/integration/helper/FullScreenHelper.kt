package com.dev.orangebrowser.bloc.browser.integration.helper

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.dev.base.extension.enterToImmersiveMode
import com.dev.base.extension.exitImmersiveModeIfNeeded
import com.dev.base.extension.hide
import com.dev.base.extension.show
import com.dev.browser.session.Session
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainActivity
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.orangebrowser.bloc.browser.view.WebViewToggleBehavior
import com.dev.view.StatusBarUtil

class FullScreenHelper(var binding:FragmentBrowserBinding,var activity: Activity){
    var lastScreenMode:Int=-1
    private var behavior: WebViewToggleBehavior?=null
    init {
        if(binding.webViewContainer.layoutParams is CoordinatorLayout.LayoutParams){
            val layoutParams= binding.webViewContainer.layoutParams as CoordinatorLayout.LayoutParams
            if (layoutParams.behavior is WebViewToggleBehavior){
                behavior=layoutParams.behavior as WebViewToggleBehavior
            }
        }
    }
    fun toggleFullScreen(session: Session, fullScreen: Boolean){
           //进入全局视野
           if (fullScreen){
               //隐藏下部导航栏
               activity.enterToImmersiveMode()
               StatusBarUtil.hideStatusBar(activity)
               lastScreenMode=session.visionMode

               binding.topBar.hide()
               binding.bottomBar.hide()
               binding.miniBottomBar.hide()
               session.visionMode=Session.STATIC_FULL_SCREEN_MODE
               binding.fragmentContainer.requestLayout()
               (activity as? MainActivity)?.apply {
                   enableAutoOrientation=false
               }
               if (activity.resources.configuration.orientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                   activity.requestedOrientation= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
               }

           }else{ //退出全局视野
               //如果不是全屏模式，则显示StatusBar
//               if(!activity.getPreferences(Context.MODE_PRIVATE).getBoolean(activity.getString(R.string.pref_setting_full_screen),false)){
//                   StatusBarUtil.showStatusBar(activity)
//               }
               //退出全屏模式
               activity.exitImmersiveModeIfNeeded()
               //隐藏StatusBar之后，其文字的颜色会变为默认颜色，我们需要修改其颜色
               if (session.isStatusBarDarkMode){
                   StatusBarUtil.setLightIcon(activity)
               }else{
                   StatusBarUtil.setDarkIcon(activity)
               }
               //恢复默认位置
               binding.topBar.show()
               binding.bottomBar.show()
               //显示bottombar
               binding.bottomBar.translationY=0f
               binding.miniBottomBar.show()
               //隐藏miniBottomBar
               binding.miniBottomBar.translationY=binding.miniBottomBar.height.toFloat()
               session.visionMode=lastScreenMode
               binding.fragmentContainer.requestLayout()
               (activity as? MainActivity)?.apply {
                   enableAutoOrientation=true
               }
               if (activity.resources.configuration.orientation!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                   activity.requestedOrientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
               }

           }
    }
}
