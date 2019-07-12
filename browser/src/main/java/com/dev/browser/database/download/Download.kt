/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.database.download

import android.os.Environment
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dev.base.util.EncodeUtil
import com.dev.browser.session.Download
import java.io.File
import java.util.*

const val COMMON=1
const val IMAGE=2
const val VIDEO=3
const val AUDIO=4
const val APK=5

const val STATUS_FINISH=1
const val STATUS_FAILED=2
const val STATUS_NOT_FINISH=3
const val STATUS_OTHER_DOWNLOADER=4
/**
 * 下载记录
 */
@Entity(tableName = "download")
data class DownloadEntity(
    @PrimaryKey
    @ColumnInfo(name = "url")
    var url: String,
    @ColumnInfo(name = "date")
    var date:Long= Date().time,
    @ColumnInfo(name = "status")
    var status:Int= STATUS_NOT_FINISH,
    @ColumnInfo(name = "file_type")
    var type:Int= COMMON,
    @ColumnInfo(name = "file_name")
    var fileName: String,
    @ColumnInfo(name = "referer")
    var referer: String="",
    @ColumnInfo(name = "cookies")
    var cookies: String="",
    @ColumnInfo(name = "content_type")
    var contentType: String="",
    @ColumnInfo(name = "content_length")
    var contentLength: Long=0,
    @ColumnInfo(name = "user_agent")
    var userAgent: String="",
    @ColumnInfo(name = "poster")
    var poster: String="",
    @ColumnInfo(name = "local_poster")
    var localPoster: String="",
    @ColumnInfo(name = "destination_directory")
    var destinationDirectory: String="",
    @ColumnInfo(name = "path")
    var path: String=""
){
    companion object{
        fun fromDownload(download: Download):DownloadEntity{
            var type= COMMON
            download.contentType?.apply {
                when {
                    this.toLowerCase().startsWith("image") -> type= IMAGE
                    this.toLowerCase().startsWith("video") -> type= VIDEO
                    this.toLowerCase().startsWith("audio") -> type= AUDIO
                }
            }
            if (download.fileName.toLowerCase().endsWith(".apk")){
                type= APK
            }

            val path=Environment.getExternalStorageDirectory().absolutePath + File.separator + download.destinationDirectory + File.separator + download.fileName
            return DownloadEntity(
                url = download.url,
                type = type,
                fileName = download.fileName,
                referer = download.referer ?: "",
                cookies = download.cookies ?: "",
                contentType = download.contentType ?: "",
                contentLength = download.contentLength ?: 0,
                userAgent = download.userAgent ?: "",
                poster = download.poster ?: "",
                localPoster = "",
                destinationDirectory = download.destinationDirectory,
                path = path
            )
        }
    }
}
