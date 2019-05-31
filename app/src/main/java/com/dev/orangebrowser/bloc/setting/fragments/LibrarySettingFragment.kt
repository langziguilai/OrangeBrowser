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
import com.dev.orangebrowser.bloc.setting.viewholder.*
import com.dev.orangebrowser.bloc.setting.viewholder.base.Action
import com.dev.orangebrowser.databinding.FragmentLibrarySettingBinding
import com.dev.orangebrowser.extension.*
import java.util.*

class LibrarySettingFragment : BaseFragment(), BackHandler {


    companion object {
        const val Tag = "LibrarySettingFragment"
        fun newInstance() = LibrarySettingFragment()
    }

    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentLibrarySettingBinding
    override fun onBackPressed(): Boolean {
        fragmentManager?.popBackStack()
        return true

    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_library_setting
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
        binding = FragmentLibrarySettingBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
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

    private fun getData(): List<Any> {
        val list = LinkedList<Any>()
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))

        list.add(SwitchItem(title = getString(R.string.scan), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_enable_lib_scan, data)
            }
        }, value = getSpBool(R.string.pref_setting_enable_lib_scan, true)))
        list.add(SwitchItem(title = getString(R.string.share), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_enable_lib_share, data)
            }
        }, value = getSpBool(R.string.pref_setting_enable_lib_share, true)))

        list.add(SwitchItem(title = getString(R.string.read_mode), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_enable_lib_read_mode, data)
            }
        }, value = getSpBool(R.string.pref_setting_enable_lib_read_mode, true)))

        list.add(SwitchItem(title = getString(R.string.image_mode), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_enable_lib_image_mode, data)
            }
        }, value = getSpBool(R.string.pref_setting_enable_lib_image_mode, true)))

//        list.add(SwitchItem(title = getString(R.string.mark_ad), action = object : Action<Boolean> {
//            override fun invoke(data: Boolean) {
//                setSpBool(R.string.pref_setting_enable_lib_mark_ad, data)
//            }
//        }, value = getSpBool(R.string.pref_setting_enable_lib_mark_ad, true)))

        list.add(SwitchItem(title = getString(R.string.find_in_page), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_enable_lib_find_in_page, data)
            }
        }, value = getSpBool(R.string.pref_setting_enable_lib_find_in_page, true)))

        list.add(SwitchItem(title = getString(R.string.save_resource_offline), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_enable_lib_save_resource_offline, data)
            }
        }, value = getSpBool(R.string.pref_setting_enable_lib_save_resource_offline, true)))

        list.add(SwitchItem(title = getString(R.string.translation), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_enable_lib_translation, data)
            }
        }, value = getSpBool(R.string.pref_setting_enable_lib_translation, true)))

//        list.add(SwitchItem(title = getString(R.string.view_source_code), action = object : Action<Boolean> {
//            override fun invoke(data: Boolean) {
//                setSpBool(R.string.pref_setting_enable_lib_view_source_code, data)
//            }
//        }, value = getSpBool(R.string.pref_setting_enable_lib_view_source_code, true)))

        list.add(SwitchItem(title = getString(R.string.detect_resource), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_enable_lib_detect_resource, data)
            }
        }, value = getSpBool(R.string.pref_setting_enable_lib_detect_resource, true)))

        list.add(SwitchItem(title = getString(R.string.add_to_home_page), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_enable_lib_add_to_home_page, data)
            }
        }, value = getSpBool(R.string.pref_setting_enable_lib_add_to_home_page, true)))

//        list.add(SwitchItem(title = getString(R.string.sky_net), action = object : Action<Boolean> {
//            override fun invoke(data: Boolean) {
//                setSpBool(R.string.pref_setting_enable_lib_sky_net, data)
//            }
//        }, value = getSpBool(R.string.pref_setting_enable_lib_sky_net, true)))


        list.add(
            CategoryHeaderItem(
                height = 40,
                title = getString(R.string.sky_net_tip),
                background = getColor(R.color.color_F8F8F8)
            )
        )
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))

        list.add(
            TileItem(
                title = getString(R.string.import_bookmarks),
                tip = "",
                icon = getString(R.string.ic_right),
                action = object : Action<TileItem> {
                    override fun invoke(data: TileItem) {
                        //TODO
                    }
                })
        )
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))

        list.add(SwitchItem(title = getString(R.string.auto_video_detect), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_enable_auto_detect_video, data)
            }
        }, value = getSpBool(R.string.pref_setting_enable_auto_detect_video, false)))
        list.add(
            SwitchItem(
                title = getString(R.string.site_recommend),
                action = object : Action<Boolean> {
                    override fun invoke(data: Boolean) {
                        setSpBool(R.string.pref_setting_enable_site_recommend, data)
                    }
                },
                value = getSpBool(R.string.pref_setting_enable_site_recommend, false)
            )
        )

        list.add(
            CategoryHeaderItem(
                height = 24,
                title = getString(R.string.site_recommend_tip),
                background = getColor(R.color.color_F8F8F8)
            )
        )
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))

        return list
    }
}
