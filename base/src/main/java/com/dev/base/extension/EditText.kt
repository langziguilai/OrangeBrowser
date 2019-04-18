package com.dev.base.extension

import android.view.inputmethod.EditorInfo
import android.widget.EditText

//在layout之后调用：可以获取View的Height,Width等等属性
fun EditText.updateIme(ime:Int){
    imeOptions=ime
    setSingleLine(true)
    inputType= EditorInfo.TYPE_CLASS_TEXT
}