package com.dev.orangebrowser.di

import android.content.Context
import androidx.room.Room
import com.dev.browser.concept.Engine
import com.dev.browser.engine.SystemEngine
import com.dev.browser.feature.SessionUseCases
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.data.AppDatabase
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
    fun provideEngine(context: Context):Engine{
        //TODO:默认的Setting
        return SystemEngine(context)
    }
    @Provides
    @Singleton
    fun provideSessionUseCases(sessionManager: SessionManager):SessionUseCases{
        return SessionUseCases(sessionManager)
    }
}
