package com.dev.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject


abstract class BaseLazyFragment : LogLifeCycleEventFragment() {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    //不保存这两个状态是因为：我们不知道数据初始化是不是成功了，如果不成功，那么，我们必须要重新初始化，这个要交给具体的Fragment处理，我们不能拦截，不让子类初始化数据
    //@State
    var hasOnActivityCreatedTriggered: Boolean = false
    //@State
    var hasDataInitialized:Boolean=false  //数据时候已经初始化

    //获取layoutId创建View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d(this.javaClass.simpleName+this.hashCode(), "onCreateView")
        return inflater.inflate(getLayoutResId(), container, false)
    }

    abstract fun getLayoutResId(): Int

     var hasViewInitialized: Boolean=false

    //在view被创建好之后初始化view
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!useDataBinding()){
            initView(view, savedInstanceState)
            hasViewInitialized=true
            Log.d(this.javaClass.simpleName+this.hashCode(), "initView")
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
            hasViewInitialized=true
            Log.d(this.javaClass.simpleName+this.hashCode(), "initView")
        }
        super.onActivityCreated(savedInstanceState)
        if (!hasOnActivityCreatedTriggered){
            hasOnActivityCreatedTriggered=true
            initPrepare(savedInstanceState)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        Log.d(this.javaClass.simpleName+this.hashCode(), "setUserVisibleHint is $isVisibleToUser")
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && !hasDataInitialized) {
                initPrepare(null)
        }
    }

    private fun initPrepare(savedInstanceState: Bundle?) {
        //如果OnActivityCreated()方法已经触发，并且可见，并且数据尚未被初始化，那么执行初始化函数，
        // 即：初始化函数必须在首次可见并且OnActivityCreated()调用之后执行
        if (userVisibleHint && hasOnActivityCreatedTriggered && !hasDataInitialized) {
            //如果尚未初始化数据
            initData(savedInstanceState)
            Log.d(this.javaClass.simpleName,"initData")
            hasDataInitialized=true
        }
    }

    /*
    * initData仅仅执行一次
    * */
    abstract fun initData(savedInstanceState: Bundle?)
}