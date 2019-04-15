package com.dev.orangebrowser.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dev.orangebrowser.data.model.CommonSite
import com.dev.orangebrowser.data.model.FavoriteSite
import com.dev.orangebrowser.data.model.NewRecommendedSite

@Database(entities = [
    FavoriteSite::class,
    CommonSite::class,
    NewRecommendedSite::class
], version = 1)
abstract class AppDatabase : RoomDatabase() {
//    abstract fun favoriteSiteDao(): FavoriteSiteDao
//    abstract fun commonSiteDao(): CommonSiteDao
//    abstract fun newRecommendedDao(): NewRecommendedDao
}