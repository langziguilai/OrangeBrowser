package com.dev.orangebrowser.bloc.browser.integration

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.extension.hide
import com.dev.base.extension.onGlobalLayoutComplete
import com.dev.base.extension.show
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.database.bookmark.BookMarkCategoryDao
import com.dev.browser.database.bookmark.BookMarkCategoryEntity
import com.dev.browser.database.bookmark.BookMarkDao
import com.dev.browser.database.bookmark.BookMarkEntity
import com.dev.browser.feature.session.SessionUseCases
import com.dev.browser.session.Session
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.browser.integration.helper.BottomPanelHelper
import com.dev.orangebrowser.bloc.browser.integration.helper.redirect
import com.dev.orangebrowser.data.model.ActionItem
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.orangebrowser.bloc.browser.view.WebViewToggleBehavior
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.extension.*
import com.dev.util.DensityUtil
import com.dev.view.GridView
import com.dev.view.dialog.DialogBuilder
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.GridDividerItemDecoration
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import com.noober.background.drawable.DrawableCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class BottomPanelMenuIntegration(
    private var binding: FragmentBrowserBinding,
    var fragment: BrowserFragment,
    var savedInstanceState: Bundle?,
    var bottomPanelHelper: BottomPanelHelper,
    var session: Session,
    var sessionUseCases: SessionUseCases,
    var bookMarkDao: BookMarkDao,
    var bookMarkCategoryDao: BookMarkCategoryDao,
    var activityViewModel: MainViewModel
) :
    LifecycleAwareFeature {
    private var sessionObserver: Session.Observer
    private var behavior: WebViewToggleBehavior? = null

    init {
        initBottomMenuData()
        initBottomMenuGridView(binding.bottomMenuGridView)
        if (binding.webViewContainer.layoutParams is CoordinatorLayout.LayoutParams) {
            val layoutParams = binding.webViewContainer.layoutParams as CoordinatorLayout.LayoutParams
            if (layoutParams.behavior is WebViewToggleBehavior) {
                behavior = layoutParams.behavior as WebViewToggleBehavior
            }
        }
        binding.bottomMenuGridViewClose.setOnClickListener {
            bottomPanelHelper.toggleBottomPanel()
        }
        binding.overLayerBottomPanel.setOnClickListener {
            bottomPanelHelper.toggleBottomPanel()
        }
        //开始的时候隐藏
        binding.bottomMenuPanel.apply {
            onGlobalLayoutComplete {
                fragment.context?.apply {
                    it.translationY = it.height.toFloat() + DensityUtil.dip2px(fragment.requireContext(), 16f)
                }
            }
        }
        sessionObserver = object : Session.Observer {
            override fun onDesktopModeChanged(session: Session, enabled: Boolean) {
                //自动重载，所以不需要在此处重新加载sessionUseCases.reload.invoke(session)
            }
        }
    }

    private fun initBottomMenuData() {
        //设置视野模式
        fragment.appDataForFragment.bottomMenuActionItems.find { it.id == R.string.ic_normal_screen }?.apply {

            val viewMode = if (session.visionMode != Session.NO_SET_SCREEN_MODE) {
                session.visionMode
            } else {
                fragment.getSpInt(R.string.pref_setting_view_mode, Session.NORMAL_SCREEN_MODE)
            }
            session.visionMode=viewMode
            if (viewMode == Session.NORMAL_SCREEN_MODE) {
                this.active = false
                this.iconRes = R.string.ic_normal_screen
            }
            if (viewMode == Session.SCROLL_FULL_SCREEN_MODE) {
                this.active = true
                this.iconRes = R.string.ic_auto_fullscreen
            }
            if (viewMode == Session.MAX_SCREEN_MODE) {
                this.active = true
                this.iconRes = R.string.ic_fullscreen
                binding.miniBottomBar.visibility = View.VISIBLE
            } else {
                binding.miniBottomBar.visibility = View.GONE
            }

        }

        //设置UserAgent
        fragment.appDataForFragment.bottomMenuActionItems.find { it.id == R.string.ic_desktop }?.apply {
            this.active = session.desktopMode
        }
        //设置无图模式
        fragment.appDataForFragment.bottomMenuActionItems.find { it.id == R.string.ic_forbid_image }?.apply {
            if (session.forbidImageMode) {
                this.active = true
                this.iconRes = R.string.ic_forbid_image
            } else {
                this.active = false
                this.iconRes = R.string.ic_image
            }
        }
        //设置隐私模式
        fragment.appDataForFragment.bottomMenuActionItems.find { it.id == R.string.ic_privacy }?.apply {
            this.active = session.private
        }
    }

    private fun initBottomMenuGridView(bottomMenuGridView: GridView) {
        val adapter = object : BaseQuickAdapter<ActionItem, CustomBaseViewHolder>(
            R.layout.item_bottom_action_item,
            fragment.appDataForFragment.bottomMenuActionItems
        ) {
            override fun convert(helper: CustomBaseViewHolder, item: ActionItem) {
                if (item.active) {
                    helper.setTextColor(R.id.icon, fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                    helper.setTextColor(R.id.name, fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                } else {
                    helper.setTextColor(R.id.icon, fragment.activityViewModel.theme.value!!.colorPrimary)
                    helper.setTextColor(R.id.name, fragment.activityViewModel.theme.value!!.colorPrimary)
                }
                helper.setText(R.id.icon, item.iconRes)
                helper.setText(R.id.name, item.nameRes)
            }
        }
        adapter.setOnItemClickListener { _, view, position ->
            onBottomMenuActionItemClick(view, fragment.appDataForFragment.bottomMenuActionItems[position])
        }
        bottomMenuGridView.adapter = adapter
    }

    //TODO:点击底部MenuItem
    private fun onBottomMenuActionItemClick(view: View, actionItem: ActionItem) {
        when (actionItem.id) {
            //无图模式
            R.string.ic_forbid_image -> {
                actionItem.active = !actionItem.active
                if (actionItem.active) {
                    view.findViewById<TextView>(R.id.icon)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                    view.findViewById<TextView>(R.id.name)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                } else {
                    view.findViewById<TextView>(R.id.icon)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                    view.findViewById<TextView>(R.id.name)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                }
                sessionUseCases.forbidLoadImage.invoke(actionItem.active)
                session.forbidImageMode = actionItem.active
            }
            //隐身
            R.string.ic_privacy -> {
                actionItem.active = !actionItem.active
                if (actionItem.active) {
                    view.findViewById<TextView>(R.id.icon)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                    view.findViewById<TextView>(R.id.name)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                } else {
                    view.findViewById<TextView>(R.id.icon)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                    view.findViewById<TextView>(R.id.name)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                }
                session.private = actionItem.active
            }
            //全局视野
            R.string.ic_normal_screen -> {
                var newVisionMode = Session.SCROLL_FULL_SCREEN_MODE
                when {
                    session.visionMode == Session.NORMAL_SCREEN_MODE -> {
                        newVisionMode = Session.SCROLL_FULL_SCREEN_MODE
                        actionItem.active = true
                        actionItem.iconRes = R.string.ic_auto_fullscreen
                        view.findViewById<TextView>(R.id.icon)
                            .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                        view.findViewById<TextView>(R.id.icon).setText(R.string.ic_auto_fullscreen)
                        view.findViewById<TextView>(R.id.name)
                            .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                    }
                    session.visionMode == Session.SCROLL_FULL_SCREEN_MODE -> {
                        newVisionMode = Session.MAX_SCREEN_MODE
                        actionItem.active = true
                        actionItem.iconRes = R.string.ic_fullscreen
                        view.findViewById<TextView>(R.id.icon).setText(R.string.ic_fullscreen)
                    }
                    session.visionMode == Session.MAX_SCREEN_MODE -> {
                        newVisionMode = Session.NORMAL_SCREEN_MODE
                        actionItem.active = false
                        actionItem.iconRes = R.string.ic_normal_screen
                        view.findViewById<TextView>(R.id.icon)
                            .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                        view.findViewById<TextView>(R.id.icon).setText(R.string.ic_normal_screen)
                        view.findViewById<TextView>(R.id.name)
                            .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                    }
                }
                session.visionMode = newVisionMode
                if (session.visionMode == Session.MAX_SCREEN_MODE) {
                    binding.miniBottomBar.visibility = View.VISIBLE
                } else {
                    binding.miniBottomBar.visibility = View.GONE
                }
                binding.fragmentContainer.requestLayout()
            }
            //电脑
            R.string.ic_desktop -> {
                actionItem.active = !actionItem.active
                session.desktopMode = actionItem.active
                if (actionItem.active) {
                    sessionUseCases.setUserAgent.invoke(
                        fragment.requireContext().getString(R.string.user_agent_pc),
                        session
                    )
                    sessionUseCases.requestDesktopSite.invoke(true, session)
                    view.findViewById<TextView>(R.id.icon)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                    view.findViewById<TextView>(R.id.name)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryActive)
                } else {
                    val ua = fragment.getSpString(R.string.pref_setting_ua)
                    if (ua != fragment.requireContext().getString(R.string.user_agent_pc)) {
                        sessionUseCases.setUserAgent.invoke(ua, session)
                    } else {
                        sessionUseCases.setUserAgent.invoke(
                            fragment.requireContext().getString(R.string.user_agent_android),
                            session
                        )
                    }
                    sessionUseCases.requestDesktopSite.invoke(false, session)
                    view.findViewById<TextView>(R.id.icon)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                    view.findViewById<TextView>(R.id.name)
                        .setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                }

            }
            //发现
            R.string.ic_found -> {
                bottomPanelHelper.toggleBottomPanel(Runnable {
                    redirect(binding = binding, session = session, runnable = kotlinx.coroutines.Runnable {
                        fragment.RouterActivity?.loadFoundFragment()
                    })
                })
            }
            //历史
            R.string.ic_history -> {
                bottomPanelHelper.toggleBottomPanel(Runnable {
                    redirect(binding = binding, session = session, runnable = kotlinx.coroutines.Runnable {
                        fragment.RouterActivity?.loadHistoryFragment()
                    })
                })

            }
            //书签
            R.string.ic_bookmark -> {
                bottomPanelHelper.toggleBottomPanel(Runnable {
                    redirect(binding = binding, session = session, runnable = kotlinx.coroutines.Runnable {
                        fragment.RouterActivity?.loadBookMarkFragment()
                    })
                })

            }
            //收藏
            R.string.ic_star -> {
                bottomPanelHelper.toggleBottomPanel(Runnable {
                    openBookMarkEditorDialog(
                        BookMarkEntity(
                            url = session.url,
                            title = session.title,
                            date = Date().time,
                            categoryName = ""
                        ),
                        view
                    )
                })
            }
            //主题
            R.string.ic_theme -> {
                bottomPanelHelper.toggleBottomPanel(Runnable {
                    redirect(binding = binding, session = session, runnable = kotlinx.coroutines.Runnable {
                        fragment.RouterActivity?.loadThemeFragment()
                    })
                })

            }
            //下载
            R.string.ic_download -> {
                bottomPanelHelper.toggleBottomPanel(Runnable {
                    redirect(binding = binding, session = session, runnable = kotlinx.coroutines.Runnable {
                        fragment.RouterActivity?.loadDownloadFragment()
                    })
                })

            }
            //设置
            R.string.ic_setting -> {
                bottomPanelHelper.toggleBottomPanel(Runnable {
                    redirect(binding = binding, session = session, runnable = kotlinx.coroutines.Runnable {
                        fragment.RouterActivity?.loadSettingFragment()
                    })
                })

            }
            //退出
            R.string.ic_quit -> {
                fragment.RouterActivity?.quit()
            }
        }
    }


    private var editBookMarkDialog: Dialog? = null
    private fun openBookMarkEditorDialog(bookMarkEntity: BookMarkEntity, editTextContainer: View) {
        editBookMarkDialog = DialogBuilder()
            .setLayoutId(R.layout.dialog_add_bookmark)
            .setOnViewCreateListener(object : DialogBuilder.OnViewCreateListener {
                override fun onViewCreated(view: View) {
                    initEditBookMarkDialog(view, bookMarkEntity, editTextContainer)
                }
            })
            .setCanceledOnTouchOutside(true)
            .setEnterAnimationId(R.anim.slide_up)
            .setExitAnimationId(R.anim.slide_down)
            .setGravity(Gravity.BOTTOM)
            .build(fragment.requireContext())
        editBookMarkDialog?.show()
    }

    var categories: List<BookMarkCategoryEntity>? = null
    var selectedCategory: BookMarkCategoryEntity? = null
    private fun initEditBookMarkDialog(view: View, bookMark: BookMarkEntity, editTextContainer: View) {
        fragment.launch(Dispatchers.IO) {
            val exitBookMark = bookMarkDao.getBookMarkByUrl(bookMark.url)
            categories = bookMarkCategoryDao.getCategoryList()
            launch(Dispatchers.Main) {
                val title = view.findViewById<EditText>(R.id.input_title)
                title.setText(bookMark.title)
                val url = view.findViewById<EditText>(R.id.url)
                url.setText(bookMark.url)
                val category = view.findViewById<EditText>(R.id.category)
                exitBookMark?.apply {
                    title.setText(exitBookMark.title)
                    url.setText(exitBookMark.url)
                    category.setText(exitBookMark.categoryName)
                }
                val recyclerView = view.findViewById<RecyclerView>(R.id.category_list).apply {
                    this.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                    this.addItemDecoration(
                        GridDividerItemDecoration(
                            DensityUtil.dip2px(fragment.requireContext(), 6f),
                            0,
                            fragment.getColor(R.color.transparent)
                        )
                    )
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
                                    .setCornersRadius(DensityUtil.dip2px(fragment.requireContext(), 1000f).toFloat())
                                    .setStrokeColor(fragment.getColor(R.color.color_EEEEEE))
                                    .setStrokeWidth(DensityUtil.dip2px(fragment.requireContext(), 1f).toFloat())
                                    .build()
                            helper.setTextColor(R.id.category, fragment.getColor(R.color.colorWhite))
                            helper.itemView.background = bg
                        } else {
                            val bg = DrawableCreator.Builder().setSolidColor(fragment.getColor(R.color.transparent))
                                .setCornersRadius(DensityUtil.dip2px(fragment.requireContext(), 1000f).toFloat())
                                .setStrokeColor(fragment.getColor(R.color.color_EEEEEE))
                                .setStrokeWidth(DensityUtil.dip2px(fragment.requireContext(), 1f).toFloat())
                                .build()
                            helper.itemView.background = bg
                            helper.setTextColor(R.id.category, fragment.getColor(R.color.colorBlack))
                        }
                        helper.setText(R.id.category, item.categoryName)
                        helper.itemView.setOnClickListener {
                            selectedCategory = item
                            category.setText(item.categoryName)
                            recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }
                }

                view.findViewById<View>(R.id.cancel).setOnClickListener {
                    editBookMarkDialog?.dismiss()
                }
                view.findViewById<View>(R.id.sure).setOnClickListener {
                    //若category不存在，则添加
                    if (category.text.toString().isNotBlank() && categories?.find { it.categoryName == category.text.toString() } == null) {
                        val categoryNew =
                            BookMarkCategoryEntity(date = Date().time, categoryName = category.text.toString())
                        fragment.launch(Dispatchers.IO) {
                            bookMarkCategoryDao.insert(categoryNew)
                        }
                    }
                    bookMark.title = title.text.toString()
                    bookMark.url = url.text.toString()
                    bookMark.categoryName = category.text.toString()
                    bookMark.date = Date().time
                    fragment.launch(Dispatchers.IO) {
                        bookMarkDao.delete(bookMark)
                        bookMarkDao.insert(bookMark)
                    }
                    editBookMarkDialog?.dismiss()
                }
            }
        }

    }


    override fun start() {
        session.register(sessionObserver)
    }

    override fun stop() {
        session.unregister(sessionObserver)
    }

}