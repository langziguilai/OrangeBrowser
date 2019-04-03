package com.dev.orangebrowser.bloc.home


import android.content.Context
import com.dev.base.CoroutineViewModel
import javax.inject.Inject

class HomeViewModel @Inject constructor(var context: Context) : CoroutineViewModel()  {
//    val categoryList: MutableLiveData<List<SiteCategory>> = MutableLiveData()
//    fun loadCategoryList()=launch(Dispatchers.IO){
//        try {
//            val inputStream=context.assets.open("site_category.json")
//            val data=
//                Gson().fromJson<List<SiteCategory>>(InputStreamReader(inputStream), object : TypeToken<List<SiteCategory>>(){}.type)
//            launch (Dispatchers.Main ){
//                categoryList.setValue(data)
//            }
//        }catch (e: IOException){
//            launch (Dispatchers.Main ){
//
//            }
//        }
//    }
}
