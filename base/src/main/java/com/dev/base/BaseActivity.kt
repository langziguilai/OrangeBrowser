package com.dev.base

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject


abstract class BaseActivity : AdaptUiBaseActivity() {
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        initView(savedInstanceState)
        initData(savedInstanceState)
    }
    abstract fun getLayoutResId(): Int

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun initData(savedInstanceState: Bundle?)
}