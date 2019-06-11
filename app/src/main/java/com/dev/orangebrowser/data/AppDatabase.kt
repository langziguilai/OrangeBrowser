package com.dev.orangebrowser.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dev.orangebrowser.data.dao.AdBlockFilterDao
import com.dev.orangebrowser.data.dao.SavedFileDao
import com.dev.orangebrowser.data.model.*

@Database(
    entities = [
        FavoriteSite::class,
        CommonSite::class,
        NewRecommendedSite::class,
        AdBlockFilter::class,
        SavedFile::class
    ], version = 1,exportSchema=false
)
abstract class AppDatabase : RoomDatabase() {
    //    abstract fun favoriteSiteDao(): FavoriteSiteDao
//    abstract fun commonSiteDao(): CommonSiteDao
//    abstract fun newRecommendedDao(): NewRecommendedDao
    abstract fun adBlockFilterDao(): AdBlockFilterDao
    abstract fun savedFileDao(): SavedFileDao
}