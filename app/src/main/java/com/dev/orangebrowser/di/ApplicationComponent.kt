package com.dev.orangebrowser.di


import com.dev.orangebrowser.AndroidApplication
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.home.HomeFragment
import com.dev.orangebrowser.bloc.host.MainActivity
import com.dev.orangebrowser.bloc.news.NewsFragment
import com.dev.orangebrowser.bloc.search.SearchFragment
import com.dev.orangebrowser.bloc.tabs.TabFragment
import com.dev.orangebrowser.di.viewmodel.ViewModelModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, ViewModelModule::class,DatabaseModule::class,BrowserModule::class,NetworkModule::class])
interface ApplicationComponent {
    fun inject(application: AndroidApplication)
    fun inject(mainActivity: MainActivity)
//

    fun inject(homeFragment: HomeFragment)
    fun inject(newsFragment: NewsFragment)
    fun inject(browserFragment: BrowserFragment)
    fun inject(searchFragment: SearchFragment)
    fun inject(tabFragment: TabFragment)
}
