package com.dev.orangebrowser.bloc.download

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.BaseFragment
import com.dev.base.extension.copyText
import com.dev.base.extension.onGlobalLayoutComplete
import com.dev.base.extension.shareLink
import com.dev.base.extension.showToast
import com.dev.base.support.BackHandler
import com.dev.browser.database.download.*
import com.dev.browser.session.Download
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.databinding.FragmentDownloadBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.extension.getColor
import com.dev.orangebrowser.utils.FileSizeHelper
import com.dev.orangebrowser.utils.PositionUtils.calculateRecyclerViewTopMargin
import com.dev.orangebrowser.view.contextmenu.Action
import com.dev.orangebrowser.view.contextmenu.CommonContextMenuAdapter
import com.dev.orangebrowser.view.contextmenu.MenuItem
import com.dev.util.DensityUtil
import com.dev.util.FileUtil
import com.dev.view.StatusBarUtil
import com.dev.view.dialog.DialogBuilder
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.GridDividerItemDecoration
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.inject.Inject

class DownloadFragment : BaseFragment(),BackHandler {
    override fun onBackPressed(): Boolean {
        sessoinManager.selectedSession?.apply {
            RouterActivity?.loadHomeFragment(this.id,enterAnimationId = R.anim.holder,exitAnimationId = R.anim.slide_right_out)
        }
        return true
    }


    companion object {
        val Tag = "DownloadFragment"
        fun newInstance() = DownloadFragment()
    }
    @Inject
    lateinit var sessoinManager:SessionManager
    @Inject
    lateinit var downloadDao: DownloadDao
    lateinit var viewModel: DownloadViewModel
    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentDownloadBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(DownloadViewModel::class.java)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_download
    }

    override fun useDataBinding(): Boolean {
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDownloadBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(this.requireActivity(), factory).get(MainViewModel::class.java)
        binding.activityViewModel = activityViewModel
        binding.backHandler=this
        super.onActivityCreated(savedInstanceState)
    }
    var offSet: Int = -1
    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        StatusBarUtil.setIconColor(requireActivity(), activityViewModel.theme.value!!.colorPrimary)
        offSet = DensityUtil.dip2px(requireContext(), 20f)
        binding.recyclerView.apply {
            this.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            this.addItemDecoration(
                GridDividerItemDecoration(
                    0,
                    DensityUtil.dip2px(requireContext(), 0.5f),
                    getColor(R.color.material_grey_300)
                )
            )

        }
        binding.savedVideos.setOnClickListener {

        }

        binding.downloadHtml.setOnClickListener {

        }
        binding.downloadImage.setOnClickListener {

        }
    }
    lateinit var downloads: LinkedList<DownloadEntity>
    override fun initData(savedInstanceState: Bundle?) {
        launch(Dispatchers.IO) {
            downloads = LinkedList(downloadDao.getAll())
            launch(Dispatchers.Main) {
                val adapter = object :
                    BaseQuickAdapter<DownloadEntity, CustomBaseViewHolder>(R.layout.item_download_item, downloads) {
                    override fun convert(helper: CustomBaseViewHolder, item: DownloadEntity) {
                        helper.setTextColor(R.id.icon, activityViewModel.theme.value!!.colorPrimary)
                        helper.setText(R.id.title, item.fileName)
                        helper.setText(R.id.size, FileSizeHelper.ShowLongFileSize(item.contentLength))
                        when (item.type) {
                            IMAGE -> {
                                helper.setText(R.id.icon,getString(R.string.ic_image))
                            }
                            VIDEO -> {
                                helper.setText(R.id.icon,getString(R.string.ic_video))
                            }
                            AUDIO -> {
                                helper.setText(R.id.icon,getString(R.string.ic_audio))
                            }
                            COMMON -> {
                                helper.setText(R.id.icon,getString(R.string.ic_file))
                            }
                            APK -> {
                                helper.setText(R.id.icon,getString(R.string.ic_store))
                            }
                        }
                    }
                }
                initDownloadItemDialog(adapter)
                binding.recyclerView.adapter=adapter

            }

        }
    }
    var downloadItemDialog: Dialog?=null
    private fun initDownloadItemDialog(adapter: BaseQuickAdapter<DownloadEntity, CustomBaseViewHolder>?) {
        adapter?.setOnItemLongClickListener { _, _, position ->
                downloadItemDialog=  DialogBuilder()
                    .setLayoutId(R.layout.dialog_context_menu)
                    .setHeightParent(1f)
                    .setWidthPercent(1f)
                    .setOnViewCreateListener(object : DialogBuilder.OnViewCreateListener {
                        override fun onViewCreated(view: View) {
                            initDownloadItemDialogView(view,position)
                        }
                    })
                    .setGravity(Gravity.TOP)
                    .build(requireContext())
                downloadItemDialog?.show()
            true
        }
    }
    private fun initDownloadItemDialogView(view:View, position:Int){
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            this.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            this.adapter = CommonContextMenuAdapter(
                R.layout.mozac_feature_contextmenu_item, listOf(
                    MenuItem(label = getString(R.string.menu_delete),action = object: Action<MenuItem> {
                        override fun execute(data: MenuItem) {
                            val item=downloads[position]
                            deleteDownloadItem(item)
                            downloads.remove(item)
                            binding.recyclerView.adapter?.notifyItemRemoved(position)
                            downloadItemDialog?.dismiss()
                        }
                    }),
                    MenuItem(label = getString(R.string.menu_share),action = object:Action<MenuItem>{
                        override fun execute(data: MenuItem) {
                            downloadItemDialog?.dismiss()
                            val item= downloads[position]
                            if(!requireContext().shareLink(title =item.fileName,url =item.url)){
                                requireContext().showToast(getString(R.string.tip_share_fail))
                            }
                        }
                    }),
                    MenuItem(label = getString(R.string.menu_copy_link),action = object:Action<MenuItem>{
                        override fun execute(data: MenuItem) {
                            downloadItemDialog?.dismiss()
                            val item= downloads[position]
                            requireContext().copyText(getString(R.string.link),item.url)
                            requireContext().showToast(getString(R.string.tip_copy_link))
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
    //计算距离
    private fun calculateRecyclerViewTopMargin(containerHeight: Int, childHeight: Int, y: Int): Int {
        return when {
            y + childHeight / 2 + offSet > containerHeight -> containerHeight - childHeight - offSet
            y - childHeight / 2 - offSet < 0 -> offSet
            else -> y - childHeight / 2
        }
    }

    //删除下载文件
    fun deleteDownloadItem(downloadItem:DownloadEntity){
         launch(Dispatchers.IO) {
             downloadDao.delete(downloadItem)
             FileUtil.deleteFile(downloadItem.path)
         }
    }
}