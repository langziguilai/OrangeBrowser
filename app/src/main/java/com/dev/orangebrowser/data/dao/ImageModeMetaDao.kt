package com.dev.orangebrowser.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dev.orangebrowser.data.model.ImageModeMeta
import com.dev.orangebrowser.data.model.SiteCategory

@Dao
interface ImageModeMetaDao {
    //按照rank排序
    @Query("SELECT * FROM image_mode_meta")
    fun getAll(): List<ImageModeMeta>

    //
    @Insert
    fun insertAll(vararg imageModeMeta: ImageModeMeta)

    //删除某个
    @Query("DELETE FROM image_mode_meta WHERE uid=:uid")
    fun delete(uid: Int)
    //选中
    @Query("SELECT * FROM image_mode_meta WHERE site=:site")
    fun get(site: String):List<ImageModeMeta>
    //更新
    @Update
    fun update(vararg imageModeMeta: ImageModeMeta)
    //删除所有
    @Query("DELETE FROM image_mode_meta")
    fun deleteAll()
    //exist
    @Query("SELECT * FROM image_mode_meta WHERE unique_key=:uniqueKey LIMIT 1")
    fun getByUniqueKey(uniqueKey:String):ImageModeMeta?
}