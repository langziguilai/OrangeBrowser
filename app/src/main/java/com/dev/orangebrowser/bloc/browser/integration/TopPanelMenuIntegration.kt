package com.dev.orangebrowser.bloc.browser.integration

import android.os.Bundle
import android.util.Log
import androidx.annotation.ColorRes
import com.dev.base.extension.hide
import com.dev.base.extension.onGlobalLayoutComplete
import com.dev.base.extension.shareLink
import com.dev.base.extension.showToast
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.concept.EngineSession
import com.dev.browser.feature.tabs.TabsUseCases
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.browser.integration.helper.TopPanelHelper
import com.dev.orangebrowser.bloc.browser.integration.helper.redirect
import com.dev.orangebrowser.data.dao.SavedFileDao
import com.dev.orangebrowser.data.model.ActionItem
import com.dev.orangebrowser.data.model.SavedFile
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appData
import com.dev.orangebrowser.extension.getSpBool
import com.dev.view.GridView
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.util.*


class TopPanelMenuIntegration(
    var binding: FragmentBrowserBinding,
    var fragment: BrowserFragment,
    var session: Session,
    var sessionManager: SessionManager,
    var tabsUseCases: TabsUseCases,
    var savedInstanceState: Bundle?,
    var topPanelHelper: TopPanelHelper,
    var savedFileDao: SavedFileDao,
    var findInPageIntegration: FindInPageIntegration
) :
    LifecycleAwareFeature {

    init {
        initTopMenuData()
        initTopMenuGridView(binding.topMenuPanel)
        binding.overLayerTopPanel.setOnClickListener {
            topPanelHelper.toggleTopPanel()
        }
        //initial hide
        binding.topMenuPanel.apply {
            onGlobalLayoutComplete {
                binding.topMenuPanelContainer.hide()
                fragment.context?.apply {
                    it.translationY = -it.height.toFloat() - binding.topBar.height.toFloat()
                }
            }
        }

    }
    private fun initTopMenuData(){
        val result = LinkedList<ActionItem>()
        fragment.getSpBool(R.string.pref_setting_enable_lib_scan, true).apply {
            if (this){
                result.add(ActionItem(nameRes = R.string.scan, iconRes = R.string.ic_scan, id = R.string.ic_scan))
            }
        }
        fragment.getSpBool(R.string.pref_setting_enable_lib_share, true).apply {
            if (this){
                result.add(ActionItem(nameRes = R.string.share, iconRes = R.string.ic_share, id = R.string.ic_share))
            }
        }
        fragment.getSpBool(R.string.pref_setting_enable_lib_read_mode, true).apply {
            if (this){
                result.add(ActionItem(nameRes = R.string.read_mode, iconRes = R.string.ic_read, id = R.string.ic_read))
            }
        }
        fragment.getSpBool(R.string.pref_setting_enable_lib_image_mode, true).apply {
            if (this){
                result.add(ActionItem(nameRes = R.string.image_mode, iconRes = R.string.ic_image, id = R.string.ic_image))
            }
        }
        fragment.getSpBool(R.string.pref_setting_enable_lib_find_in_page, true).apply {
            if (this){
                result.add(ActionItem(nameRes = R.string.find_in_page, iconRes = R.string.ic_search, id = R.string.ic_search))
            }
        }
        fragment.getSpBool(R.string.pref_setting_enable_lib_save_resource_offline, true).apply {
            if (this){
                result.add(ActionItem(nameRes = R.string.save_resource_offline, iconRes = R.string.ic_save, id = R.string.ic_save))
            }
        }
        fragment.getSpBool(R.string.pref_setting_enable_lib_translation, true).apply {
            if (this){
                result.add(ActionItem(nameRes = R.string.translation, iconRes = R.string.ic_translate, id = R.string.ic_translate))
            }
        }
        fragment.getSpBool(R.string.pref_setting_enable_lib_detect_resource, true).apply {
            if (this){
                result.add(ActionItem(nameRes = R.string.detect_resource,iconRes = R.string.ic_resources_fang,id = R.string.ic_resources_fang))
            }
        }
        fragment.getSpBool(R.string.pref_setting_enable_lib_add_to_home_page, true).apply {
            if (this){
                result.add(ActionItem(nameRes = R.string.add_to_home_page, iconRes = R.string.ic_store, id = R.string.ic_store))
            }
        }
        fragment.appData.topMenuActionItems=result
    }
    private fun initTopMenuGridView(topMenuPanel: GridView) {
        val adapter =
            TopMenuPanelAdapter(R.color.colorWhite, R.layout.item_top_action_item, fragment.appData.topMenuActionItems)
        adapter.setOnItemClickListener { _, _, position ->
            onTopMenuActionItemClick(fragment.appData.topMenuActionItems[position])
        }
        topMenuPanel.adapter = adapter
    }

    private fun onTopMenuActionItemClick(actionItem: ActionItem) {
        when (actionItem.iconRes) {
            //扫码
            R.string.ic_scan -> {
                topPanelHelper.toggleTopPanel(Runnable {
                    fragment.RouterActivity?.loadScanActivity()
                })
            }

            R.string.ic_share -> {
                topPanelHelper.toggleTopPanel(Runnable {
                    shareLink()
                })
            }
            //阅读模式
            R.string.ic_read -> {
                redirect(binding=binding,session = session,runnable = kotlinx.coroutines.Runnable {
                    fragment.RouterActivity?.loadReadModeFragment(fragment.sessionId)
                })

            }
            //看图模式
            R.string.ic_image -> {
                redirect(binding=binding,session = session,runnable = kotlinx.coroutines.Runnable {
                    fragment.RouterActivity?.loadImageModeFragment(fragment.sessionId)
                })
            }

            R.string.ic_search -> {
                topPanelHelper.toggleTopPanel(Runnable {
                    findInPageIntegration.launch()
                })
            }

            R.string.ic_save -> {
                val path = sessionManager.getOrCreateEngineSession(session).savePage()
                if (path == EngineSession.SAVE_PAGE_ERROR) {
                    fragment.requireContext().apply {
                        showToast(getString(R.string.save_page_fail))
                    }
                }

                fragment.launch(Dispatchers.IO) {
                    try {
                        savedFileDao.insertAll(
                            SavedFile(
                                referer = session.url,
                                name = session.title, url = session.url,
                                path = path, type = SavedFile.HTML
                            )
                        )
                        launch(Dispatchers.Main) {
                            fragment.requireContext().apply {
                                showToast(getString(R.string.save_page_success))
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("离线保存", e.message)
                        launch(Dispatchers.Main) {
                            fragment.requireContext().apply {
                                showToast(getString(R.string.save_page_fail))
                            }
                        }
                    }
                }
            }
            R.string.ic_translate -> {
                topPanelHelper.toggleTopPanel(Runnable {
                    val url = "http://fanyi.youdao.com/WebpageTranslate?url=" + URLEncoder.encode(session.url, "UTF-8")
                    tabsUseCases.addTab.invoke(url, true, true, session)
                    redirect(binding=binding,session = session,runnable = kotlinx.coroutines.Runnable {
                        fragment.RouterActivity?.loadBrowserFragment(sessionManager.selectedSession!!.id)
                    })
                })
            }
            //资源嗅探
            R.string.ic_resources_fang -> {
                redirect(binding=binding,session = session,runnable = kotlinx.coroutines.Runnable {
                    fragment.RouterActivity?.loadResourceFragment(fragment.sessionId)
                })
            }
            //TODO:添加到主页
            R.string.ic_store -> {

            }
        }
    }

    private fun shareLink() {
        if (!fragment.requireContext().shareLink(title = session.title, url = session.url)) {
            fragment.requireContext().showToast(fragment.getString(R.string.tip_share_fail))
        }
    }

    override fun start() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stop() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class TopMenuPanelAdapter(@ColorRes var color: Int, layoutId: Int, data: List<ActionItem>) :
    BaseQuickAdapter<ActionItem, CustomBaseViewHolder>(layoutId, data) {
    override fun convert(helper: CustomBaseViewHolder, item: ActionItem) {
        helper.setText(R.id.icon, item.iconRes)
        helper.setTextColor(R.id.icon, helper.itemView.context.resources.getColor(color))
        helper.setText(R.id.name, item.nameRes)
        helper.setTextColor(R.id.name, helper.itemView.context.resources.getColor(color))
    }
}