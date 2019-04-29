package com.dev.orangebrowser.bloc.browser.integration

import android.drm.DrmStore
import android.os.Bundle
import androidx.annotation.ColorRes
import com.dev.base.extension.onGlobalLayoutComplete
import com.dev.base.support.LifecycleAwareFeature
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.browser.integration.helper.TopPanelHelper
import com.dev.orangebrowser.bloc.browser.integration.helper.redirect
import com.dev.orangebrowser.data.model.ActionItem
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appData
import com.dev.view.GridView
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter

class TopPanelMenuIntegration(var binding: FragmentBrowserBinding,
                              var fragment: BrowserFragment,
                              var savedInstanceState: Bundle?,
                              var topPanelHelper: TopPanelHelper,
                              var findInPageIntegration: FindInPageIntegration):
    LifecycleAwareFeature {

    init {
        initTopMenuGridView(binding.topMenuPanel)
        binding.overLayerTopPanel.setOnClickListener {
            topPanelHelper.toggleTopPanel()
        }
        //initial hide
        binding.topMenuPanel.apply {
            onGlobalLayoutComplete{
                fragment.context?.apply {
                     it.translationY=-it.height.toFloat()-binding.topBar.height.toFloat()
                }
            }
        }

    }
    private fun initTopMenuGridView(topMenuPanel: GridView) {
        val adapter=TopMenuPanelAdapter(R.color.colorWhite,R.layout.item_top_action_item,fragment.appData.topMenuActionItems)
        adapter.setOnItemClickListener { _, _, position ->
            onTopMenuActionItemClick(fragment.appData.topMenuActionItems[position])
        }
        topMenuPanel.adapter=adapter
    }

    //TODO:点击顶部MenuItem
    private fun onTopMenuActionItemClick(actionItem: ActionItem) {
        when(actionItem.iconRes){
            //扫码
            R.string.ic_scan->{
                topPanelHelper.toggleTopPanel(Runnable {
                    redirect(binding=binding,session = fragment.session,runnable = Runnable {
                        fragment.RouterActivity?.loadScanFragment()
                    })
                })
            }
            //TODO:分享
            R.string.ic_share->{

            }
            //阅读模式
            R.string.ic_read->{

                redirect(binding=binding,session = fragment.session,runnable = Runnable {
                    fragment.RouterActivity?.loadReadModeFragment(fragment.sessionId)
                })
            }
            //看图模式
            R.string.ic_image->{
                redirect(binding=binding,session = fragment.session,runnable = Runnable {
                    fragment.RouterActivity?.loadImageModeFragment(fragment.sessionId)
                })
            }
            //TODO:标记广告
            R.string.ic_ad_mark->{

            }
            //TODO:页内查找
            R.string.ic_search->{
                topPanelHelper.toggleTopPanel(Runnable {
                    findInPageIntegration.launch()
                })
            }
            //TODO:离线保存
            R.string.ic_save->{

            }
            //TODO:翻译
            R.string.ic_translate->{

            }
            //源码
            R.string.ic_code->{
                redirect(binding=binding,session = fragment.session,runnable = Runnable {
                    fragment.RouterActivity?.loadSourceCodeFragment(fragment.sessionId)
                })
            }
            //资源嗅探
            R.string.ic_resources_fang->{
                redirect(binding=binding,session = fragment.session,runnable = Runnable {
                    fragment.RouterActivity?.loadResourceFragment(fragment.sessionId)
                })
            }
            //TODO:添加到主页
            R.string.ic_store->{

            }
            //TODO:天网
            R.string.ic_sky_net->{

            }
        }
    }


    override fun start() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stop() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class TopMenuPanelAdapter(@ColorRes var color:Int, layoutId:Int, data:List<ActionItem>):BaseQuickAdapter<ActionItem,CustomBaseViewHolder>(layoutId,data){
    override fun convert(helper: CustomBaseViewHolder, item: ActionItem) {
        helper.setText(R.id.icon,item.iconRes)
        helper.setTextColor(R.id.icon,helper.itemView.context.resources.getColor(color))
        helper.setText(R.id.name,item.nameRes)
        helper.setTextColor(R.id.name,helper.itemView.context.resources.getColor(color))
    }
}