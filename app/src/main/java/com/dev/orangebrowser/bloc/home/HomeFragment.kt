package com.dev.orangebrowser.bloc.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.dev.base.BaseLazyFragment
import com.dev.base.extension.*
import com.dev.base.support.BackHandler
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.data.model.ActionItem
import com.dev.orangebrowser.data.model.Site
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.extension.appData
import com.dev.util.DensityUtil
import com.dev.view.GridView
import com.dev.view.IconfontTextView
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import com.evernote.android.state.State
import es.dmoral.toasty.Toasty
import java.util.*

class HomeFragment : BaseLazyFragment(), BackHandler {
    //data
    lateinit var viewModel: HomeViewModel
    @State
    lateinit var favorSites: ArrayList<Site>

    //view
    lateinit var topMenuPanel:GridView
    lateinit var overLayerForTopMenuPanel:View //遮罩层
    lateinit var bottomMenuPanel:View
    lateinit var bottomMenuGridView:GridView
    lateinit var overLayerForBottomMenuPanel:View //遮罩层
    lateinit var viewPager:ViewPager
    lateinit var backBtn:IconfontTextView
    lateinit var forwardBtn:IconfontTextView
    lateinit var searchBtn:IconfontTextView
    lateinit var counterNumber:TextView
    lateinit var menuBtn:IconfontTextView
    //
    private val backHandlers =LinkedList<BackHandler>()
    private var bottomPanelBackHandler:BackHandler?=null
    private var topPanelBackHandler:BackHandler?=null

