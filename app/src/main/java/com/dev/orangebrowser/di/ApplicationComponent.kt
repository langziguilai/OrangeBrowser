package com.dev.orangebrowser.di


import com.dev.orangebrowser.AndroidApplication
import com.dev.orangebrowser.bloc.bookmark.BookMarkFragment
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.download.DownloadFragment
import com.dev.orangebrowser.bloc.found.FoundFragment
import com.dev.orangebrowser.bloc.history.HistoryFragment
import com.dev.orangebrowser.bloc.home.HomeFragment
import com.dev.orangebrowser.bloc.host.MainActivity
import com.dev.orangebrowser.bloc.imageMode.ImageModeModeFragment
import com.dev.orangebrowser.bloc.news.NewsFragment
import com.dev.orangebrowser.bloc.readMode.ReadModeFragment
import com.dev.orangebrowser.bloc.resource.ResourceFragment
import com.dev.orangebrowser.bloc.scan.ScanFragment
import com.dev.orangebrowser.bloc.search.SearchFragment
import com.dev.orangebrowser.bloc.setting.SettingFragment
import com.dev.orangebrowser.bloc.setting.fragments.*
import com.dev.orangebrowser.bloc.sourcecode.SourceCodeFragment
import com.dev.orangebrowser.bloc.tabs.TabFragment
import com.dev.orangebrowser.bloc.theme.ThemeFragment
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
    fun inject(foundFragment: FoundFragment)
    fun inject(historyFragment: HistoryFragment)
    fun inject(bookMarkFragment: BookMarkFragment)
    fun inject(themeFragment: ThemeFragment)
    fun inject(downloadFragment: DownloadFragment)
    fun inject(settingFragment: SettingFragment)
    fun inject(scanFragment: ScanFragment)
    fun inject(readModeFragment: ReadModeFragment)
    fun inject(imageModeModeFragment: ImageModeModeFragment)
    fun inject(sourceCodeFragment: SourceCodeFragment)
    fun inject(resourceFragment: ResourceFragment)
    fun inject(accountFragment: AccountFragment)
    fun inject(generalFragment: GeneralSettingFragment)
    fun inject(webSettingFragment: WebSettingFragment)
    fun inject(cacheSettingFragment: CacheSettingFragment)
    fun inject(searchEngineSettingFragment: SearchEngineSettingFragment)
    fun inject(librarySettingFragment: LibrarySettingFragment)
    fun inject(gestureSettingFragment: GestureSettingFragment)
    fun inject(adBlockSettingFragment: AdBlockSettingFragment)
    fun inject(downloadSettingFragment: DownloadSettingFragment)
    fun inject(addressBarSettingFragment: AddressBarSettingFragment)
    fun inject(visionModeSettingFragment: VisionModeSettingFragment)
    fun inject(fontSizeSettingFragment: FontSizeSettingFragment)
    fun inject(languageSettingFragment: LanguageSettingFragment)
    fun inject(colorStyleSettingFragment: ColorStyleSettingFragment)
    fun inject(uaSettingFragment: UaSettingFragment)
    fun inject(openAppSettingFragment: OpenAppSettingFragment)
}
