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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView

import com.dev.browser.R
import org.adblockplus.libadblockplus.android.Utils

import java.util.LinkedList

/**
 * Whitelisted domains adblock fragment.
 * Use the [WhitelistedDomainsSettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WhitelistedDomainsSettingsFragment : BaseSettingsFragment<WhitelistedDomainsSettingsFragment.Listener>() {


    private var domain: EditText? = null
    private var addDomainButton: ImageView? = null
    private var listView: ListView? = null
    private var adapter: Adapter? = null

    private val removeDomainClickListener = View.OnClickListener { v ->
        // update and save settings
        val position = (v.tag as Int).toInt()
        val removeDomain = settings!!.whitelistedDomains[position]
        Log.w(TAG, "Removing domain: $removeDomain")
        settings!!.whitelistedDomains.removeAt(position)
        provider!!.adblockSettingsStorage.save(settings)

        // apply settings
        provider!!.adblockEngine.whitelistedDomains = settings!!.whitelistedDomains

        // signal event
        listener!!.onAdblockSettingsChanged(this@WhitelistedDomainsSettingsFragment)

        // update UI
        adapter!!.notifyDataSetChanged()
    }

    /**
     * Listener with additional `isValidDomain` method
     */
    interface Listener : BaseSettingsFragment.Listener {
        fun isValidDomain(
            fragment: WhitelistedDomainsSettingsFragment,
            domain: String, settings: AdblockSettings?
        ): Boolean
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        listener = castOrThrow(activity, Listener::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(
            R.layout.fragment_adblock_whitelisted_domains_settings,
            container,
            false
        )

        bindControls(rootView)

        return rootView
    }

    override fun onResume() {
        super.onResume()
        initControls()
    }

    private fun bindControls(rootView: View) {
        domain = rootView.findViewById<View>(R.id.fragment_adblock_wl_add_label) as EditText
        addDomainButton = rootView.findViewById<View>(R.id.fragment_adblock_wl_add_button) as ImageView
        listView = rootView.findViewById<View>(R.id.fragment_adblock_wl_listview) as ListView
    }

    // Holder for listview items
    private inner class Holder internal constructor(rootView: View) {
        internal var domain: TextView
        internal var removeButton: ImageView

        init {
            domain = rootView.findViewById<View>(R.id.fragment_adblock_wl_item_title) as TextView
            removeButton = rootView.findViewById<View>(R.id.fragment_adblock_wl_item_remove) as ImageView
        }
    }

    // Adapter
    private inner class Adapter : BaseAdapter() {
        override fun getCount(): Int {
            return if (settings!!.whitelistedDomains != null)
                settings!!.whitelistedDomains.size
            else
                0
        }

        override fun getItem(position: Int): Any {
            return settings!!.whitelistedDomains[position]
        }

        override fun getItemId(position: Int): Long {
            return getItem(position).hashCode().toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                val inflater = LayoutInflater.from(activity)
                convertView = inflater.inflate(
                    R.layout.fragment_adblock_whitelisted_domain_item,
                    parent, false
                )
                convertView!!.tag = Holder(convertView)
            }

            val domain = getItem(position) as String

            val holder = convertView.tag as Holder
            holder.domain.text = domain

            holder.removeButton.setOnClickListener(removeDomainClickListener)
            holder.removeButton.tag = Integer.valueOf(position)

            return convertView
        }
    }

    private fun initControls() {
        addDomainButton!!.setOnClickListener {
            val preparedDomain = prepareDomain(domain!!.text.toString())

            if (listener!!.isValidDomain(
                    this@WhitelistedDomainsSettingsFragment,
                    preparedDomain,
                    settings
                )
            ) {
                addDomain(preparedDomain)
            } else {
                Log.w(TAG, "Domain $preparedDomain is not valid")
            }
        }

        adapter = Adapter()
        listView!!.adapter = adapter
    }

    private fun prepareDomain(domain: String): String {
        return domain.trim { it <= ' ' }
    }

    fun addDomain(newDomain: String) {
        Log.d(TAG, "New domain added: $newDomain")

        var whitelistedDomains: MutableList<String>? = settings!!.whitelistedDomains
        if (whitelistedDomains == null) {
            whitelistedDomains = LinkedList()
            settings!!.whitelistedDomains = whitelistedDomains
        }

        // update and save settings
        whitelistedDomains.add(newDomain)
        provider!!.adblockSettingsStorage.save(settings)

        // apply settings
        provider!!.adblockEngine.whitelistedDomains = whitelistedDomains

        // signal event
        listener!!.onAdblockSettingsChanged(this@WhitelistedDomainsSettingsFragment)

        // update UI
        adapter!!.notifyDataSetChanged()
        domain!!.text.clear()
        domain!!.clearFocus()
    }

    companion object {
        private val TAG = Utils.getTag(WhitelistedDomainsSettingsFragment::class.java)

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         */
        fun newInstance(): WhitelistedDomainsSettingsFragment {
            return WhitelistedDomainsSettingsFragment()
        }
    }
}// required empty public constructor
