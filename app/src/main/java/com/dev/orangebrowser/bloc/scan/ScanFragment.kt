package com.dev.orangebrowser.bloc.scan

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.dev.base.BaseFragment
import com.dev.orangebrowser.R
import com.dev.orangebrowser.extension.appComponent

class ScanFragment : BaseFragment() {


    companion object {
        val Tag="ScanFragment"
        fun newInstance() = ScanFragment()
    }

    lateinit var viewModel: ScanViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel=ViewModelProviders.of(this,factory).get(ScanViewModel::class.java)
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
}