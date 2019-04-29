package com.dev.orangebrowser.bloc.browser.integration.helper

import android.graphics.Bitmap
import android.util.Log
import com.dev.base.extension.capture
import com.dev.browser.session.Session
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.util.FileUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.SoftReference

//先截图，再跳转
fun redirect(binding:FragmentBrowserBinding, session:Session, fragment:BrowserFragment, scope: CoroutineScope, runnable: Runnable){
    session.visionMode = Session.NORMAL_SCREEN_MODE
    binding.fragmentContainer.requestLayout()
    //capture thumbnail
    binding.webViewContainer.capture()?.apply {
        val bitmap = this
        session.tmpThumbnail = SoftReference(bitmap)
        scope.launch(Dispatchers.IO) {
            try {
                //TODO:压缩
                val fileName = "${session.id}.webp"
                val file =
                    File(FileUtil.getOrCreateDir(fragment.requireContext(), Session.THUMBNAIL_DIR), fileName)
                bitmap.compress(Bitmap.CompressFormat.WEBP, 80, FileOutputStream(file))
                session.thumbnailPath = File.separator + Session.THUMBNAIL_DIR + File.separator + fileName

            } catch (e: Exception) {
                Log.e("save thumbnail fail", e.message)
            } finally {
                scope.coroutineContext.cancelChildren()
            }
        }
    }
    //等待一段事件后跳转
    binding.fragmentContainer.postDelayed({
        runnable.run()
    }, 50)
}