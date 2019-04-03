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
import com.dev.orangebrowser.bloc.browser.BrowserViewModel
import com.dev.orangebrowser.bloc.home.HomeViewModel
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.bloc.news.NewsViewModel
import com.dev.orangebrowser.bloc.search.SearchViewModel
import com.dev.orangebrowser.bloc.tabs.TabViewModel
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
}