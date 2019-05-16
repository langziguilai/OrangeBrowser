package com.dev.orangebrowser.bloc.setting.fragments.adblock

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.extension.*
import com.dev.base.support.BackHandler
import com.dev.base.support.isUrl
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
import com.dev.util.DensityUtil
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
        binding.add.setOnClickListener {
            showAddSubscriptionDialog()
        }
        binding.addSubscriptionContainer.onGlobalLayoutComplete{
            it.translationY=(it.measuredHeight+DensityUtil.dip2px(requireContext(),8f)).toFloat()
            binding.bottomOverlay.hide()
        }
        binding.bottomOverlay.setOnClickListener {
            hideAddSubscriptionDialog()
        }
        binding.cancelAddRuleBtn.setOnClickListener {
            hideAddSubscriptionDialog()
        }
        binding.addRuleBtn.setOnClickListener {
            val text=binding.addRuleText.text.toString()
            if (text.isUrl()){
                addSubscription(text)
                hideAddSubscriptionDialog()
            }else{
                Toast.makeText(requireContext(),getString(R.string.input_valid_url),Toast.LENGTH_SHORT).show()
            }
        }
    }
    //
    private fun showAddSubscriptionDialog(){
        binding.bottomOverlay.show()
        binding.addSubscriptionContainer.animate().alpha(1f).translationY(0f)
            .setInterpolator(DEFAULT_INTERPOLATOR).setDuration(NORMAL_ANIMATION).start()
    }
    private fun hideAddSubscriptionDialog(){
        binding.addRuleText.hideKeyboard()
        val view=binding.addSubscriptionContainer
        view.animate().alpha(0f).translationY((view.measuredHeight+DensityUtil.dip2px(requireContext(),8f)).toFloat())
            .withEndAction {
                binding.bottomOverlay.hide()
            }
            .setInterpolator(DEFAULT_INTERPOLATOR).setDuration(NORMAL_ANIMATION).start()
    }
    private lateinit var dataList: LinkedList<Any>
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
    //所有可以选择的订阅
    lateinit var totalSubscriptions:LinkedList<Subscription>
    private fun getData(): LinkedList<Any> {
        val list = LinkedList<Any>()
        list.add(DividerItem(height = 24, background = getColor(R.color.color_F8F8F8)))
        //初始化所有可选的订阅数据
        totalSubscriptions=LinkedList(adBlockEngine.recommendedSubscriptions.toList())
        settings?.subscriptions?.apply {
            this.forEach {
                val subscription=it
                //是否在推荐的Subscription中
                var isInRecommendSubscriptions=false
                adBlockEngine.recommendedSubscriptions.forEach {recommendSubscription->
                    if (recommendSubscription.url==subscription.url){
                        isInRecommendSubscriptions=true
                    }
                }
                if (!isInRecommendSubscriptions){
                    totalSubscriptions.add(subscription)
                }
            }
        }

        totalSubscriptions.forEachIndexed { _, subscription ->
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
        totalSubscriptions.forEachIndexed { _, subscription ->
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

    private fun addSubscription(url:String){
        launch(Dispatchers.IO) {
            val subscriptionPtr= adBlockEngine.filterEngine.getSubscription(url)
            val subscription=convertJsSubscription(subscriptionPtr)
            //如果不在列表中,就添加到列表中
            if (!subscriptionPtr.isListed){
                subscriptionPtr.addToList()
            }
            //
            var exist=false
            totalSubscriptions.forEach {
                if (it.url==subscription.url){
                    exist=true
                }
            }
            if (!exist){
                totalSubscriptions.add(subscription)
                dataList.add(TickItem(title = subscription.title, action = object : Action<TickItem> {
                    override fun invoke(data: TickItem) {
                        onSelect(data)
                    }
                }, value = true))
                selectedTitles.add(subscription.title)
                handleFilterListsChanged(selectedTitles)
                launch(Dispatchers.Main) {
                    binding.addRuleText.setText("")
                    binding.recyclerView.adapter?.notifyDataSetChanged()
                }

            }
        }
    }

    private fun convertJsSubscription(jsSubscription: org.adblockplus.libadblockplus.Subscription): Subscription {
        val subscription = Subscription()

        val jsTitle = jsSubscription.getProperty("label")
        try {
            subscription.title = jsTitle.toString()
        } finally {
            jsTitle.dispose()
        }

        val jsUrl = jsSubscription.getProperty("url")
        try {
            subscription.url = jsUrl.toString()
        } finally {
            jsUrl.dispose()
        }

        val jsSpecialization = jsSubscription.getProperty("specialization")
        try {
            subscription.specialization = jsSpecialization.toString()
        } finally {
            jsSpecialization.dispose()
        }

        return subscription
    }
}
