package com.dev.orangebrowser.extension

import androidx.appcompat.app.AppCompatActivity
import com.dev.orangebrowser.AndroidApplication
import com.dev.orangebrowser.data.model.ApplicationData
import com.dev.orangebrowser.di.ApplicationComponent

val AppCompatActivity.appComponent: ApplicationComponent
    get() = (applicationContext as AndroidApplication).appComponent

val AppCompatActivity.appData: ApplicationData
    get() = (applicationContext as AndroidApplication).applicationData

val AppCompatActivity.myApplication:AndroidApplication
    get() = (applicationContext as AndroidApplication)