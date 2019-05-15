package com.dev.orangebrowser.di

import android.content.Context
import androidx.room.Room
import com.dev.browser.database.BrowserDatabase
import com.dev.browser.database.history.VisitHistoryDao
import com.dev.orangebrowser.data.AppDatabase
import com.dev.orangebrowser.data.dao.AdBlockFilterDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    //返回appdatabase
    @Provides
    @Singleton
    fun provideAppDatabase(applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "orange_browser_db2"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAdBlockFilterDao(appDatabase: AppDatabase): AdBlockFilterDao {
        return appDatabase.adBlockFilterDao()
    }
    @Provides
    @Singleton
    fun provideBrowserDatabase(applicationContext: Context): BrowserDatabase {
        return BrowserDatabase.get(applicationContext.applicationContext)
    }
    @Provides
    @Singleton
    fun provideHistoryDao(browserDatabase: BrowserDatabase): VisitHistoryDao {
        return browserDatabase.historyDao()
    }
}
