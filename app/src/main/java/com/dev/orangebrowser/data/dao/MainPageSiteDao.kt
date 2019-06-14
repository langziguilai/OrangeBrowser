package com.dev.orangebrowser.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dev.orangebrowser.data.model.CommonSite
import com.dev.orangebrowser.data.model.MainPageSite

//rank的排序：值越小，排名越靠前
@Dao
interface MainPageSiteDao {
    //按照rank排序
    @Query("SELECT * FROM main_page_site ORDER BY rank ASC")
    fun getAll(): List<MainPageSite>

    //更新排序
    @Query("UPDATE main_page_site SET rank=:rank where uid=:id")
    fun updateSiteRank(id: Int, rank: Int)
    //
    @Insert
    fun insertAll(vararg sites: MainPageSite)

    //删除某个
    @Query("DELETE FROM main_page_site WHERE uid=:uid")
    fun delete(uid: Int)

    //删除所有
    @Query("DELETE FROM main_page_site")
    fun deleteAll()
}