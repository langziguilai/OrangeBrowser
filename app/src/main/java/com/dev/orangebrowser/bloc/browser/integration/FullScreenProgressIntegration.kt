package com.dev.orangebrowser.bloc.browser.integration


import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.annotation.ColorInt
import com.dev.base.extension.hide
import com.dev.base.extension.show
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.concept.HitResult
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
class FullScreenProgressIntegration(
    var binding: FragmentBrowserBinding,
    var session: Session
) :
    LifecycleAwareFeature {
    private var sessionObserver: Session.Observer

    init {
        sessionObserver=object :Session.Observer{
            override fun onLoadingStateChanged(session: Session, loading: Boolean) {
                if (loading){
                    binding.progressForFullscreen.show()
                }else{
                    binding.progressForFullscreen.hide()
                }
            }

            override fun onProgress(session: Session, progress: Int) {
                binding.progressForFullscreen.progress=progress
            }
        }
    }

    override fun start() {
        session.register(sessionObserver)
    }

    override fun stop() {
        session.unregister(sessionObserver)
    }
}
