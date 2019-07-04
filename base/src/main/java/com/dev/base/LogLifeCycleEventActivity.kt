package com.dev.base

import android.os.Bundle
import android.util.Log
import com.dev.util.Keep

@Keep
open class LogLifeCycleEventActivity : CoroutineScopeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(this.javaClass.simpleName, "onCreate")
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
}
