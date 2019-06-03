package com.dev.orangebrowser.bloc.setting

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.support.BackHandler
import com.dev.browser.search.SearchEngineManager
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.bloc.setting.adapter.Adapter
import com.dev.orangebrowser.bloc.setting.fragments.adblock.BaseAdBlockSettingFragment
import com.dev.orangebrowser.bloc.setting.viewholder.*
import com.dev.orangebrowser.bloc.setting.viewholder.base.Action
import com.dev.orangebrowser.databinding.FragmentMainSettingBinding
import com.dev.orangebrowser.extension.*
import com.dev.view.StatusBarUtil
import java.util.*
import javax.inject.Inject

class SettingFragment : BaseAdBlockSettingFragment(),BackHandler {
    companion object {
        const val Tag="SettingFragment"
        fun newInstance() = SettingFragment()
    }
    @Inject
    lateinit var searchEngineManager: SearchEngineManager
    @Inject
    lateinit var sessionManager:SessionManager
    lateinit var viewModel: SettingViewModel
    lateinit var activityViewModel: MainViewModel
    lateinit var binding:FragmentMainSettingBinding
    override fun onBackPressed(): Boolean {
        sessionManager.selectedSession?.apply {
            RouterActivity?.loadHomeOrBrowserFragment(this.id,R.anim.slide_right_in,R.anim.slide_right_out)
        }
        return true
    }
    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_main_setting
    }

    override fun useDataBinding(): Boolean {
        return true
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel=ViewModelProviders.of(this,factory).get(SettingViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding= FragmentMainSettingBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
        binding.lifecycleOwner=this
        return binding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel=ViewModelProviders.of(activity!!,factory).get(MainViewModel::class.java)
        binding.activityViewModel=activityViewModel
        binding.backHandler=this
        super.onActivityCreated(savedInstanceState)
    }


    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        StatusBarUtil.setIconColor(requireActivity(),activityViewModel.theme.value!!.colorPrimary)
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
    }
    override fun initData(savedInstanceState: Bundle?) {
        val adapter=Adapter(getData())

        binding.recyclerView.adapter=adapter
    }
    private fun getData():List<Any>{
        val list=LinkedList<Any>()
        list.add(DividerItem(height=24,background =getColor(R.color.color_F8F8F8)))
        var loginTip=getString(R.string.login_register)
        if (getSpBool(R.string.pref_setting_is_login)){
            loginTip=getSpString(R.string.pref_user_email)
        }
        list.add(TileItem(title=getString(R.string.cloud_sync),tip=loginTip,icon = getString(R.string.ic_right),action = object:Action<TileItem>{
            override fun invoke(data: TileItem) {
                 RouterActivity?.loadAccountFragment()
            }
        }))
        list.add(DividerItem(height=24,background =getColor(R.color.color_F8F8F8)))
        list.add(TileItem(title=getString(R.string.general_setting),tip="",icon = getString(R.string.ic_right),action = object:Action<TileItem>{
            override fun invoke(data: TileItem) {
                  RouterActivity?.loadGeneralSettingFragment()
            }
        }))
        list.add(TileItem(title=getString(R.string.web_setting),tip="",icon = getString(R.string.ic_right),action = object:Action<TileItem>{
            override fun invoke(data: TileItem) {
                RouterActivity?.loadWebSettingFragment()
            }
        }))
        list.add(TileItem(title=getString(R.string.clear_cache),tip="",icon = getString(R.string.ic_right),action = object:Action<TileItem>{
            override fun invoke(data: TileItem) {
                  RouterActivity?.loadCacheSettingFragment()
            }
        }))
        var adBlockTip=getString(R.string.closed)
        settings?.isAdblockEnabled?.apply {
            if(this){
                adBlockTip=getString(R.string.opened)
            }
        }

        list.add(TileItem(title=getString(R.string.ad_block),tip=adBlockTip,icon = getString(R.string.ic_right),action = object:Action<TileItem>{
            override fun invoke(data: TileItem) {
                       RouterActivity?.loadAdBlockSettingFragment()
            }
        }))
        //
        list.add(DividerItem(height=24,background =getColor(R.color.color_F8F8F8)))
        list.add(TileItem(title=getString(R.string.library),tip=getString(R.string.custom_config),icon = getString(R.string.ic_right),action = object:Action<TileItem>{
            override fun invoke(data: TileItem) {
                   RouterActivity?.loadLibrarySettingFragment()
            }
        }))
        //TODO:手势支持
//        list.add(TileItem(title=getString(R.string.action_and_gesture),tip="",icon = getString(R.string.ic_right),action = object:Action<TileItem>{
//            override fun invoke(data: TileItem) {
//                  RouterActivity?.loadGestureSettingFragment()
//            }
//        }))
        //
        var searchEngineName: String
        getSpString(R.string.pref_setting_search_engine_name,"Bing").apply {
            searchEngineManager.getDefaultSearchEngine(requireContext(),this).apply {
                searchEngineName=this.name
            }
        }

        list.add(TileItem(title=getString(R.string.search_engine),tip=searchEngineName,icon = getString(R.string.ic_right),action = object:Action<TileItem>{
            override fun invoke(data: TileItem) {
                 RouterActivity?.loadSearchEngineSettingFragment()
            }
        }))

        list.add(TileItem(title=getString(R.string.download_setting),tip="",icon = getString(R.string.ic_right),action = object:Action<TileItem>{
            override fun invoke(data: TileItem) {
                RouterActivity?.loadDownloadSettingFragment()
            }
        }))
        list.add(DividerItem(height=24,background =getColor(R.color.color_F8F8F8)))
        var author: String
        getSpString(R.string.pref_setting_software_author,"").apply {
            author=this
        }
        var version: String
        getSpString(R.string.pref_setting_software_version,"").apply {
            version=this
        }
        list.add(TileItem(title=getString(R.string.check_update),tip=version,icon = getString(R.string.ic_right),action = object:Action<TileItem>{
            override fun invoke(data: TileItem) {
                  //TODO
            }
        }))
        list.add(TileItem(title=getString(R.string.about_us),tip="",icon = getString(R.string.ic_right),action = object:Action<TileItem>{
            override fun invoke(data: TileItem) {
                     //TODO
            }
        }))
        return list
    }
}
