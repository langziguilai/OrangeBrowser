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
import com.dev.orangebrowser.databinding.FragmentAddressBarShowItemSettingBinding
import com.dev.orangebrowser.extension.*
import java.util.*

class AddressBarSettingFragment : BaseFragment(), BackHandler {


    companion object {
        const val Tag = "AddressBarSettingFragment"
        fun newInstance() = AddressBarSettingFragment()
    }

    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentAddressBarShowItemSettingBinding
    override fun onBackPressed(): Boolean {
        RouterActivity?.loadGeneralSettingFragment(enterAnimationId = R.anim.slide_right_in,exitAnimationId = R.anim.slide_right_out)
        return true

    }

    override fun useDataBinding(): Boolean {
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
    }
    override fun getLayoutResId(): Int {
        return R.layout.fragment_address_bar_show_item_setting
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding =
            FragmentAddressBarShowItemSettingBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
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

    private fun onSelect(data: TickItem) {
        var selectedIndex = -1
        dataList.forEach {
            (it as? TickItem)?.apply {
                if (it.value) {
                    selectedIndex = dataList.indexOf(it)
                    it.value = false
                }
            }
        }
        if (selectedIndex >= 0) {
            binding.recyclerView.adapter?.notifyItemChanged(selectedIndex)
        }

        val index = dataList.indexOf(data)
        if (index >= 0) {
            setSpString(R.string.pref_setting_address_bar_show_title, data.title)
            data.value = true
            binding.recyclerView.adapter?.notifyItemChanged(index)
        }
    }

    //TODO:添加Action
    private fun getData(): List<Any> {
        val showAddressBar = getSpString(R.string.pref_setting_show_address_bar, getString(R.string.show_title))
        val list = LinkedList<Any>()
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        list.add(TickItem(title = getString(R.string.show_domain), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                onSelect(data)
            }
        }, value = showAddressBar == getString(R.string.show_domain)))
        list.add(TickItem(title = getString(R.string.show_title), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                onSelect(data)
            }
        }, value = showAddressBar == getString(R.string.show_title)))
        list.add(TickItem(title = getString(R.string.show_address), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                onSelect(data)
            }
        }, value = showAddressBar == getString(R.string.show_address)))
        return list
    }
}
