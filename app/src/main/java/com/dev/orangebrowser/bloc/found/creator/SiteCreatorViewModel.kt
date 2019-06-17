package com.dev.orangebrowser.bloc.found.creator

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.dev.base.CoroutineViewModel
import com.dev.orangebrowser.config.ErrorCode
import com.dev.orangebrowser.data.dao.CommonSiteDao
import com.dev.orangebrowser.data.dao.MainPageSiteDao
import com.dev.orangebrowser.data.model.CommonSite
import com.dev.orangebrowser.data.model.MainPageSite
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import javax.inject.Inject

class SiteCreatorViewModel @Inject constructor(var context: Context) : CoroutineViewModel() {
    @Inject
    lateinit var mainPageSiteDao:MainPageSiteDao
    val errorCodeLiveData:MutableLiveData<Int> = MutableLiveData()
    val saveStatusLiveData:MutableLiveData<Boolean> = MutableLiveData()

    fun addToHomePage(site:MainPageSite)=launch(Dispatchers.IO) {
        try {
            if(mainPageSiteDao.getExistCount(site.url ?: "")<=0){
                val count=mainPageSiteDao.getCount()
                site.rank=count
                mainPageSiteDao.insertAll(site)
                saveStatusLiveData.postValue(true)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}
