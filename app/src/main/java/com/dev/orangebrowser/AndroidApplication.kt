package com.dev.orangebrowser

import android.os.Environment
import android.util.Log
import com.dev.base.BaseApplication
import com.dev.browser.adblock.setting.AdblockHelper
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.browser.session.storage.SessionStorage
import com.dev.orangebrowser.data.dao.AdBlockFilterDao
import com.dev.orangebrowser.data.model.ApplicationData
import com.dev.orangebrowser.di.ApplicationComponent
import com.dev.orangebrowser.di.ApplicationModule
import com.dev.orangebrowser.di.DaggerApplicationComponent
import com.dev.orangebrowser.di.DatabaseModule
import com.dev.util.FileUtil
import com.dev.view.biv.concept.BigImageViewer
import com.dev.view.biv.loader.glide.GlideImageLoader
import kotlinx.coroutines.*
import org.adblockplus.libadblockplus.android.AdblockEngine
import org.adblockplus.libadblockplus.android.SingleInstanceEngineProvider
import java.lang.Exception
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AndroidApplication:BaseApplication(),CoroutineScope {
    private val job= SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main+job

    @Inject
    lateinit var adBlockFilterDao: AdBlockFilterDao
    @Inject
    lateinit var sessionManager:SessionManager
    @Inject
    lateinit var sessionStorage:SessionStorage
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
        BigImageViewer.initialize(GlideImageLoader.with(this))
        sessionClearObserver=object:SessionManager.Observer{
            //删除thumbnial
            override fun onSessionRemoved(session: Session) {
                session.webPageThumbnailPath?.apply {
                    val thumbnailPath=this
                    launch(Dispatchers.IO) {
                        try {
                            FileUtil.delete(thumbnailPath)
                        }catch (e:Exception){
                            Log.e("delete thumb error",e.message)
                        }
                    }
                }
                session.mainPageThumbnailPath?.apply {
                    val thumbnailPath=this
                    launch(Dispatchers.IO) {
                        try {
                            FileUtil.delete(thumbnailPath)
                        }catch (e:Exception){
                            Log.e("delete thumb error",e.message)
                        }
                    }
                }
            }

            override fun beforeAllSessionsRemoved(sessions: List<Session>) {
                sessions.forEach {
                    it.webPageThumbnailPath?.apply {
                        val thumbnailPath=this
                        launch(Dispatchers.IO) {
                            try {
                                FileUtil.delete(thumbnailPath)
                            }catch (e:Exception){
                                Log.e("delete thumb error",e.message)
                            }
                        }
                    }
                    it.mainPageThumbnailPath?.apply {
                        val thumbnailPath=this
                        launch(Dispatchers.IO) {
                            try {
                                FileUtil.delete(thumbnailPath)
                            }catch (e:Exception){
                                Log.e("delete thumb error",e.message)
                            }
                        }
                    }
                }
            }
        }
        sessionManager.register(sessionClearObserver)
        AdblockHelper.get().apply {
            init(this@AndroidApplication, AdblockEngine.BASE_PATH_DIRECTORY,true,"adblock_pref")
            provider.retain(true)
            (provider as? SingleInstanceEngineProvider)?.apply {
                this.addEngineCreatedListener {
                    injectFilterToAdBlock(it)
                }
            }
        }

    }
    //注入自定义的Filter
    private fun injectFilterToAdBlock(engine:AdblockEngine){
        launch(Dispatchers.IO) {
            adBlockFilterDao.getAll().forEach {
                val filter=engine.filterEngine.getFilter(it.rule)
                if (!filter.isListed){
                    filter.addToList()
                }
            }
        }
    }
    override fun onTerminate() {
        sessionStorage.autoSave(sessionManager).triggerSave(false)
        sessionManager.unregister(sessionClearObserver)
        super.onTerminate()
        coroutineContext.cancelChildren()
    }
}