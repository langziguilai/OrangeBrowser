package com.dev.base

import android.view.LayoutInflater
import android.widget.FrameLayout
import com.dev.view.notchtools.NotchTools


abstract class BaseNotchActivity : BaseActivity() {
    /**
     * 刘海容器
     */
    lateinit var mNotchContainer: FrameLayout
    /**
     * 主内容区
     */
    lateinit var mContentContainer: FrameLayout

    override fun setContentView(layoutResID: Int) {
        super.setContentView(R.layout.activity_notch_base)
        mNotchContainer = findViewById(R.id.notch_container)
        mNotchContainer.tag = NotchTools.NOTCH_CONTAINER
        mContentContainer = findViewById(R.id.content_container)
        onBindContentContainer(layoutResID)
    }

    private fun onBindContentContainer(layoutResID: Int) {
        LayoutInflater.from(this).inflate(layoutResID, mContentContainer, true)
    }
}