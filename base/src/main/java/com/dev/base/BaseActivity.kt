package com.dev.base

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject


abstract class BaseActivity : AdaptUiBaseActivity() {
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //如果需要透明的Activity
        if (isStatusBarTransparent()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                findViewById<View>(android.R.id.content).systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        }
        setContentView(getLayoutResId())
        initView(savedInstanceState)
        initData(savedInstanceState)
    }
    abstract fun getLayoutResId(): Int

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun initData(savedInstanceState: Bundle?)

    //是否Activity的StatusBar透明
    open fun isStatusBarTransparent():Boolean{
        return false
    }
}