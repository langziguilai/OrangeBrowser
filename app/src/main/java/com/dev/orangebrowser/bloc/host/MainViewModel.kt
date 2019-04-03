package com.dev.orangebrowser.bloc.host

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.dev.base.CoroutineViewModel
import com.dev.base.exception.Failure
import com.dev.base.functional.Either
import com.dev.orangebrowser.data.model.ApplicationData
import com.dev.orangebrowser.data.model.NewsCategory
import com.dev.orangebrowser.data.model.Site
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject

class MainViewModel @Inject constructor(var context: Context) : CoroutineViewModel() {
    val appData: MutableLiveData<Either<Failure, ApplicationData>> = MutableLiveData()
    fun loadAppData()=launch(Dispatchers.IO){
        try {

            val favorSitesInputStream=context.assets.open("favor_site.json")
            val favorSites=Gson().fromJson<List<Site>>(InputStreamReader(favorSitesInputStream), object : TypeToken<List<Site>>(){}.type)
            val categoryInputStream=context.assets.open("news_category.json")
            val categories=Gson().fromJson<List<NewsCategory>>(InputStreamReader(categoryInputStream), object : TypeToken<List<NewsCategory>>(){}.type)
            val applicationData=ApplicationData(favorSites,categories)
            launch (Dispatchers.Main ){
                appData.value=Either.Right(applicationData)
            }
        }catch (e: IOException){
            launch (Dispatchers.Main ){
                appData.value=Either.Left(Failure.IOError)
            }
        }
    }

}