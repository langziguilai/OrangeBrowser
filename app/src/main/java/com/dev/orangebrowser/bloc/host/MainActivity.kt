package com.dev.orangebrowser.bloc.host


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dev.base.BaseActivity
import com.dev.base.extension.showToast
import com.dev.base.support.BackHandler
import com.dev.base.support.isUrl
import com.dev.browser.feature.downloads.DownloadManager
import com.dev.browser.feature.tabs.TabsUseCases
import com.dev.browser.session.Download
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.bookmark.BookMarkFragment
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.download.DownloadFragment
import com.dev.orangebrowser.bloc.found.FoundFragment
import com.dev.orangebrowser.bloc.history.HistoryFragment
import com.dev.orangebrowser.bloc.home.HomeFragment
import com.dev.orangebrowser.bloc.imageMode.ImageModeModeFragment
import com.dev.orangebrowser.bloc.news.NewsFragment
import com.dev.orangebrowser.bloc.readMode.ReadModeFragment
import com.dev.orangebrowser.bloc.resource.ResourceFragment
import com.dev.orangebrowser.bloc.search.SearchFragment
import com.dev.orangebrowser.bloc.setting.SettingFragment
import com.dev.orangebrowser.bloc.setting.fragments.*
import com.dev.orangebrowser.bloc.setting.fragments.adblock.*
import com.dev.orangebrowser.bloc.tabs.TabFragment
import com.dev.orangebrowser.bloc.theme.ThemeFragment
import com.dev.orangebrowser.data.model.ApplicationData
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.extension.appData
import com.dev.orangebrowser.extension.myApplication
import com.dev.orangebrowser.utils.auto_install.InstallAppInstance
import com.dev.view.NavigationBarUtil
import com.dev.view.StatusBarUtil
import com.yzq.zxinglibrary.android.CaptureActivity
import com.yzq.zxinglibrary.bean.ZxingConfig
import com.yzq.zxinglibrary.common.Constant
import es.dmoral.toasty.Toasty
import permissions.dispatcher.*
import java.io.File
import javax.inject.Inject


const val APPLICATION_DATA = "application_data"

@RuntimePermissions
class MainActivity : BaseActivity(), DownloadManager.OnAutoInstallDownloadAppListener {
    override fun isStatusBarTransparent(): Boolean {
        return true
    }

    private var installAppInstance: InstallAppInstance? = null
    //自动安装应用
    override fun onAutoInstallDownloadApp(download: Download) {
        Log.d("get download path is", download.destinationDirectory + File.separator + download.fileName)
        installAppInstance = InstallAppInstance(
            this,
            Environment.getExternalStorageDirectory().absolutePath + File.separator + download.destinationDirectory + File.separator + download.fileName
        ).apply {
            install()
        }
    }

