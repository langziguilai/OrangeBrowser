package com.dev.orangebrowser.bloc.search

import com.dev.base.CoroutineViewModel
import com.dev.orangebrowser.data.dao.SearchHistoryItemDao
import com.dev.orangebrowser.data.model.SearchHistoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class SearchViewModel @Inject constructor() : CoroutineViewModel() {
    @Inject
    lateinit var searchHistoryItemDao: SearchHistoryItemDao
    fun saveSearchItem(searchItem:String,callback: Runnable)=launch(Dispatchers.IO) {
        try {
            searchHistoryItemDao.insert(SearchHistoryItem(uid = 0,searchItem = searchItem))
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            launch(Dispatchers.Main) {
                callback.run()
            }
        }

    }
}
