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
import com.dev.orangebrowser.bloc.setting.viewholder.CheckboxItem
import com.dev.orangebrowser.bloc.setting.viewholder.DividerItem
import com.dev.orangebrowser.bloc.setting.viewholder.TileItem
import com.dev.orangebrowser.bloc.setting.viewholder.base.Action
import com.dev.orangebrowser.databinding.FragmentCacheSettingBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.extension.getColor
import java.util.*

class CacheSettingFragment : BaseFragment(), BackHandler {


    companion object {
        const val Tag = "CacheSettingFragment"
        fun newInstance() = CacheSettingFragment()
    }

    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentCacheSettingBinding
    override fun onBackPressed(): Boolean {
        RouterActivity?.loadSettingFragment()
        return true

    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_cache_setting
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
        binding = FragmentCacheSettingBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
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

    var needClearPageCache = false
    var needClearFormCache = false
    var needClearBrowserHistory = false
    var needClearCookies = false
    var needClearPasswordCache = false
    var needClearSearchHistory = false
    var needClearFileCache = false
    //TODO:添加Action
    private fun getData(): List<Any> {
        val list = LinkedList<Any>()
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))

        list.add(CheckboxItem(title = getString(R.string.paeg_cache), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                needClearPageCache = data
            }
        }, value = false))
        list.add(CheckboxItem(title = getString(R.string.form_cache), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                needClearFormCache = data
            }
        }, value = false))
        list.add(CheckboxItem(title = getString(R.string.browser_history), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                needClearBrowserHistory = data
            }
        }, value = false))
        list.add(CheckboxItem(title = getString(R.string.cookies), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                needClearCookies = data
            }
        }, value = false))
        list.add(CheckboxItem(title = getString(R.string.password_cache), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                needClearPasswordCache = data
            }
        }, value = false))
        list.add(CheckboxItem(title = getString(R.string.search_history), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                needClearSearchHistory = data
            }
        }, value = false))
        list.add(CheckboxItem(title = getString(R.string.file_cache), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                needClearFileCache = data
            }
        }, value = false))

        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))

        list.add(
            TileItem(
                title = getString(R.string.start_clean),
                tip = "",
                icon = getString(R.string.ic_right),
                action = object : Action<TileItem> {
                    override fun invoke(data: TileItem) {
                        //TODO：如果选中，开始清理
                    }
                })
        )
        return list
    }
}
