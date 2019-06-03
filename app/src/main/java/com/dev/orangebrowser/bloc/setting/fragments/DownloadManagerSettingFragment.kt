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
import com.dev.browser.feature.downloads.DownloadManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.bloc.setting.adapter.Adapter
import com.dev.orangebrowser.bloc.setting.viewholder.*
import com.dev.orangebrowser.bloc.setting.viewholder.base.Action
import com.dev.orangebrowser.databinding.FragmentDownloadManagerSettingBinding
import com.dev.orangebrowser.extension.*
import java.util.*

class DownloadManagerSettingFragment : BaseFragment(), BackHandler {


    companion object {
        const val Tag = "DownloadManagerSettingFragment"
        fun newInstance() = DownloadManagerSettingFragment()
    }

    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentDownloadManagerSettingBinding
    override fun onBackPressed(): Boolean {
        RouterActivity?.loadDownloadSettingFragment(enterAnimationId = R.anim.slide_right_in,exitAnimationId = R.anim.slide_right_out)
        return true

    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_download_manager_setting
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
        binding = FragmentDownloadManagerSettingBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
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
            setSpString(R.string.pref_setting_download_manager,data.title)
            //如果使用系统下载器
            if (data.title==getString(R.string.system_download_manager)){
                DownloadManager.getInstance(requireContext().applicationContext).setDownloadManager(useSystemDownloadManager = true)
            }else{
                DownloadManager.getInstance(requireContext().applicationContext).setDownloadManager(useSystemDownloadManager = false)
            }
            binding.recyclerView.adapter?.notifyItemChanged(index)
        }
    }

    private fun getData(): List<Any> {
        //是否使用系统下载器
        val useSystemDownloadManager=DownloadManager.getInstance(requireContext().applicationContext).useSystemDownloadManager
        val list = LinkedList<Any>()
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        list.add(TickItem(title = getString(R.string.system_download_manager), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                onSelect(data)
            }
        }, value = useSystemDownloadManager))
        //使用第三方下载器
        list.add(TickItem(title = getString(R.string.other_download_manager), action = object : Action<TickItem> {
            override fun invoke(data: TickItem) {
                onSelect(data)
            }
        }, value = !useSystemDownloadManager))
        return list
    }
}
