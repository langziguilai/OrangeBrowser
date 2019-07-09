package com.dev.orangebrowser.bloc.browser.integration

import android.os.Bundle
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.feature.session.SessionUseCases
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.browser.integration.helper.BottomPanelHelper
import com.dev.orangebrowser.bloc.browser.integration.helper.redirect
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.orangebrowser.extension.RouterActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

class BottomBarIntegration(
    private var binding: FragmentBrowserBinding,
    var fragment: BrowserFragment,
    var savedInstanceState: Bundle?,
    var bottomPanelHelper: BottomPanelHelper,
    var sessionUseCases: SessionUseCases,
    var sessionManager: SessionManager,
    var session: Session
) :
    LifecycleAwareFeature, CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    lateinit var sessionObserver: Session.Observer
    lateinit var sessionManagerObserver: SessionManager.Observer

    init {
        initBottomBar(savedInstanceState = savedInstanceState)
    }

    private fun initBottomBar(savedInstanceState: Bundle?) {
        setBottomBarInitialState(savedInstanceState)
        //后退
        binding.back.setOnClickListener {
            fragment.onBackPressed()
        }
        //前进
        binding.forward.setOnClickListener {
            if (session.canGoForward) {
                sessionUseCases.goForward.invoke(session)
            }
        }
        binding.home.setOnClickListener {
            redirect(binding=binding,session = session,runnable = Runnable {
                fragment.RouterActivity?.loadHomeFragment(fragment.sessionId)
            })
        }
        //跳转到TabFragment
        binding.counter.setOnClickListener {
            redirect(binding=binding,session = session,runnable = Runnable {
                val height=binding.fragmentContainer.height-binding.topBar.height-binding.bottomBar.height
                val ratio = (height.toFloat()) / (binding.fragmentContainer.width.toFloat())
                fragment.RouterActivity?.loadTabFragment(fragment.sessionId, ratio)
            })
        }
        binding.menu.setOnClickListener {
            bottomPanelHelper.toggleBottomPanel()
        }
        sessionObserver = object : Session.Observer {
            override fun onNavigationStateChanged(session: Session, canGoBack: Boolean, canGoForward: Boolean) {
                if (canGoForward) {
                    binding.forward.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                } else {
                    binding.forward.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryDisable)
                }
            }
        }
        sessionManagerObserver = object : SessionManager.Observer {
            override fun onSessionAdded(session: Session) {
                binding.counterNumber.text = sessionManager.size.toString()
            }

            override fun onSessionRemoved(session: Session) {
                binding.counterNumber.text = sessionManager.size.toString()
            }

            override fun onAllSessionsRemoved() {
                binding.counterNumber.text = sessionManager.size.toString()
            }
        }
    }

    //设置初始化状态
    private fun setBottomBarInitialState(savedInstanceState: Bundle?) {
        //设置forward颜色
        if (session.canGoForward) {
            binding.forward.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
        } else {
            binding.forward.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryDisable)
        }
        binding.counterNumber.text = sessionManager.size.toString()
    }


    override fun start() {
        session.register(sessionObserver)
        sessionManager.register(sessionManagerObserver)
    }

    override fun stop() {
        session.unregister(sessionObserver)
        sessionManager.unregister(sessionManagerObserver)
    }
}
