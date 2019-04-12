package com.dev.orangebrowser.bloc.host

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.dev.base.CoroutineViewModel
import com.dev.base.exception.Failure
import com.dev.base.extension.getPreferenceKey
import com.dev.base.extension.loadJsonArray
import com.dev.base.extension.loadJsonObject
import com.dev.base.functional.Either
import com.dev.orangebrowser.R
import com.dev.orangebrowser.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class MainViewModel @Inject constructor(var context: Context) : CoroutineViewModel() {
    val appData: MutableLiveData<Either<Failure, ApplicationData>> = MutableLiveData()
    val theme:MutableLiveData<Theme> = MutableLiveData()
    var quitSignalClear:MutableLiveData<Boolean> = MutableLiveData()
    fun loadAppData()=launch(Dispatchers.IO){
        try {

//            val favorSitesInputStream=context.assets.open("favor_site.json")
//            val favorSites=Gson().fromJson<List<Site>>(InputStreamReader(favorSitesInputStream), object : TypeToken<List<Site>>(){}.type)
            val favorSites=context.loadJsonArray("favor_site.json",Site::class.java)
//            val browserSetting=context.loadJsonObject<BrowserSetting>("browser_setting.json",BrowserSetting::class.java)
            val themes=ThemeSources.loadThemeSources(context)
            val applicationData=ApplicationData(favorSites,themes = themes)
            launch (Dispatchers.Main ){
                theme.value=applicationData.themes.getActiveThemeSource()?.toTheme() ?: Theme.defaultTheme(context)
                appData.value=Either.Right(applicationData)
            }
        }catch (e: IOException){
            launch (Dispatchers.Main ){
                appData.value=Either.Left(Failure.IOError)
            }
        }
    }
    fun clearQuitSignal()=launch(Dispatchers.IO){
        delay(2000)
        launch (Dispatchers.Main ){
            quitSignalClear.value=false
        }
    }
}