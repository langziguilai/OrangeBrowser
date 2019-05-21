package com.dev.orangebrowser.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/***
 * 最喜欢的网站
 */

@Entity(tableName="saved_file")
data class SavedFile(
    @PrimaryKey(autoGenerate = true) var uid: Int=0,
    @ColumnInfo(name = "referer") var referer: String?, //来源
    @ColumnInfo(name = "name") var name: String?,//名称
    @ColumnInfo(name = "path") var path: String?,//保存路径
    @ColumnInfo(name = "_type") var type: String= UNKNOWN, //类型
    @ColumnInfo(name = "date") var date: Long= Date().time //创建日期
){
    companion object{
        const val IMAGE="IMAGE"
        const val HTML="HTML"
        const val VIDEO="VIDEO"
        const val JS="JS"
        const val CSS="CSS"
        const val UNKNOWN="UNKNOWN"
    }
}
