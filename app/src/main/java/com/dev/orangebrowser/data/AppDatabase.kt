package com.dev.orangebrowser.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dev.orangebrowser.data.dao.*
import com.dev.orangebrowser.data.model.*

@Database(
    entities = [
        SiteCategory::class,
        MainPageSite::class,
        FavoriteSite::class,
        CommonSite::class,
        NewRecommendedSite::class,
        AdBlockFilter::class,
        SavedFile::class
    ], version = 2, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    //    abstract fun favoriteSiteDao(): FavoriteSiteDao
    abstract fun commonSiteDao(): CommonSiteDao
    //    abstract fun newRecommendedDao(): NewRecommendedDao
    abstract fun siteCategoryDao(): SiteCategoryDao
    abstract fun mainPageSiteDao(): MainPageSiteDao
    abstract fun adBlockFilterDao(): AdBlockFilterDao
    abstract fun savedFileDao(): SavedFileDao
}