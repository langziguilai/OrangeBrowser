package com.dev.orangebrowser.bloc.search

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.dev.base.BaseFragment
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.extension.appComponent

class SearchFragment : BaseFragment() {

    val originalSessionId: String
        get() = arguments?.getString(BrowserFragment.SESSION_ID) ?: ""


    lateinit var viewModel: SearchViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel=ViewModelProviders.of(this,factory).get(SearchViewModel::class.java)
    }
    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_search
    }
    override fun initView(view: View,savedInstanceState: Bundle?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initData(savedInstanceState: Bundle?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        // TODO: Use the ViewModel
    }
    companion object {
        val Tag="SearchFragment"
        fun newInstance(sessionId:String) = SearchFragment().apply {
            arguments = Bundle().apply {
                putString(BrowserFragment.SESSION_ID, sessionId)
            }
        }
    }
}
