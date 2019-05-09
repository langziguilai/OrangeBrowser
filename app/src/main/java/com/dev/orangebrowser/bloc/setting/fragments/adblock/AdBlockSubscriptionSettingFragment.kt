package com.dev.orangebrowser.bloc.setting.fragments.adblock

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.support.BackHandler
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.bloc.setting.adapter.Adapter
import com.dev.orangebrowser.bloc.setting.viewholder.DividerItem
import com.dev.orangebrowser.bloc.setting.viewholder.TickItem
import com.dev.orangebrowser.bloc.setting.viewholder.base.Action
import com.dev.orangebrowser.databinding.FragmentAdBlockSubscriptionSettingBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.extension.getColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.adblockplus.libadblockplus.android.Subscription
import java.util.*

class AdBlockSubscriptionSettingFragment : BaseAdBlockSettingFragment(), BackHandler {

    companion object {
        const val Tag = "AdBlockSubscriptionSettingFragment"
        fun newInstance() = AdBlockSubscriptionSettingFragment()
    }

    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentAdBlockSubscriptionSettingBinding
    override fun onBackPressed(): Boolean {
        RouterActivity?.loadAdBlockSettinglFragment()
        return true

    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_ad_block_subscription_setting
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
        binding = FragmentAdBlockSubscriptionSettingBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
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
        launch(Dispatchers.IO) {
            dataList = getData()
            launch(Dispatchers.Main) {
                val adapter = Adapter(dataList)
                binding.recyclerView.adapter = adapter
            }
        }
    }
    private var selectedTitles=LinkedList<String>()
    private fun onSelect(data:TickItem) {
        if (selectedTitles.contains(data.title)){
            selectedTitles.remove(data.title)
        }else{
            selectedTitles.add(data.title)
        }

        dataList.forEachIndexed { index, item ->
            (item as? TickItem)?.apply {
                    this.value = selectedTitles.contains(item.title)
            }
        }
        binding.recyclerView.adapter?.notifyDataSetChanged()
        launch(Dispatchers.IO) {
            handleFilterListsChanged(selectedTitles)
        }
    }

    private fun getData(): List<Any> {
        val list = LinkedList<Any>()
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        adBlockEngine.listedSubscriptions.forEachIndexed { index, subscription ->
            var selected=false
            settings?.subscriptions?.apply {
                forEach { it ->
                    if (it.url== subscription.url){
                        selected=true
                        selectedTitles.add(subscription.title)
                    }
                }
            }
            list.add(TickItem(title = subscription.title, action = object : Action<TickItem> {
                override fun invoke(data: TickItem) {
                    onSelect(data)
                }
            }, value = selected))
        }
        return list
    }

    private fun handleFilterListsChanged(selectedTitles: LinkedList<String>) {
        val selectedSubscriptions = LinkedList<Subscription>()
        val selectedSubscriptionUrls=LinkedList<String>()
        adBlockEngine.recommendedSubscriptions.forEachIndexed { _, subscription ->
            if (selectedTitles.contains(subscription.title)){
                selectedSubscriptions.add(subscription)
                selectedSubscriptionUrls.add(subscription.url)
            }
        }

        // update and save settings
        settings?.subscriptions = selectedSubscriptions
        adBlockSettingsStorage.save(settings)

        // apply settings
        adBlockEngine.setSubscriptions(selectedSubscriptionUrls)

        // since 'aa enabled' setting affects subscriptions list, we need to set it again
        adBlockEngine.isAcceptableAdsEnabled = settings!!.isAcceptableAdsEnabled

    }
    //TODO:添加订阅
    private fun addSubscription(url:String){
        launch(Dispatchers.IO) {
            val subscription= adBlockEngine.filterEngine.getSubscription(url)
            //如果不在列表中
            if (!subscription.isListed){
                  launch(Dispatchers.Main) {

                  }
            }
        }
    }
    //TODO:自定义过滤器
    private fun addFilter(filter:String){

    }
}
