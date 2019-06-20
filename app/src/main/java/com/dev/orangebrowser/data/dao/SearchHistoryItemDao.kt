package com.dev.orangebrowser.data.dao

import androidx.room.*
import com.dev.orangebrowser.data.model.SearchHistoryItem

@Dao
interface SearchHistoryItemDao {
    @Insert
    fun insert(entity: SearchHistoryItem): Long
    @Query("SELECT * FROM search_history_item ORDER BY uid DESC")
    fun getSearchHistoryList(): List<SearchHistoryItem>
    @Query("SELECT * FROM search_history_item WHERE uid<=:end AND uid>=:start ORDER BY uid DESC")
    fun getSearchHistoryListByRange(start:Int,end:Int): List<SearchHistoryItem>
    @Query("SELECT * FROM search_history_item WHERE (search_item like :query) ORDER BY uid LIMIT :limit ")
    fun getSearchHistoryByQuery(query:String,limit:Int): List<SearchHistoryItem>
    @Query("SELECT * FROM search_history_item WHERE (search_item like :query)  ORDER BY uid LIMIT 1 ")
    fun getSearchHistoryByQueryOne(query:String): SearchHistoryItem?
    @Delete
    fun deleteSearchHistory(entity: SearchHistoryItem)
    @Query("DELETE  FROM search_history_item")
    fun clearSearchHistory()
}