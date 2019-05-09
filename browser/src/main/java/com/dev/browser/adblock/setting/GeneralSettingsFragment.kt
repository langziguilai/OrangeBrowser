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
import androidx.preference.ListPreference
import androidx.preference.MultiSelectListPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreference

import com.dev.browser.R
import org.adblockplus.libadblockplus.android.AdblockEngine
import org.adblockplus.libadblockplus.android.ConnectionType
import org.adblockplus.libadblockplus.android.Subscription

import java.util.HashSet
import java.util.LinkedList

/**
 * General Adblock settings fragment.
 * Use the [GeneralSettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GeneralSettingsFragment(
    override val adblockEngine: AdblockEngine,
    override val adblockSettingsStorage: AdblockSettingsStorage
) : BaseSettingsPreferenceFragment<GeneralSettingsFragment.Listener>(),
    Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener,BaseSettingsPreferenceFragment.Provider {
    private var SETTINGS_ENABLED_KEY: String? = null
    private var SETTINGS_FILTER_LISTS_KEY: String? = null
    private var SETTINGS_AA_ENABLED_KEY: String? = null
    private var SETTINGS_WL_DOMAINS_KEY: String? = null
    private var SETTINGS_ALLOWED_CONNECTION_TYPE_KEY: String? = null

    private var adblockEnabled: SwitchPreference? = null
    private var filterLists: MultiSelectListPreference? = null
    private var acceptableAdsEnabled: SwitchPreference? = null
    private var whitelistedDomains: Preference? = null
    private var allowedConnectionType: ListPreference? = null

    /**
     * Listener with additional `onWhitelistedDomainsClicked` event
     */
    interface Listener : BaseSettingsPreferenceFragment.Listener {
        fun onWhitelistedDomainsClicked(fragment: GeneralSettingsFragment)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        //listener =
        provider=this
    }

    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        readKeys()

        addPreferencesFromResource(R.xml.preference_adblock_general)
        bindPreferences()
    }

    override fun onResume() {
        super.onResume()
        initPreferences()
    }

    private fun readKeys() {
        SETTINGS_ENABLED_KEY = getString(R.string.fragment_adblock_settings_enabled_key)
        SETTINGS_FILTER_LISTS_KEY = getString(R.string.fragment_adblock_settings_filter_lists_key)
        SETTINGS_AA_ENABLED_KEY = getString(R.string.fragment_adblock_settings_aa_enabled_key)
        SETTINGS_WL_DOMAINS_KEY = getString(R.string.fragment_adblock_settings_wl_key)
        SETTINGS_ALLOWED_CONNECTION_TYPE_KEY = getString(R.string.fragment_adblock_settings_allowed_connection_type_key)
    }

    private fun bindPreferences() {
        adblockEnabled = findPreference<Preference>(SETTINGS_ENABLED_KEY!!) as SwitchPreference?
        filterLists = findPreference<Preference>(SETTINGS_FILTER_LISTS_KEY!!) as MultiSelectListPreference?
        acceptableAdsEnabled = findPreference<Preference>(SETTINGS_AA_ENABLED_KEY!!) as SwitchPreference?
        whitelistedDomains = findPreference(SETTINGS_WL_DOMAINS_KEY!!)
        allowedConnectionType = findPreference<Preference>(SETTINGS_ALLOWED_CONNECTION_TYPE_KEY!!) as ListPreference?
    }

    private fun initPreferences() {
        initEnabled()
        initFilterLists()
        initAcceptableAdsEnabled()
        initWhitelistedDomains()
        initUpdatesConnection()
    }

    private fun initUpdatesConnection() {
        val values = arrayOf<CharSequence>(
            ConnectionType.WIFI_NON_METERED.value,
            ConnectionType.WIFI.value,
            ConnectionType.ANY.value
        )

        val titles = arrayOf<CharSequence>(
            getString(R.string.fragment_adblock_settings_allowed_connection_type_wifi_non_metered),
            getString(R.string.fragment_adblock_settings_allowed_connection_type_wifi),
            getString(R.string.fragment_adblock_settings_allowed_connection_type_all)
        )

        allowedConnectionType!!.entryValues = values
        allowedConnectionType!!.entries = titles

        // selected value
        var connectionType: ConnectionType? = settings!!.allowedConnectionType
        if (connectionType == null) {
            connectionType = ConnectionType.ANY
        }
        allowedConnectionType!!.value = connectionType.value
        allowedConnectionType!!.onPreferenceChangeListener = this
    }

    private fun initWhitelistedDomains() {
        whitelistedDomains!!.onPreferenceClickListener = this
    }

    private fun initAcceptableAdsEnabled() {
        acceptableAdsEnabled!!.isChecked = settings!!.isAcceptableAdsEnabled
        acceptableAdsEnabled!!.onPreferenceChangeListener = this
    }

    private fun initFilterLists() {
        // all available values
        val availableSubscriptions = provider!!.adblockEngine.recommendedSubscriptions
        val availableSubscriptionsTitles = arrayOfNulls<CharSequence>(availableSubscriptions.size)
        val availableSubscriptionsValues = arrayOfNulls<CharSequence>(availableSubscriptions.size)
        for (i in availableSubscriptions.indices) {
            availableSubscriptionsTitles[i] = availableSubscriptions[i].specialization
            availableSubscriptionsValues[i] = availableSubscriptions[i].url
        }
        filterLists!!.entries = availableSubscriptionsTitles
        filterLists!!.entryValues = availableSubscriptionsValues

        // selected values
        val selectedSubscriptionValues = HashSet<String>()
        for (eachSubscription in settings!!.subscriptions) {
            selectedSubscriptionValues.add(eachSubscription.url)
        }
        filterLists!!.values = selectedSubscriptionValues
        filterLists!!.onPreferenceChangeListener = this
    }

    private fun initEnabled() {
        val enabled = settings!!.isAdblockEnabled
        adblockEnabled!!.isChecked = enabled
        adblockEnabled!!.onPreferenceChangeListener = this
        applyAdblockEnabled(enabled)
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        Log.d(TAG, "\"" + preference.title + "\" new value is " + newValue)

        if (preference.key == SETTINGS_ENABLED_KEY) {
            handleEnabledChanged(newValue as Boolean)
        } else if (preference.key == SETTINGS_FILTER_LISTS_KEY) {
            handleFilterListsChanged(newValue as Set<String>)
        } else if (preference.key == SETTINGS_AA_ENABLED_KEY) {
            handleAcceptableAdsEnabledChanged(newValue as Boolean)
        } else if (preference.key == SETTINGS_ALLOWED_CONNECTION_TYPE_KEY) {
            handleAllowedConnectionTypeChanged(newValue as String)
        } else {
            // handle other values if changed
            // `false` for NOT update preference view state
            return false
        }

        // `true` for update preference view state
        return true
    }

    private fun handleAllowedConnectionTypeChanged(value: String) {
        // update and save settings
        settings!!.allowedConnectionType = ConnectionType.findByValue(value)
        provider!!.adblockSettingsStorage.save(settings)

        // apply settings
        allowedConnectionType!!.value = value
        provider!!.adblockEngine.filterEngine.allowedConnectionType = value

        // signal event
        listener!!.onAdblockSettingsChanged(this)

    }

    private fun handleAcceptableAdsEnabledChanged(newValue: Boolean?) {
        val enabledValue = newValue!!

        // update and save settings
        settings!!.isAcceptableAdsEnabled = enabledValue
        provider!!.adblockSettingsStorage.save(settings)

        // apply settings
        provider!!.adblockEngine.isAcceptableAdsEnabled = enabledValue

        // signal event
        listener!!.onAdblockSettingsChanged(this)
    }

    private fun handleFilterListsChanged(newValue: Set<String>) {
        val selectedSubscriptions = LinkedList<Subscription>()

        for (eachSubscription in provider!!.adblockEngine.recommendedSubscriptions) {
            if (newValue.contains(eachSubscription.url)) {
                selectedSubscriptions.add(eachSubscription)
            }
        }

        // update and save settings
        settings!!.subscriptions = selectedSubscriptions
        provider!!.adblockSettingsStorage.save(settings)

        // apply settings
        provider!!.adblockEngine.setSubscriptions(newValue)

        // since 'aa enabled' setting affects subscriptions list, we need to set it again
        provider!!.adblockEngine.isAcceptableAdsEnabled = settings!!.isAcceptableAdsEnabled

        // signal event
        listener!!.onAdblockSettingsChanged(this)
    }

    private fun handleEnabledChanged(newValue: Boolean) {
        // update and save settings
        settings!!.isAdblockEnabled = newValue
        provider!!.adblockSettingsStorage.save(settings)

        // apply settings
        provider!!.adblockEngine.isEnabled = newValue

        // signal event
        listener!!.onAdblockSettingsChanged(this)

        // all other settings are meaningless if adblocking is disabled
        applyAdblockEnabled(newValue)
    }

    private fun applyAdblockEnabled(enabledValue: Boolean) {
        filterLists!!.isEnabled = enabledValue
        acceptableAdsEnabled!!.isEnabled = enabledValue
        whitelistedDomains!!.isEnabled = enabledValue
        allowedConnectionType!!.isEnabled = enabledValue
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        if (preference.key == SETTINGS_WL_DOMAINS_KEY) {
            listener!!.onWhitelistedDomainsClicked(this)
        } else {
            // should not be invoked as only 'wl' preference is subscribed for callback
            return false
        }

        return true
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment GeneralSettingsFragment.
         */
        fun newInstance(): GeneralSettingsFragment {
            return GeneralSettingsFragment(adblockEngine = AdblockHelper.get().engine,adblockSettingsStorage = AdblockHelper.get().storage)
        }
    }
}// required empty public constructor
