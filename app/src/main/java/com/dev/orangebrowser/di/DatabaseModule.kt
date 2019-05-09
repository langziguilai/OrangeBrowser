package com.dev.orangebrowser.di

import android.content.Context
import androidx.room.Room
import com.dev.orangebrowser.data.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    //返回appdatabase
    @Provides @Singleton fun provideAppDatabase(applicationContext: Context): AppDatabase {
        return  Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "orange_browser_db1"
        ).build()
    }

}
