package com.dev.orangebrowser.bloc.setting.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.BaseFragment
import com.dev.base.support.BackHandler
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainActivity
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.bloc.setting.adapter.Adapter
import com.dev.orangebrowser.bloc.setting.viewholder.*
import com.dev.orangebrowser.bloc.setting.viewholder.base.Action
import com.dev.orangebrowser.databinding.FragmentAdblockSettingBinding
import com.dev.orangebrowser.extension.*
import java.util.*

class AdBlockSettingFragment : BaseFragment(), BackHandler {


    companion object {
        const val Tag = "AdBlockSettingFragment"
        fun newInstance() = AdBlockSettingFragment()
    }

    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentAdblockSettingBinding
    override fun onBackPressed(): Boolean {
        RouterActivity?.loadSettingFragment()
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

    override fun initData(savedInstanceState: Bundle?) {
        val adapter = Adapter(getData())
        binding.recyclerView.adapter = adapter
    }

    //TODO:添加Action
    private fun getData(): List<Any> {
        val list = LinkedList<Any>()
        val adBlockCount = getSpInt(R.string.pref_setting_ad_block_count, 0)
        list.add(
            AdblockStatusItem(
                count = adBlockCount.toString(),
                status = getString(R.string.ad_block_status).replace("0", adBlockCount.toString())
            )
        )
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))

        list.add(SwitchItem(title = getString(R.string.ad_block), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_enable_ad_block, data)
            }
        }, value = getSpBool(R.string.pref_setting_enable_ad_block, false)))

        list.add(SwitchItem(title = getString(R.string.show_tip_when_block_ad), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_enable_show_tip_when_block_ad, data)
            }
        }, value = getSpBool(R.string.pref_setting_enable_show_tip_when_block_ad, true)))
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
                title = getString(R.string.magic_browser_rules),
                tip = getString(R.string.ab_block_status).replace("?", adBlockCount.toString()),
                icon = getString(R.string.ic_right),
                action = object : Action<TileItem> {
                    override fun invoke(data: TileItem) {
                        RouterActivity?.loadAdBlockRecordSettingFragment()
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
                        //TODO
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
                        //TODO
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
}
