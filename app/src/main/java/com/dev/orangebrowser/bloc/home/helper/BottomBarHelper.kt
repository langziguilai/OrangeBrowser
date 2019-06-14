package com.dev.orangebrowser.bloc.home.helper

import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.lifecycle.Observer
import com.dev.base.extension.*
import com.dev.base.support.BackHandler
import com.dev.browser.session.Session
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.home.HomeFragment
import com.dev.orangebrowser.data.model.ActionItem
import com.dev.orangebrowser.data.model.Theme
import com.dev.orangebrowser.data.model.getBottomMenuActionItems
import com.dev.orangebrowser.databinding.FragmentHomeBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.util.DensityUtil
import com.dev.view.GridView
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import es.dmoral.toasty.Toasty

class BottomBarHelper(private var binding: FragmentHomeBinding, var fragment: HomeFragment, var savedInstanceState: Bundle?){
    private lateinit var bottomPanelBackHandler: BackHandler
    init{
        initBottomBar(savedInstanceState = savedInstanceState)
    }
    private fun initBottomBar(savedInstanceState: Bundle?) {
        //设置back颜色
        binding.back.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryDisable)
        //设置forward颜色
        fragment.sessionManager.findSessionById(fragment.sessionId)?.apply {
            if (this.url!= Session.NO_EXIST_URL){
                binding.forward.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                binding.forward.isEnabled=true
            }else{
                binding.forward.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryDisable)
                binding.forward.isEnabled=false
            }
        }
        //监听更新主题
        fragment.activityViewModel.theme.observe(fragment, Observer<Theme> {
            fragment.sessionManager.findSessionById(fragment.sessionId)?.apply {
                if (this.url!= Session.NO_EXIST_URL){
                    binding.forward.setTextColor(it.colorPrimary)
                    binding.forward.isEnabled=true
                }else{
                    binding.forward.setTextColor(it.colorPrimaryDisable)
                    binding.forward.isEnabled=false
                }
            }
            binding.bottomMenuGridView.adapter?.notifyDataSetChanged()
        })
        binding.forward.setOnClickListener {
             fragment.RouterActivity?.loadBrowserFragment(fragment.sessionId)
        }
        binding.search.setOnClickListener {
            fragment.RouterActivity?.loadSearchFragment(fragment.sessionId)
        }
        //跳转到TabFragment
        binding.counter.setOnClickListener {
            val ratio=(binding.recyclerView.height.toFloat())/(binding.recyclerView.width.toFloat())
            fragment.RouterActivity?.loadTabFragment(fragment.sessionId,ratio)
        }
        //获取session数量
        binding.counterNumber.text=fragment.sessionManager.size.toString()
        binding.menu.setOnClickListener {
            toggleBottomPanel()
        }
        initBottomMenuGridView(binding.bottomMenuGridView)
        binding.bottomMenuGridViewClose.setOnClickListener {
            toggleBottomPanel()
        }
        binding.overLayerBottomPanel.setOnClickListener {
            toggleBottomPanel()
        }
        binding.bottomMenuPanel.apply {
            onGlobalLayoutComplete{
                if (fragment.context!=null){
                    it.translationY=this.height.toFloat()+ DensityUtil.dip2px(fragment.requireContext(),16f)
                }
            }
        }
        bottomPanelBackHandler=object: BackHandler {
            override fun onBackPressed(): Boolean {
                toggleBottomPanel()
                return true
            }
        }
    }
    private fun initBottomMenuGridView(bottomMenuGridView: GridView) {
        //过滤掉无用过的选项
        val data=getBottomMenuActionItems().filter {
            !(it.id==R.string.ic_forbid_image || it.id==R.string.ic_privacy || it.id==R.string.ic_normal_screen || it.id==R.string.ic_desktop)
        }
        val adapter=object: BaseQuickAdapter<ActionItem, CustomBaseViewHolder>(R.layout.item_bottom_action_item,data){
            override fun convert(helper: CustomBaseViewHolder, item: ActionItem) {
                if (item.active){
                    helper.setTextColor(R.id.icon,fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                    helper.setTextColor(R.id.name,fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                }else{
                    helper.setTextColor(R.id.icon,fragment.activityViewModel.theme.value!!.colorPrimary)
                    helper.setTextColor(R.id.name,fragment.activityViewModel.theme.value!!.colorPrimary)
                }
                helper.setText(R.id.icon,item.iconRes)
                helper.setText(R.id.name,item.nameRes)
            }
        }
        adapter.setOnItemClickListener { _, view, position ->
            onBottomMenuActionItemClick(view,data[position])
        }
        bottomMenuGridView.adapter=adapter
    }
    private fun onBottomMenuActionItemClick(view: View, actionItem: ActionItem) {
        when(actionItem.iconRes){
            //发现
            R.string.ic_found->{
                toggleBottomPanel(Runnable {
                    fragment.RouterActivity?.loadFoundFragment()
                })
            }
            //历史
            R.string.ic_history->{
                toggleBottomPanel(Runnable {
                    fragment.RouterActivity?.loadHistoryFragment()
                })

            }
            //书签
            R.string.ic_bookmark->{
                toggleBottomPanel(Runnable {
                    fragment.RouterActivity?.loadBookMarkFragment()
                })

            }
            //收藏
            R.string.ic_star->{
                Toasty.warning(fragment.requireContext(), R.string.webview_not_available_hint, Toast.LENGTH_SHORT, true).show()
            }
            //主题
            R.string.ic_theme->{
                toggleBottomPanel(Runnable {
                    fragment.RouterActivity?.loadThemeFragment()
                })

            }
            //下载
            R.string.ic_download->{
                toggleBottomPanel(Runnable {
                    fragment.RouterActivity?.loadDownloadFragment()
                })

            }
            //设置
            R.string.ic_setting->{
                toggleBottomPanel(Runnable {
                    fragment.RouterActivity?.loadSettingFragment()
                })

            }
            //退出
            R.string.ic_quit->{
                fragment.RouterActivity?.quit()
            }
        }
    }
    fun toggleBottomPanel(runnable: Runnable= Runnable { }){
        //如果是隐藏的，那么显示
        if (binding.overLayerBottomPanel.isHidden()){
            showBottomPanel(runnable)
            fragment.backHandlers.add(0,bottomPanelBackHandler)
        }else{
            hideBottomPanel(runnable)
            fragment.backHandlers.remove(bottomPanelBackHandler)
        }
    }
    private fun showBottomPanel(runnable: Runnable= Runnable {  }) {
        binding.overLayerBottomPanel.show()
        binding.bottomMenuPanel.apply {
            animate().translationY(0f).setDuration(FAST_ANIMATION).setInterpolator(AccelerateInterpolator()).withEndAction(runnable).start()
        }
    }
    private fun hideBottomPanel(runnable: Runnable= Runnable { }) {
        binding.overLayerBottomPanel.hide()
        binding.bottomMenuPanel.apply {
            animate().translationY(this.height.toFloat()+ DensityUtil.dip2px(fragment.requireContext(),16f)).setDuration(
                FAST_ANIMATION
            ).withEndAction(runnable).start()
        }
    }
}
