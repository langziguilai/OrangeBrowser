package com.dev.orangebrowser.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dev.orangebrowser.data.model.AdBlockRecord
import com.dev.orangebrowser.data.model.FavoriteSite

@Dao
interface AdBlockRecordDao {
    //按照rank排序
    @Query("SELECT * FROM ad_block_record order by uid")
    fun getAll(): List<AdBlockRecord>

    //更新命中次数
    @Query("UPDATE ad_block_record SET count=count+1 where uid=:id")
    fun updateCount(id: Int)
    //获取记录，才能更新
    @Query("SELECT * FROM ad_block_record WHERE rule=:rule")
    fun getByRule(rule:String):List<AdBlockRecord>
    //
    @Insert
    fun insertAll(vararg records: AdBlockRecord)

    //删除某个
    @Query("DELETE FROM ad_block_record WHERE uid=:uid")
    fun delete(uid: Int)

    //删除所有
    @Query("DELETE FROM ad_block_record")
    fun deleteAll()
}