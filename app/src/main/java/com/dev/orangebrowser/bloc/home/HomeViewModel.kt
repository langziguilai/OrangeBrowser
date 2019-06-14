package com.dev.orangebrowser.bloc.home


import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.MutableLiveData
import com.dev.base.CoroutineViewModel
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
        const val DELETE_FAIL=0x01
        const val MOVE_FAIL=0x02
        const val LOAD_FAIL=0x03
        const val UPDATE_FAIL=0x04
        const val MAIN_PAGE_SITES_FILES="main_page_sites_file.json"
    }
    @Inject
    lateinit var mainPageSiteDao: MainPageSiteDao
    val siteList: MutableLiveData<List<MainPageSite>> = MutableLiveData()
    val errorCode:MutableLiveData<Int> = MutableLiveData()
    fun loadMainPageSites()=launch(Dispatchers.IO){
        try {
            var sites=mainPageSiteDao.getAll()
            if (sites.isEmpty()){
                val inputStream=context.assets.open("main_page_sites.json")
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
                siteList.value=sites
            }
        }catch (e: Exception){
            e.printStackTrace()
            launch (Dispatchers.Main ){
                errorCode.value= LOAD_FAIL
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
            errorCode.value= MOVE_FAIL
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
                  errorCode.value= DELETE_FAIL
            }
        }
    }
}
