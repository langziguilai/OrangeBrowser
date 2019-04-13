package com.dev.orangebrowser.bloc.browser.integration

import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.session.Session
import com.dev.orangebrowser.bloc.browser.integration.helper.WebViewVisionHelper
import com.dev.orangebrowser.databinding.FragmentBrowserBinding


class WebViewScrollHandlerIntegration(var binding: FragmentBrowserBinding, session:Session, webViewVisionHelper: WebViewVisionHelper):
    LifecycleAwareFeature {
    private var mWebViewOnTouchListener: WebViewOnTouchListener
    init {
        val listener = object : OnWebViewScrollDirectionListener {
            override fun onScrollDirection(direction: Int) {
                when {
                    session.visionMode == Session.NORMAL_SCREEN_MODE -> return   //正常视野模式
                    session.visionMode == Session.SCROLL_FULL_SCREEN_MODE -> {     //滑动最大视野模式

                        if (direction == OnWebViewScrollDirectionListener.UP) {
                            session.enterFullScreenMode=true
                            webViewVisionHelper.hideBottomAndTopBarAnimate()
                        } else {
                            session.enterFullScreenMode=false
                            webViewVisionHelper.showBottomAndTopBarAnimate()
                        }
                    }
                    session.visionMode==Session.MAX_SCREEN_MODE -> {    //最大视野模式
                        if (direction == OnWebViewScrollDirectionListener.UP) {
                            session.enterFullScreenMode=true
                            webViewVisionHelper.hideBottomAndTopBarAnimate()
                            webViewVisionHelper.showMiniBottomBarAnimate()
                        }
                    }
                    session.visionMode==Session.STATIC_FULL_SCREEN_MODE-> return //静态最大视野
                }
            }
        }
        mWebViewOnTouchListener= WebViewOnTouchListener(listener,session)
    }
    override fun start() {
       binding.webViewContainer.setListener(mWebViewOnTouchListener)
    }

    override fun stop() {
       binding.webViewContainer.setListener(null)
    }
}

interface OnWebViewScrollDirectionListener {
    companion object {
        const val UP = 1
        const val DOWN = 2
    }

    fun onScrollDirection(direction: Int)
}

class WebViewOnTouchListener(var listener: OnWebViewScrollDirectionListener,var session:Session) : View.OnTouchListener {
    var isDragging = false
    var initialY = -1f
    var mLastY = -1f

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val action = event.actionMasked
        //正在加载时，不允许滑动
        if (!session.loading){
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    initialY = event.y
                    mLastY = event.y
                }
                MotionEvent.ACTION_UP -> {
                    val dy = event.y - initialY
                    if (Math.abs(dy) > ViewConfiguration.getTouchSlop()) {
                        if (dy > 0) {
                            listener.onScrollDirection(OnWebViewScrollDirectionListener.DOWN)
                        } else {
                            listener.onScrollDirection(OnWebViewScrollDirectionListener.UP)
                        }
                    }
                    var initialY = -1f
                    var mLastY = -1f
                }
                MotionEvent.ACTION_CANCEL -> {
                    val dy = event.y - initialY
                    if (Math.abs(dy) > ViewConfiguration.getTouchSlop()) {
                        if (dy > 0) {
                            listener.onScrollDirection(OnWebViewScrollDirectionListener.DOWN)
                        } else {
                            listener.onScrollDirection(OnWebViewScrollDirectionListener.UP)
                        }
                    }
                    var initialY = -1f
                    var mLastY = -1f
                }
                MotionEvent.ACTION_MOVE -> {
                    mLastY = event.y
                }
            }
        }

        return false
    }

}