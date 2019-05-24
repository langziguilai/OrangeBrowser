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
import com.dev.base.extension.enterToImmersiveMode
import com.dev.base.extension.exitImmersiveModeIfNeeded
import com.dev.base.support.BackHandler
import com.dev.browser.search.SearchEngineManager
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.bloc.setting.adapter.Adapter
import com.dev.orangebrowser.bloc.setting.viewholder.*
import com.dev.orangebrowser.bloc.setting.viewholder.base.Action
import com.dev.orangebrowser.databinding.FragmentGeneralSettingBinding
import com.dev.orangebrowser.databinding.FragmentMainSettingBinding
import com.dev.orangebrowser.extension.*
import com.dev.view.StatusBarUtil
import okhttp3.Route
import java.util.*
import javax.inject.Inject

class GeneralSettingFragment : BaseFragment(), BackHandler {


    companion object {
        const val Tag = "GeneralSettingFragment"
        fun newInstance() = GeneralSettingFragment()
    }

    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentGeneralSettingBinding
    override fun onBackPressed(): Boolean {
       RouterActivity?.loadSettingFragment()
        return true

    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_general_setting
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
        binding = FragmentGeneralSettingBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
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
        list.add(
            CategoryHeaderItem(
                height = 24,
                title = getString(R.string.address_bar),
                background = getColor(R.color.color_F8F8F8)
            )
        )
        val addressBarShowItem = getString(R.string.address_bar_show_title)
        list.add(
            TileItem(
                title = getString(R.string.address_bar_show_item),
                tip = getSpString(R.string.pref_setting_address_bar_show_title, addressBarShowItem),
                icon = getString(R.string.ic_right),
                action = object : Action<TileItem> {
                    override fun invoke(data: TileItem) {
                        RouterActivity?.loadAddressBarSettingFragment()
                    }
                })
        )
        val viewMode = getString(R.string.normal_vision_mode)
        list.add(
            TileItem(
                title = getString(R.string.view_mode),
                tip = getSpString(R.string.pref_setting_view_mode_title, viewMode),
                icon = getString(R.string.ic_right),
                action = object : Action<TileItem> {
                    override fun invoke(data: TileItem) {
                        RouterActivity?.loadVisionModeSettingFragment()
                    }
                })
        )
//        list.add(SwitchItem(title = getString(R.string.fullscreen), action = object : Action<Boolean> {
//            override fun invoke(fullscreen: Boolean) {
//                setSpBool(R.string.pref_setting_full_screen, fullscreen)
//                //如果全屏
//                if (fullscreen){
//                    StatusBarUtil.hideStatusBar(requireActivity())
//                }else{
//                    StatusBarUtil.showStatusBar(requireActivity())
//                }
//
//            }
//        }, value = getSpBool(R.string.pref_setting_full_screen, false)))
        list.add(SwitchItem(title = getString(R.string.record_search_history), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_record_search_history, data)
            }
        }, value = getSpBool(R.string.pref_setting_record_search_history, true)))
        list.add(SwitchItem(title = getString(R.string.show_clipboard_when_search), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_show_clipboard_when_search, data)
            }
        }, value = getSpBool(R.string.pref_setting_show_clipboard_when_search, true)))
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        list.add(
            CategoryHeaderItem(
                height = 24,
                title = getString(R.string.theme_and_style),
                background = getColor(R.color.color_F8F8F8)
            )
        )
//        list.add(
//            TileItem(
//                title = getString(R.string.color_style),
//                tip = getSpString(R.string.pref_setting_color_style, getString(R.string.colorful)),
//                icon = getString(R.string.ic_right),
//                action = object : Action<TileItem> {
//                    override fun invoke(data: TileItem) {
//                       RouterActivity?.loadColorStyleSettingFragment()
//                    }
//                })
//        )
        list.add(SwitchItem(title = getString(R.string.immerse_browse_style), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_user_immerse_browse_style, data)
            }
        }, value = getSpBool(R.string.pref_setting_user_immerse_browse_style, false)))
//        list.add(
//            SwitchItem(
//                title = getString(R.string.add_shadow_to_white_status_bar),
//                action = object : Action<Boolean> {
//                    override fun invoke(data: Boolean) {
//                        setSpBool(R.string.pref_setting_add_shadow_to_white_status_bar, data)
//                    }
//                },
//                value = getSpBool(R.string.pref_setting_add_shadow_to_white_status_bar, false)
//            )
//        )
//        list.add(
//            SwitchItem(
//                title = getString(R.string.add_shadow_to_white_navigation_bar),
//                action = object : Action<Boolean> {
//                    override fun invoke(data: Boolean) {
//                        setSpBool(R.string.pref_setting_add_shadow_to_white_navigation_bar, data)
//                    }
//                },
//                value = getSpBool(R.string.pref_setting_add_shadow_to_white_navigation_bar, false)
//            )
//        )
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        list.add(
            CategoryHeaderItem(
                height = 24,
                title = getString(R.string.language_and_font),
                background = getColor(R.color.color_F8F8F8)
            )
        )
        list.add(
            TileItem(
                title = getString(R.string.font_size),
                tip = getSpString(R.string.pref_setting_font_size_title, getString(R.string.font_size_middle)),
                icon = getString(R.string.ic_right),
                action = object : Action<TileItem> {
                    override fun invoke(data: TileItem) {
                       RouterActivity?.loadFontSizeSettingFragment()
                    }
                })
        )
        list.add(
            TileItem(
                title = getString(R.string.language),
                tip = getSpString(R.string.pref_setting_language_title, getString(R.string.language_follow_system)),
                icon = getString(R.string.ic_right),
                action = object : Action<TileItem> {
                    override fun invoke(data: TileItem) {
                        RouterActivity?.loadLanguageSettingFragment()
                    }
                })
        )
        return list
    }
}
