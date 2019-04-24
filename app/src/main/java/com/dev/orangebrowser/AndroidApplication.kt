package com.dev.orangebrowser

import android.os.Environment
import android.util.Log
import com.dev.base.BaseApplication
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.data.model.ApplicationData
import com.dev.orangebrowser.di.ApplicationComponent
import com.dev.orangebrowser.di.ApplicationModule
import com.dev.orangebrowser.di.DaggerApplicationComponent
import com.dev.orangebrowser.di.DatabaseModule
import com.dev.util.FileUtil
import kotlinx.coroutines.*
import org.adblockplus.libadblockplus.android.settings.AdblockHelper
import java.lang.Exception
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AndroidApplication:BaseApplication(),CoroutineScope {
    private val job= SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main+job

    @Inject
    lateinit var sessionManager:SessionManager
    val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .databaseModule(DatabaseModule())
            .build()
    }
    //全局数据
    lateinit var applicationData: ApplicationData

    fun initApplicationData(data:ApplicationData){applicationData=data}
    override fun injectMembers() {
        appComponent.inject(this)
    }
    lateinit var sessionClearObserver:SessionManager.Observer
    override fun initialize() {
        sessionClearObserver=object:SessionManager.Observer{
            //删除thumbnial
            override fun onSessionRemoved(session: Session) {
                session.thumbnailPath?.apply {
                    val thumbnailPath=this
                    launch(Dispatchers.IO) {
                        try {
                            val path=Environment.getExternalStorageDirectory().path+thumbnailPath
                            FileUtil.delete(path)
                        }catch (e:Exception){
                            Log.e("delete thumb error",e.message)
                        }
                    }
                }
            }

            override fun beforeAllSessionsRemoved(sessions: List<Session>) {
                sessions.forEach {
                    it.thumbnailPath?.apply {
                        val thumbnailPath=this
                        launch(Dispatchers.IO) {
                            try {
                                val path=Environment.getExternalStorageDirectory().path+thumbnailPath
                                FileUtil.delete(path)
                            }catch (e:Exception){
                                Log.e("delete thumb error",e.message)
                            }
                        }
                    }
                }
            }
        }
        sessionManager.register(sessionClearObserver)
        //初始化adblock
        AdblockHelper.get().init(this, filesDir.absolutePath, true, AdblockHelper.PREFERENCE_NAME);
    }

    override fun onTerminate() {
        sessionManager.unregister(sessionClearObserver)
        super.onTerminate()
        coroutineContext.cancelChildren()
    }
}