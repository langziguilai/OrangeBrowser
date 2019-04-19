package com.dev.orangebrowser.bloc.tabs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.dev.base.BaseFragment
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.databinding.FragmentSearchBinding
import com.dev.orangebrowser.databinding.FragmentTabBinding
import com.dev.orangebrowser.extension.appComponent

class TabFragment : BaseFragment() {
    companion object {
        const val Tag="TabFragment"
        fun newInstance(sessionId:String)=TabFragment().apply {
            arguments = Bundle().apply {
                putString(BrowserFragment.SESSION_ID, sessionId)
            }
        }
    }
    val originalSessionId: String
        get() = arguments?.getString(BrowserFragment.SESSION_ID) ?: ""
    lateinit var viewModel: TabViewModel
    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentTabBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel=ViewModelProviders.of(this,factory).get(TabViewModel::class.java)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(activity!!, factory).get(MainViewModel::class.java)
        binding.activityViewModel = activityViewModel
        super.onActivityCreated(savedInstanceState)
    }
    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_tab
    }

    override fun useDataBinding(): Boolean {
        return true
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTabBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
        return binding.root
    }
    override fun initView(view: View,savedInstanceState: Bundle?) {

    }

    override fun initData(savedInstanceState: Bundle?) {
       //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
