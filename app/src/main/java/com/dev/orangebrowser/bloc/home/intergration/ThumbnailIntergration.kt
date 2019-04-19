package com.dev.orangebrowser.bloc.home.intergration

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import com.dev.base.extension.capture
import com.dev.base.extension.isOSOnLowMemory
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.feature.session.ThumbnailFeature
import com.dev.browser.session.SessionManager
import com.dev.util.CommonUtil
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class ThumbnailIntergration(var context: Context, var view: View, var sessionId: String, var sessionManager: SessionManager) :
    LifecycleAwareFeature,
    CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun start() {

    }

    override fun stop() {
        if (!context.isOSOnLowMemory()){
            view.capture()?.apply {
                val bitmap = this
                launch(Dispatchers.IO) {
                    try {
                        val fileName = "$sessionId.webp"
                        val file = File(CommonUtil.getOrCreateDir(ThumbnailFeature.THUMBNAIL_DIR), fileName)
                        bitmap.compress(Bitmap.CompressFormat.WEBP, 80, FileOutputStream(file))
                        sessionManager.findSessionById(sessionId)?.apply {
                            this.thumbnailPath = File.separator + ThumbnailFeature.THUMBNAIL_DIR + File.separator + fileName
                        }
                    } catch (e: Exception) {
                        Log.e("save thumbnail fail", e.message)
                    } finally {
                        coroutineContext.cancelChildren()
                    }
                }
            }
        }

    }
}