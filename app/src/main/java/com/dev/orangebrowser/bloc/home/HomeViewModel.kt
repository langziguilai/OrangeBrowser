package com.dev.orangebrowser.bloc.home


import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.dev.base.CoroutineViewModel
import com.dev.orangebrowser.config.ErrorCode
import com.dev.orangebrowser.data.dao.MainPageSiteDao
import com.dev.orangebrowser.data.model.CloseItem
import com.dev.orangebrowser.data.model.MainPageSite
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.lang.Exception
import javax.inject.Inject

class HomeViewModel @Inject constructor(var context: Context) : CoroutineViewModel()  {
    companion object{
        const val MAIN_PAGE_SITES_JSON_FILE_NAME="main_page_sites.json"
    }
    @Inject
    lateinit var mainPageSiteDao: MainPageSiteDao
    val siteListLiveData: MutableLiveData<List<MainPageSite>> = MutableLiveData()
    val errorCodeLiveData:MutableLiveData<Int> = MutableLiveData()
    fun loadMainPageSites()=launch(Dispatchers.IO){
        try {
            var sites=mainPageSiteDao.getAll()
            if (sites.isEmpty()){
                val inputStream=context.assets.open(MAIN_PAGE_SITES_JSON_FILE_NAME)
                val dataList=
                    Gson().fromJson<List<MainPageSite>>(InputStreamReader(inputStream), object : TypeToken<List<MainPageSite>>(){}.type)
                var i=0
                dataList.forEach {
                    it.rank=i
                    mainPageSiteDao.insertAll(it)
                    i += 1
                }
                sites=dataList
            }
            launch (Dispatchers.Main ){
                siteListLiveData.value=sites
            }
        }catch (e: Exception){
            e.printStackTrace()
            launch (Dispatchers.Main ){
                errorCodeLiveData.value= ErrorCode.LOAD_FAIL
            }
        }
    }
    fun updateSites(closeItems:List<CloseItem<MainPageSite>>)=launch(Dispatchers.IO) {
        try {
            for ((index, item) in closeItems.withIndex()){
                item.data.rank=index
                mainPageSiteDao.updateSiteRank(item.data.uid,item.data.rank)
            }
        }catch (e:Exception){
            errorCodeLiveData.value= ErrorCode.MOVE_FAIL
        }
    }
    fun delete(deletedSite:MainPageSite,closeItems: List<CloseItem<MainPageSite>>)=launch(Dispatchers.IO) {
        try {
            mainPageSiteDao.delete(deletedSite.uid)
            for ((index, item) in closeItems.withIndex()){
                item.data.rank=index
                mainPageSiteDao.updateSiteRank(item.data.uid,item.data.rank)
            }
        }catch (e:Exception){
            launch(Dispatchers.Main) {
                  errorCodeLiveData.value= ErrorCode.DELETE_FAIL
            }
        }
    }
}
