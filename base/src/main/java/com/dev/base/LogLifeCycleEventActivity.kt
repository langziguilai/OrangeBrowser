package com.dev.base

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import com.dev.util.DensityUtil
import com.dev.util.Keep

@Keep
open class LogLifeCycleEventActivity : CoroutineScopeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(this.toString(), "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        Log.d(this.javaClass.simpleName, "onStart")
        super.onStart()
    }

    override fun onStop() {
        Log.d(this.javaClass.simpleName, "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(this.javaClass.simpleName, "onDestroy")
        super.onDestroy()
    }

    override fun onRestart() {
        Log.d(this.javaClass.simpleName, "onRestart")
        super.onRestart()
    }

    override fun onPause() {
        Log.d(this.javaClass.simpleName, "onPause")
        super.onPause()
    }

    override fun onResume() {
        Log.d(this.javaClass.simpleName, "onResume")
        super.onResume()
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if(newConfig.orientation== Configuration.ORIENTATION_LANDSCAPE){

        }else{
            DensityUtil.resetDensity()
        }
    }
}
