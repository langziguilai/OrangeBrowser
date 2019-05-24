package com.dev.orangebrowser.bloc.host


import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dev.base.BaseActivity
import com.dev.base.support.BackHandler
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
import com.dev.orangebrowser.data.model.*
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.extension.appData
import com.dev.orangebrowser.extension.myApplication
import com.dev.view.NavigationBarUtil
import com.dev.view.StatusBarUtil
import com.yzq.zxinglibrary.android.CaptureActivity
import com.yzq.zxinglibrary.bean.ZxingConfig
import com.yzq.zxinglibrary.common.Constant
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import permissions.dispatcher.*
import javax.inject.Inject
import android.app.Activity
import android.content.Context
import com.dev.base.extension.showToast
import com.dev.base.support.isUrl
import com.dev.browser.feature.tabs.TabsUseCases
import java.util.*


const val APPLICATION_DATA="application_data"

@RuntimePermissions
class MainActivity : BaseActivity() {

    companion object{
        const val REQUEST_CODE_SCAN=0x1234
    }

    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var tabsUseCases: TabsUseCases
    lateinit var viewModel: MainViewModel
    lateinit var mOrientationDetector: OrientationDetector
    //是否可以自动旋转屏幕，默认可以
    var enableAutoOrientation: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        super.onCreate(savedInstanceState)
        //每隔1s，设置Orientation
        launch(Dispatchers.IO) {
            while (true) {
                launch(Dispatchers.Main) {
                    if (enableAutoOrientation) {
                        if (requestedOrientation != mOrientationDetector.orientationType) {
                            requestedOrientation = mOrientationDetector.orientationType
                        }
                    }
                }
                delay(1000)
            }
        }
        mOrientationDetector = OrientationDetector(30, contentResolver, this)
    }

    override fun onResume() {
        super.onResume()
        if (mOrientationDetector.canDetectOrientation()) {
            mOrientationDetector.enable()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mOrientationDetector.canDetectOrientation()) {
            mOrientationDetector.disable()
        }
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
        StatusBarUtil.setStatusBarBackGroundColorAndIconColor(this, color)
        NavigationBarUtil.setNavigationBarColor(this, color)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (appData!=null){
            outState.putParcelable(APPLICATION_DATA, appData)
        }
        super.onSaveInstanceState(outState)
    }

    override fun initData(savedInstanceState: Bundle?) {
//        //如果是全屏模式，则隐藏StatusBar
//        if(!getPreferences(Context.MODE_PRIVATE).getBoolean(getString(R.string.pref_setting_full_screen),false)){
//            StatusBarUtil.hideStatusBar(this)
//        }
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
    fun loadBrowserFragment(sessionId: String) {
        supportFragmentManager.beginTransaction().replace(R.id.container, BrowserFragment.newInstance(sessionId))
            .commit()
    }

    //加载HomeFragment
    fun loadHomeFragment(sessionId: String) {
        //跳转的时候恢复状态
        val fragment = HomeFragment.newInstance(sessionId)
        sessionManager.findSessionById(sessionId)?.apply {
            fragment.setInitialSavedState(this.homeScreenState)
        }
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    fun loadHomeOrBrowserFragment(sessionId: String) {
        val session = sessionManager.findSessionById(sessionId)
        if (session != null) {
            if (session.screenNumber == Session.HOME_SCREEN) {
                loadHomeFragment(sessionId)
            } else {
                loadBrowserFragment(sessionId)
            }
        } else {
            loadHomeFragment(HomeFragment.NO_SESSION_ID)
        }
    }

    //加载TabFragment
    fun loadTabFragment(sessionId: String, ratio: Float) {
        supportFragmentManager.beginTransaction().replace(R.id.container, TabFragment.newInstance(sessionId, ratio))
            .commit()
    }

    //
    fun loadNewsFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, NewsFragment.newInstance()).commit()
    }

    //加载搜索页面
    fun loadSearchFragment(sessionId: String) {
        supportFragmentManager.beginTransaction().replace(R.id.container, SearchFragment.newInstance(sessionId))
            .commit()
    }

    //加载发现页面
    fun loadFoundFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, FoundFragment.newInstance()).commit()
    }

    //加载历史页面
    fun loadHistoryFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, HistoryFragment.newInstance()).commit()
    }

    //加载书签页面
    fun loadBookMarkFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, BookMarkFragment.newInstance()).commit()
    }

    //加载主题页面
    fun loadThemeFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, ThemeFragment.newInstance()).commit()
    }

    //加载下载页面
    fun loadDownloadFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, DownloadFragment.newInstance()).commit()
    }

    //加载设置页面
    fun loadSettingFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, SettingFragment.newInstance()).commit()
    }

    //加载阅读模式页面
    fun loadReadModeFragment(sessionId: String) {
        supportFragmentManager.beginTransaction().replace(R.id.container, ReadModeFragment.newInstance(sessionId))
            .commit()
    }

    //加载图片模式页面
    fun loadImageModeFragment(sessionId: String) {
        supportFragmentManager.beginTransaction().replace(R.id.container, ImageModeModeFragment.newInstance(sessionId))
            .commit()
    }

    //资源嗅探页面
    fun loadResourceFragment(sessionId: String) {
        supportFragmentManager.beginTransaction().replace(R.id.container, ResourceFragment.newInstance(sessionId))
            .commit()
    }

    //Account界面
    fun loadAccountFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, AccountFragment.newInstance()).commit()
    }

    //通用设置界面
    fun loadGeneralSettinglFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, GeneralSettingFragment.newInstance()).commit()
    }

    //网页设置界面
    fun loadWebSettinglFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, WebSettingFragment.newInstance()).commit()
    }

    //缓存设置界面
    fun loadCacheSettinglFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, CacheSettingFragment.newInstance())
            .commit()
    }

    //广告拦截设置界面
    fun loadAdBlockSettinglFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, AdBlockSettingFragment.newInstance()).commit()
    }

    //实验室功能设置界面
    fun loadLibrarySettinglFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, LibrarySettingFragment.newInstance()).commit()
    }

    //手势设置界面
    fun loadGestureSettinglFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, GestureSettingFragment.newInstance()).commit()
    }

    //搜索引擎设置界面
    fun loadSearchEngineSettingFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, SearchEngineSettingFragment.newInstance())
            .commit()
    }

    //搜索引擎设置界面
    fun loadDownloadSettingFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, DownloadSettingFragment.newInstance())
            .commit()
    }

    //地址栏展示设置界面
    fun loadAddressBarSettingFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, AddressBarSettingFragment.newInstance())
            .commit()
    }

    //视野模式设置界面
    fun loadVisionModeSettingFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, VisionModeSettingFragment.newInstance())
            .commit()
    }

    //字体大小设置界面
    fun loadFontSizeSettingFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, FontSizeSettingFragment.newInstance())
            .commit()
    }

    //语言设置界面
    fun loadLanguageSettingFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, LanguageSettingFragment.newInstance())
            .commit()
    }

    //色彩风格设置界面
    fun loadColorStyleSettingFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, ColorStyleSettingFragment.newInstance())
            .commit()
    }

    //UA设置界面
    fun loadUaSettingFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, UaSettingFragment.newInstance()).commit()
    }

    //打开应用设置界面
    fun loadOpenAppSettingFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, OpenAppSettingFragment.newInstance()).commit()
    }


    //设置AdBlock的订阅
    fun loadAdBlockSubscriptionSettingFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, AdBlockSubscriptionSettingFragment.newInstance())
            .commit()
    }

    //设置AdBlock的自定义过滤规则
    fun loadAdBlockFilterSettingFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, AdBlockFilterSettingFragment.newInstance())
            .commit()
    }

    //设置AdBlock的白名单
    fun loadAdBlockWhiteListSettingFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, AdBlockWhiteListSettingFragment.newInstance())
            .commit()
    }

    //设置AdBlock的更新选择
    fun loadAdBlockConnectionSettingFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, AdBlockConnectionSettingFragment.newInstance())
            .commit()
    }

    //下载器设置
    fun loadDownloadManagerSettingFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, DownloadManagerSettingFragment.newInstance())
            .commit()
    }

    //下载路径设置
    fun loadDownloadPathSettingFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, DownloadPathSettingFragment.newInstance())
            .commit()
    }
    fun loadScanActivity(){
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
                if(!content.isUrl()){
                    showToast(content)
                }else{
                    tabsUseCases.addTab.invoke(url=content,selectTab = true,startLoading = true,parent = sessionManager.selectedSession)
                    loadBrowserFragment(sessionManager.selectedSession!!.id)
                }
            }
        }
    }
}
