package com.dev.orangebrowser.view.contextmenu

interface Action<T> {
    fun execute(data: T)
}