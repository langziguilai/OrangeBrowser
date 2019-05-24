package com.dev.orangebrowser.bloc.browser.integration

import android.net.Uri
import android.os.Bundle
import com.dev.base.extension.hide
import com.dev.base.extension.isHidden
import com.dev.base.extension.show
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.feature.session.SessionUseCases
import com.dev.browser.session.Session
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.browser.integration.helper.TopPanelHelper
import com.dev.orangebrowser.bloc.browser.integration.helper.redirect
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.getSpInt
import com.dev.orangebrowser.extension.getSpString


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
    val showTitle= fragment.requireContext().getString(R.string.show_title)
    val showAddress= fragment.requireContext().getString(R.string.show_address)
    val showDomain= fragment.requireContext().getString(R.string.show_domain)
    val titleSelection= fragment.getSpString(R.string.pref_setting_address_bar_show_title,showTitle)
    init {
        initTopBar(savedInstanceState)
    }

    private fun initTopBar(savedInstanceState: Bundle?) {
        setTopBarInitialState(savedInstanceState)
        binding.searchText.setOnClickListener {
            if (!binding.overLayerTopPanel.isHidden()) {
                topPanelHelper.toggleTopPanel(Runnable {
                    redirect(binding=binding,session = session,runnable = Runnable {
                        fragment.RouterActivity?.loadSearchFragment(fragment.sessionId)
                    })
                })
            } else {
                redirect(binding=binding,session = session,runnable = Runnable {
                    fragment.RouterActivity?.loadSearchFragment(fragment.sessionId)
                })
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
                    if (titleSelection==showTitle){
                        binding.searchText.text = title
                    }
                }
            }

            //url改变
            override fun onUrlChanged(session: Session, url: String) {
                if (!url.isBlank()){
                    if (titleSelection==showAddress){
                        binding.searchText.text = url
                    }
                    if (titleSelection==showDomain){
                        binding.searchText.text = Uri.parse(url).host
                    }
                }
            }

            //加载状态改变
            override fun onProgress(session: Session, progress: Int) {
                binding.progress.progress=progress
            }
        }
    }

    private fun setTopBarInitialState(savedInstanceState:Bundle?){
        //优先设置为title，其次为url
        when(titleSelection){
            showTitle->binding.searchText.text = session.title
            showDomain->binding.searchText.text = Uri.parse(session.url).host
            showAddress->binding.searchText.text = session.url
        }
//        if (session.title.isNotEmpty() && session.title!=Session.HOME_TITLE) {
//            binding.searchText.text = session.title
//        } else if (session.url.isNotEmpty()) {
//            binding.searchText.text = session.url
//        }
        if (session.securityInfo.secure){
            binding.securityIcon.show()
        }else{
            binding.securityIcon.hide()
        }
        if(session.loading){
            binding.reloadIcon.hide()
            binding.stopIcon.show()
            binding.progress.show()
        } else {
            binding.reloadIcon.show()
            binding.stopIcon.hide()
            binding.progress.hide()
        }
    }

    override fun start() {
        session.register(sessionObserver)
    }

    override fun stop() {
        session.unregister(sessionObserver)
    }
}
