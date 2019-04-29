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
import com.dev.util.FileUtil
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext

class ThumbnailIntergration(
    var context: Context,
    var view: View,
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
        if (!context.isOSOnLowMemory()) {
            sessionManager.findSessionById(sessionId)?.apply {
                val session = this
                view.capture()?.apply {
                    val bitmap = this
                    session.tmpThumbnail = SoftReference(bitmap)
                    launch(Dispatchers.IO) {
                        try {
                            val fileName = "$sessionId.webp"
                            val file = File(FileUtil.getOrCreateDir(context, Session.THUMBNAIL_DIR), fileName)
                            bitmap.compress(Bitmap.CompressFormat.WEBP, 80, FileOutputStream(file))
                            session.thumbnailPath = File.separator + Session.THUMBNAIL_DIR + File.separator + fileName
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
}