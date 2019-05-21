package com.dev.orangebrowser.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dev.orangebrowser.data.model.FavoriteSite
import com.dev.orangebrowser.data.model.SavedFile

@Dao
interface SavedFileDao {
    //按照rank排序
    @Query("SELECT * FROM saved_file order by date DESC")
    fun getAll(): List<SavedFile>

    @Query("SELECT * FROM saved_file WHERE _type=:type order by date DESC")
    fun getByType(type: String): List<SavedFile>

    @Query("SELECT * FROM saved_file WHERE referer=:referer order by date DESC")
    fun getByReferer(referer: String): List<SavedFile>

    //
    @Insert
    fun insertAll(vararg files: SavedFile)

    //删除某个
    @Query("DELETE FROM saved_file WHERE uid=:uid")
    fun delete(uid: Int)

    //删除所有
    @Query("DELETE FROM saved_file")
    fun deleteAll()
}