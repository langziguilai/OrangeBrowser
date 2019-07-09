package com.dev.orangebrowser

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.dev.base.BaseApplication
import com.dev.base.crash.cockroach.Cockroach
import com.dev.base.crash.cockroach.ExceptionHandler
import com.dev.browser.adblock.setting.AdBlockHelper
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
import com.dev.util.Keep
import com.dev.view.biv.concept.BigImageViewer
import com.dev.view.biv.loader.glide.GlideImageLoader
import com.hw.ycshareelement.YcShareElement
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import kotlinx.coroutines.*
import org.adblockplus.libadblockplus.android.AdblockEngine
import org.adblockplus.libadblockplus.android.SingleInstanceEngineProvider
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


@Keep
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
    var applicationData: ApplicationData?=null

    fun initApplicationData(data:ApplicationData){applicationData=data}
    override fun injectMembers() {
        appComponent.inject(this)
    }
    lateinit var sessionClearObserver:SessionManager.Observer
    override fun initialize() {
        initCrashHandler()
        YcShareElement.enableContentTransition(this)
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
        AdBlockHelper.get().apply {
            init(this@AndroidApplication, AdblockEngine.BASE_PATH_DIRECTORY,true,"adblock_pref")
            provider.retain(true)
            (provider as? SingleInstanceEngineProvider)?.apply {
                this.addEngineCreatedListener {
                    injectFilterToAdBlock(it)
                }
            }
        }

        //设置GSYVideoPLAYER的播放方式
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)

    }

    //
    private fun initCrashHandler(){
        val sysExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        val toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)
        Cockroach.install(this,object:ExceptionHandler(){
            override fun onUncaughtExceptionHappened(thread: Thread, throwable: Throwable) {
                Log.e("AndroidRuntime", "--->onUncaughtExceptionHappened:$thread<---", throwable)
                if (BuildConfig.DEBUG) {
                    Handler(Looper.getMainLooper()).post(Runnable {
                        toast.setText(R.string.safe_mode_excep_tips)
                        toast.show()
                    })
                }
            }

            override fun onBandageExceptionHappened(throwable: Throwable) {
                throwable.printStackTrace()//打印警告级别log，该throwable可能是最开始的bug导致的，无需关心
                if (BuildConfig.DEBUG) {
                    toast.setText("Cockroach Worked")
                    toast.show()
                }
            }

            override fun onEnterSafeMode() {

            }

            override fun onMayBeBlackScreen(e: Throwable) {
                super.onMayBeBlackScreen(e)
                val thread = Looper.getMainLooper().thread
                Log.e("AndroidRuntime", "--->onUncaughtExceptionHappened:$thread<---", e)
                //黑屏时建议直接杀死app
                sysExceptionHandler.uncaughtException(thread, RuntimeException("black screen"))
            }
        })
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