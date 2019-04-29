package com.dev.orangebrowser.bloc.browser.integration.helper

import android.graphics.Bitmap
import android.util.Log
import com.dev.base.extension.capture
import com.dev.browser.session.Session
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appData
import com.dev.util.FileUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.SoftReference

//先截图，再跳转
fun redirect(binding:FragmentBrowserBinding,session:Session, runnable: Runnable){
    session.visionMode = Session.NORMAL_SCREEN_MODE
    session.enterFullScreenMode=false
    binding.fragmentContainer.requestLayout()
    //等待一段事件后跳转
    binding.fragmentContainer.postDelayed({
        runnable.run()
    }, 50)
}