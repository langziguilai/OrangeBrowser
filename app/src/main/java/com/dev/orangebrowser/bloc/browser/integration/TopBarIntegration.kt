package com.dev.orangebrowser.bloc.browser.integration

import android.os.Bundle
import com.dev.base.extension.hide
import com.dev.base.extension.isHidden
import com.dev.base.extension.show
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.feature.SessionUseCases
import com.dev.browser.session.SelectionAwareSessionObserver
import com.dev.browser.session.Session
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.browser.integration.helper.TopPanelHelper
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.orangebrowser.extension.RouterActivity


class TopBarIntegration(
    var binding: FragmentBrowserBinding,
    var fragment: BrowserFragment,
    var savedInstanceState: Bundle?,
    var topPanelHelper: TopPanelHelper,
    var session: Session,
    var sessionUseCases: SessionUseCases
) :
    LifecycleAwareFeature {
    lateinit var sessionObserver: Session.Observer

    init {
        initTopBar(savedInstanceState)
    }

    private fun initTopBar(savedInstanceState: Bundle?) {
        //优先设置为title，其次为url
        if (!session.title.isEmpty()) {
            binding.searchText.text = session.title
        } else if (!session.url.isEmpty()) {
            binding.searchText.text = session.url
        }

        binding.searchText.setOnClickListener {
            if (!binding.overLayerTopPanel.isHidden()) {
                topPanelHelper.toggleTopPanel(Runnable {
                    fragment.RouterActivity?.loadSearchFragment(fragment.sessionId)
                })
            } else {
                fragment.RouterActivity?.loadSearchFragment(fragment.sessionId)
            }
        }
        binding.topMenu.setOnClickListener {
            topPanelHelper.toggleTopPanel()
        }
        binding.reloadIcon.setOnClickListener {
            sessionUseCases.reload.invoke(session)
        }
        binding.stopIcon.setOnClickListener {
            sessionUseCases.stopLoading.invoke(session)
        }
        sessionObserver = object : Session.Observer {
            //加载改变
            override fun onLoadingStateChanged(session: Session, loading: Boolean) {
                if (loading) {
                    binding.reloadIcon.hide()
                    binding.stopIcon.show()
                    binding.progress.show()
                } else {
                    binding.reloadIcon.show()
                    binding.stopIcon.hide()
                    binding.progress.hide()
                }
            }

            //安全状态改变
            override fun onSecurityChanged(session: Session, securityInfo: Session.SecurityInfo) {
                //如果是安全的
                if (securityInfo.secure) {
                    binding.securityIcon.show()
                } else {
                    binding.securityIcon.hide()
                }
            }

            //title改变
            override fun onTitleChanged(session: Session, title: String) {
                if (!title.isBlank()){
                    binding.searchText.text = title
                }
            }

            //url改变
            override fun onUrlChanged(session: Session, url: String) {
                if (!url.isBlank()){
                    binding.searchText.text = url
                }
            }

            //加载状态改变
            override fun onProgress(session: Session, progress: Int) {
                binding.progress.progress=progress
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
