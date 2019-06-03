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
import com.dev.orangebrowser.databinding.FragmentAdBlockConnectionSettingBinding
import com.dev.orangebrowser.extension.*
import org.adblockplus.libadblockplus.android.ConnectionType
import java.util.*

class AdBlockConnectionSettingFragment : BaseAdBlockSettingFragment(), BackHandler {

    companion object {
        const val Tag = "AdBlockConnectionSettingFragment"
        fun newInstance() = AdBlockConnectionSettingFragment()
    }

    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentAdBlockConnectionSettingBinding
    override fun onBackPressed(): Boolean {
        fragmentManager?.popBackStack()
        return true
    }

    override fun useDataBinding(): Boolean {
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
    }
    override fun getLayoutResId(): Int {
        return R.layout.fragment_ad_block_connection_setting
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAdBlockConnectionSettingBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
        binding.lifecycleOwner=this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(activity!!, factory).get(MainViewModel::class.java)
        binding.activityViewModel = activityViewModel
        binding.backHandler=this
        super.onActivityCreated(savedInstanceState)
    }


    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    }

    private lateinit var dataList: List<Any>
    override fun initData(savedInstanceState: Bundle?) {
        dataList = getData()
        val adapter = Adapter(dataList)
        binding.recyclerView.adapter = adapter
    }

    private fun onSelect(data:TickItem) {
        var selectedIndex=-1
        dataList.forEach {
            (it as? TickItem)?.apply {
                if (it.value){
                    selectedIndex=dataList.indexOf(it)
                    it.value=false
                }
            }
        }
        if (selectedIndex>=0){
            binding.recyclerView.adapter?.notifyItemChanged(selectedIndex)
        }

        val index=dataList.indexOf(data)
        if (index>=0){
            data.value=true
            handleAllowedConnectionTypeChanged(data.title)
            binding.recyclerView.adapter?.notifyItemChanged(index)
        }
    }

    private fun getData(): List<Any> {
        val connectionTypeValue = settings?.allowedConnectionType?.value  ?:  ConnectionType.ANY.value
        var title=getString(R.string.fragment_adblock_settings_allowed_connection_type_all)
        when(connectionTypeValue){
            ConnectionType.ANY.value->title=getString(R.string.fragment_adblock_settings_allowed_connection_type_all)
            ConnectionType.WIFI.value->title=getString(R.string.fragment_adblock_settings_allowed_connection_type_wifi)
            ConnectionType.WIFI_NON_METERED.value->title=getString(R.string.fragment_adblock_settings_allowed_connection_type_wifi_non_metered)
        }
        val list = LinkedList<Any>()
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))

        list.add(TickItem(title = getString(R.string.fragment_adblock_settings_allowed_connection_type_wifi_non_metered), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                onSelect(data)
            }
        }, value = title==getString(R.string.fragment_adblock_settings_allowed_connection_type_wifi_non_metered)))
        list.add(TickItem(title =  getString(R.string.fragment_adblock_settings_allowed_connection_type_wifi), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                onSelect(data)
            }
        }, value = title==getString(R.string.fragment_adblock_settings_allowed_connection_type_wifi)))
        list.add(TickItem(title = getString(R.string.fragment_adblock_settings_allowed_connection_type_all), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                onSelect(data)
            }
        }, value = title==getString(R.string.fragment_adblock_settings_allowed_connection_type_all)))
        return list
    }

    private fun handleAllowedConnectionTypeChanged(title: String) {
        var value= ConnectionType.ANY.value
        when(title){
            getString(R.string.fragment_adblock_settings_allowed_connection_type_wifi_non_metered)->{
                value=ConnectionType.WIFI_NON_METERED.value
            }
            getString(R.string.fragment_adblock_settings_allowed_connection_type_wifi)->{
                value=ConnectionType.WIFI.value
            }
            getString(R.string.fragment_adblock_settings_allowed_connection_type_all)->{
                value=ConnectionType.ANY.value
            }
        }
        // update and save settings
        settings?.allowedConnectionType = ConnectionType.findByValue(value)
        adBlockSettingsStorage.save(settings)
        // apply settings
        adBlockEngine.filterEngine.allowedConnectionType = value
    }
}
