package com.dev.orangebrowser.bloc.browser.integration

import androidx.lifecycle.LifecycleOwner
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.concept.EngineView
import com.dev.browser.concept.EngineViewLifecycleObserver
import com.dev.orangebrowser.databinding.FragmentBrowserBinding

//绑定EngineView到LifecycleOwner，防止内存泄漏
class WebViewLifeCycleIntegration(binding: FragmentBrowserBinding, owner: LifecycleOwner,engineView: EngineView):
    LifecycleAwareFeature {
    init{
        //添加生命周期监听
        owner.lifecycle.addObserver(EngineViewLifecycleObserver(engineView))
    }
    override fun start() {
        //TODO
    }

    override fun stop() {
        //TODO
    }
}
