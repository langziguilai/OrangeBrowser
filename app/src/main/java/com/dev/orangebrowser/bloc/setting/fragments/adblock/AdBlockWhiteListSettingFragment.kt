package com.dev.orangebrowser.bloc.setting.fragments.adblock

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.extension.*
import com.dev.base.support.BackHandler
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.bloc.setting.adapter.Adapter
import com.dev.orangebrowser.bloc.setting.viewholder.TickItem
import com.dev.orangebrowser.bloc.setting.viewholder.base.Action
import com.dev.orangebrowser.databinding.FragmentAdBlockWhiteListSettingBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.util.DensityUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class AdBlockWhiteListSettingFragment : BaseAdBlockSettingFragment(), BackHandler {

    companion object {
        const val Tag = "AdBlockWhiteListSettingFragment"
        fun newInstance() = AdBlockWhiteListSettingFragment()
    }

    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentAdBlockWhiteListSettingBinding
    override fun onBackPressed(): Boolean {
        RouterActivity?.loadAdBlockSettingFragment(enterAnimationId = R.anim.slide_right_in,exitAnimationId = R.anim.slide_right_out)
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
        return R.layout.fragment_ad_block_white_list_setting
    }
    override  fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAdBlockWhiteListSettingBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
        binding.lifecycleOwner=this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(activity!!, factory).get(MainViewModel::class.java)
        binding.activityViewModel = activityViewModel
        binding.backHandler = this
        binding.fragment=this
        super.onActivityCreated(savedInstanceState)
    }


    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        ItemTouchHelper(object:ItemTouchHelper.Callback(){
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                return makeMovementFlags(0, ItemTouchHelper.LEFT)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.adapterPosition
                if (position>=0){
                   val domain= (dataList[position] as TickItem).title
                    deleteDomain(domain)
                    dataList.removeAt(position)
                    binding.recyclerView.adapter?.notifyItemRemoved(position)
                }
            }

        }).attachToRecyclerView(binding.recyclerView)

        binding.addWhiteListContainer.onGlobalLayoutComplete{
            it.translationY=(it.measuredHeight+DensityUtil.dip2px(requireContext(),8f)).toFloat()
            binding.bottomOverlay.hide()
        }

    }
    fun addToWhiteList(){
        val text=binding.addWhiteListText.text.toString().trim()
        if (isValidSite(text)){
            addWhiteListDomain(text)
            hideAddWhiteListDialog()
        }else{
            Toast.makeText(requireContext(),getString(R.string.input_valid_rule),Toast.LENGTH_SHORT).show()
        }
    }
    //TODO:判断规则是否准确
    private fun isValidSite(rule:String):Boolean{
        return true
    }
    //
     fun showAddWhiteListDialog(){
        binding.bottomOverlay.show()
        binding.addWhiteListContainer.animate().alpha(1f).translationY(0f)
            .setInterpolator(DEFAULT_INTERPOLATOR).setDuration(NORMAL_ANIMATION).start()
    }
     fun hideAddWhiteListDialog(){
        binding.addWhiteListText.hideKeyboard()
        val view=binding.addWhiteListContainer
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

    private fun getData(): LinkedList<Any> {
        val list = LinkedList<Any>()
        settings?.whitelistedDomains?.apply {
            this.forEach {
                    list.add(TickItem(title = it, action = object : Action<TickItem> {
                        override fun invoke(data: TickItem) {
                        }
                    }, value = false))
            }
        }
        return list
    }

    private fun addWhiteListDomain(domain:String){
        settings?.apply {
            val localSettings=this
            var whitelistedDomains: MutableList<String>? = localSettings.whitelistedDomains
            if (whitelistedDomains == null) {
                whitelistedDomains = LinkedList()
                localSettings.whitelistedDomains = whitelistedDomains
            }
            // update and save settings
            if (!whitelistedDomains.contains(domain)){
                launch (Dispatchers.IO){
                    whitelistedDomains.add(domain)
                    adBlockSettingsStorage.save(localSettings)
                    adBlockEngine.whitelistedDomains = whitelistedDomains
                    dataList.add(TickItem(title =domain, action = object : Action<TickItem> {
                        override fun invoke(data: TickItem) {
                        }
                    }, value = false))
                    launch(Dispatchers.Main) {
                        binding.addWhiteListText.setText("")
                        binding.recyclerView.adapter?.notifyItemInserted(dataList.size-1)
                    }
                }
            }
        }

    }
    private fun deleteDomain(domain:String){
        settings?.apply {
            val localSettings=this
            var whitelistedDomains: MutableList<String>? = localSettings.whitelistedDomains
            if (whitelistedDomains == null) {
                whitelistedDomains = LinkedList()
                localSettings.whitelistedDomains = whitelistedDomains
            }
            // update and save settings
            if (whitelistedDomains.contains(domain)){
                launch (Dispatchers.IO){
                    whitelistedDomains.remove(domain)
                    adBlockSettingsStorage.save(localSettings)
                    adBlockEngine.whitelistedDomains = whitelistedDomains
                    dataList.add(TickItem(title =domain, action = object : Action<TickItem> {
                        override fun invoke(data: TickItem) {
                        }
                    }, value = false))
                    launch(Dispatchers.Main) {
                        Toast.makeText(requireContext(),getString(R.string.tip_domain_deleted),Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
