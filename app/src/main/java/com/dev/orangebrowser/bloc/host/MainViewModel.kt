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
    fun loadAppData(activity:MainActivity)=launch(Dispatchers.IO){
        try {
            val favorSites=context.loadJsonArray("favor_site.json",Site::class.java)
            val themes=ThemeSources.loadThemeSources(context)
            val applicationData=ApplicationData(favorSites,themes = themes)
            launch (Dispatchers.Main ){
                val themeName=activity.getPreferences(Context.MODE_PRIVATE).getString(activity.getString(R.string.pref_setting_theme),"") ?: ""
                if (themeName.isNotBlank()){
                    theme.value= applicationData.themes.themeSources.first { it.name==themeName }.toTheme()
                }else{
                    theme.value=applicationData.themes.getActiveThemeSource()?.toTheme() ?: Theme.defaultTheme(context)
                }
                appData.value=Either.Right(applicationData)
            }
        }catch (e: IOException){
            launch (Dispatchers.Main ){
                appData.value=Either.Left(Failure.IOError)
            }
        }
    }
    fun initFromApplicationData(applicationData: ApplicationData){
        theme.value=applicationData.themes.getActiveThemeSource()?.toTheme() ?: Theme.defaultTheme(context)
        appData.value=Either.Right(applicationData)
    }
    fun clearQuitSignal()=launch(Dispatchers.IO){
        delay(2000)
        launch (Dispatchers.Main ){
            quitSignalClear.value=false
        }
    }
}