package com.dev.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.dev.util.Keep

@Keep
abstract class BaseTransparentFullScreenDialogFragment : DialogFragment() {
    lateinit var rootView:View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.Dialog_FullScreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(getLayoutId(), null)
        rootView=view
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewAndData(rootView=rootView,bundle = savedInstanceState)
    }
    abstract fun getLayoutId():Int
    abstract fun initViewAndData(rootView:View,bundle:Bundle?)
    override fun onStart() {
        super.onStart()
        //TODO:这里暂时设置固定的宽和高，此时如果有键盘弹出
        val viewParams=rootView.layoutParams
        viewParams.width=requireContext().resources.displayMetrics.widthPixels
        viewParams.height=requireContext().resources.displayMetrics.heightPixels
        rootView.layoutParams=viewParams
    }
}