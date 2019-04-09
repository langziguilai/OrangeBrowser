package com.dev.orangebrowser.bloc.tabs

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.dev.base.BaseFragment
import com.dev.orangebrowser.R
import com.dev.orangebrowser.extension.appComponent

class TabFragment : BaseFragment() {

    companion object {
        const val SESSION_ID="SESSION_ID"
        fun newInstance(sessionId:String)=TabFragment().apply {
            arguments = Bundle().apply {
                putString(TabFragment.SESSION_ID, sessionId)
            }
        }
    }

    lateinit var viewModel: TabViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel=ViewModelProviders.of(this,factory).get(TabViewModel::class.java)
    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_tab
    }
    override fun initView(view: View,savedInstanceState: Bundle?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initData(savedInstanceState: Bundle?) {
       //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
