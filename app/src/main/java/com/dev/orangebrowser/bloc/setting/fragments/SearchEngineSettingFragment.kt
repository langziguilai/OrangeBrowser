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
import com.dev.browser.search.SearchEngineManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.bloc.setting.adapter.Adapter
import com.dev.orangebrowser.bloc.setting.viewholder.*
import com.dev.orangebrowser.bloc.setting.viewholder.base.Action
import com.dev.orangebrowser.databinding.FragmentSearchEngineSettingBinding
import com.dev.orangebrowser.extension.*
import java.util.*
import javax.inject.Inject

class SearchEngineSettingFragment : BaseFragment(), BackHandler {


    companion object {
        const val Tag = "SearchEngineSettingFragment"
        fun newInstance() = SearchEngineSettingFragment()
    }

    @Inject
    lateinit var searchEngineManager: SearchEngineManager
    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentSearchEngineSettingBinding
    override fun onBackPressed(): Boolean {
       RouterActivity?.loadSettingFragment()
        return true

    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_search_engine_setting
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
        binding = FragmentSearchEngineSettingBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
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
            binding.recyclerView.adapter?.notifyItemChanged(index)
        }
    }


    private fun getData(): List<Any> {
        val engineSelected = getSpString(R.string.pref_setting_search_engine_name,"Bing")
        val list = LinkedList<Any>()
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        searchEngineManager.getSearchEngines(requireContext()).forEachIndexed { index, it ->
            var value = false
            if (engineSelected == it.name) {
                value = true
            }

            list.add(TickItem(title = it.name, action = object : Action<TickItem> {
                override fun invoke(data: TickItem) {
                    setSpString(R.string.pref_setting_search_engine_id, it.identifier)
                    setSpString(R.string.pref_setting_search_engine_name, it.name)
                    onSelect(data)
                }
            }, value = value))
        }

        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))

//TODO：开始自定义搜索引擎
//        list.add(
//            TileItem(
//                label = getString(R.string.custom_search_engine),
//                tip = "",
//                icon = getString(R.string.ic_right),
//                action = object : Action<TileItem> {
//                    override fun invoke(data: TileItem) {
//
//                    }
//                })
//        )
        return list
    }
}
