package com.dev.orangebrowser.bloc.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.BaseLazyFragment
import com.dev.base.extension.hide
import com.dev.base.extension.show
import com.dev.base.extension.showToast
import com.dev.base.support.BackHandler
import com.dev.base.support.ViewBoundFeatureWrapper
import com.dev.base.support.isUrl
import com.dev.browser.feature.session.SessionUseCases
import com.dev.browser.feature.tabs.TabsUseCases
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.home.helper.BottomBarHelper
import com.dev.orangebrowser.bloc.home.helper.TopBarHelper
import com.dev.orangebrowser.bloc.home.intergration.ContentBlinkFixIntegration
import com.dev.orangebrowser.bloc.home.intergration.ThumbnailIntergration
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.config.ResultCode
import com.dev.orangebrowser.data.model.CloseItem
import com.dev.orangebrowser.data.model.MainPageSite
import com.dev.orangebrowser.data.model.Site
import com.dev.orangebrowser.databinding.FragmentHomeBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.util.DensityUtil
import com.dev.view.StatusBarUtil
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.adapter.base.BaseItemDraggableAdapter
import com.dev.view.recyclerview.adapter.base.callback.ItemDragAndSwipeCallback
import com.dev.view.recyclerview.adapter.base.listener.OnItemDragListener
import com.evernote.android.state.State
import com.noober.background.drawable.DrawableCreator
import java.util.*
import javax.inject.Inject




class HomeFragment : BaseLazyFragment(), BackHandler {
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var tabsUserCase:TabsUseCases
    @Inject
    lateinit var sessionUseCases: SessionUseCases
    //dataList
    lateinit var viewModel: HomeViewModel
    lateinit var activityViewModel:MainViewModel
    @State
    lateinit var favorSites: ArrayList<Site>

    val thumbnailIntergration= ViewBoundFeatureWrapper<ThumbnailIntergration>()

    //
    val backHandlers =LinkedList<BackHandler>()

