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
import com.dev.orangebrowser.data.dao.AdBlockFilterDao
import com.dev.orangebrowser.data.model.AdBlockFilter
import com.dev.orangebrowser.databinding.FragmentAdBlockFilterSettingBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.util.DensityUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class AdBlockFilterSettingFragment : BaseAdBlockSettingFragment(), BackHandler {

    companion object {
        const val Tag = "AdBlockFilterSettingFragment"
        fun newInstance() = AdBlockFilterSettingFragment()
    }
    @Inject
    lateinit var dao:AdBlockFilterDao
    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentAdBlockFilterSettingBinding
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
        return R.layout.fragment_ad_block_filter_setting
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAdBlockFilterSettingBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
        binding.lifecycleOwner=this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(activity!!, factory).get(MainViewModel::class.java)
        binding.activityViewModel = activityViewModel
        binding.backHandler=this
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
                   val rule= (dataList[position] as TickItem).title
                    deleteRule(rule)
                    dataList.removeAt(position)
                    binding.recyclerView.adapter?.notifyItemRemoved(position)
                }
            }

        }).attachToRecyclerView(binding.recyclerView)

        binding.addFilterContainer.onGlobalLayoutComplete{
            it.translationY=(it.measuredHeight+DensityUtil.dip2px(requireContext(),8f)).toFloat()
            binding.bottomOverlay.hide()
        }
    }
    fun addFilter(){
        val text=binding.addFilterText.text.toString().trim()
        if (isValidRule(text)){
            addFilter(text)
            hideAddFilterDialog()
        }else{
            Toast.makeText(requireContext(),getString(R.string.input_valid_rule),Toast.LENGTH_SHORT).show()
        }
    }
    //TODO:判断规则是否准确
    private fun isValidRule(rule:String):Boolean{
        return true
    }
    //
    fun showAddFilterDialog(){
        binding.bottomOverlay.show()
        binding.addFilterContainer.animate().alpha(1f).translationY(0f)
            .setInterpolator(DEFAULT_INTERPOLATOR).setDuration(NORMAL_ANIMATION).start()
    }
    fun hideAddFilterDialog(){
        binding.addFilterText.hideKeyboard()
        val view=binding.addFilterContainer
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
        dao.getAll().forEach {
            it.rule?.apply {
                list.add(TickItem(title = this, action = object : Action<TickItem> {
                    override fun invoke(data: TickItem) {
                    }
                }, value = false))
            }
        }

        return list
    }

    private fun addFilter(rule:String){
        launch(Dispatchers.IO) {
            val filerPtr= adBlockEngine.filterEngine.getFilter(rule)
            //如果不在列表中,就添加到列表中
            if (!filerPtr.isListed){
                filerPtr.addToList()
            }
            //如果不存在，则添加
            if(dao.count(rule)==0){
                val adBlockFilter=AdBlockFilter(rule = rule)
                dao.insertAll(adBlockFilter)
                dataList.add(
                    TickItem(title =adBlockFilter.rule ?: "", action = object : Action<TickItem> {
                        override fun invoke(data: TickItem) {
                        }
                    }, value = false)
                )
                launch(Dispatchers.Main) {
                    binding.addFilterText.setText("")
                    binding.recyclerView.adapter?.notifyItemInserted(dataList.size-1)
                }
            }
        }
    }
    private fun deleteRule(rule:String){
        launch(Dispatchers.IO) {
            dao.delete(rule)
            val filter=adBlockEngine.filterEngine.getFilter(rule)
            if (filter.isListed){
                filter.removeFromList()
            }
            launch(Dispatchers.Main) {
                Toast.makeText(requireContext(),getString(R.string.tip_rule_deleted),Toast.LENGTH_SHORT).show()
            }
        }
    }
}
