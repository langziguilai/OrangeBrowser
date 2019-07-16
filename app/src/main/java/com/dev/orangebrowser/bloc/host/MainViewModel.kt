package com.dev.orangebrowser.bloc.host

import android.content.Context
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import com.dev.base.CoroutineViewModel
import com.dev.base.exception.Failure
import com.dev.base.extension.getPreferenceKey
import com.dev.base.extension.loadJsonArray
import com.dev.base.extension.loadJsonObject
import com.dev.base.functional.Either
import com.dev.base.util.EncodeUtil
import com.dev.browser.concept.BrowserSetting
import com.dev.browser.concept.REDIRECT_TO_APP_NO
import com.dev.browser.database.download.*
import com.dev.orangebrowser.R
import com.dev.orangebrowser.data.model.*
import com.dev.orangebrowser.extension.getSpBool
import com.dev.orangebrowser.extension.getSpInt
import com.dev.util.FileTypeUtil
import com.dev.util.FileUtil
import com.dev.util.VideoUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject

class MainViewModel @Inject constructor(var context: Context) : CoroutineViewModel() {
    @Inject
    lateinit var downloadDao:DownloadDao
    val appData: MutableLiveData<Either<Failure, ApplicationData>> = MutableLiveData()
    val theme:MutableLiveData<Theme> = MutableLiveData()
    var quitSignalClear:MutableLiveData<Boolean> = MutableLiveData()
    fun loadAppData(activity:MainActivity)=launch(Dispatchers.IO){
        try {
            val favorSites=context.loadJsonArray("favor_site.json",Site::class.java)
            val themes=ThemeSources.loadThemeSources(context)
            val applicationData=ApplicationData(favorSites,themes = themes)
            getBrowserSetting(activity)
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
    private fun getBrowserSetting(activity: MainActivity){
        BrowserSetting.RedirectToApp=activity.getSpInt(R.string.pref_setting_need_intercept_open_app_value, REDIRECT_TO_APP_NO)
        BrowserSetting.ShouldUseCacheMode=activity.getSpBool(R.string.pref_setting_should_use_cache_mode, false)
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
    //检查并更新下载：因为使用的是系统下载器，只能用通知来更新下载状态，所以，我们需要在应用恢复之后，手动更新状态，以避免那些错误的通知
    fun checkAndUpdateDownload()=launch(Dispatchers.IO) {
        try {
            val downloads=downloadDao.getUnFinished()
            downloads.forEach {download->
                val downloadFilePath= download.path
                if (FileUtil.exist(downloadFilePath)){
                    //更新封面和类型
                    if (FileTypeUtil.isVideo(path=downloadFilePath)){
                        val bitmap= VideoUtil.getVideoThumb(downloadFilePath)
                        val fileName=
                            Environment.getExternalStorageDirectory().absolutePath + File.separator + download.destinationDirectory + File.separator + EncodeUtil.md5(download.url)+".webp"
                        val success= VideoUtil.saveVideoThumbnial(bitmap, File(fileName))
                        if (success){
                            downloadDao.updateLocalPosterAndType(download.url, localPoster = fileName,type = VIDEO)
                        }
                    }else if (FileTypeUtil.isImage(path=downloadFilePath)){
                        downloadDao.updateType(download.url,type= IMAGE)
                    }else if(download.fileName.toLowerCase().endsWith(".apk")){
                        downloadDao.updateType(download.url,type= APK)
                    }else if(FileTypeUtil.isAudio(path=downloadFilePath)){
                        downloadDao.updateType(download.url,type= AUDIO)
                    }else{
                        downloadDao.updateType(download.url,type= COMMON)
                    }
                    //更新大小
                    val size=
                        FileUtil.getSize(Environment.getExternalStorageDirectory().absolutePath + File.separator + download.destinationDirectory + File.separator + download.fileName)
                    downloadDao.updateStatus(download.url, STATUS_FINISH,size)
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}