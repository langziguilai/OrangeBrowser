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
import com.dev.orangebrowser.databinding.FragmentFontSizeSettingBinding
import com.dev.orangebrowser.extension.*
import java.util.*

class FontSizeSettingFragment : BaseFragment(), BackHandler {


    companion object {
        const val Tag = "FontSizeSettingFragment"
        fun newInstance() = FontSizeSettingFragment()
    }

    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentFontSizeSettingBinding
    override fun onBackPressed(): Boolean {
        RouterActivity?.loadGeneralSettingFragment(enterAnimationId = R.anim.slide_right_in,exitAnimationId = R.anim.slide_right_out)
        return true

    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_font_size_setting
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
        binding = FragmentFontSizeSettingBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
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
            setSpString(R.string.pref_setting_font_size_title,data.title)
            when(data.title){
                getString(R.string.font_size_super_small)->{
                    setSpInt(R.string.pref_setting_font_size,60)
                }
                getString(R.string.font_size_small)->{
                    setSpInt(R.string.pref_setting_font_size,80)
                }
                getString(R.string.font_size_middle)->{
                    setSpInt(R.string.pref_setting_font_size,100)
                }
                getString(R.string.font_size_large)->{
                    setSpInt(R.string.pref_setting_font_size,150)
                }
                getString(R.string.font_size_super_large)->{
                    setSpInt(R.string.pref_setting_font_size,200)
                }
            }
            binding.recyclerView.adapter?.notifyItemChanged(index)
        }
    }

    private fun getData(): List<Any> {
        val fontSize = getSpString(R.string.pref_setting_font_size_title,getString(R.string.font_size_middle))
        val list = LinkedList<Any>()
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        list.add(TickItem(title = getString(R.string.font_size_super_small), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                onSelect(data)
            }
        }, value = fontSize==getString(R.string.font_size_super_small)))
        list.add(TickItem(title = getString(R.string.font_size_small), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                onSelect(data)
            }
        }, value = fontSize==getString(R.string.font_size_small)))
        list.add(TickItem(title = getString(R.string.font_size_middle), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                onSelect(data)
            }
        }, value = fontSize==getString(R.string.font_size_middle)))
        list.add(TickItem(title = getString(R.string.font_size_large), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                onSelect(data)
            }
        }, value = fontSize==getString(R.string.font_size_large)))
        list.add(TickItem(title = getString(R.string.font_size_super_large), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                onSelect(data)
            }
        }, value = fontSize==getString(R.string.font_size_super_large)))
        return list
    }
}
