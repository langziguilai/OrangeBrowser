package com.dev.orangebrowser.bloc.display.video

import androidx.lifecycle.MutableLiveData
import com.dev.base.CoroutineViewModel
import javax.inject.Inject

class VideoDisplayViewModel @Inject constructor() : CoroutineViewModel() {
     val colorPrimary=MutableLiveData<Int>()
     fun changeColor(color:Int){
         colorPrimary.value=color
     }
}
