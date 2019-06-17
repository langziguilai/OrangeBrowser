package com.dev.orangebrowser.bloc.theme

import com.dev.base.CoroutineViewModel
import com.dev.orangebrowser.data.model.Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch
import javax.inject.Inject

class ThemeViewModel @Inject constructor() : CoroutineViewModel() {
     fun saveTheme(theme:Theme)=launch(Dispatchers.IO) {

     }
}
