package com.dev.orangebrowser.bloc.setting.fragments.adblock

import android.os.Bundle
import android.util.Log
import com.dev.base.BaseFragment
import com.dev.base.support.BackHandler
import com.dev.browser.adblock.setting.AdBlockSettings
import com.dev.browser.adblock.setting.AdBlockSettingsStorage
import org.adblockplus.libadblockplus.android.AdblockEngine
import javax.inject.Inject

abstract class BaseAdBlockSettingFragment : BaseFragment(), BackHandler {

    var settings: AdBlockSettings? = null
    @Inject
    lateinit var adBlockEngine: AdblockEngine
    @Inject
    lateinit var adBlockSettingsStorage: AdBlockSettingsStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadSettings()
    }



    override fun onResume() {
        super.onResume()
        loadSettings()
    }



    private fun loadSettings() {
        settings = adBlockSettingsStorage.load()
        if (settings == null) {
            Log.w("", "No adblock settings, yet. Using defdault ones from adblock engine")
            // null because it was not saved yet
            settings = AdBlockSettingsStorage.getDefaultSettings(adBlockEngine)
        }
    }
}
