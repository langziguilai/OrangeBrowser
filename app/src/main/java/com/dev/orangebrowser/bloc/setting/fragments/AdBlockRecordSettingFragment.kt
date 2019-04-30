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
import com.dev.orangebrowser.data.dao.AdBlockRecordDao
import com.dev.orangebrowser.data.model.AdBlockRecord
import com.dev.orangebrowser.databinding.FragmentAbBlockRecordSettingBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AdBlockRecordSettingFragment : BaseFragment(), BackHandler {


    companion object {
        const val Tag = "AdBlockRecordSettingFragment"
        fun newInstance() = AdBlockRecordSettingFragment()
    }

    @Inject
    lateinit var dao: AdBlockRecordDao

    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentAbBlockRecordSettingBinding
    override fun onBackPressed(): Boolean {
        RouterActivity?.loadSettingFragment()
        return true

    }

    //TODO:获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_ab_block_record_setting
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
        binding = FragmentAbBlockRecordSettingBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
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
        binding.setting.setOnClickListener {
            //TODO:弹窗
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        launch(Dispatchers.IO) {
            val records = dao.getAll()
            launch(Dispatchers.Main) {
                val adapter = object :
                    BaseQuickAdapter<AdBlockRecord, CustomBaseViewHolder>(R.layout.item_ad_block_record, records) {
                    override fun convert(helper: CustomBaseViewHolder, item: AdBlockRecord) {
                            helper.setText(R.id.rule,item.rule)
                            helper.setText(R.id.count,getString(R.string.ab_block_match_count)+item.count.toString())
                    }

                }
                binding.recyclerView.adapter = adapter
            }
        }
    }
}
