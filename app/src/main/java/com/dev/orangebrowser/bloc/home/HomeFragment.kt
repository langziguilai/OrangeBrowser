package com.dev.orangebrowser.bloc.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.dev.base.BaseLazyFragment
import com.dev.base.support.BackHandler
import com.dev.browser.feature.tabs.TabsUseCases
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.home.helper.BottomBarHelper
import com.dev.orangebrowser.bloc.home.helper.TopBarHelper
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.data.model.Site
import com.dev.orangebrowser.databinding.FragmentHomeBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.view.StatusBarUtil
import com.evernote.android.state.State
import java.util.*
import javax.inject.Inject

class HomeFragment : BaseLazyFragment(), BackHandler {
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var tabsUserCase:TabsUseCases
    //data
    lateinit var viewModel: HomeViewModel
    lateinit var activityViewModel:MainViewModel
    @State
    lateinit var favorSites: ArrayList<Site>

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
        return binding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel=ViewModelProviders.of(activity!!,factory).get(MainViewModel::class.java)
        binding.activityViewModel=activityViewModel
        super.onActivityCreated(savedInstanceState)
    }
    //找到或者新建一个Session
    private fun initSession(){
        sessionId=arguments?.getString(BrowserFragment.SESSION_ID) ?: NO_SESSION_ID
        //根据session ID查询，如果不存在，那么就新建,但是不加载url
        val session=sessionManager.findSessionById(sessionId)
        if (session==null){
            tabsUserCase.addTabWithoutUrl.invoke(selectTab = true)
            sessionId=sessionManager.selectedSession!!.id
            return
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
        val topBarHelper=
            TopBarHelper(binding, this, savedInstanceState, bottomBarHelper)
        initViewPager(savedInstanceState)
        binding.search.setOnClickListener {
            RouterActivity?.loadSearchFragment(sessionId)
        }
    }

    //初始化ViewPager
    private fun initViewPager(savedInstanceState: Bundle?) {
        //TODO:init ViewPager
        binding.viewpager
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
        const val Tag = "HomeFragment"
        const val NO_SESSION_ID="NO_SESSION_ID"
        fun newInstance(sessionId:String):HomeFragment = HomeFragment().apply {
            arguments = Bundle().apply {
                putString(BrowserFragment.SESSION_ID, sessionId)
            }
        }
    }

}

