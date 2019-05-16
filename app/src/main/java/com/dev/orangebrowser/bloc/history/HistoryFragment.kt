package com.dev.orangebrowser.bloc.history

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.BaseFragment
import com.dev.base.extension.onGlobalLayoutComplete
import com.dev.base.support.BackHandler
import com.dev.browser.database.history.VisitHistoryDao
import com.dev.browser.database.history.VisitHistoryEntity
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.data.dao.FavoriteSiteDao
import com.dev.orangebrowser.databinding.FragmentHistoryBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.util.DensityUtil
import com.dev.view.dialog.DialogBuilder
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import com.dev.view.recyclerview.adapter.base.BaseSectionQuickAdapter
import com.dev.view.recyclerview.adapter.base.entity.SectionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class HistoryFragment : BaseFragment(), BackHandler {


    companion object {
        val Tag = "HistoryFragment"
        fun newInstance() = HistoryFragment()
    }

    @Inject
    lateinit var historyDao: VisitHistoryDao
    @Inject
    lateinit var favoriteSiteDao:FavoriteSiteDao

    lateinit var sessionManager: SessionManager
    lateinit var viewModel: HistoryViewModel
    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentHistoryBinding
    override fun onBackPressed(): Boolean {
        sessionManager.selectedSession?.apply {
            RouterActivity?.loadHomeOrBrowserFragment(this.id)
            return true
        }
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(HistoryViewModel::class.java)
    }

    override fun useDataBinding(): Boolean {
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHistoryBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(activity!!, factory).get(MainViewModel::class.java)
        binding.activityViewModel = activityViewModel
        super.onActivityCreated(savedInstanceState)
    }

    var clearHistoryDialog: Dialog? = null
    var offSet: Int = -1
    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        offSet = DensityUtil.dip2px(requireContext(), 20f)
        binding.goBack.setOnClickListener {
            onBackPressed()
        }
        binding.clear.setOnClickListener {
            if (clearHistoryDialog != null) {
                clearHistoryDialog?.show()
            }
            clearHistoryDialog = DialogBuilder()
                .setLayoutId(R.layout.dialog_delete_history)
                .setGravity(Gravity.CENTER)
                .setWidthPercent(0.9f)
                .setCanceledOnTouchOutside(true)
                .setOnViewCreateListener(object : DialogBuilder.OnViewCreateListener {
                    override fun onViewCreated(view: View) {
                        view.findViewById<View>(R.id.cancel).setOnClickListener {
                            clearHistoryDialog?.dismiss()
                        }
                        view.findViewById<View>(R.id.sure).setOnClickListener {
                            clearHistoryDialog?.dismiss()
                            clearHistory()
                        }
                    }
                }).createDialog(requireContext())
            clearHistoryDialog?.show()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        adapter = object : BaseSectionQuickAdapter<MySectionEntity, CustomBaseViewHolder>(
            R.layout.item_history,
            R.layout.item_history_header,
            sectionEntityList
        ) {
            override fun convert(helper: CustomBaseViewHolder, item: MySectionEntity) {
                helper.itemView.findViewById<TextView>(R.id.icon)
                    .setTextColor(activityViewModel.theme.value!!.colorPrimary)
                if (item.t.title.isBlank()) {
                    helper.setText(R.id.title, item.t.url)
                } else {
                    helper.setText(R.id.title, item.t.title)
                }

            }

            override fun convertHead(helper: CustomBaseViewHolder, item: MySectionEntity) {
                helper.setText(R.id.title, item.header)
            }
        }
        adapter?.apply {
            binding.recyclerView.adapter = this
        }
        adapter?.setPreLoadNumber(5)

        adapter?.setEnableLoadMore(true)
        adapter?.setOnLoadMoreListener({
            launch(Dispatchers.IO) {
                val addSize = addSectionEntityList()
                launch(Dispatchers.Main) {
                    if (addSize < 100) {
                        adapter?.loadMoreEnd()
                    }
                    adapter?.notifyDataSetChanged()
                }
            }
        }, binding.recyclerView)
        initHistoryItemDialog(adapter)
    }

    private fun initHistoryItemDialog(adapter: BaseQuickAdapter<MySectionEntity, CustomBaseViewHolder>?) {
        adapter?.setOnItemLongClickListener { _, _, position ->
            //如果不是header，那么就显示dialog
            if (!sectionEntityList[position].isHeader) {
                    DialogBuilder()
                        .setLayoutId(R.layout.dialog_process_history_item)
                        .setHeightParent(1f)
                        .setWidthPercent(1f)
                        .setOnViewCreateListener(object : DialogBuilder.OnViewCreateListener {
                            override fun onViewCreated(view: View) {
                                val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
                                    this.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                                    this.adapter = CommonContextMenuAdapter(
                                        R.layout.mozac_feature_contextmenu_item, listOf(
                                            MenuItem(label = getString(R.string.menu_delete),action = object:Action<MenuItem>{
                                                override fun execute(data: MenuItem) {
                                                    deleteHistoryItem(sectionEntityList[position].t)
                                                }
                                            }),
                                            MenuItem(label = getString(R.string.menu_add_book_mark),action = object:Action<MenuItem>{
                                                override fun execute(data: MenuItem) {
                                                    addToBookmark(sectionEntityList[position].t)
                                                }
                                            }),
                                            MenuItem(label = getString(R.string.menu_share),action = object:Action<MenuItem>{
                                                override fun execute(data: MenuItem) {
                                                    shareLink(sectionEntityList[position].t)
                                                }
                                            }),
                                            MenuItem(label = getString(R.string.menu_copy_link),action = object:Action<MenuItem>{
                                                override fun execute(data: MenuItem) {
                                                    copyLink(sectionEntityList[position].t)
                                                }
                                            })
                                        )
                                    )
                                }
                                recyclerView.onGlobalLayoutComplete {
                                    (it.layoutParams as? FrameLayout.LayoutParams)?.apply {
                                        this.leftMargin = calculateRecyclerViewLeftMargin(
                                            binding.container.width,
                                            it.width, binding.container.getLongClickPosition().x
                                        )
                                        this.topMargin = calculateRecyclerViewTopMargin(
                                            binding.container.height,
                                            it.height, binding.container.getLongClickPosition().y
                                        )
                                        it.layoutParams = this
                                    }
                                }
                            }
                        })
                        .setGravity(Gravity.TOP)
                        .createDialog(requireContext()).show()
            }
            true
        }
    }

    //计算左边的距离
    private fun calculateRecyclerViewLeftMargin(containerWidth: Int, childWidth: Int, x: Int): Int {
        return if (x + childWidth + offSet > containerWidth) {
            x - childWidth - offSet
        } else {
            x + offSet
        }
    }

    //计算左边的距离
    private fun calculateRecyclerViewTopMargin(containerHeight: Int, childHeight: Int, y: Int): Int {
        return when {
            y + childHeight / 2 + offSet > containerHeight -> containerHeight - childHeight - offSet
            y - childHeight / 2 - offSet < 0 -> offSet
            else -> y - childHeight / 2
        }
    }

    private fun clearHistory() {
        launch(Dispatchers.IO) {
            historyDao.clearHistory()
            sectionEntityList.clear()
            launch(Dispatchers.Main) {
                adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun deleteHistoryItem(item:VisitHistoryEntity){
        launch(Dispatchers.IO) {
            historyDao.deleteHistory(item)
        }
    }
    private fun addToBookmark(item:VisitHistoryEntity){

    }
    private fun shareLink(item:VisitHistoryEntity){

    }
    private fun copyLink(item:VisitHistoryEntity){

    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_history
    }

    private val groupTitleSet = HashSet<String>()
    val sectionEntityList = LinkedList<MySectionEntity>()
    var adapter: BaseSectionQuickAdapter<MySectionEntity, CustomBaseViewHolder>? = null
    var lastVisitHistoryEntity: VisitHistoryEntity? = null
    override fun initData(savedInstanceState: Bundle?) {
        launch(Dispatchers.IO) {
            val addSize = addSectionEntityList()
            launch(Dispatchers.Main) {
                if (addSize < 100) {
                    adapter?.loadMoreEnd()
                }
                adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun addSectionEntityList(): Int {
        var endTime = Date().time
        lastVisitHistoryEntity?.apply {
            endTime = this.date
        }
        val list = getHistory(endTime)
        lastVisitHistoryEntity = list.last()
        //分组
        list.forEach {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.date
            val groupTitle = getDay(calendar)
            if (groupTitleSet.contains(groupTitle)) {
                sectionEntityList.add(MySectionEntity(it))
            } else {
                groupTitleSet.add(groupTitle)
                sectionEntityList.add(MySectionEntity(isHeader = true, header = groupTitle))
                sectionEntityList.add(MySectionEntity(it))
            }
        }
        return list.size
    }

    private fun getHistory(end: Long): List<VisitHistoryEntity> {
        return historyDao.getVisitHistoryListByDate(end = end, limit = 100)
    }

    private fun getDay(calendar: Calendar): String {
        if (isToday(calendar)) {
            return getString(R.string.tip_today)
        }
        return getMonthString(calendar) + getString(R.string.date_month) +
                getDayString(calendar) + getString(R.string.date_day) + " " +
                getWeekDay(calendar)
    }

    private fun getMonthString(calendar: Calendar): String {
        val month = calendar.get(Calendar.MONTH) + 1
        if (month >= 10) {
            return month.toString()
        }
        return "0$month"
    }

    private fun getDayString(calendar: Calendar): String {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        if (day >= 10) {
            return day.toString()
        }
        return "0$day"
    }

    private fun getWeekDay(calendar: Calendar): String {
        when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> return getString(R.string.week_day_sunday)
            Calendar.MONDAY -> return getString(R.string.week_day_monday)
            Calendar.TUESDAY -> return getString(R.string.week_day_tuesday)
            Calendar.WEDNESDAY -> return getString(R.string.week_day_wednesday)
            Calendar.THURSDAY -> return getString(R.string.week_day_thursday)
            Calendar.FRIDAY -> return getString(R.string.week_day_friday)
            Calendar.SATURDAY -> return getString(R.string.week_day_saturday)
        }
        return ""
    }

    private val todayCalendar = Calendar.getInstance()
    private fun isToday(calendar: Calendar): Boolean {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        if (todayCalendar.get(Calendar.YEAR) == year
            && todayCalendar.get(Calendar.MONTH) == month
            && todayCalendar.get(Calendar.DAY_OF_MONTH) == day
        ) {
            return true
        }
        return false
    }
}

class MySectionEntity : SectionEntity<VisitHistoryEntity> {
    constructor(entity: VisitHistoryEntity) : super(entity)
    constructor(isHeader: Boolean, header: String) : super(isHeader, header)
}

interface Action<T> {
    fun execute(data: T)
}

data class MenuItem(
    var label: String,
    var icon: String? = null,
    var iconColor: Int = -1,
    var labelColor: Int = -1,
    var key: String = "",
    var action: Action<MenuItem>? = null
)

class CommonContextMenuAdapter(menuItemLayout: Int, dataList: List<MenuItem>) :
    BaseQuickAdapter<MenuItem, CustomBaseViewHolder>(menuItemLayout, dataList) {
    override fun convert(helper: CustomBaseViewHolder, item: MenuItem) {
        helper.setText(R.id.label, item.label)
        if (item.labelColor > 0) {
            helper.setTextColor(R.id.label, item.labelColor)
        }
        item.icon?.apply {
            helper.setText(R.id.icon, this)
            if (item.iconColor > 0) {
                helper.setTextColor(R.id.icon, item.iconColor)
            }
        }
        helper.itemView.apply {
            isClickable = true
            isFocusable = true
            setOnClickListener {
                item.action?.execute(item)
            }
        }
    }

}