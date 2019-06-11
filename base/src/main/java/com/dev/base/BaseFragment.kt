package com.dev.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.core.view.ViewCompat
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
    //默认不设置
    open fun getLayoutResId(): Int{
        return -1
    }

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
        if (useDataBinding()){
            initViewWithDataBinding(savedInstanceState)
        }
        super.onActivityCreated(savedInstanceState)
        initData(savedInstanceState)
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (nextAnim == R.anim.holder) {
            ViewCompat.setTranslationZ(view!!, 0f)
        } else {
            ViewCompat.setTranslationZ(view!!, 1f)
        }
        return super.onCreateAnimation(transit, enter, nextAnim)
    }
    abstract fun initData(savedInstanceState: Bundle?)
}