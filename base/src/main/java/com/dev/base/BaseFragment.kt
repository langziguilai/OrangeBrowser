package com.dev.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
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
        initView(view, savedInstanceState)
    }

    abstract fun initView(view: View, savedInstanceState: Bundle?)

    //在activity被创建好之后初始化数据
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData(savedInstanceState)
    }


    abstract fun initData(savedInstanceState: Bundle?)
}