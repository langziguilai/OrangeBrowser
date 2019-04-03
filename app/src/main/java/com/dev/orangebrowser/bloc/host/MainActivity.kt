package com.dev.orangebrowser.bloc.host


import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.dev.base.BaseActivity
import com.dev.base.support.BackHandler
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.home.HomeFragment
import com.dev.orangebrowser.bloc.news.NewsFragment
import com.dev.orangebrowser.bloc.tabs.TabFragment
import com.dev.orangebrowser.extension.appComponent
import javax.inject.Inject

class MainActivity : BaseActivity() {
//    @Inject
//    lateinit var sessionManager:SessionManager
    lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        super.onCreate(savedInstanceState)
    }
    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }

    override fun initView(savedInstanceState: Bundle?) {
//        val currentSession=sessionManager.currentSessionOrNewOne()
//        if (currentSession.isHome){
//            loadHomeFragment(currentSession.id)
//        }else{
//            loadBrowserFragment(currentSession.id)
//        }
    }
    override fun initData(savedInstanceState: Bundle?) {
        //do nothing
    }
    override fun onBackPressed() {
        supportFragmentManager.fragments.forEach {
            if (it is BackHandler && it.onBackPressed()){
                return
            }
        }
        super.onBackPressed()
    }

    //加载浏览器页面
    fun loadBrowserFragment(sessionId:String){
        supportFragmentManager.beginTransaction().replace(R.id.container, BrowserFragment.newInstance(sessionId)).commit()
    }

    //加载HomeFragment
    fun loadHomeFragment(sessionId:String){
        supportFragmentManager.beginTransaction().replace(R.id.container, HomeFragment.newInstance(sessionId)).commit()
    }
    //加载TabFragment
    fun loadTabFragment(){
        supportFragmentManager.beginTransaction().replace(R.id.container, TabFragment.newInstance()).commit()
    }
    //
    fun loadNewsFragment(){
        supportFragmentManager.beginTransaction().replace(R.id.container, NewsFragment.newInstance()).addToBackStack(null).commit()
    }
}
