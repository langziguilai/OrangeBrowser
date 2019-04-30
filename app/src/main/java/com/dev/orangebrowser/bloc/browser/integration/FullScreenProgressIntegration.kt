package com.dev.orangebrowser.bloc.browser.integration


import com.dev.base.extension.hide
import com.dev.base.extension.show
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.session.Session
import com.dev.orangebrowser.databinding.FragmentBrowserBinding

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
