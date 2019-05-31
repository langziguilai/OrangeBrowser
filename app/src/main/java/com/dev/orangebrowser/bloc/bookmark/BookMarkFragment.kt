package com.dev.orangebrowser.bloc.bookmark

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
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
import com.dev.browser.feature.tabs.TabsUseCases
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.databinding.FragmentBookmarkBinding
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
import com.noober.background.drawable.DrawableCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class BookMarkFragment : BaseFragment(), BackHandler {


    companion object {
        val Tag = "BookMarkFragment"
        val CATEGORY="CATEGORY"
        fun newInstance() = BookMarkFragment()
    }

    @Inject
    lateinit var bookMarkCategoryDao: BookMarkCategoryDao
    @Inject
    lateinit var bookMarkDao: BookMarkDao
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var tabUseCases: TabsUseCases

    lateinit var viewModel: BookMarkViewModel
    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentBookmarkBinding

    override fun onBackPressed(): Boolean {
        RouterActivity?.popUpToHomeOrBrowserFragment()
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(BookMarkViewModel::class.java)
    }

    override fun useDataBinding(): Boolean {
        return true
    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_bookmark
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBookmarkBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(activity!!, factory).get(MainViewModel::class.java)
        binding.activityViewModel = activityViewModel
        super.onActivityCreated(savedInstanceState)
    }

    var editBookMarkDialog: Dialog? = null
    var offSet: Int = -1
    var bookMarkAdapter: BaseQuickAdapter<LeftRightEntity<BookMarkCategoryEntity, BookMarkEntity>, CustomBaseViewHolder>? =
        null
    var bookMarkItems = LinkedList<LeftRightEntity<BookMarkCategoryEntity, BookMarkEntity>>()
    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        StatusBarUtil.setIconColor(requireActivity(),activityViewModel.theme.value!!.colorPrimary)
        offSet = DensityUtil.dip2px(requireContext(), 20f)
        binding.goBack.setOnClickListener {
            onBackPressed()
        }
        binding.add.setOnClickListener {
            if (editBookMarkDialog != null) {
                if (!editBookMarkDialog!!.isShowing) editBookMarkDialog?.show()
            } else {
                editBookMarkDialog = DialogBuilder()
                    .setLayoutId(R.layout.dialog_add_bookmark)
                    .setGravity(Gravity.BOTTOM)
                    .setCanceledOnTouchOutside(true)
                    .setOnViewCreateListener(object : DialogBuilder.OnViewCreateListener {
                        override fun onViewCreated(view: View) {
                            initEditBookMarkDialog(view)
                        }
                    }).build(requireContext())
                editBookMarkDialog?.show()
            }

        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        bookMarkAdapter =
            object : BaseQuickAdapter<LeftRightEntity<BookMarkCategoryEntity, BookMarkEntity>, CustomBaseViewHolder>(
                R.layout.item_book_mark,
                bookMarkItems
            ) {
                override fun convert(
                    helper: CustomBaseViewHolder,
                    item: LeftRightEntity<BookMarkCategoryEntity, BookMarkEntity>
                ) {
                    if (item.left != null) {
                        helper.setText(R.id.icon, getString(R.string.ic_tag))
                        helper.setText(R.id.title, item.left!!.categoryName)
                        helper.itemView.setOnClickListener {
                            openCategory(item.left!!)
                        }
                        helper.itemView.findViewById<View>(R.id.icon_right)?.show()
                    } else if (item.right != null) {
                        helper.setText(R.id.icon, getString(R.string.ic_bookmark))
                        helper.setText(R.id.title, item.right!!.title)
                        helper.itemView.setOnClickListener {
                            //打开这个history
                            tabUseCases.addTab.invoke(url = item.right!!.url)
                            RouterActivity?.loadBrowserFragment(sessionManager.selectedSession!!.id)
                        }
                        helper.itemView.findViewById<View>(R.id.icon_right)?.hide()
                    }
                    helper.setTextColor(R.id.icon,activityViewModel.theme.value!!.colorPrimary)
                    helper.setTextColor(R.id.icon_right,activityViewModel.theme.value!!.colorPrimary)

                }
            }
        bookMarkAdapter?.apply {
            binding.recyclerView.adapter = this
        }

        initContextMenu(bookMarkAdapter)
    }

    private fun openCategory(categoryEntity: BookMarkCategoryEntity) {
           val fragment= newInstance()
           fragment.arguments=Bundle().apply {
               putString(CATEGORY,categoryEntity.categoryName)
           }
           fragmentManager?.beginTransaction()?.replace(R.id.root_container,fragment)?.commit()
    }

    private var bookMarkItemContextMenu: Dialog? = null
    private var bookMarkCategoryItemContextMenu: Dialog? = null
    private fun initContextMenu(adapter: BaseQuickAdapter<LeftRightEntity<BookMarkCategoryEntity, BookMarkEntity>, CustomBaseViewHolder>?) {
        adapter?.setOnItemLongClickListener { _, _, position ->
            //如果是category
            if (bookMarkItems[position].left != null) {
                bookMarkCategoryItemContextMenu = DialogBuilder()
                    .setLayoutId(R.layout.dialog_context_menu)
                    .setHeightParent(1f)
                    .setWidthPercent(1f)
                    .setOnViewCreateListener(object : DialogBuilder.OnViewCreateListener {
                        override fun onViewCreated(view: View) {
                            initBookMarkCategoryItemContextMenuView(view, position)
                        }
                    })
                    .setGravity(Gravity.TOP)
                    .build(requireContext())
                bookMarkCategoryItemContextMenu?.show()
            } else if (bookMarkItems[position].right != null) {
                bookMarkItemContextMenu = DialogBuilder()
                    .setLayoutId(R.layout.dialog_context_menu)
                    .setHeightParent(1f)
                    .setWidthPercent(1f)
                    .setOnViewCreateListener(object : DialogBuilder.OnViewCreateListener {
                        override fun onViewCreated(view: View) {
                            initBookMarkItemContextMenuView(view, position)
                        }
                    })
                    .setGravity(Gravity.TOP)
                    .build(requireContext())
                bookMarkItemContextMenu?.show()
            }
            true
        }
    }

    private fun initBookMarkCategoryItemContextMenuView(view: View, position: Int) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            this.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            this.adapter = CommonContextMenuAdapter(
                R.layout.mozac_feature_contextmenu_item, listOf(

                    MenuItem(label = getString(R.string.menu_edit), action = object : Action<MenuItem> {
                        override fun execute(data: MenuItem) {
                            bookMarkItems[position].left?.apply {
                                openCategoryEditorDialog(this)
                            }
                            bookMarkItemContextMenu?.dismiss()
                        }
                    }),
                    MenuItem(label = getString(R.string.menu_delete), action = object : Action<MenuItem> {
                        override fun execute(data: MenuItem) {
                            bookMarkItems[position].left?.apply {
                                deleteCategory(this)
                            }
                            bookMarkItemContextMenu?.dismiss()
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

    private fun deleteCategory(entity: BookMarkCategoryEntity?) {
        entity?.apply {
            val category = this
            launch(Dispatchers.IO) {
                bookMarkCategoryDao.deleteCategory(category)
                bookMarkDao.updateCategoryName(category = category.categoryName, newCategoryName = "")
            }
            bookMarkItems.find { it.left==this }?.apply {
                bookMarkItems.remove(this)
            }
        }
        bookMarkCategoryItemContextMenu?.dismiss()
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

    private var editCategoryDialog: Dialog? = null
    private fun openCategoryEditorDialog(categoryEntity: BookMarkCategoryEntity) {
        editCategoryDialog = DialogBuilder()
            .setLayoutId(R.layout.dialog_edit_bookmark_category)
            .setOnViewCreateListener(object : DialogBuilder.OnViewCreateListener {
                override fun onViewCreated(view: View) {
                    initEditCategoryDialog(view, categoryEntity)
                }
            })
            .setEnterAnimationId(R.anim.slide_up)
            .setExitAnimationId(R.anim.slide_down)
            .setGravity(Gravity.BOTTOM)
            .build(requireContext())
        editCategoryDialog?.show()
        bookMarkCategoryItemContextMenu?.dismiss()
    }

    var categories: List<BookMarkCategoryEntity>? = null
    var selectedCategory: BookMarkCategoryEntity? = null
    private fun initEditCategoryDialog(view: View, categoryEntity: BookMarkCategoryEntity) {
        val category = view.findViewById<EditText>(R.id.category)
        val recyclerView = view.findViewById<RecyclerView>(R.id.category_list).apply {
            this.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            this.addItemDecoration(
                GridDividerItemDecoration(
                    DensityUtil.dip2px(requireContext(), 6f),
                    0,
                    getColor(R.color.transparent)
                )
            )
        }

        category.setText(categoryEntity.categoryName)

        launch(Dispatchers.IO) {
            categories = bookMarkCategoryDao.getCategoryList()
            launch(Dispatchers.Main) {
                categories?.apply {
                    if (this.isNotEmpty()) {
                        recyclerView.show()
                    } else {
                        recyclerView.hide()
                    }
                }
                recyclerView.adapter = object :
                    BaseQuickAdapter<BookMarkCategoryEntity, CustomBaseViewHolder>(R.layout.item_category, categories) {
                    override fun convert(helper: CustomBaseViewHolder, item: BookMarkCategoryEntity) {
                        if (categoryEntity != null && categoryEntity.categoryName == item.categoryName) {
                            val bg =
                                DrawableCreator.Builder().setSolidColor(activityViewModel.theme.value!!.colorPrimary)
                                    .setCornersRadius(DensityUtil.dip2px(requireContext(), 1000f).toFloat())
                                    .setStrokeColor(getColor(R.color.color_EEEEEE))
                                    .setStrokeWidth(DensityUtil.dip2px(requireContext(), 1f).toFloat())
                                    .build()
                            helper.setTextColor(R.id.category, getColor(R.color.colorWhite))
                            helper.itemView.background = bg
                        } else {
                            val bg = DrawableCreator.Builder().setSolidColor(getColor(R.color.transparent))
                                .setCornersRadius(DensityUtil.dip2px(requireContext(), 1000f).toFloat())
                                .setStrokeColor(getColor(R.color.color_EEEEEE))
                                .setStrokeWidth(DensityUtil.dip2px(requireContext(), 1f).toFloat())
                                .build()
                            helper.itemView.background = bg
                            helper.setTextColor(R.id.category, getColor(R.color.colorBlack))
                        }
                        helper.setText(R.id.category, item.categoryName)
                        helper.itemView.setOnClickListener {
                            selectedCategory = item
                            category.setText(item.categoryName)
                            recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        view.findViewById<View>(R.id.cancel).setOnClickListener {
            editCategoryDialog?.dismiss()
        }
        view.findViewById<View>(R.id.sure).setOnClickListener {
            val newCategory = category.text.toString()
            launch(Dispatchers.IO) {
                if (newCategory != categoryEntity.categoryName) {
                    //如果新的categoryName在已有的列表中不存在，就更新
                    if (categories?.find { it.categoryName == newCategory } == null) {
                        bookMarkCategoryDao.updateCategoryName(
                            category = categoryEntity.categoryName,
                            newCategory = newCategory
                        )
                        categoryEntity.categoryName = newCategory
                    } else { //如果已经存在,删除原有的数据
                        bookMarkItems.find { it.left == categoryEntity }?.apply {
                            bookMarkItems.remove(this)
                        }
                        bookMarkCategoryDao.deleteCategory(categoryEntity)
                    }
                    bookMarkDao.updateCategoryName(
                        category = categoryEntity.categoryName,
                        newCategoryName = newCategory
                    )
                }
                launch(Dispatchers.Main) {
                    editCategoryDialog?.dismiss()
                    binding.recyclerView.adapter?.notifyDataSetChanged()
                }
            }
        }
    }


    private fun initBookMarkItemContextMenuView(view: View, position: Int) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            this.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            this.adapter = CommonContextMenuAdapter(
                R.layout.mozac_feature_contextmenu_item, listOf(
                    MenuItem(label = getString(R.string.menu_share), action = object : Action<MenuItem> {
                        override fun execute(data: MenuItem) {
                            bookMarkItems[position].right?.apply {
                                shareLink(this)
                            }
                            bookMarkItemContextMenu?.dismiss()
                        }
                    }),
                    MenuItem(label = getString(R.string.menu_edit), action = object : Action<MenuItem> {
                        override fun execute(data: MenuItem) {
                            bookMarkItems[position].right?.apply {
                                openBookMarkEditorDialog(this)
                            }
                            bookMarkItemContextMenu?.dismiss()
                        }
                    }),
                    MenuItem(label = getString(R.string.menu_delete), action = object : Action<MenuItem> {
                        override fun execute(data: MenuItem) {
                            bookMarkItems[position].right?.apply {
                                deleteBookMarkItem(this, bookMarkItems[position])
                            }
                            bookMarkItemContextMenu?.dismiss()
                        }
                    }),
                    MenuItem(label = getString(R.string.menu_add_to_front), action = object : Action<MenuItem> {
                        override fun execute(data: MenuItem) {
                            bookMarkItems[position].right?.apply {
                                addToFrontPage(this)
                            }
                            bookMarkItemContextMenu?.dismiss()
                        }
                    }),

                    MenuItem(label = getString(R.string.menu_copy_link), action = object : Action<MenuItem> {
                        override fun execute(data: MenuItem) {
                            bookMarkItems[position].right?.apply {
                                copyLink(this)
                            }
                            bookMarkItemContextMenu?.dismiss()
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

    //TODO:添加到首页
    private fun addToFrontPage(bookMarkEntity: BookMarkEntity) {

    }

    private fun openBookMarkEditorDialog(bookMarkEntity: BookMarkEntity) {
        editBookMarkDialog = DialogBuilder()
            .setLayoutId(R.layout.dialog_add_bookmark)
            .setOnViewCreateListener(object : DialogBuilder.OnViewCreateListener {
                override fun onViewCreated(view: View) {
                    initEditBookMarkDialog(view, bookMarkEntity)
                }
            })
            .setEnterAnimationId(R.anim.slide_up)
            .setExitAnimationId(R.anim.slide_down)
            .setGravity(Gravity.BOTTOM)
            .build(requireContext())
        editBookMarkDialog?.show()
    }

    private fun initEditBookMarkDialog(view: View, bookMark: BookMarkEntity? = null) {
        val title = view.findViewById<EditText>(R.id.input_title)
        title.setText(bookMark?.title)
        val url = view.findViewById<EditText>(R.id.url)
        url.setText(bookMark?.url)
        val category = view.findViewById<EditText>(R.id.category)
        val recyclerView = view.findViewById<RecyclerView>(R.id.category_list).apply {
            this.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            this.addItemDecoration(
                GridDividerItemDecoration(
                    DensityUtil.dip2px(requireContext(), 6f),
                    0,
                    getColor(R.color.transparent)
                )
            )
        }
        launch(Dispatchers.IO) {
            categories = bookMarkCategoryDao.getCategoryList()
            launch(Dispatchers.Main) {
                bookMark?.apply {
                    category.setText(this.categoryName)
                    selectedCategory = BookMarkCategoryEntity(date = 0, categoryName = this.categoryName)
                }
                categories?.apply {
                    if (this.isNotEmpty()) {
                        recyclerView.show()
                    } else {
                        recyclerView.hide()
                    }
                }
                recyclerView.adapter = object :
                    BaseQuickAdapter<BookMarkCategoryEntity, CustomBaseViewHolder>(R.layout.item_category, categories) {
                    override fun convert(helper: CustomBaseViewHolder, item: BookMarkCategoryEntity) {
                        if (selectedCategory != null && selectedCategory!!.categoryName == item.categoryName) {
                            val bg =
                                DrawableCreator.Builder().setSolidColor(activityViewModel.theme.value!!.colorPrimary)
                                    .setCornersRadius(DensityUtil.dip2px(requireContext(), 1000f).toFloat())
                                    .setStrokeColor(getColor(R.color.color_EEEEEE))
                                    .setStrokeWidth(DensityUtil.dip2px(requireContext(), 1f).toFloat())
                                    .build()
                            helper.setTextColor(R.id.category, getColor(R.color.colorWhite))
                            helper.itemView.background = bg
                        } else {
                            val bg = DrawableCreator.Builder().setSolidColor(getColor(R.color.transparent))
                                .setCornersRadius(DensityUtil.dip2px(requireContext(), 1000f).toFloat())
                                .setStrokeColor(getColor(R.color.color_EEEEEE))
                                .setStrokeWidth(DensityUtil.dip2px(requireContext(), 1f).toFloat())
                                .build()
                            helper.itemView.background = bg
                            helper.setTextColor(R.id.category, getColor(R.color.colorBlack))
                        }
                        helper.setText(R.id.category, item.categoryName)
                        helper.itemView.setOnClickListener {
                            selectedCategory = item
                            category.setText(item.categoryName)
                            recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        view.findViewById<View>(R.id.cancel).setOnClickListener {
            editBookMarkDialog?.dismiss()
        }
        view.findViewById<View>(R.id.sure).setOnClickListener {
            //若category不存在，则添加
            if (category.text.toString().isNotBlank() && categories?.find { it.categoryName == category.text.toString() } == null) {
                val category = BookMarkCategoryEntity(date = Date().time, categoryName = category.text.toString())
                launch(Dispatchers.IO) {
                    bookMarkCategoryDao.insert(category)
                }
                bookMarkItems.add(LeftRightEntity(left = category))
            }
            var newBookMark: BookMarkEntity? = null
            if (bookMark == null) {
                newBookMark = BookMarkEntity(
                    title = title.text.toString(),
                    url = url.text.toString(),
                    date = Date().time,
                    categoryName = category.text.toString()
                )
            } else {
                bookMark.title = title.text.toString()
                bookMark.url = url.text.toString()
                bookMark.categoryName = category.text.toString()
            }

            launch(Dispatchers.IO) {
                if (bookMark != null) {
                    bookMarkDao.update(bookMark)
                    if (bookMark.categoryName.isNotBlank()){
                        bookMarkItems.find { it.right==bookMark }?.apply {
                            bookMarkItems.remove(this)
                        }

                    }
                }
                if (newBookMark != null) {
                    bookMarkDao.insert(newBookMark)
                    if (newBookMark.categoryName.isBlank()){
                        bookMarkItems.add(LeftRightEntity(right = newBookMark))
                    }
                }
                launch(Dispatchers.Main) {
                    editBookMarkDialog?.dismiss()
                    binding.recyclerView.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun shareLink(bookMarkEntity: BookMarkEntity) {
        if (!requireContext().shareLink(title = bookMarkEntity.title, url = bookMarkEntity.url)) {
            requireContext().showToast(getString(R.string.tip_share_fail))
        }
    }

    private fun copyLink(bookMarkEntity: BookMarkEntity) {
        requireContext().copyText(getString(R.string.link), bookMarkEntity.url)
        requireContext().showToast(getString(R.string.tip_copy_link))
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


    private fun deleteBookMarkItem(
        bookMarkEntity: BookMarkEntity,
        item: LeftRightEntity<BookMarkCategoryEntity, BookMarkEntity>
    ) {
        launch(Dispatchers.IO) {
            bookMarkDao.delete(bookMarkEntity)
            //当选中的Item的前面是Header，并且后面是Header或者无下一个Item时，删除Header
            bookMarkItems.remove(item)
            launch(Dispatchers.Main) {
                bookMarkAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        launch(Dispatchers.IO) {
            val category=arguments?.getString(CATEGORY)
            if (category.isNullOrBlank()){
                bookMarkCategoryDao.getCategoryList().forEach {
                    bookMarkItems.add(LeftRightEntity(left = it))
                }
                bookMarkDao.getBookMarkListByCategory("").forEach {
                    bookMarkItems.add(LeftRightEntity(right = it))
                }
            }else{
                bookMarkDao.getBookMarkListByCategory(category).forEach {
                    bookMarkItems.add(LeftRightEntity(right = it))
                }
                binding.title.text = category
            }
            launch(Dispatchers.Main) {

                binding.recyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }
}

data class LeftRightEntity<L, R>(var left: L? = null, var right: R? = null)