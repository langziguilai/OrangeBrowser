package com.dev.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evernote.android.state.StateSaver

abstract class LogLifeCycleEventFragment: CoroutineScopeFragment() {
    private  var onCreateTime:Long=0
    private  var onResumeTime:Long=0
    override fun onStart() {
        Log.d(this.javaClass.simpleName+this.hashCode(), "onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d(this.javaClass.simpleName+this.hashCode(), "onResume")
        super.onResume()
        onResumeTime=System.currentTimeMillis()
        Log.d(this.javaClass.simpleName+this.hashCode(), "from onCreate to onResume take ${onResumeTime-onCreateTime} ms")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(this.javaClass.simpleName+this.hashCode(), "onCreate")
        super.onCreate(savedInstanceState)
        onCreateTime=System.currentTimeMillis()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(this.javaClass.simpleName+this.hashCode(), "backstackentrycount " + fragmentManager!!.backStackEntryCount)
        Log.d(this.javaClass.simpleName+this.hashCode(), "getFragments size " + fragmentManager!!.fragments.size)
        Log.d(this.javaClass.simpleName+this.hashCode(), "onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onPause() {
        Log.d(this.javaClass.simpleName+this.hashCode(), "onPause")
        super.onPause()
    }

    override fun onDestroy() {
        Log.d(this.javaClass.simpleName+this.hashCode(), "onDestroy")
        super.onDestroy()
    }

    override fun onDestroyView() {
        Log.d(this.javaClass.simpleName+this.hashCode(), "onDestroyView")
        super.onDestroyView()
    }

    override fun onAttach(context: Context) {
        Log.d(this.javaClass.simpleName+this.hashCode(), "onAttach")
        super.onAttach(context)
    }

    override fun onDetach() {
        Log.d(this.javaClass.simpleName+this.hashCode(), "onDetach")
        super.onDetach()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(this.javaClass.simpleName+this.hashCode(), "onActivityCreated")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(this.javaClass.simpleName+this.hashCode(), "onActivityResult")
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStop() {
        Log.d(this.javaClass.simpleName+this.hashCode(), "onStop")
        super.onStop()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(this.javaClass.simpleName+this.hashCode(), "onSaveInstanceState")
    }
}
