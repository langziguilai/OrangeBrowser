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
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.bloc.setting.adapter.Adapter
import com.dev.orangebrowser.bloc.setting.viewholder.CategoryHeaderItem
import com.dev.orangebrowser.bloc.setting.viewholder.DividerItem
import com.dev.orangebrowser.bloc.setting.viewholder.SwitchItem
import com.dev.orangebrowser.bloc.setting.viewholder.TileItem
import com.dev.orangebrowser.bloc.setting.viewholder.base.Action
import com.dev.orangebrowser.databinding.FragmentWebSettingBinding
import com.dev.orangebrowser.extension.*
import java.util.*

class WebSettingFragment : BaseFragment(), BackHandler {


    companion object {
        const val Tag = "GeneralSettingFragment"
        fun newInstance() = WebSettingFragment()
    }

    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentWebSettingBinding
    override fun onBackPressed(): Boolean {
        RouterActivity?.loadSettingFragment()
        return true

    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_web_setting
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
        binding = FragmentWebSettingBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
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
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        val uaTip = getString(R.string.ua_android)
        list.add(
            TileItem(
                title = getString(R.string.ua_setting),
                tip = getSpString(R.string.pref_setting_ua_title, uaTip),
                icon = getString(R.string.ic_right),
                action = object : Action<TileItem> {
                    override fun invoke(data: TileItem) {
                        //TODO:
                    }
                })
        )
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        list.add(
            CategoryHeaderItem(
                height = 24,
                title = getString(R.string.secure_and_privacy),
                background = getColor(R.color.color_F8F8F8)
            )
        )

        list.add(SwitchItem(title = getString(R.string.refuse_track), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_refuse_track, data)
            }
        }, value = getSpBool(R.string.pref_setting_refuse_track, false)))

        list.add(SwitchItem(title = getString(R.string.hide_device_info), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_need_hide_device_info, data)
            }
        }, value = getSpBool(R.string.pref_setting_need_hide_device_info, false)))
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        list.add(SwitchItem(title = getString(R.string.show_https_icon), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_need_show_https_icon, data)
            }
        }, value = getSpBool(R.string.pref_setting_need_show_https_icon, true)))
        list.add(SwitchItem(title = getString(R.string.show_no_secure_https_icon), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_need_show_no_secure_https_icon, data)
            }
        }, value = getSpBool(R.string.pref_setting_need_show_no_secure_https_icon, false)))

        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        list.add(SwitchItem(title = getString(R.string.intercept_alert), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_need_intercept_alert, data)
            }
        }, value = getSpBool(R.string.pref_setting_need_intercept_alert, true)))
        list.add(
            TileItem(
                title = getString(R.string.intercept_open_app),
                tip = getSpString(R.string.pref_setting_need_intercept_open_app_title, getString(R.string.give_tip)),
                icon = getString(R.string.ic_right),
                action = object : Action<TileItem> {
                    override fun invoke(data: TileItem) {
                        //TODO:
                    }
                })
        )
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        list.add(SwitchItem(title = getString(R.string.keep_last_page), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_need_keep_last_page, data)
            }
        }, value = getSpBool(R.string.pref_setting_need_keep_last_page, true)))
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        list.add(SwitchItem(title = getString(R.string.super_cache), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_need_use_super_cache, data)
            }
        }, value = getSpBool(R.string.pref_setting_need_use_super_cache, false)))
        list.add(
            CategoryHeaderItem(
                height = 48,
                title = getString(R.string.super_cache_tip),
                background = getColor(R.color.color_F8F8F8)
            )
        )
        return list
    }
}
