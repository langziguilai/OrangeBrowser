package com.dev.orangebrowser.bloc.host


import android.Manifest
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
import com.dev.orangebrowser.bloc.scan.ScanFragment
import com.dev.orangebrowser.bloc.search.SearchFragment
import com.dev.orangebrowser.bloc.setting.SettingFragment
import com.dev.orangebrowser.bloc.sourcecode.SourceCodeFragment
import com.dev.orangebrowser.bloc.tabs.TabFragment
import com.dev.orangebrowser.bloc.theme.ThemeFragment
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.extension.myApplication
import com.dev.view.StatusBarUtil
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import permissions.dispatcher.*
import javax.inject.Inject

@RuntimePermissions
class MainActivity : BaseActivity() {


    @Inject
    lateinit var sessionManager: SessionManager
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
        if (savedInstanceState != null) {

        } else {
            viewModel.appData.observe(this, Observer {
                it.either(fun(failure) {
                    Toasty.error(this, failure.error, Toast.LENGTH_SHORT).show()
                }, fun(data) {
                    myApplication.initApplicationData(data)
                    //loadFirstInFragment()
                    loadFirstInFragmentWithPermissionCheck()
                })
            })
            viewModel.theme.observe(this, Observer {
                StatusBarUtil.setStatusBarBackGroundColorAndIconColor(this, it.colorPrimaryDark)
            })
            viewModel.quitSignalClear.observe(this, Observer {
                quitSignal = it
            })
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
        Manifest.permission.RECORD_AUDIO)
    fun showRationale(request: PermissionRequest) {
        //showRationaleDialog(R.string.permission_camera_rationale, request)
        Toast.makeText(this,"showRationale", Toast.LENGTH_SHORT).show()
    }

    @OnPermissionDenied(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.RECORD_AUDIO)
    fun onDenied() {
        Toast.makeText(this,"onDenied", Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.RECORD_AUDIO)
    fun onNeverAskAgain() {
        Toast.makeText(this, "onNeverAskAgain", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
    override fun initData(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            viewModel.loadAppData()
        }
    }

    override fun onBackPressed() {
        supportFragmentManager.fragments.forEach {
            if (it is BackHandler && it.onBackPressed()) {
                return
            }
        }
        super.onBackPressed()
    }


    //在低内存时，保存截图到文件
    override fun onLowMemory() {
        super.onLowMemory()
        sessionManager.onLowMemory()
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
    fun loadTabFragment(sessionId: String,ratio:Float) {
        supportFragmentManager.beginTransaction().replace(R.id.container, TabFragment.newInstance(sessionId,ratio)).commit()
    }

    //
    fun loadNewsFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, NewsFragment.newInstance())
            .addToBackStack(null).commit()
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

    //加载扫码页面
    fun loadScanFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, ScanFragment.newInstance()).commit()
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

    //加载源代码页面
    fun loadSourceCodeFragment(sessionId: String) {
        supportFragmentManager.beginTransaction().replace(R.id.container, SourceCodeFragment.newInstance(sessionId))
            .commit()
    }

    //资源嗅探页面
    fun loadResourceFragment(sessionId: String) {
        supportFragmentManager.beginTransaction().replace(R.id.container, ResourceFragment.newInstance(sessionId))
            .commit()
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
}
