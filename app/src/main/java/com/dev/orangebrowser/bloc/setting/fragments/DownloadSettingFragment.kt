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
import com.dev.orangebrowser.bloc.setting.fragments.DownloadPathSettingFragment.Companion.START_PATH
import com.dev.orangebrowser.bloc.setting.viewholder.DividerItem
import com.dev.orangebrowser.bloc.setting.viewholder.SwitchItem
import com.dev.orangebrowser.bloc.setting.viewholder.TileItem
import com.dev.orangebrowser.bloc.setting.viewholder.base.Action
import com.dev.orangebrowser.databinding.FragmentDownloadSettingBinding
import com.dev.orangebrowser.extension.*
import java.util.*

class DownloadSettingFragment : BaseFragment(), BackHandler {


    companion object {
        const val Tag = "DownloadSettingFragment"
        fun newInstance() = DownloadSettingFragment()
    }

    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentDownloadSettingBinding
    override fun onBackPressed(): Boolean {
        fragmentManager?.popBackStack()
        return true

    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_download_setting
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
        binding = FragmentDownloadSettingBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
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


    private fun getData(): List<Any> {
        val list = LinkedList<Any>()
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))

        list.add(SwitchItem(title = getString(R.string.auto_install_download_app), action = object : Action<Boolean> {
            override fun invoke(data: Boolean) {
                DownloadManager.getInstance(requireContext().applicationContext).setAutoInstallApp(data)
            }
        }, value = DownloadManager.getInstance(requireContext().applicationContext).autoInstallDownloadApp))
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        var downloadManagerTitle=getString(R.string.system_download_manager)
        if (!DownloadManager.getInstance(requireContext().applicationContext).useSystemDownloadManager){
            downloadManagerTitle=getString(R.string.other_download_manager)
        }
        list.add(
            TileItem(
                title = getString(R.string.download_manager),
                tip = downloadManagerTitle,
                icon = getString(R.string.ic_right),
                action = object : Action<TileItem> {
                    override fun invoke(data: TileItem) {
                        RouterActivity?.addDownloadManagerSettingFragment()
                    }
                })
        )
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        list.add(
            TileItem(
                title = getString(R.string.download_path),
                tip = DownloadManager.getInstance(requireContext().applicationContext).downloadPath.removePrefix(START_PATH),
                icon = getString(R.string.ic_right),
                action = object : Action<TileItem> {
                    override fun invoke(data: TileItem) {
                        RouterActivity?.addDownloadPathSettingFragment()
                    }
                })
        )
        return list
    }
}
