package com.dev.orangebrowser.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dev.orangebrowser.data.model.CommonSite

@Dao
interface CommonSiteDao {
//按照rank排序
    @Query("SELECT * FROM common_site GROUP BY category ORDER BY rank")
    fun getAll(): List<CommonSite>

//    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<User>
//更新排序
    @Query("UPDATE common_site SET rank=:rank where uid=:id")
    fun updateSiteRank(id: Int,rank: Int)
    //更改分类
    @Query("UPDATE common_site SET category=:category where uid=:id")
    fun updateSiteCategory(id: Int,category: String)
    //更改分类名称
    @Query("UPDATE common_site SET category=:newCategory where category=:category")
    fun updateCategoryName(category: String,newCategory:String)
//
    @Insert
    fun insertAll(vararg sites: CommonSite)
//删除某个
    @Query("DELETE FROM common_site WHERE uid=:uid")
    fun delete(uid: Int)
//删除所有
    @Query("DELETE FROM common_site")
    fun deleteAll()
}