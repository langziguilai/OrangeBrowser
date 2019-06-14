package com.dev.orangebrowser.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dev.orangebrowser.data.model.SiteCategory

@Dao
interface SiteCategoryDao {
    //按照rank排序
    @Query("SELECT * FROM site_category")
    fun getAll(): List<SiteCategory>

    //
    @Insert
    fun insertAll(vararg siteCategory: SiteCategory)

    //删除某个
    @Query("DELETE FROM site_category WHERE uid=:uid")
    fun delete(uid: Int)

    //删除所有
    @Query("DELETE FROM site_category")
    fun deleteAll()
}