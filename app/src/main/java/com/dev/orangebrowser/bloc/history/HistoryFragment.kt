package com.dev.orangebrowser.bloc.history

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.BaseFragment
import com.dev.base.extension.*
import com.dev.base.support.BackHandler
import com.dev.browser.database.bookmark.BookMarkCategoryDao
import com.dev.browser.database.bookmark.BookMarkCategoryEntity
import com.dev.browser.database.bookmark.BookMarkDao
import com.dev.browser.database.bookmark.BookMarkEntity
import com.dev.browser.database.history.VisitHistoryDao
import com.dev.browser.database.history.VisitHistoryEntity
import com.dev.browser.feature.tabs.TabsUseCases
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.databinding.FragmentHistoryBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.extension.getColor
import com.dev.orangebrowser.view.contextmenu.Action
import com.dev.orangebrowser.view.contextmenu.CommonContextMenuAdapter
import com.dev.orangebrowser.view.contextmenu.MenuItem
import com.dev.util.DensityUtil
import com.dev.view.StatusBarUtil
import com.dev.view.dialog.DialogBuilder
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.GridDividerItemDecoration
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import com.dev.view.recyclerview.adapter.base.BaseSectionQuickAdapter
import com.dev.view.recyclerview.adapter.base.entity.SectionEntity
import com.noober.background.drawable.DrawableCreator
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
    lateinit var bookMarkCategoryDao:BookMarkCategoryDao
    @Inject
    lateinit var bookMarkDao:BookMarkDao
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var tabUseCases: TabsUseCases
    lateinit var viewModel: HistoryViewModel
    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentHistoryBinding
    override fun onBackPressed(): Boolean {
        sessionManager.selectedSession?.apply {
            RouterActivity?.loadHomeOrBrowserFragment(this.id,R.anim.holder,R.anim.slide_right_out)
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

    var clearHistoryDialog: Dialog? = null
    var offSet: Int = -1
    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        StatusBarUtil.setIconColor(requireActivity(),activityViewModel.theme.value!!.colorPrimary)
        offSet = DensityUtil.dip2px(requireContext(), 20f)
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
        adapter?.setOnItemClickListener { _, _, position ->
            //如果不是header，那么就显示dialog
            if (!sectionEntityList[position].isHeader) {
                 //打开这个history
                 tabUseCases.addTab.invoke(url=sectionEntityList[position].t.url)
                 RouterActivity?.loadBrowserFragment(sessionManager.selectedSession!!.id)
            }
        }
        initHistoryItemDialog(adapter)
    }
    fun showClearHistoryDialog(){
        if (clearHistoryDialog != null) {
            if(!clearHistoryDialog!!.isShowing) clearHistoryDialog?.show()
        }else{
            clearHistoryDialog = DialogBuilder()
                .setLayoutId(R.layout.dialog_delete_history)
                .setGravity(Gravity.CENTER)
                .setWidthPercent(0.9f)
                .setCanceledOnTouchOutside(true)
                .setOnViewCreateListener(object : DialogBuilder.OnViewCreateListener {
                    override fun onViewCreated(view: View) {
                        view.findViewById<View>(R.id.cancel).setOnClickListener {
                            if (clearHistoryDialog!=null && clearHistoryDialog!!.isShowing){
                                clearHistoryDialog?.dismiss()
                            }
                        }
                        view.findViewById<View>(R.id.sure).setOnClickListener {
                            if (clearHistoryDialog!=null && clearHistoryDialog!!.isShowing){
                                clearHistoryDialog?.dismiss()
                            }
                            clearHistory()
                        }
                    }
                }).build(requireContext())
            clearHistoryDialog?.show()
        }
    }
    var historyItemDialog:Dialog?=null
    private fun initHistoryItemDialog(adapter: BaseQuickAdapter<MySectionEntity, CustomBaseViewHolder>?) {
        adapter?.setOnItemLongClickListener { _, _, position ->
            //如果不是header，那么就显示dialog
            if (!sectionEntityList[position].isHeader) {
                historyItemDialog=  DialogBuilder()
                        .setLayoutId(R.layout.dialog_context_menu)
                        .setHeightParent(1f)
                        .setWidthPercent(1f)
                        .setOnViewCreateListener(object : DialogBuilder.OnViewCreateListener {
                            override fun onViewCreated(view: View) {
                                initHistoryItemDialogView(view,position)
                            }
                        })
                        .setGravity(Gravity.TOP)
                        .build(requireContext())
                      historyItemDialog?.show()
            }
            true
        }
    }
    private fun initHistoryItemDialogView(view:View,position:Int){
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            this.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            this.adapter = CommonContextMenuAdapter(
                R.layout.mozac_feature_contextmenu_item, listOf(
                    MenuItem(label = getString(R.string.menu_delete),action = object: Action<MenuItem> {
                        override fun execute(data: MenuItem) {
                            deleteHistoryItem(position)
                            historyItemDialog?.dismiss()
                        }
                    }),
                    MenuItem(label = getString(R.string.menu_add_book_mark),action = object:Action<MenuItem>{
                        override fun execute(data: MenuItem) {
                            addToBookmark(position)
                            historyItemDialog?.dismiss()
                        }
                    }),
                    MenuItem(label = getString(R.string.menu_share),action = object:Action<MenuItem>{
                        override fun execute(data: MenuItem) {
                            shareLink(position)
                            historyItemDialog?.dismiss()
                        }
                    }),
                    MenuItem(label = getString(R.string.menu_copy_link),action = object:Action<MenuItem>{
                        override fun execute(data: MenuItem) {
                            copyLink(position)
                            historyItemDialog?.dismiss()
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
    //计算左边的距离
    private fun calculateRecyclerViewLeftMargin(containerWidth: Int, childWidth: Int, x: Int): Int {
        return if (x - childWidth - offSet > 0) {
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

    private fun deleteHistoryItem(position:Int){
        launch(Dispatchers.IO) {
            if(!sectionEntityList[position].isHeader){
                historyDao.deleteHistory(sectionEntityList[position].t)
                //当选中的Item的前面是Header，并且后面是Header或者无下一个Item时，删除Header
                if (sectionEntityList[position-1].isHeader && (sectionEntityList.size==position+1 || sectionEntityList[position+1].isHeader)){
                    //删除Item
                    sectionEntityList.removeAt(position)
                    //删除前面的Header
                    sectionEntityList.removeAt(position-1)
                }else{
                    //删除Item
                    sectionEntityList.removeAt(position)
                }
                launch(Dispatchers.Main) {
                    adapter?.notifyDataSetChanged()
                }
            }
        }
    }
    private var addBookMarkDialog:Dialog?=null
    private fun addToBookmark(position:Int){
        if(!sectionEntityList[position].isHeader){
            addBookMarkDialog=  DialogBuilder()
                .setLayoutId(R.layout.dialog_add_history_to_bookmark)
                .setOnViewCreateListener(object : DialogBuilder.OnViewCreateListener {
                    override fun onViewCreated(view: View) {
                        initAddBookMarkDialog(view,sectionEntityList[position].t)
                    }
                })
                .setEnterAnimationId(R.anim.slide_up)
                .setExitAnimationId(R.anim.slide_down)
                .setGravity(Gravity.BOTTOM)
                .build(requireContext())
            addBookMarkDialog?.show()
        }
    }
    var categories:List<BookMarkCategoryEntity>?=null
    var selectedCategory:BookMarkCategoryEntity?=null
    private fun initAddBookMarkDialog(view:View,historyEntity: VisitHistoryEntity){
        val title=view.findViewById<EditText>(R.id.input_title)
        title.setText(historyEntity.title)
        val url=view.findViewById<EditText>(R.id.url)
        url.setText(historyEntity.url)
        val category=view.findViewById<EditText>(R.id.category)
        val recyclerView = view.findViewById<RecyclerView>(R.id.category_list).apply {
            this.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            this.addItemDecoration(GridDividerItemDecoration(DensityUtil.dip2px(requireContext(),6f),0,getColor(R.color.transparent)))
        }
        var existBookMark:BookMarkEntity?=null
        launch(Dispatchers.IO) {
            categories=bookMarkCategoryDao.getCategoryList()
            existBookMark=bookMarkDao.getBookMarkByUrl(historyEntity.url)
            launch(Dispatchers.Main) {
                existBookMark?.apply {
                    category.setText(this.categoryName)
                    selectedCategory=BookMarkCategoryEntity(date=0,categoryName = this.categoryName)
                }
                categories?.apply {
                    if(this.isNotEmpty()){
                        recyclerView.show()
                    }else{
                        recyclerView.hide()
                    }
                }
                recyclerView.adapter=object:BaseQuickAdapter<BookMarkCategoryEntity,CustomBaseViewHolder>(R.layout.item_category,categories){
                    override fun convert(helper: CustomBaseViewHolder, item: BookMarkCategoryEntity) {
                         if (selectedCategory!=null && selectedCategory!!.categoryName==item.categoryName){
                             val bg= DrawableCreator.Builder().setSolidColor(activityViewModel.theme.value!!.colorPrimary)
                                  .setCornersRadius(DensityUtil.dip2px(requireContext(),1000f).toFloat())
                                  .setStrokeColor(getColor(R.color.color_EEEEEE)).setStrokeWidth(DensityUtil.dip2px(requireContext(),1f).toFloat())
                                  .build()
                             helper.setTextColor(R.id.category,getColor(R.color.colorWhite))
                             helper.itemView.background=bg
                         }else{
                             val bg= DrawableCreator.Builder().setSolidColor(getColor(R.color.transparent))
                                 .setCornersRadius(DensityUtil.dip2px(requireContext(),1000f).toFloat())
                                 .setStrokeColor(getColor(R.color.color_EEEEEE)).setStrokeWidth(DensityUtil.dip2px(requireContext(),1f).toFloat())
                                 .build()
                             helper.itemView.background=bg
                             helper.setTextColor(R.id.category,getColor(R.color.colorBlack))
                         }
                         helper.setText(R.id.category,item.categoryName)
                         helper.itemView.setOnClickListener {
                             selectedCategory=item
                             category.setText(item.categoryName)
                             recyclerView.adapter?.notifyDataSetChanged()
                         }
                    }
                }
            }
        }

        view.findViewById<View>(R.id.cancel).setOnClickListener {
            addBookMarkDialog?.dismiss()
        }
        view.findViewById<View>(R.id.sure).setOnClickListener {
            //若category不存在，则添加
            if(category.text.toString().isNotBlank() && categories?.find { it.categoryName==category.text.toString() }==null){
                 val categoryNew=BookMarkCategoryEntity(date=Date().time,categoryName = category.text.toString())
                 launch(Dispatchers.IO) {
                     bookMarkCategoryDao.insert(categoryNew)
                 }
            }

            val bookMark=BookMarkEntity(
                title=title.text.toString(),
                url = url.text.toString(),
                date=Date().time,
                categoryName = category.text.toString()
            )
            launch(Dispatchers.IO) {
                if (existBookMark!=null){
                    bookMarkDao.delete(existBookMark!!)
                }
                bookMarkDao.insert(bookMark)
                launch(Dispatchers.Main) {
                    addBookMarkDialog?.dismiss()
                }
            }
        }
    }
    private fun shareLink(position:Int){
        val mySectionEntity=sectionEntityList[position]
        if(!mySectionEntity.isHeader){
             if(!requireContext().shareLink(title =mySectionEntity.t.title,url =mySectionEntity.t.url)){
                 requireContext().showToast(getString(R.string.tip_share_fail))
             }
        }
    }
    private fun copyLink(position:Int){
        if(!sectionEntityList[position].isHeader){
             requireContext().copyText(getString(R.string.link),sectionEntityList[position].t.url)
             requireContext().showToast(getString(R.string.tip_copy_link))
        }
    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_history
    }

    private val groupTitleSet = HashSet<String>()
    val sectionEntityList = LinkedList<MySectionEntity>()
    var adapter: BaseSectionQuickAdapter<MySectionEntity, CustomBaseViewHolder>? = null
    private var lastVisitHistoryEntity: VisitHistoryEntity? = null
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
        if (list.isNotEmpty()){
            lastVisitHistoryEntity = list.last()
        }
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