    companion object {
        const val REQUEST_CODE_SCAN = 0x1234
    }

    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var tabsUseCases: TabsUseCases
    lateinit var viewModel: MainViewModel
    //是否可以自动旋转屏幕，默认可以
    var enableAutoOrientation: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        DownloadManager.getInstance(applicationContext).setOnAutoInstallDownloadAppListener(this)
        super.onCreate(savedInstanceState)
    }


    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }

    override fun initView(savedInstanceState: Bundle?) {
        viewModel.appData.observe(this, Observer {
            it.either(fun(failure) {
                Toasty.error(this, failure.error, Toast.LENGTH_SHORT).show()
            }, fun(data) {
                myApplication.initApplicationData(data)
                loadFirstInFragmentWithPermissionCheck()
            })
        })
        viewModel.theme.observe(this, Observer {
            setColor(it.colorPrimary)
        })
        viewModel.quitSignalClear.observe(this, Observer {
            quitSignal = it
        })
    }

    private fun setColor(color: Int) {
        StatusBarUtil.setIconColor(this, color)
        NavigationBarUtil.setNavigationBarColor(this, color)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (appData != null) {
            outState.putParcelable(APPLICATION_DATA, appData)
        }
        super.onSaveInstanceState(outState)
    }

    override fun initData(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            viewModel.loadAppData()
        } else {
            val data = savedInstanceState.getParcelable<ApplicationData>(APPLICATION_DATA)
            if (data == null) {
                viewModel.loadAppData()
            } else {
                myApplication.initApplicationData(data)
                viewModel.initFromApplicationData(data)
            }
        }
    }

    @NeedsPermission(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.RECORD_AUDIO
    )
    fun loadFirstInFragment() {
        loadHomeFragment("")
    }

    @OnShowRationale(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.RECORD_AUDIO
    )
    fun showRationale(request: PermissionRequest) {
        //showRationaleDialog(R.string.permission_camera_rationale, request)
        Toast.makeText(this, "showRationale", Toast.LENGTH_SHORT).show()
    }

    @OnPermissionDenied(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.RECORD_AUDIO
    )
    fun onDenied() {
        Toast.makeText(this, "onDenied", Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.RECORD_AUDIO
    )
    fun onNeverAskAgain() {
        Toast.makeText(this, "onNeverAskAgain", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }


    override fun onBackPressed() {
        //这里倒序是因为：默认结构不是栈，是列表，我们要首先处理栈顶的fragment,再依次向下处理
        supportFragmentManager.fragments.asReversed().forEach {
            if (it is BackHandler && it.onBackPressed()) {
                return
            }
        }
        super.onBackPressed()
    }


    //在低内存时，保存截图到文件
    override fun onLowMemory() {
        sessionManager.onLowMemory()
        super.onLowMemory()
    }

    //加载浏览器页面
    fun loadBrowserFragment(sessionId: String, enterAnimationId: Int? = null, exitAnimationId: Int? = null) {
        if (enterAnimationId != null && exitAnimationId != null) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(enterAnimationId, exitAnimationId)
                .replace(R.id.root_container, BrowserFragment.newInstance(sessionId))
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.root_container, BrowserFragment.newInstance(sessionId))
                .commit()
        }
    }

    //加载HomeFragment
    fun loadHomeFragment(sessionId: String, enterAnimationId: Int? = null, exitAnimationId: Int? = null) {
        //跳转的时候恢复状态
        val fragment = HomeFragment.newInstance(sessionId)
        sessionManager.findSessionById(sessionId)?.apply {
            fragment.setInitialSavedState(this.homeScreenState)
        }
        if (enterAnimationId != null && exitAnimationId != null) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(enterAnimationId, exitAnimationId)
                .replace(R.id.root_container, fragment)
                .commit()
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.root_container, fragment)
                .commit()
        }
    }

    fun loadHomeOrBrowserFragment(sessionId: String, enterAnimationId: Int? = null, exitAnimationId: Int? = null) {
        val session = sessionManager.findSessionById(sessionId)
        if (session != null) {
            if (session.screenNumber == Session.HOME_SCREEN) {
                loadHomeFragment(sessionId, enterAnimationId, exitAnimationId)
            } else {
                loadBrowserFragment(sessionId, enterAnimationId, exitAnimationId)
            }
        } else {
            loadHomeFragment(HomeFragment.NO_SESSION_ID, enterAnimationId, exitAnimationId)
        }
    }

    fun popUpToHomeOrBrowserFragment() {
        val session = sessionManager.selectedSession!!
        if (session.screenNumber != Session.HOME_SCREEN) {
            val host = Uri.parse(session.url).host ?: ""
            if (session.themeColorMap.containsKey(host)) {
                StatusBarUtil.setIconColor(this, session.themeColorMap[host]!!)
            } else {
                StatusBarUtil.setIconColor(this, viewModel.theme.value!!.colorPrimary)
            }
        }
        supportFragmentManager.popBackStack()
    }

    //加载TabFragment
    fun loadTabFragment(sessionId: String, ratio: Float, enterAnimationId: Int? = null, exitAnimationId: Int? = null) {
        if (enterAnimationId != null && exitAnimationId != null) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(enterAnimationId, exitAnimationId)
                .replace(R.id.root_container, TabFragment.newInstance(sessionId, ratio))
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.root_container, TabFragment.newInstance(sessionId, ratio))
                .commit()
        }
    }

    //
    fun loadNewsFragment(enterAnimationId: Int? = null, exitAnimationId: Int? = null) {
        if (enterAnimationId != null && exitAnimationId != null) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(enterAnimationId, exitAnimationId)
                .replace(R.id.root_container, NewsFragment.newInstance())
                .commit()
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.root_container, NewsFragment.newInstance())
                .commit()
        }
    }

    //加载搜索页面
    fun loadSearchFragment(sessionId: String,color:Int?=null, enterAnimationId: Int? = null, exitAnimationId: Int? = null) {
        if (enterAnimationId != null && exitAnimationId != null) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(enterAnimationId, exitAnimationId)
                .replace(R.id.root_container, SearchFragment.newInstance(sessionId,color))
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.root_container, SearchFragment.newInstance(sessionId,color))
                .commit()
        }
    }

    //加载发现页面
    fun loadFoundFragment() {
        val fragment = FoundFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //加载历史页面
    fun loadHistoryFragment() {
        val fragment = HistoryFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //加载书签页面
    fun loadBookMarkFragment() {
        val fragment = BookMarkFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //加载主题页面
    fun loadThemeFragment() {
        val fragment = ThemeFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //加载下载页面
    fun loadDownloadFragment() {
        val fragment = DownloadFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //加载设置页面
    fun loadSettingFragment(enterAnimationId: Int=R.anim.slide_left_in,exitAnimationId: Int=R.anim.holder) {
        val fragment = SettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(enterAnimationId, exitAnimationId)
            .replace(R.id.root_container, fragment)
            .commit()

    }

    //加载阅读模式页面
    fun loadReadModeFragment(
        sessionId: String
    ) {
        val fragment = ReadModeFragment.newInstance(sessionId)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //加载图片模式页面
    fun loadImageModeFragment(
        sessionId: String
    ) {
        val fragment = ImageModeModeFragment.newInstance(sessionId)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //资源嗅探页面
    fun loadResourceFragment(
        sessionId: String
    ) {
        val fragment = ResourceFragment.newInstance(sessionId)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //Account界面
    fun loadAccountFragment() {
        val fragment = AccountFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //通用设置界面
    fun loadGeneralSettingFragment(enterAnimationId: Int=R.anim.slide_left_in,exitAnimationId: Int=R.anim.holder) {
        val fragment = GeneralSettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(enterAnimationId, exitAnimationId)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //网页设置界面
    fun loadWebSettingFragment(enterAnimationId: Int=R.anim.slide_left_in,exitAnimationId: Int=R.anim.holder) {
        val fragment = WebSettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(enterAnimationId, exitAnimationId)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //缓存设置界面
    fun loadCacheSettingFragment() {
        val fragment = CacheSettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //广告拦截设置界面
    fun loadAdBlockSettingFragment(enterAnimationId: Int=R.anim.slide_left_in,exitAnimationId: Int=R.anim.holder) {
        val fragment = AdBlockSettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(enterAnimationId, exitAnimationId)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //实验室功能设置界面
    fun loadLibrarySettingFragment() {
        val fragment = LibrarySettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //手势设置界面
    fun loadGestureSettingFragment() {
        val fragment = GestureSettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //搜索引擎设置界面
    fun loadSearchEngineSettingFragment() {
        val fragment = SearchEngineSettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //下载引擎设置界面
    fun loadDownloadSettingFragment(enterAnimationId: Int=R.anim.slide_left_in,exitAnimationId: Int=R.anim.holder) {
        val fragment = DownloadSettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(enterAnimationId, exitAnimationId)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //地址栏展示设置界面
    fun loadAddressBarSettingFragment() {
        val fragment = AddressBarSettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //视野模式设置界面
    fun loadVisionModeSettingFragment() {
        val fragment = VisionModeSettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //字体大小设置界面
    fun loadFontSizeSettingFragment() {
        val fragment = FontSizeSettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //语言设置界面
    fun loadLanguageSettingFragment() {
        val fragment = LanguageSettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //色彩风格设置界面
    fun loadColorStyleSettingFragment() {
        val fragment = ColorStyleSettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //UA设置界面
    fun loadUaSettingFragment() {
        val fragment = UaSettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //打开应用设置界面
    fun loadOpenAppSettingFragment() {
        val fragment = OpenAppSettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }


    //设置AdBlock的订阅
    fun loadAdBlockSubscriptionSettingFragment() {
        val fragment = AdBlockSubscriptionSettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //设置AdBlock的自定义过滤规则
    fun loadAdBlockFilterSettingFragment() {
        val fragment = AdBlockFilterSettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //设置AdBlock的白名单
    fun loadAdBlockWhiteListSettingFragment() {
        val fragment = AdBlockWhiteListSettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //设置AdBlock的更新选择
    fun loadAdBlockConnectionSettingFragment() {
        val fragment = AdBlockConnectionSettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //下载器设置
    fun loadDownloadManagerSettingFragment() {
        val fragment = DownloadManagerSettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    //下载路径设置
    fun loadDownloadPathSettingFragment() {
        val fragment = DownloadPathSettingFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_left_in, R.anim.holder)
            .replace(R.id.root_container, fragment)
            .commit()
    }

    fun loadScanActivity() {
        val intent = Intent(this, CaptureActivity::class.java)
        val config = ZxingConfig()
        config.isPlayBeep = true//是否播放扫描声音 默认为true
        config.isShake = true//是否震动  默认为true
        config.isDecodeBarCode = true//是否扫描条形码 默认为true
        config.reactColor = R.color.colorAccent//设置扫描框四个角的颜色 默认为白色
        config.frameLineColor = R.color.colorAccent//设置扫描框边框颜色 默认无色
        config.scanLineColor = R.color.colorAccent//设置扫描线的颜色 默认白色
        config.isFullScreenScan = false//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config)
        startActivityForResult(intent, REQUEST_CODE_SCAN)
    }

    var quitSignal: Boolean = false
    //双击退出
    fun quit() {
        if (quitSignal) {
            this.finish()
        } else {
            quitSignal = !quitSignal
            viewModel.clearQuitSignal()
            //提示
            Toast.makeText(this, R.string.quit_hint, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val content = data.getStringExtra(Constant.CODED_CONTENT)
                if (!content.isUrl()) {
                    showToast(content)
                } else {
                    tabsUseCases.addTab.invoke(
                        url = content,
                        selectTab = true,
                        startLoading = true,
                        parent = sessionManager.selectedSession
                    )
                    loadBrowserFragment(sessionManager.selectedSession!!.id)
                }
            }
        }
        //如果是安装应用
        if (requestCode == InstallAppInstance.UNKNOWN_CODE && resultCode == Activity.RESULT_OK) {
            installAppInstance?.install()
        }
    }
}
