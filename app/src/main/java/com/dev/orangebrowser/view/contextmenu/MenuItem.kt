package com.dev.orangebrowser.view.contextmenu

data class MenuItem(
    var label: String,
    var icon: String? = null,
    var iconColor: Int = -1,
    var labelColor: Int = -1,
    var key: String = "",
    var action: Action<MenuItem>? = null
)