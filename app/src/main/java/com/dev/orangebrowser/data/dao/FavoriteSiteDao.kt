package com.dev.orangebrowser.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dev.orangebrowser.data.model.FavoriteSite

@Dao
interface FavoriteSiteDao {
//按照rank排序
    @Query("SELECT * FROM favorite_site order by rank")
    fun getAll(): List<FavoriteSite>

//    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<User>
//更新排序
    @Query("UPDATE favorite_site SET rank=:rank where uid=:id")
    fun updateRank(id: Int,rank: Int)
//
    @Insert
    fun insertAll(vararg sites: FavoriteSite)
//删除某个
    @Query("DELETE FROM favorite_site WHERE uid=:uid")
    fun delete(uid: Int)
//删除所有
    @Query("DELETE FROM favorite_site")
    fun deleteAll()
}