    //
    private val sessionId: String
        get() = arguments?.getString(BrowserFragment.SESSION_ID) ?: ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return super.onCreateView(inflater, container, savedInstanceState)
    }
    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_home
    }

    //初始化view
    override fun initView(view: View, savedInstanceState: Bundle?) {
        initTopBar(view,savedInstanceState)
        initBottomBar(view,savedInstanceState)
        initViewPager(view,savedInstanceState)
    }
    //初始化TopBar
    private fun initTopBar(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.search_text).setOnClickListener {
              if (!overLayerForTopMenuPanel.isHidden()){
                toggleTopPanel(Runnable{
                    RouterActivity?.loadSearchFragment()
                })
              }else{
                  RouterActivity?.loadSearchFragment()
              }
        }
        view.findViewById<View>(R.id.top_menu).setOnClickListener {
              toggleTopPanel()
        }
        topMenuPanel=view.findViewById(R.id.top_menu_panel)
        initTopMenuGridView(topMenuPanel)
        overLayerForTopMenuPanel=view.findViewById(R.id.over_layer_top_panel)
        overLayerForTopMenuPanel.setOnClickListener {
            toggleTopPanel()
        }
        //initial hide
        topMenuPanel.apply {
            onGlobalLayoutComplete{
                it.animate().translationY(-this.height.toFloat()).setDuration(0).start()
            }
        }
        topPanelBackHandler=object:BackHandler{
            override fun onBackPressed(): Boolean {
                toggleTopPanel()
                return true
            }

        }
    }

    private fun initTopMenuGridView(topMenuPanel: GridView) {
           val adapter=object:BaseQuickAdapter<ActionItem,CustomBaseViewHolder>(R.layout.item_top_action_item,appData.topMenuActionItems){
               override fun convert(helper: CustomBaseViewHolder, item: ActionItem) {
                   helper.setText(R.id.icon,item.iconRes)
                   helper.setText(R.id.name,item.nameRes)
               }
           }
           adapter.setOnItemClickListener { _, _, position ->
               onTopMenuActionItemClick(appData.topMenuActionItems[position])
           }
           topMenuPanel.adapter=adapter
    }

    //处理Top Menu Item的点击
    private fun onTopMenuActionItemClick(actionItem: ActionItem) {
        when(actionItem.iconRes){
            //扫码
            R.string.ic_scan->{
                toggleTopPanel(Runnable {
                    RouterActivity?.loadScanFragment()
                })
            }
            //TODO:分享
            R.string.ic_share->{

            }
            //阅读模式
            R.string.ic_read->{
                Toasty.warning(requireContext(),R.string.webview_not_available_hint,Toast.LENGTH_SHORT).show()
            }
            //看图模式
            R.string.ic_image->{
                Toasty.warning(requireContext(),R.string.webview_not_available_hint,Toast.LENGTH_SHORT).show()
            }
            //标记广告
            R.string.ic_ad_mark->{
                Toasty.warning(requireContext(),R.string.webview_not_available_hint,Toast.LENGTH_SHORT).show()
            }
            //页内查找
            R.string.ic_search->{
                Toasty.warning(requireContext(),R.string.webview_not_available_hint,Toast.LENGTH_SHORT).show()
            }
            //离线保存
            R.string.ic_save->{
                Toasty.warning(requireContext(),R.string.webview_not_available_hint,Toast.LENGTH_SHORT).show()
            }
            //翻译
            R.string.ic_translate->{
                Toasty.warning(requireContext(),R.string.webview_not_available_hint,Toast.LENGTH_SHORT).show()
            }
            //源码
            R.string.ic_code->{
                Toasty.warning(requireContext(),R.string.webview_not_available_hint,Toast.LENGTH_SHORT).show()
            }
            //资源嗅探
            R.string.ic_resources_fang->{
                Toasty.warning(requireContext(),R.string.webview_not_available_hint,Toast.LENGTH_SHORT).show()
            }
            //添加到主页
            R.string.ic_store->{
                Toasty.warning(requireContext(),R.string.webview_not_available_hint,Toast.LENGTH_SHORT).show()
            }
            //天网
            R.string.ic_sky_net->{
                Toasty.warning(requireContext(),R.string.webview_not_available_hint,Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleTopPanel(runnable: Runnable= Runnable { }){
        //如果是隐藏的，那么显示
       if (overLayerForTopMenuPanel.isHidden()){
           showTopPanel(runnable)
           backHandlers.add(0,topPanelBackHandler!!)
       }else{
           hideTopPanel(runnable)
           backHandlers.remove(topPanelBackHandler)
       }
    }
    private fun hideTopPanel(runnable: Runnable= Runnable { }) {
        overLayerForTopMenuPanel.hide()
        topMenuPanel.apply {
            animate().translationY(-this.height.toFloat()).setDuration(FAST_ANIMATION).setInterpolator(
                AccelerateInterpolator()
            ).withEndAction(runnable).start()
        }
    }
    private fun showTopPanel(runnable: Runnable= Runnable { }){
        overLayerForTopMenuPanel.show()
        topMenuPanel.apply {
            animate().translationY(0f).setDuration(FAST_ANIMATION).setInterpolator(AccelerateInterpolator()).withEndAction(runnable).start()
        }
        //如果bottomPanelBar是显示的，那么隐藏bottomPanel
        if (overLayerForBottomMenuPanel.isShowing()){
            toggleBottomPanel()
        }
    }
    //初始化BottomBar
    private fun initBottomBar(view: View, savedInstanceState: Bundle?) {
        backBtn=view.findViewById(R.id.back)
        forwardBtn=view.findViewById(R.id.forward)
        searchBtn=view.findViewById(R.id.search)
        searchBtn.setOnClickListener {
            RouterActivity?.loadSearchFragment()
        }
        //跳转到TabFragment
        view.findViewById<View>(R.id.counter).setOnClickListener {
            RouterActivity?.loadTabFragment(sessionId)
        }
        counterNumber=view.findViewById(R.id.counterNumber)
        menuBtn=view.findViewById(R.id.menu)
        menuBtn.setOnClickListener {
            toggleBottomPanel()
        }
        bottomMenuPanel=view.findViewById(R.id.bottom_menu_panel)
        bottomMenuGridView=view.findViewById(R.id.bottom_menu_grid_view)
        initBottomMenuGridView(bottomMenuGridView)
        view.findViewById<View>(R.id.bottom_menu_grid_view_close).setOnClickListener {
            toggleBottomPanel()
        }
        overLayerForBottomMenuPanel=view.findViewById(R.id.over_layer_bottom_panel)
        overLayerForBottomMenuPanel.setOnClickListener {
            toggleBottomPanel()
        }
        bottomMenuPanel.apply {
            onGlobalLayoutComplete{
                it.animate().translationY(this.height.toFloat()+DensityUtil.dip2px(requireContext(),16f)).setDuration(0).start()
            }
        }
        bottomPanelBackHandler=object:BackHandler{
            override fun onBackPressed(): Boolean {
                toggleBottomPanel()
                return true
            }
        }
    }

    private fun initBottomMenuGridView(bottomMenuGridView: GridView) {
        val adapter=object:BaseQuickAdapter<ActionItem,CustomBaseViewHolder>(R.layout.item_bottom_action_item,appData.bottomMenuActionItems){
            override fun convert(helper: CustomBaseViewHolder, item: ActionItem) {
                if (item.active){
                    helper.setTextColor(R.id.icon,resources.getColor(R.color.colorPrimaryActive))
                    helper.setTextColor(R.id.name,resources.getColor(R.color.colorPrimaryActive))
                }else{
                    helper.setTextColor(R.id.icon,resources.getColor(R.color.colorPrimary))
                    helper.setTextColor(R.id.name,resources.getColor(R.color.colorPrimary))
                }
                helper.setText(R.id.icon,item.iconRes)
                helper.setText(R.id.name,item.nameRes)
            }
        }
        adapter.setOnItemClickListener { _, view, position ->
                onBottomMenuActionItemClick(view,appData.bottomMenuActionItems[position])
        }
        bottomMenuGridView.adapter=adapter
    }

    private fun onBottomMenuActionItemClick(view:View,actionItem: ActionItem) {
        when(actionItem.iconRes){
            //无图模式
            R.string.ic_forbid_image->{
                actionItem.active= !actionItem.active
                if (actionItem.active){
                    view.findViewById<TextView>(R.id.icon).setTextColor(resources.getColor(R.color.colorPrimaryActive))
                    view.findViewById<TextView>(R.id.name).setTextColor(resources.getColor(R.color.colorPrimaryActive))
                }else{
                    view.findViewById<TextView>(R.id.icon).setTextColor(resources.getColor(R.color.colorPrimary))
                    view.findViewById<TextView>(R.id.name).setTextColor(resources.getColor(R.color.colorPrimary))
                }
            }
            //隐身
            R.string.ic_desktop->{
                actionItem.active= !actionItem.active
                if (actionItem.active){
                    view.findViewById<TextView>(R.id.icon).setTextColor(resources.getColor(R.color.colorPrimaryActive))
                    view.findViewById<TextView>(R.id.name).setTextColor(resources.getColor(R.color.colorPrimaryActive))
                }else{
                    view.findViewById<TextView>(R.id.icon).setTextColor(resources.getColor(R.color.colorPrimary))
                    view.findViewById<TextView>(R.id.name).setTextColor(resources.getColor(R.color.colorPrimary))
                }
            }
            //全局视野
            R.string.ic_fullscreen->{
                actionItem.active= !actionItem.active
                if (actionItem.active){
                    view.findViewById<TextView>(R.id.icon).setTextColor(resources.getColor(R.color.colorPrimaryActive))
                    view.findViewById<TextView>(R.id.name).setTextColor(resources.getColor(R.color.colorPrimaryActive))
                }else{
                    view.findViewById<TextView>(R.id.icon).setTextColor(resources.getColor(R.color.colorPrimary))
                    view.findViewById<TextView>(R.id.name).setTextColor(resources.getColor(R.color.colorPrimary))
                }
            }
            //电脑
            R.string.ic_privacy->{
                actionItem.active= !actionItem.active
                if (actionItem.active){
                    view.findViewById<TextView>(R.id.icon).setTextColor(resources.getColor(R.color.colorPrimaryActive))
                    view.findViewById<TextView>(R.id.name).setTextColor(resources.getColor(R.color.colorPrimaryActive))
                }else{
                    view.findViewById<TextView>(R.id.icon).setTextColor(resources.getColor(R.color.colorPrimary))
                    view.findViewById<TextView>(R.id.name).setTextColor(resources.getColor(R.color.colorPrimary))
                }
            }
            //发现
            R.string.ic_found->{
                toggleBottomPanel(Runnable {
                    RouterActivity?.loadFoundFragment()
                })
            }
            //历史
            R.string.ic_history->{
                toggleBottomPanel(Runnable {
                    RouterActivity?.loadHistoryFragment()
                })

            }
            //书签
            R.string.ic_bookmark->{
                toggleBottomPanel(Runnable {
                    RouterActivity?.loadBookMarkFragment()
                })

            }
            //收藏
            R.string.ic_star->{
                Toasty.warning(requireContext(), R.string.webview_not_available_hint, Toast.LENGTH_SHORT, true).show()
            }
            //主题
            R.string.ic_theme->{
                toggleBottomPanel(Runnable {
                    RouterActivity?.loadThemeFragment()
                })

            }
            //下载
            R.string.ic_download->{
                toggleBottomPanel(Runnable {
                    RouterActivity?.loadDownloadFragment()
                })

            }
            //设置
            R.string.ic_setting->{
                toggleBottomPanel(Runnable {
                    RouterActivity?.loadSettingFragment()
                })

            }
            //退出
            R.string.ic_quit->{
                RouterActivity?.quit()
            }
        }
    }



    private fun toggleBottomPanel(runnable: Runnable= Runnable { }){
        //如果是隐藏的，那么显示
        if (overLayerForBottomMenuPanel.isHidden()){
            showBottomPanel(runnable)
            backHandlers.add(0,bottomPanelBackHandler!!)
        }else{
            hideBottomPanel(runnable)
            backHandlers.remove(bottomPanelBackHandler)
        }
    }
    private fun showBottomPanel(runnable: Runnable= Runnable {  }) {
        overLayerForBottomMenuPanel.show()
        bottomMenuPanel.apply {
            animate().translationY(0f).setDuration(FAST_ANIMATION).setInterpolator(AccelerateInterpolator()).withEndAction(runnable).start()
        }
    }
    private fun hideBottomPanel(runnable: Runnable= Runnable { }) {
        overLayerForBottomMenuPanel.hide()
        bottomMenuPanel.apply {
            animate().translationY(this.height.toFloat()+DensityUtil.dip2px(requireContext(),16f)).setDuration(
                FAST_ANIMATION).withEndAction(runnable).start()
        }
    }
    //初始化ViewPager
    private fun initViewPager(view: View, savedInstanceState: Bundle?) {
        viewPager=view.findViewById(R.id.viewpager)
    }
    //初始化数据
    override fun initData(savedInstanceState: Bundle?) {

    }

    //处理返回值
    override fun onBackPressed(): Boolean {
        for (backHandler in backHandlers){
            if (backHandler.onBackPressed()){
                return true
            }
        }
        return false
    }

    companion object {
        val Tag = "HomeFragment"
        fun newInstance(sessionId:String):HomeFragment = HomeFragment().apply {
            arguments = Bundle().apply {
                putString(BrowserFragment.SESSION_ID, sessionId)
            }
        }
    }

}