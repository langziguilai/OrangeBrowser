package com.dev.base

import android.app.Application
import android.util.Log

import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary

abstract class BaseApplication : Application() {


    override fun onCreate() {
        val t1=System.currentTimeMillis()
        super.onCreate()
        this.injectMembers()
        //memory leak detect
        this.initializeLeakDetection()
        //observe with stetho
        this.initializeStetho()
        val t2=System.currentTimeMillis()
        Log.d(this.javaClass.simpleName,"Application OnCreate take ${t2-t1} ms")
    }

    abstract fun injectMembers()
    private fun initializeStetho(){
        if(BuildConfig.DEBUG){
            Stetho.initializeWithDefaults(this)
        }
    }
    private fun initializeLeakDetection() {
        if (BuildConfig.DEBUG) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return
            }
            LeakCanary.install(this)
        }
    }
}
