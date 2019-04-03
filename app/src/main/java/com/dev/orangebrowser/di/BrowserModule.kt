package com.dev.orangebrowser.di

import android.content.Context
import androidx.room.Room
import com.dev.orangebrowser.data.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BrowserModule {
    //返回appdatabase
    @Provides @Singleton fun provideAppDatabase(applicationContext:Context): AppDatabase {
        return  Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "quicker-version_2"
        ).build()
    }
//    @Provides
//    @Singleton
//    fun provideFavoriteSiteDao(appDatabase: AppDatabase): FavoriteSiteDao {
//        return appDatabase.favoriteSiteDao()
//    }
}
