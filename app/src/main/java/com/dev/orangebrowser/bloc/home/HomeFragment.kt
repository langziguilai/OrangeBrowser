package com.dev.orangebrowser.bloc.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.dev.base.BaseLazyFragment
import com.dev.base.extension.*
import com.dev.base.support.BackHandler
import com.dev.base.support.LifecycleAwareFeature
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.data.model.ActionItem
import com.dev.orangebrowser.data.model.Site
import com.dev.orangebrowser.databinding.FragmentHomeBinding
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
    lateinit var activityViewModel:MainViewModel
    @State
    lateinit var favorSites: ArrayList<Site>

    //
    val backHandlers =LinkedList<BackHandler>()

    //
    lateinit var binding: FragmentHomeBinding
    //
    val sessionId: String
        get() = arguments?.getString(BrowserFragment.SESSION_ID) ?: ""

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

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_home
    }

    //使用DataBinding
    override fun useDataBinding(): Boolean {
        return true
    }

    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        val bottomBarHelper=BottomBarHelper(binding,this,savedInstanceState)
        val topBarHelper=TopBarHelper(binding,this,savedInstanceState,bottomBarHelper)
        bottomBarHelper.initView()
        topBarHelper.initView()
        initViewPager(savedInstanceState)

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
        val Tag = "HomeFragment"
        fun newInstance(sessionId:String):HomeFragment = HomeFragment().apply {
            arguments = Bundle().apply {
                putString(BrowserFragment.SESSION_ID, sessionId)
            }
        }
    }

}

