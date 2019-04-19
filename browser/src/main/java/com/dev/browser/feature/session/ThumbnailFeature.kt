/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.feature.session

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import androidx.core.os.EnvironmentCompat
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.concept.EngineView
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.util.CommonUtil
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.CoroutineContext

/**
 * Feature implementation for automatically taking thumbnails of sites.
 * The feature will take a screenshot when the page finishes loading,
 * and will add it to the [Session.themeThumbnail] property.
 *
 * If the OS is under low memory conditions, the screenshot will be not taken.
 * Ideally, this should be used in conjunction with [SessionManager.onLowMemory] to allow
 * free up some [Session.themeThumbnail] from memory.
 */
class ThumbnailFeature(
    private val context:Context,
    private val sessionId:String,
    private val sessionManager: SessionManager,
    private val engineView: EngineView
) : LifecycleAwareFeature, CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    /**
     * Starts observing the selected session to listen for when a session finish loading.
     */
    override fun start() {
    }

    /**
     * Stops observing the selected session.
     */
    override fun stop() {
        engineView.captureThumbnail {
            launch (Dispatchers.IO){
                it?.apply {
                    try {
                        val fileName="$sessionId.webp"
                        val file=File(CommonUtil.getOrCreateDir(THUMBNAIL_DIR),fileName)
                        this.compress(Bitmap.CompressFormat.WEBP,80,FileOutputStream(file))
                        sessionManager.findSessionById(sessionId)?.apply {
                            this.thumbnailPath= File.separator+THUMBNAIL_DIR+File.separator+fileName
                        }
                    }catch (e:Exception){
                        Log.e("save thumbnail failed:",e.message)
                    }finally {
                        coroutineContext.cancelChildren()
                    }
                }
            }
        }
    }
    companion object{
        const val THUMBNAIL_DIR="thumbnail"
    }
}
