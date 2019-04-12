package com.dev.orangebrowser.bloc.browser.integration

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.dev.base.extension.onGlobalLayoutComplete
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.feature.SessionUseCases
import com.dev.browser.session.Session
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.browser.integration.helper.BottomPanelHelper
import com.dev.orangebrowser.data.model.ActionItem
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appData
import com.dev.orangebrowser.view.WebViewToggleBehavior
import com.dev.util.DensityUtil
import com.dev.view.GridView
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import es.dmoral.toasty.Toasty

class BottomPanelMenuIntegration(
    private var binding: FragmentBrowserBinding,
    var fragment: BrowserFragment,
    var savedInstanceState: Bundle?,
    var bottomPanelHelper: BottomPanelHelper,
    var session: Session,
    var sessionUseCases: SessionUseCases
) :
    LifecycleAwareFeature {
    private var sessionObserver:Session.Observer
    private var behavior:WebViewToggleBehavior?=null
    init {
        initBottomMenuGridView(binding.bottomMenuGridView)
        if(binding.webViewContainer.layoutParams is CoordinatorLayout.LayoutParams){
           val layoutParams= binding.webViewContainer.layoutParams as CoordinatorLayout.LayoutParams
            if (layoutParams.behavior is WebViewToggleBehavior){
                behavior=layoutParams.behavior as WebViewToggleBehavior
            }
        }
        binding.bottomMenuGridViewClose.setOnClickListener {
            bottomPanelHelper.toggleBottomPanel()
        }
        binding.overLayerBottomPanel.setOnClickListener {
            bottomPanelHelper.toggleBottomPanel()
        }
        //开始的时候隐藏
        binding.bottomMenuPanel.apply {
            onGlobalLayoutComplete {
                fragment.context?.apply {
                    it.translationY=it.height.toFloat() + DensityUtil.dip2px(fragment.requireContext(), 16f)
                }
            }
        }
        sessionObserver=object :Session.Observer{
            override fun onDesktopModeChanged(session: Session, enabled: Boolean) {
                //自动重载，所以不需要在此处重新加载sessionUseCases.reload.invoke(session)
            }
        }
    }

    private fun initBottomMenuGridView(bottomMenuGridView: GridView) {
        val adapter = object : BaseQuickAdapter<ActionItem, CustomBaseViewHolder>(
            R.layout.item_bottom_action_item,
            fragment.appData.bottomMenuActionItems
        ) {
            override fun convert(helper: CustomBaseViewHolder, item: ActionItem) {
                if (item.active) {
                    helper.setTextColor(R.id.icon, fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                    helper.setTextColor(R.id.name, fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                } else {
                    helper.setTextColor(R.id.icon, fragment.activityViewModel.theme.value!!.colorPrimary)
                    helper.setTextColor(R.id.name, fragment.activityViewModel.theme.value!!.colorPrimary)
                }
                helper.setText(R.id.icon, item.iconRes)
                helper.setText(R.id.name, item.nameRes)
            }
        }
        adapter.setOnItemClickListener { _, view, position ->
            onBottomMenuActionItemClick(view, fragment.appData.bottomMenuActionItems[position])
        }
        bottomMenuGridView.adapter = adapter
    }

    //TODO:点击底部MenuItem
    private fun onBottomMenuActionItemClick(view: View, actionItem: ActionItem) {
        when (actionItem.id) {
            //无图模式
            R.string.ic_forbid_image -> {
                actionItem.active = !actionItem.active
                if (actionItem.active) {
                    view.findViewById<TextView>(R.id.icon)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                    view.findViewById<TextView>(R.id.name)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                } else {
                    view.findViewById<TextView>(R.id.icon)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                    view.findViewById<TextView>(R.id.name)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                }
                sessionUseCases.forbidLoadImage.invoke(actionItem.active)
                session.forbidImageMode=actionItem.active
            }
            //隐身
            R.string.ic_privacy -> {
                actionItem.active = !actionItem.active
                if (actionItem.active) {
                    view.findViewById<TextView>(R.id.icon)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                    view.findViewById<TextView>(R.id.name)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                } else {
                    view.findViewById<TextView>(R.id.icon)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                    view.findViewById<TextView>(R.id.name)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                }
                session.private=actionItem.active
            }
            //全局视野
            R.string.ic_normal_screen -> {
                var newVisionMode=Session.SCROLL_FULL_SCREEN_MODE
                when {
                    session.visionMode==Session.NORMAL_SCREEN_MODE -> {
                        newVisionMode=Session.SCROLL_FULL_SCREEN_MODE
                        actionItem.active = !actionItem.active
                        view.findViewById<TextView>(R.id.icon)
                            .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                        view.findViewById<TextView>(R.id.icon).setText(R.string.ic_auto_fullscreen)
                        view.findViewById<TextView>(R.id.name)
                            .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                    }
                    session.visionMode==Session.SCROLL_FULL_SCREEN_MODE -> {
                        newVisionMode=Session.MAX_SCREEN_MODE
                        view.findViewById<TextView>(R.id.icon).setText(R.string.ic_fullscreen)
                    }
                    session.visionMode==Session.MAX_SCREEN_MODE  -> {
                        newVisionMode=Session.NORMAL_SCREEN_MODE
                        actionItem.active = !actionItem.active
                        view.findViewById<TextView>(R.id.icon)
                            .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                        view.findViewById<TextView>(R.id.icon).setText(R.string.ic_normal_screen)
                        view.findViewById<TextView>(R.id.name)
                            .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                    }
                }
                session.visionMode=newVisionMode
                if (session.visionMode==Session.MAX_SCREEN_MODE){
                    binding.miniBottomBar.visibility=View.VISIBLE
                }else{
                    binding.miniBottomBar.visibility=View.GONE
                }
                behavior?.screenMode=session.visionMode
                binding.fragmentContainer.requestLayout()
            }
            //电脑
            R.string.ic_desktop -> {
                actionItem.active = !actionItem.active
                if (actionItem.active) {
                    sessionUseCases.requestDesktopSite.invoke(true,session)
                    view.findViewById<TextView>(R.id.icon)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                    view.findViewById<TextView>(R.id.name)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                } else {
                    sessionUseCases.requestDesktopSite.invoke(false,session)
                    view.findViewById<TextView>(R.id.icon)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                    view.findViewById<TextView>(R.id.name)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                }

            }
            //发现
            R.string.ic_found -> {
                bottomPanelHelper.toggleBottomPanel(Runnable {
                    fragment.RouterActivity?.loadFoundFragment()
                })
            }
            //历史
            R.string.ic_history -> {
                bottomPanelHelper.toggleBottomPanel(Runnable {
                    fragment.RouterActivity?.loadHistoryFragment()
                })

            }
            //书签
            R.string.ic_bookmark -> {
                bottomPanelHelper.toggleBottomPanel(Runnable {
                    fragment.RouterActivity?.loadBookMarkFragment()
                })

            }
            //收藏
            R.string.ic_star -> {
                Toasty.warning(fragment.requireContext(), R.string.webview_not_available_hint, Toast.LENGTH_SHORT, true)
                    .show()
            }
            //主题
            R.string.ic_theme -> {
                bottomPanelHelper.toggleBottomPanel(Runnable {
                    fragment.RouterActivity?.loadThemeFragment()
                })

            }
            //下载
            R.string.ic_download -> {
                bottomPanelHelper.toggleBottomPanel(Runnable {
                    fragment.RouterActivity?.loadDownloadFragment()
                })

            }
            //设置
            R.string.ic_setting -> {
                bottomPanelHelper.toggleBottomPanel(Runnable {
                    fragment.RouterActivity?.loadSettingFragment()
                })

            }
            //退出
            R.string.ic_quit -> {
                fragment.RouterActivity?.quit()
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