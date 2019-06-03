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
import com.dev.orangebrowser.databinding.FragmentGestureSettingBinding
import com.dev.orangebrowser.extension.*
import java.util.*

class GestureSettingFragment : BaseFragment(), BackHandler {


    companion object {
        const val Tag = "GestureSettingFragment"
        fun newInstance() = GestureSettingFragment()
    }

    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentGestureSettingBinding
    override fun onBackPressed(): Boolean {
        fragmentManager?.popBackStack()
        return true

    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_gesture_setting
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
        binding = FragmentGestureSettingBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
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

    override fun initData(savedInstanceState: Bundle?) {
        val adapter = Adapter(getData())
        binding.recyclerView.adapter = adapter
    }

    //TODO:添加Action
    private fun getData(): List<Any> {
        val list = LinkedList<Any>()
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))

        list.add(SwitchItem(title = getString(R.string.double_click_blank_to_flip), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_enable_double_click_blank_to_flip, data)
            }
        }, value = getSpBool(R.string.pref_setting_enable_double_click_blank_to_flip, false)))

        list.add(SwitchItem(title = getString(R.string.page_pull_to_fresh), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_enable_page_pull_to_fresh, data)
            }
        }, value = getSpBool(R.string.pref_setting_enable_page_pull_to_fresh, true)))
        list.add(SwitchItem(title = getString(R.string.edge_forward_back), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_enable_edge_forward_back, data)
            }
        }, value = getSpBool(R.string.pref_setting_enable_edge_forward_back, true)))

        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        list.add(
            CategoryHeaderItem(
                height = 24,
                title = getString(R.string.control_bar),
                background = getColor(R.color.color_F8F8F8)
            )
        )

        list.add(SwitchItem(title = getString(R.string.show_page_count), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                setSpBool(R.string.pref_setting_enable_show_page_count, data)
            }
        }, value = getSpBool(R.string.pref_setting_enable_show_page_count, true)))


        list.add(
            TileItem(
                title = getString(R.string.forward_button_long_press_shortcut),
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
                title = getString(R.string.forward_button_long_press_tip),
                background = getColor(R.color.color_F8F8F8)
            )
        )
        return list
    }
}
