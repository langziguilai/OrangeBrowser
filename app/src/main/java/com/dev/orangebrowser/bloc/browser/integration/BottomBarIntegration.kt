package com.dev.orangebrowser.bloc.browser.integration

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import com.dev.base.extension.capture
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.feature.session.SessionUseCases
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.browser.integration.helper.BottomPanelHelper
import com.dev.orangebrowser.bloc.browser.integration.helper.redirect
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.application
import com.dev.util.DensityUtil
import com.dev.util.FileUtil
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference
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
        //后退
//        binding.back.setOnClickListener {
//            fragment.onBackPressed()
//        }
//        //设置forward颜色
//        if (session.canGoForward){
//            binding.forward.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
//        }else{
//            binding.forward.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryDisable)
//        }
//        binding.counterNumber.text=sessionManager.size.toString()
        setBottomBarInitialState(savedInstanceState)
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
                val ratio = (binding.webViewContainer.height.toFloat()) / (binding.webViewContainer.width.toFloat())
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
        //后退
        binding.back.setOnClickListener {
            fragment.onBackPressed()
        }
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
