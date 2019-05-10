package com.dev.orangebrowser.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dev.orangebrowser.data.model.AdBlockFilter
import com.dev.orangebrowser.data.model.FavoriteSite

@Dao
interface AdBlockFilterDao {
    @Query("SELECT * FROM ad_block_filter")
    fun getAll(): List<AdBlockFilter>

    @Insert
    fun insertAll(vararg adBlockFilters: AdBlockFilter)

    @Query("SELECT COUNT(*) FROM ad_block_filter WHERE rule=:rule")
    fun count(rule:String):Int

    //删除某个
    @Query("DELETE FROM ad_block_filter WHERE rule=:rule")
    fun delete(rule: String)

    //删除所有
    @Query("DELETE FROM ad_block_filter")
    fun deleteAll()
}