    //
    lateinit var binding: FragmentHomeBinding
    //
    var sessionId: String = NO_SESSION_ID


    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding= FragmentHomeBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
        binding.lifecycleOwner=this
        return binding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel=ViewModelProviders.of(activity!!,factory).get(MainViewModel::class.java)
        binding.activityViewModel=activityViewModel
        super.onActivityCreated(savedInstanceState)
    }
    private var lastUrl:String?=null
    //找到或者新建一个Session
    private fun initSession(){
        sessionId=arguments?.getString(BrowserFragment.SESSION_ID) ?: NO_SESSION_ID
        //根据session ID查询，如果不存在，那么就新建,但是不加载url
        val session=sessionManager.findSessionById(sessionId)
        if (session==null){
            tabsUserCase.addTabWithoutUrl.invoke(selectTab = true)
            sessionId=sessionManager.selectedSession!!.id
        }else{
            lastUrl=session.url
            //选中session
            sessionManager.select(session)
            //更新
            session.screenNumber=Session.HOME_SCREEN
        }

    }
    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_home
    }

    //使用DataBinding
    override fun useDataBinding(): Boolean {
        return true
    }

    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        initSession()
        StatusBarUtil.setLightIcon(requireActivity())
        val bottomBarHelper= BottomBarHelper(binding, this, savedInstanceState)
            TopBarHelper(binding, this, savedInstanceState, bottomBarHelper)
        initRecyclerView(savedInstanceState)
        thumbnailIntergration.set(
            feature = ThumbnailIntergration(context=requireContext(),view=binding.recyclerView,sessionId = sessionId,sessionManager = sessionManager),
            owner = this,
            view = binding.root
        )
        sessionManager.findSessionById(sessionId)?.apply {
            ContentBlinkFixIntegration(binding=binding,fragment = this@HomeFragment,session = this)
        }
    }
    lateinit var adapter:BaseItemDraggableAdapter<CloseItem<MainPageSite>,CustomBaseViewHolder>
    var data:LinkedList<CloseItem<MainPageSite>> = LinkedList()
    var moveFrom:Int=0
    lateinit var mLayoutManager: GridLayoutManager
    //初始化ViewPager
    private fun initRecyclerView(savedInstanceState: Bundle?) {
        mLayoutManager=GridLayoutManager(requireContext(),3, RecyclerView.VERTICAL,false)
        binding.recyclerView.layoutManager=mLayoutManager
        adapter=object: BaseItemDraggableAdapter<CloseItem<MainPageSite>, CustomBaseViewHolder>(R.layout.item_site_favorite,data){
            override fun convert(helper: CustomBaseViewHolder, item: CloseItem<MainPageSite>) {
                helper.addOnClickListener(R.id.delete_icon)
                if (item.showCloseItem){
                    helper.itemView.findViewById<View>(R.id.delete_icon)?.show()
                }else{
                    helper.itemView.findViewById<View>(R.id.delete_icon)?.hide()
                }
                helper.setTextToAppCompatTextView(R.id.title,item.data.name ?: "")
                if (item.data.icon!=null && item.data.icon!!.isNotBlank()){
                    helper.itemView.findViewById<View>(R.id.text_image)?.hide()
                    helper.itemView.findViewById<View>(R.id.icon)?.show()
                    helper.loadImage(R.id.icon,item.data.icon!!)
                }else{
                    helper.itemView.findViewById<View>(R.id.text_image)?.show()
                    helper.itemView.findViewById<View>(R.id.icon)?.hide()
                    helper.setText(R.id.page_title,item.data.textIcon)
                    helper.setTextColor(R.id.page_title, item.data.textColor ?: 0x000000)
                    helper.setText(R.id.page_subtitle,item.data.subTextIcon)
                    helper.setTextColor(R.id.page_subtitle, item.data.textColor ?: 0x000000)
                    helper.setBackgroundColor(R.id.text_image,item.data.backgroundColor ?: activityViewModel.theme.value!!.colorPrimary)
                    val drawable = DrawableCreator.Builder()
                        .setSolidColor(item.data.backgroundColor ?: activityViewModel.theme.value!!.colorPrimary)
                        .setCornersRadius(DensityUtil.dip2px(requireContext(), 2f).toFloat())
                        .setStrokeWidth(1f)
                        .setStrokeColor(0xdfdfdf).build()
                    helper.itemView.findViewById<View>(R.id.text_image)?.background=drawable
                }
            }
        }
        adapter.setOnItemClickListener { _, _, position ->
            if (showingDeleteIcon){
                hideDeleteIcon()
            }else{
                val item=data[position].data
                if (item.url!=null){
                    item.url?.apply {
                        if (this.isUrl()){
                            sessionUseCases.loadUrl.invoke(this)
                            RouterActivity?.loadBrowserFragment(sessionId,lastUrl=lastUrl)
                        }
                    }
                }
            }

        }
        adapter.setOnItemChildClickListener { _, view, position ->
            //删除
            if (view.id==R.id.delete_icon){
                deleteSite(position)
            }
        }
        val itemDragAndSwipeCallback = ItemDragAndSwipeCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(itemDragAndSwipeCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
        adapter.enableDragItem(itemTouchHelper)
        adapter.setOnItemDragListener(object:OnItemDragListener{
            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                moveFrom=pos
                showDeleteIcon()
            }
            override fun onItemDragMoving(
                source: RecyclerView.ViewHolder,
                from: Int,
                target: RecyclerView.ViewHolder,
                to: Int
            ) {
            }
            override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                moveSiteRank(moveFrom,pos)
            }
        })
        binding.recyclerView.adapter=adapter
        viewModel.siteListLiveData.observe(this, Observer<List<MainPageSite>> { newData->
            data.clear()
            data.addAll(newData.map { CloseItem(showCloseItem = false,data = it) })
            adapter.notifyDataSetChanged()
        })
        viewModel.errorCodeLiveData.observe(this, Observer<Int> {
            when(it){
                ResultCode.DELETE_FAIL->{requireContext().showToast(getString(R.string.delete_fail))}
                ResultCode.MOVE_FAIL->{requireContext().showToast(getString(R.string.move_site_fail))}
                ResultCode.LOAD_FAIL->{requireContext().showToast(getString(R.string.load_fail))}
            }
        })
    }

    //初始化数据
    override fun initData(savedInstanceState: Bundle?) {
         viewModel.loadMainPageSites()
    }
    //移动位置
    private fun moveSiteRank(from:Int,to:Int){
        if (from==to) return
        viewModel.updateSites(data)
    }
    //删除网站
    private fun deleteSite(position:Int){
        val deletedSite= data[position].data
        data.removeAt(position)
        adapter.notifyItemRemoved(position)
        viewModel.delete(deletedSite,data)
    }
    var showingDeleteIcon=false
    private fun showDeleteIcon(){
        showingDeleteIcon=true
        data.forEach {
            it.showCloseItem=true
        }
        val firstVisible=mLayoutManager.findFirstVisibleItemPosition()
        val lastVisible=mLayoutManager.findLastVisibleItemPosition()
        for (i in firstVisible..lastVisible){
            adapter.getViewByPosition(binding.recyclerView,i,R.id.delete_icon)?.apply {
                this.show()
            }
        }
    }
    private fun hideDeleteIcon(){
        if (!showingDeleteIcon) return
        showingDeleteIcon=false
        data.forEach {
            it.showCloseItem=false
        }
        val firstVisible=mLayoutManager.findFirstVisibleItemPosition()
        val lastVisible=mLayoutManager.findLastVisibleItemPosition()
        for (i in firstVisible..lastVisible){
            adapter.getViewByPosition(binding.recyclerView,i,R.id.delete_icon)?.apply {
                this.hide()
            }
        }
    }
    //处理返回值
    override fun onBackPressed(): Boolean {
        for (backHandler in backHandlers){
            if (backHandler.onBackPressed()){
                return true
            }
        }
        //如果处于网站编辑状态，那么返回就隐藏按钮
        if (data.firstOrNull()!=null){
            if (data.first.showCloseItem){
                hideDeleteIcon()
                return true
            }
        }
        return false
    }
    //暂停的时候，保存fragment状态,以便恢复
    override fun onPause() {
        sessionManager.findSessionById(sessionId)?.apply {
            val session=this
            fragmentManager?.saveFragmentInstanceState(this@HomeFragment)?.apply {
                session.homeScreenState=this
            }
        }
        super.onPause()
    }
    companion object {
        const val Tag = "HomeFragment"
        const val NO_SESSION_ID="NO_SESSION_ID"
        fun newInstance(sessionId:String):HomeFragment = HomeFragment().apply {
            arguments = Bundle().apply {
                putString(BrowserFragment.SESSION_ID, sessionId)
            }
        }
    }
}

