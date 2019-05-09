/*
 * This file is part of Adblock Plus <https://adblockplus.org/>,
 * Copyright (C) 2006-present eyeo GmbH
 *
 * Adblock Plus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * Adblock Plus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Adblock Plus.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dev.browser.adblock.setting

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

import androidx.preference.PreferenceFragmentCompat
import org.adblockplus.libadblockplus.android.AdblockEngine

abstract class BaseSettingsFragment<ListenerClass : BaseSettingsFragment.Listener> : Fragment() {

    protected var TAG = this.javaClass.simpleName
    var settings: AdblockSettings? = null
    protected var provider: Provider? = null
    protected var listener: ListenerClass? = null

    /**
     * Provides AdblockEngine and SharedPreferences to store settings
     * (activity holding BaseSettingsPreferenceFragment fragment should implement this interface)
     */
    interface Provider {
        val adblockEngine: AdblockEngine

        val adblockSettingsStorage: AdblockSettingsStorage
    }

    /**
     * Listens for Adblock settings events
     */
    interface Listener {
        /**
         * `Settings were changed` callback
         * Note: settings are available using BaseSettingsPreferenceFragment.getSettings()
         *
         * @param fragment fragment
         */
        fun onAdblockSettingsChanged(fragment: BaseSettingsFragment<*>)
    }

    protected fun <T> castOrThrow(activity: Activity, clazz: Class<T>): T {
        if (activity !is Provider) {
            val message = (activity.javaClass.simpleName
                    + " should implement "
                    + clazz.simpleName
                    + " interface")

            Log.e("TAG", message)
            throw RuntimeException(message)
        }

        return activity as T
    }

    fun loadSettings() {
        settings = provider!!.adblockSettingsStorage.load()
        if (settings == null) {
            Log.w("", "No adblock settings, yet. Using defdault ones from adblock engine")

            // null because it was not saved yet
            settings = AdblockSettingsStorage.getDefaultSettings(provider!!.adblockEngine)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadSettings()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        provider = castOrThrow(activity, Provider::class.java)
    }

    override fun onResume() {
        super.onResume()
        loadSettings()
    }

    override fun onDetach() {
        super.onDetach()
        provider = null
        listener = null
    }
}
