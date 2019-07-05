package com.dev.orangebrowser.bloc.browser.integration

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import com.dev.base.extension.capture
import com.dev.base.extension.isOSOnLowMemory
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.util.FileUtil
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.SoftReference
import kotlin.coroutines.CoroutineContext

class ThumbnailIntegration(
    var context: Context,
    var view: View,
    var binding: FragmentBrowserBinding,
    var sessionId: String,
    var sessionManager: SessionManager
) :
    LifecycleAwareFeature,
    CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun start() {

    }

    override fun stop() {
        val width=binding.webViewContainer.width
        val height=binding.fragmentContainer.height - binding.topBar.height-binding.bottomBar.height
        if (!context.isOSOnLowMemory()) {
            sessionManager.findSessionById(sessionId)?.apply {
                val session = this
                view.capture()?.apply {
                    //val bitmap = this
                    val bitmap=if(height<this.height){
                        Bitmap.createBitmap(this,0,0,width,height)
                    }else{
                        this
                    }
                    session.webPageThumbnailRef = SoftReference(bitmap)
                    val fileName = "$sessionId.webp"
                    val file = File(FileUtil.getOrCreateDir(context, Session.THUMBNAIL_DIR), fileName)
                    session.webPageThumbnailPath = file.absolutePath
                    launch(Dispatchers.IO) {
                        try {
                            bitmap.compress(Bitmap.CompressFormat.WEBP, 80, FileOutputStream(file))
                            Log.d("ThumbnailIntegration","save thumbnail success")
                        } catch (e: Exception) {
                            Log.e("ThumbnailIntegration","save thumbnail fail")
                        } finally {
                            coroutineContext.cancelChildren()
                        }
                    }
                }
            }
        }

    }
}