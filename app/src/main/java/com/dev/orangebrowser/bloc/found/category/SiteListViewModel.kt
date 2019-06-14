package com.dev.orangebrowser.bloc.found.category

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

class SiteListViewModel @Inject constructor(var context: Context) : CoroutineViewModel() {
    companion object{
        const val COMMON_SITE_JSON_FILE_NAME="common_sites.json"
    }
    @Inject
    lateinit var commonSiteDao:CommonSiteDao
    @Inject
    lateinit var mainPageSiteDao:MainPageSiteDao
    val categoryListLiveData: MutableLiveData<List<CommonSite>> = MutableLiveData()
    val errorCodeLiveData:MutableLiveData<Int> = MutableLiveData()
    fun loadCommonSites(category:String)=launch(Dispatchers.IO){
        if (category.isNotBlank()){
            try {
                var commonSites=commonSiteDao.getByCategory(category)
                if (commonSites.isEmpty()){
                    val mainPageSiteList=mainPageSiteDao.getAll()
                    val inputStream=context.assets.open(COMMON_SITE_JSON_FILE_NAME)
                    val dataList=
                        Gson().fromJson<List<CommonSite>>(InputStreamReader(inputStream), object : TypeToken<List<CommonSite>>(){}.type)
                    dataList.forEach { commonSite ->
                        //如果已经存在
                        if (mainPageSiteList.any { it.url == commonSite.url }){
                            commonSite.added=true
                        }
                        commonSiteDao.insertAll(commonSite)
                    }
                    commonSites=dataList.filter { it.category==category }
                }

                launch (Dispatchers.Main ){
                    categoryListLiveData.value=commonSites
                }
            }catch (e: Exception){
                e.printStackTrace()
                launch (Dispatchers.Main ){
                    errorCodeLiveData.value= ErrorCode.LOAD_FAIL
                }
            }
        }
    }
    //TODO:远程加载，并更新
    fun loadFromRemote()=launch(Dispatchers.IO){

    }

    fun addToHomePage(site:CommonSite)=launch(Dispatchers.IO) {
        try {
            commonSiteDao.updateAddStatus(site.uid,true)
            if(mainPageSiteDao.getExistCount(site.url ?: "")<=0){
                val count=mainPageSiteDao.getCount()
                mainPageSiteDao.insertAll(MainPageSite(url = site.url,
                    name = site.name,
                    description = site.description,
                    icon = site.icon,rank = count))
            }
        }catch (e: Exception){
            e.printStackTrace()
        }

    }
}
