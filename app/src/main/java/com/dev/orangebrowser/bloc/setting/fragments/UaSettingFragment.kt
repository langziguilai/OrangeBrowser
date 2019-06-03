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
import com.dev.orangebrowser.databinding.FragmentUaSettingBinding
import com.dev.orangebrowser.extension.*
import java.util.*

class UaSettingFragment : BaseFragment(), BackHandler {


    companion object {
        const val Tag = "UaSettingFragment"
        fun newInstance() = UaSettingFragment()
    }

    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentUaSettingBinding
    override fun onBackPressed(): Boolean {
        RouterActivity?.loadWebSettingFragment(enterAnimationId = R.anim.slide_right_in,exitAnimationId = R.anim.slide_right_out)
        return true

    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_ua_setting
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
        binding = FragmentUaSettingBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
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
        binding.goBack.setOnClickListener {
            onBackPressed()
        }
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
            save(data.title)
            binding.recyclerView.adapter?.notifyItemChanged(index)
        }
    }
    private fun save(title:String){
        setSpString(R.string.pref_setting_ua_title,title)
        if (title==getString(R.string.ua_android)){
            setSpString(R.string.pref_setting_ua,getString(R.string.user_agent_android))
        }
        if (title==getString(R.string.ua_iphone)){
            setSpString(R.string.pref_setting_ua,getString(R.string.user_agent_iphone))
        }
        if (title==getString(R.string.ua_iPad)){
            setSpString(R.string.pref_setting_ua,getString(R.string.user_agent_iPad))
        }

        if (title==getString(R.string.ua_wap)){
            setSpString(R.string.pref_setting_ua,getString(R.string.user_agent_wap))
        }
        if (title==getString(R.string.ua_pc)){
            setSpString(R.string.pref_setting_ua,getString(R.string.user_agent_pc))
        }
    }

    private fun getData(): List<Any> {
        val userAgent = getSpString(R.string.pref_setting_ua_title,getString(R.string.ua_android))
        val list = LinkedList<Any>()
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        list.add(TickItem(title = getString(R.string.ua_android), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                onSelect(data)
            }
        }, value = userAgent==getString(R.string.ua_android)))
        list.add(TickItem(title = getString(R.string.ua_iphone), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                onSelect(data)
            }
        }, value = userAgent==getString(R.string.ua_iphone)))
        list.add(TickItem(title = getString(R.string.ua_iPad), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                onSelect(data)
            }
        }, value = userAgent==getString(R.string.ua_iPad)))
        list.add(TickItem(title = getString(R.string.ua_pc), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                onSelect(data)
            }
        }, value = userAgent==getString(R.string.ua_pc)))
        list.add(TickItem(title = getString(R.string.ua_wap), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                onSelect(data)
            }
        }, value = userAgent==getString(R.string.ua_wap)))
        return list
    }
}
