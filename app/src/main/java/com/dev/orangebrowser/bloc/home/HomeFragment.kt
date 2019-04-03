package com.dev.orangebrowser.bloc.home

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.BaseLazyFragment
import com.dev.base.support.BackHandler
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.data.model.NewsCategory
import com.dev.orangebrowser.data.model.Site
import com.dev.orangebrowser.extension.appComponent
import com.evernote.android.state.State
import javax.inject.Inject

class HomeFragment : BaseLazyFragment(), BackHandler {

    //处理返回值
    override fun onBackPressed(): Boolean {
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
//    @Inject
//    lateinit var sessionManager: SessionManager
    lateinit var viewModel: HomeViewModel
    @State
    lateinit var favorSites: ArrayList<Site>
    @State
    var recommendSelectPageIndex: Int = 0
    @State
    lateinit var mNewsCategoryList: ArrayList<NewsCategory>

    //view
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)
    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.home_fragment
    }

    //初始化view
    override fun initView(view: View, savedInstanceState: Bundle?) {

    }


    //初始化数据
    override fun initData(savedInstanceState: Bundle?) {
       // viewModel.loadCategoryList()
    }


}