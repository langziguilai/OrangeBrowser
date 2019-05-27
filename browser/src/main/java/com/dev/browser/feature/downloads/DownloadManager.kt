/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.feature.downloads

import android.Manifest.permission.INTERNET
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE
import android.app.DownloadManager.EXTRA_DOWNLOAD_ID
import android.app.DownloadManager.Request
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.annotation.RequiresPermission
import com.dev.browser.R
import com.dev.browser.extension.isPermissionGranted
import com.dev.browser.session.Download
import com.dev.browser.support.DownloadUtils
import java.io.File

typealias OnDownloadCompleted = (Download, Long) -> Unit
typealias AndroidDownloadManager = android.app.DownloadManager

internal const val FILE_NOT_SUPPORTED = -1L

/**
 * Handles the interactions with the [AndroidDownloadManager].
 * @property onDownloadCompleted a callback to be notified when a download is completed.
 * @property applicationContext a reference to [Context] applicationContext.
 */
class DownloadManager(
    private val applicationContext: Context,
    var onDownloadCompleted: OnDownloadCompleted = { _, _ -> }
) {
    interface OnAutoInstallDownloadAppListener{
        fun onAutoInstallDownloadApp(download: Download)
    }
    private val queuedDownloads = HashMap<Long, Download>()
    private var isSubscribedReceiver = false
    private lateinit var androidDownloadManager: AndroidDownloadManager
    var useSystemDownloadManager:Boolean=true
    var autoInstallDownloadApp:Boolean=false
    var downloadPath:String= Environment.DIRECTORY_DOWNLOADS
    var mOnAutoInstallDownloadAppListener:OnAutoInstallDownloadAppListener?=null

    fun setOnAutoInstallDownloadAppListener(onAutoInstallDownloadAppListener:OnAutoInstallDownloadAppListener){
        this.mOnAutoInstallDownloadAppListener=onAutoInstallDownloadAppListener
    }

    fun initDownloadManager(){
        useSystemDownloadManager=applicationContext.getSharedPreferences(DOWNLOAD_MANAGER_STORE, Context.MODE_PRIVATE).getBoolean(USE_SYSTEM_DOWNLOAD_MANAGER,true)
        autoInstallDownloadApp=applicationContext.getSharedPreferences(DOWNLOAD_MANAGER_STORE, Context.MODE_PRIVATE).getBoolean(AUTO_INSTALL_DOWNLOAD_APP,false)
        downloadPath=applicationContext.getSharedPreferences(DOWNLOAD_MANAGER_STORE, Context.MODE_PRIVATE).getString(DOWNLOAD_PATH,Environment.DIRECTORY_DOWNLOADS)!!
    }
    fun setDownloadManager(useSystemDownloadManager: Boolean){
        this.useSystemDownloadManager=useSystemDownloadManager
        applicationContext.getSharedPreferences(DOWNLOAD_MANAGER_STORE, Context.MODE_PRIVATE)
            .edit().putBoolean(USE_SYSTEM_DOWNLOAD_MANAGER,useSystemDownloadManager).apply()

    }
    fun setAutoInstallApp(autoInstallDownloadApp:Boolean){
        this.autoInstallDownloadApp=autoInstallDownloadApp
        applicationContext.getSharedPreferences(DOWNLOAD_MANAGER_STORE, Context.MODE_PRIVATE)
            .edit().putBoolean(AUTO_INSTALL_DOWNLOAD_APP,autoInstallDownloadApp).apply()
    }
    fun setCustomDownloadPath(path:String){
        this.downloadPath=path
        applicationContext.getSharedPreferences(DOWNLOAD_MANAGER_STORE, Context.MODE_PRIVATE)
            .edit().putString(DOWNLOAD_PATH,path).apply()
    }
    /**
     * Schedule a download through the [AndroidDownloadManager].
     * @param download metadata related to the download.
     * @param refererURL the url from where this download was referred.
     * @param cookie any additional cookie to add as part of the download request.
     * @return the id reference of the scheduled download.
     */
    @RequiresPermission(allOf = arrayOf(INTERNET, WRITE_EXTERNAL_STORAGE))
    fun download(
        download: Download,
        refererURL: String = "",
        cookie: String = ""
    ): Long {
        //如果是不支持的格式，或者是使用第三方下载
        if (download.isNotSupportedProtocol() || !useSystemDownloadManager) {
            val intent = Intent(ACTION_VIEW,Uri.parse(download.url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            applicationContext.startActivity(intent)
            // We are ignoring everything that is not http or https. This is a limitation of
            // Android's download manager. There's no reason to show a download dialog for
            // something we can't download anyways.
            //showUnSupportFileErrorMessage()
            return FILE_NOT_SUPPORTED
        }
        //如果是应用下载
        if (download.fileName.toLowerCase().endsWith(".apk")){
            download.destinationDirectory="orange_download_apks"
        }else{
            download.destinationDirectory=downloadPath
        }
        if (!applicationContext.isPermissionGranted(INTERNET, WRITE_EXTERNAL_STORAGE)) {
            throw SecurityException("You must be granted INTERNET and WRITE_EXTERNAL_STORAGE permissions")
        }

        androidDownloadManager = applicationContext.getSystemService(DOWNLOAD_SERVICE) as AndroidDownloadManager

        val fileName = getFileName(download)

        val request = Request(Uri.parse(download.url))
            .setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        if (!download.contentType.isNullOrEmpty()) {
            request.setMimeType(download.contentType)
        }

        with(request) {
            addRequestHeaderSafely("User-Agent", download.userAgent)
            addRequestHeaderSafely("Cookie", cookie)
            addRequestHeaderSafely("Referer", refererURL)
        }

        request.setDestinationInExternalPublicDir(download.destinationDirectory, fileName)

        val downloadID = androidDownloadManager.enqueue(request)
        queuedDownloads[downloadID] = download

        if (!isSubscribedReceiver) {
            registerBroadcastReceiver(applicationContext)
        }

        return downloadID
    }

    /**
     * Remove all the listeners.
     */
    fun unregisterListener() {
        if (isSubscribedReceiver) {
            applicationContext.unregisterReceiver(onDownloadComplete)
            isSubscribedReceiver = false
            queuedDownloads.clear()
        }
    }

    private fun registerBroadcastReceiver(context: Context) {
        val filter = IntentFilter(ACTION_DOWNLOAD_COMPLETE)
        context.registerReceiver(onDownloadComplete, filter)
        isSubscribedReceiver = true
    }

    private fun getFileName(download: Download): String? {
        return if (!download.fileName.isNullOrEmpty()) {
            download.fileName
        } else {
            DownloadUtils.guessFileName(
                "",
                download.url,
                download.contentType
            )
        }
    }

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (queuedDownloads.isEmpty()) {
                unregisterListener()
            }

            val downloadID = intent.getLongExtra(EXTRA_DOWNLOAD_ID, -1)

            if (downloadID in queuedDownloads) {
                val download = queuedDownloads[downloadID]

                download?.let {
                    //如果能自动安装应用
                    if (autoInstallDownloadApp && download.fileName.toLowerCase().endsWith(".apk")){
                        mOnAutoInstallDownloadAppListener?.onAutoInstallDownloadApp(download=download)
                    }
                    onDownloadCompleted.invoke(download, downloadID)
                }
                queuedDownloads -= (downloadID)

                if (queuedDownloads.isEmpty()) {
                    unregisterListener()
                }
            }
        }
    }

    private fun Download.isNotSupportedProtocol(): Boolean {
        val scheme = Uri.parse(url.trim()).scheme
        return (scheme == null || scheme != "http" && scheme != "https")
    }

    private fun showUnSupportFileErrorMessage() {
        Toast.makeText(applicationContext, R.string.mozac_feature_downloads_file_not_supported, Toast.LENGTH_LONG)
            .show()
    }

    companion object{
        const val DOWNLOAD_MANAGER_STORE="app_download_manager_setting"
        const val USE_SYSTEM_DOWNLOAD_MANAGER="use_system_download_manager"
        const val AUTO_INSTALL_DOWNLOAD_APP="auto_install_download_app"
        const val DOWNLOAD_PATH="download_path"
        var downloadManager:DownloadManager?=null
        fun getInstance(applicationContext: Context,onDownloadCompleted: OnDownloadCompleted = { _, _ -> }):DownloadManager{
            if (downloadManager==null){
                downloadManager=DownloadManager(applicationContext, onDownloadCompleted).apply {
                    initDownloadManager()
                }
            }
            return downloadManager!!
        }
    }
}

internal fun Request.addRequestHeaderSafely(name: String, value: String?) {
    if (value.isNullOrEmpty()) {
        return
    }
    addRequestHeader(name, value)
}
