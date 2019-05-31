package com.dev.orangebrowser.bloc.setting.fragments.adblock

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.support.BackHandler
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.bloc.setting.adapter.Adapter
import com.dev.orangebrowser.bloc.setting.viewholder.*
import com.dev.orangebrowser.bloc.setting.viewholder.base.Action
import com.dev.orangebrowser.databinding.FragmentAdblockSettingBinding
import com.dev.orangebrowser.extension.*
import org.adblockplus.libadblockplus.android.ConnectionType
import java.util.*

class AdBlockSettingFragment : BaseAdBlockSettingFragment(), BackHandler {


    companion object {
        const val Tag = "AdBlockSettingFragment"
        fun newInstance() = AdBlockSettingFragment()
    }


    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentAdblockSettingBinding
    override fun onBackPressed(): Boolean {
        fragmentManager?.popBackStack()
        return true

    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_adblock_setting
    }

    override fun useDataBinding(): Boolean {
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAdblockSettingBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(activity!!, factory).get(MainViewModel::class.java)
        binding.activityViewModel = activityViewModel
        super.onActivityCreated(savedInstanceState)
    }


    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        binding.goBack.setOnClickListener {
            onBackPressed()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    }

    lateinit var dataList: LinkedList<Any>
    override fun initData(savedInstanceState: Bundle?) {
        dataList = getData()
        val adapter = Adapter(dataList)
        binding.recyclerView.adapter = adapter
    }


    //TODO:添加Action
    private fun getData(): LinkedList<Any> {
        val list = LinkedList<Any>()
//        val adBlockCount = getSpInt(R.string.pref_setting_ad_block_count, 0)
//        list.add(
//            AdblockStatusItem(
//                count = adBlockCount.toString(),
//                status = getString(R.string.ad_block_status).replace("0", adBlockCount.toString())
//            )
//        )
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))

        list.add(SwitchItem(title = getString(R.string.ad_block), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                handleEnabledChanged(data)
            }
        }, value = settings?.isAdblockEnabled ?: false))
        list.add(SwitchItem(title = getString(R.string.accept_ad), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                handleAcceptableAdsEnabledChanged(data)
            }
        }, value = settings?.isAcceptableAdsEnabled ?: true))

//        list.add(SwitchItem(label = getString(R.string.show_tip_when_block_ad), action = object : Action<Boolean> {
//            override fun invoke(data: Boolean) {
//                setSpBool(R.string.pref_setting_enable_show_tip_when_block_ad, data)
//            }
//        }, value = getSpBool(R.string.pref_setting_enable_show_tip_when_block_ad, true)))
        var updateTip = ""
        settings?.allowedConnectionType?.name?.apply {
            when (this) {
                ConnectionType.ANY.name -> updateTip =
                    getString(R.string.fragment_adblock_settings_allowed_connection_type_all)
                ConnectionType.WIFI.name -> updateTip =
                    getString(R.string.fragment_adblock_settings_allowed_connection_type_wifi)
                ConnectionType.WIFI_NON_METERED.name -> updateTip =
                    getString(R.string.fragment_adblock_settings_allowed_connection_type_wifi_non_metered)
            }
        }
        list.add(
            TileItem(
                title = getString(R.string.ad_block_update_connection),
                tip = updateTip,
                icon = getString(R.string.ic_right),
                action = object : Action<TileItem> {
                    override fun invoke(data: TileItem) {
                        RouterActivity?.addAdBlockConnectionSettingFragment()
                    }
                })
        )
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        list.add(
            CategoryHeaderItem(
                title = getString(R.string.default_ad_block_rule),
                height = 24,
                background = getColor(R.color.color_F8F8F8)
            )
        )

        list.add(
            TileItem(
                title = getString(R.string.ad_block_subscriptions),
                tip = "",
                icon = getString(R.string.ic_right),
                action = object : Action<TileItem> {
                    override fun invoke(data: TileItem) {
                        RouterActivity?.addAdBlockSubscriptionSettingFragment()
                    }
                })
        )
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))

        list.add(
            TileItem(
                title = getString(R.string.my_ad_block_rules),
                tip = "",
                icon = getString(R.string.ic_right),
                action = object : Action<TileItem> {
                    override fun invoke(data: TileItem) {
                        RouterActivity?.addAdBlockFilterSettingFragment()
                    }
                })
        )
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        list.add(
            TileItem(
                title = getString(R.string.white_list_sites),
                tip = "",
                icon = getString(R.string.ic_right),
                action = object : Action<TileItem> {
                    override fun invoke(data: TileItem) {
                        RouterActivity?.addAdBlockWhiteListSettingFragment()
                    }
                })
        )

        list.add(
            CategoryHeaderItem(
                height = 24,
                title = getString(R.string.add_white_list_sites_you_not_block),
                background = getColor(R.color.color_F8F8F8)
            )
        )
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        list.add(
            TileItem(
                title = getString(R.string.help),
                tip = "",
                icon = getString(R.string.ic_right),
                action = object : Action<TileItem> {
                    override fun invoke(data: TileItem) {
                        //TODO
                    }
                })
        )
        return list
    }

    private fun handleAcceptableAdsEnabledChanged(newValue: Boolean?) {
        val enabledValue = newValue!!

        // update and save settings
        settings?.isAcceptableAdsEnabled = enabledValue
        adBlockSettingsStorage.save(settings)

        // apply settings
        adBlockEngine.isAcceptableAdsEnabled = enabledValue
    }

    private fun handleEnabledChanged(newValue: Boolean) {
        // update and save settings
        settings?.isAdblockEnabled = newValue
        adBlockSettingsStorage.save(settings)

        // apply settings
        adBlockEngine.isEnabled = newValue
    }
}
