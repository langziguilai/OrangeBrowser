package com.dev.orangebrowser.bloc.setting.fragments.web

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
import com.dev.browser.concept.BrowserSetting
import com.dev.browser.concept.REDIRECT_TO_APP_ASK
import com.dev.browser.concept.REDIRECT_TO_APP_NO
import com.dev.browser.concept.REDIRECT_TO_APP_YES
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.bloc.setting.adapter.Adapter
import com.dev.orangebrowser.bloc.setting.viewholder.DividerItem
import com.dev.orangebrowser.bloc.setting.viewholder.TickItem
import com.dev.orangebrowser.bloc.setting.viewholder.base.Action
import com.dev.orangebrowser.databinding.FragmentSettingOpenAppBinding
import com.dev.orangebrowser.extension.*
import java.util.*

class OpenAppSettingFragment : BaseFragment(), BackHandler {


    companion object {
        const val Tag = "OpenAppSettingFragment"
        fun newInstance() = OpenAppSettingFragment()
    }

    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentSettingOpenAppBinding
    override fun onBackPressed(): Boolean {
        RouterActivity?.loadDownloadSettingFragment(R.anim.holder,R.anim.slide_right_out)
        return true

    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_setting_open_app
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
        binding = FragmentSettingOpenAppBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
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
            setSpString(R.string.pref_setting_need_intercept_open_app_title,data.title)
            binding.recyclerView.adapter?.notifyItemChanged(index)
        }
    }

    private fun getData(): List<Any> {
        val interceptApp = getSpString(R.string.pref_setting_need_intercept_open_app_title,getString(R.string.intercept_all))
        val list = LinkedList<Any>()
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        list.add(TickItem(title = getString(R.string.not_intercept), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                BrowserSetting.RedirectToApp= REDIRECT_TO_APP_YES
                setSpInt(R.string.pref_setting_need_intercept_open_app_value,REDIRECT_TO_APP_YES)
                onSelect(data)
            }
        }, value = interceptApp==getString(R.string.not_intercept)))
        list.add(TickItem(title = getString(R.string.give_tip), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                BrowserSetting.RedirectToApp= REDIRECT_TO_APP_ASK
                setSpInt(R.string.pref_setting_need_intercept_open_app_value,REDIRECT_TO_APP_ASK)
                onSelect(data)
            }
        }, value = interceptApp==getString(R.string.give_tip)))
        list.add(TickItem(title = getString(R.string.intercept_all), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                BrowserSetting.RedirectToApp= REDIRECT_TO_APP_NO
                setSpInt(R.string.pref_setting_need_intercept_open_app_value,REDIRECT_TO_APP_NO)
                onSelect(data)
            }
        }, value = interceptApp==getString(R.string.intercept_all)))
        return list
    }
}
