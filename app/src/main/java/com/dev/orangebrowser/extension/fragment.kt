package com.dev.orangebrowser.extension

import com.dev.orangebrowser.AndroidApplication
import com.dev.orangebrowser.bloc.host.MainActivity
import com.dev.orangebrowser.data.model.ApplicationData
import com.dev.orangebrowser.di.ApplicationComponent

val androidx.fragment.app.Fragment.appComponent: ApplicationComponent
            get() = (requireContext().applicationContext as AndroidApplication).appComponent



val androidx.fragment.app.Fragment.appData: ApplicationData
    get() = (requireContext().applicationContext as AndroidApplication).applicationData

val androidx.fragment.app.Fragment.MainActivity: MainActivity?
    get(){
        if (activity is MainActivity){
            return activity as MainActivity
        }
        return null
    }

val androidx.fragment.app.Fragment.application:AndroidApplication
    get() = requireContext().applicationContext as AndroidApplication

