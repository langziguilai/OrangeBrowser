package com.dev.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.dev.util.DensityUtil
import javax.inject.Inject


abstract class BaseFragment : LogLifeCycleEventFragment() {
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    //获取layoutId创建View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d(this.javaClass.simpleName+this.hashCode(), "onCreateView")
        return inflater.inflate(getLayoutResId(), container, false)
    }

    abstract fun getLayoutResId(): Int

    //在view被创建好之后初始化view
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(this.javaClass.simpleName+this.hashCode(), "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        if (!useDataBinding()){
            initView(view, savedInstanceState)
        }
    }

    open fun initView(view: View, savedInstanceState: Bundle?){}
    open fun initViewWithDataBinding(savedInstanceState: Bundle?){}
    //使用DataBinding，延后到Activity Attach时初始化View
    open fun useDataBinding():Boolean{
        return false
    }
    //在activity被创建好之后初始化数据
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //webview会重置density所以，我们需要重置
        DensityUtil.resetDensity(requireActivity(),requireActivity().application)
        if (useDataBinding()){
            initViewWithDataBinding(savedInstanceState)
        }
        super.onActivityCreated(savedInstanceState)
        initData(savedInstanceState)
    }


    abstract fun initData(savedInstanceState: Bundle?)
}