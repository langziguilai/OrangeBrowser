package com.dev.orangebrowser.di

import android.content.Context
import com.dev.browser.concept.Engine
import com.dev.browser.concept.fetch.Client
import com.dev.browser.concept.history.HistoryTrackingDelegate
import com.dev.browser.concept.storage.HistoryStorage
import com.dev.browser.engine.SystemEngine
import com.dev.browser.feature.search.SearchUseCases
import com.dev.browser.feature.session.HistoryDelegate
import com.dev.browser.feature.session.SessionUseCases
import com.dev.browser.feature.tabs.TabsUseCases
import com.dev.browser.fetch.OkHttpClient
import com.dev.browser.search.SearchEngineManager
import com.dev.browser.session.SessionManager
import com.dev.browser.storage.local.PlacesHistoryStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BrowserModule {

    @Provides
    @Singleton
    fun provideSessionManager(context: Context,engine: Engine): SessionManager {
        return SessionManager(engine)
    }
    @Provides
    @Singleton
    fun provideEngine(context: Context,historyTrackingDelegate:HistoryTrackingDelegate):Engine{
        //TODO:默认的Setting
        return SystemEngine(context).apply {
            settings.historyTrackingDelegate=historyTrackingDelegate
        }
    }
    @Provides
    @Singleton
    fun provideSessionUseCases(sessionManager: SessionManager): SessionUseCases {
        return SessionUseCases(sessionManager)
    }
    @Provides
    @Singleton
    fun provideTabsUseCases(sessionManager: SessionManager): TabsUseCases {
        return TabsUseCases(sessionManager)
    }
    @Provides
    @Singleton
    fun provideSearchEngineManager(): SearchEngineManager {
        return SearchEngineManager()
    }
    @Provides
    @Singleton
    fun provideSearchUseCases(context: Context,sessionManager: SessionManager,searchEngineManager: SearchEngineManager): SearchUseCases {
        return SearchUseCases(context=context,searchEngineManager=searchEngineManager,sessionManager=sessionManager)
    }
    @Provides
    @Singleton
    fun provideClient(context: Context): Client {
        return OkHttpClient(context = context)
    }
    @Provides
    @Singleton
    fun provideHistoryStorage(context: Context): HistoryStorage {
        return PlacesHistoryStorage(context = context)
    }
    @Provides
    @Singleton
    fun provideHistoryTrackingDelegate(historyStorage: HistoryStorage): HistoryTrackingDelegate {
        return HistoryDelegate(historyStorage)
    }
}
