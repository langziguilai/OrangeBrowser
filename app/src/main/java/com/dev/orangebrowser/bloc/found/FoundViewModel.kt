package com.dev.orangebrowser.bloc.found

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.dev.base.CoroutineViewModel
import com.dev.orangebrowser.config.ResultCode
import com.dev.orangebrowser.data.dao.SiteCategoryDao
import com.dev.orangebrowser.data.model.SiteCategory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import javax.inject.Inject

class FoundViewModel @Inject constructor(var context: Context) : CoroutineViewModel() {
    companion object{
        const val SITE_CATEGORIES_JSON_FILE_NAME="site_categories.json"
    }
    @Inject
    lateinit var siteCategoryDao:SiteCategoryDao
    val categoryListLiveData: MutableLiveData<List<SiteCategory>> = MutableLiveData()
    val errorCodeLiveData:MutableLiveData<Int> = MutableLiveData()
    fun loadCategoryList()=launch(Dispatchers.IO){
        try {
            var categoryList=siteCategoryDao.getAll()
            if (categoryList.isEmpty()){
                val inputStream=context.assets.open(SITE_CATEGORIES_JSON_FILE_NAME)
                val dataList=
                    Gson().fromJson<List<SiteCategory>>(InputStreamReader(inputStream), object : TypeToken<List<SiteCategory>>(){}.type)
                dataList.forEach {
                    siteCategoryDao.insertAll(it)
                }
                categoryList=dataList
            }
            launch (Dispatchers.Main ){
                categoryListLiveData.value=categoryList
            }
        }catch (e: Exception){
            e.printStackTrace()
            launch (Dispatchers.Main ){
                errorCodeLiveData.value= ResultCode.LOAD_FAIL
            }
        }
    }
    //TODO:远程加载，并更新
    fun loadFromRemote()=launch(Dispatchers.IO){

    }
}
