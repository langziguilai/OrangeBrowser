package com.dev.orangebrowser

import com.dev.base.BaseApplication
import com.dev.orangebrowser.data.model.ApplicationData
import com.dev.orangebrowser.di.ApplicationComponent
import com.dev.orangebrowser.di.ApplicationModule
import com.dev.orangebrowser.di.DaggerApplicationComponent
import com.dev.orangebrowser.di.DatabaseModule

class AndroidApplication:BaseApplication() {
    val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .databaseModule(DatabaseModule())
            .build()
    }
    //全局数据
    lateinit var applicationData: ApplicationData

    fun initApplicationData(data:ApplicationData){applicationData=data}
    override fun injectMembers() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}