/**
 * Copyright (C) 2018 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dev.orangebrowser.di.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dev.orangebrowser.bloc.bookmark.BookMarkViewModel
import com.dev.orangebrowser.bloc.browser.BrowserViewModel
import com.dev.orangebrowser.bloc.download.DownloadViewModel
import com.dev.orangebrowser.bloc.download.html.DownloadHtmlViewModel
import com.dev.orangebrowser.bloc.download.image.DownloadImageViewModel
import com.dev.orangebrowser.bloc.download.video.DownloadVideoViewModel
import com.dev.orangebrowser.bloc.found.FoundViewModel
import com.dev.orangebrowser.bloc.history.HistoryViewModel
import com.dev.orangebrowser.bloc.home.HomeViewModel
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.bloc.imageMode.ImageModeViewModel
import com.dev.orangebrowser.bloc.news.NewsViewModel
import com.dev.orangebrowser.bloc.readMode.ReadModeViewModel
import com.dev.orangebrowser.bloc.resource.ResourceViewModel
import com.dev.orangebrowser.bloc.search.SearchViewModel
import com.dev.orangebrowser.bloc.setting.SettingViewModel
import com.dev.orangebrowser.bloc.tabs.TabViewModel
import com.dev.orangebrowser.bloc.theme.ThemeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindsHomeViewModel(homeViewModel: HomeViewModel): ViewModel
    //
    @Binds
    @IntoMap
    @ViewModelKey(BrowserViewModel::class)
    abstract fun bindsBrowserViewModel(browserViewModel: BrowserViewModel): ViewModel
    //
    @Binds
    @IntoMap
    @ViewModelKey(TabViewModel::class)
    abstract fun bindsTabViewModel(tabViewModel: TabViewModel): ViewModel
    //
    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindsSearchViewModel(searchViewModel: SearchViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(NewsViewModel::class)
    abstract fun bindsNewsViewModel(newsViewModel: NewsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FoundViewModel::class)
    abstract fun bindsFoundViewModel(foundViewModel: FoundViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(HistoryViewModel::class)
    abstract fun bindsHistoryViewModel(historyViewModel: HistoryViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(BookMarkViewModel::class)
    abstract fun bindsBookMarkViewModel(bookMarkViewModel: BookMarkViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(ThemeViewModel::class)
    abstract fun bindsThemeViewModel(themeViewModel: ThemeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DownloadViewModel::class)
    abstract fun bindsDownloadViewModel(downloadViewModel: DownloadViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(DownloadImageViewModel::class)
    abstract fun bindsDownloadImageViewModel(downloadImageViewModel: DownloadImageViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(DownloadHtmlViewModel::class)
    abstract fun bindsDownloadHtmlViewModel(downloadHtmlViewModel: DownloadHtmlViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(DownloadVideoViewModel::class)
    abstract fun bindsDownloadVideoViewModel(downloadVideoViewModel: DownloadVideoViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(SettingViewModel::class)
    abstract fun bindsSettingViewModel(settingViewModel:SettingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReadModeViewModel::class)
    abstract fun bindsReadModeViewModel(readModeViewModel: ReadModeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ImageModeViewModel::class)
    abstract fun bindsImageModeViewModel(imageModeViewModel: ImageModeViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(ResourceViewModel::class)
    abstract fun bindsResourceViewModel(resourceViewModel: ResourceViewModel): ViewModel
}