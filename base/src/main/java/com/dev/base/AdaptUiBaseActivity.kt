package com.dev.base

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import com.dev.util.DensityUtil

open class AdaptUiBaseActivity : CoroutineScopeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        DensityUtil.init(this,application)
        Log.d(this.javaClass.simpleName, "onCreate")
        super.onCreate(savedInstanceState)
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            DensityUtil.setDesignWidthByOrientation(false)
        } else if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            DensityUtil.setDesignWidthByOrientation(true)
        }
        DensityUtil.resetDensity()
    }

    //TODO：待检验如果跳转到其他程序，再跳转回来，据说会还原，又要重新设置一下
    override fun onResume() {
        super.onResume()
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            DensityUtil.setDesignWidthByOrientation(false)
        } else if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            DensityUtil.setDesignWidthByOrientation(true)
        }
        DensityUtil.resetDensity()
    }

    override fun onDestroy() {
        DensityUtil.clear()
        super.onDestroy()
    }
}
