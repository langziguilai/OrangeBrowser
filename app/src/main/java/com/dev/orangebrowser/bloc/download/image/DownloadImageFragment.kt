package com.dev.orangebrowser.bloc.download.image

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.BaseFragment
import com.dev.base.extension.copyText
import com.dev.base.extension.onGlobalLayoutComplete
import com.dev.base.extension.shareLink
import com.dev.base.extension.showToast
import com.dev.base.support.BackHandler
import com.dev.browser.database.download.DownloadDao
import com.dev.browser.database.download.DownloadEntity
import com.dev.browser.database.download.IMAGE
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.display.image.ImageDisplayActivity
import com.dev.orangebrowser.bloc.host.MainActivity
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.data.model.SimpleImage
import com.dev.orangebrowser.databinding.FragmentDownloadImageBinding
import com.dev.orangebrowser.eventbus.ChangeRecyclerViewIndexEvent
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.extension.getColor
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
import com.hw.ycshareelement.YcShareElement
import com.hw.ycshareelement.transition.IShareElements
import com.hw.ycshareelement.transition.ShareElementInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import javax.inject.Inject

class DownloadImageFragment : BaseFragment(), BackHandler, IShareElements {
    override fun onBackPressed(): Boolean {
        fragmentManager?.popBackStack()
        return true
    }

    companion object {
        val Tag = "DownloadHtmlFragment"
        fun newInstance() = DownloadImageFragment()
    }

    @Inject
    lateinit var downloadDao: DownloadDao
    lateinit var viewModel: DownloadImageViewModel
    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentDownloadImageBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(DownloadImageViewModel::class.java)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_download_image
    }

    override fun useDataBinding(): Boolean {
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDownloadImageBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(this.requireActivity(), factory).get(MainViewModel::class.java)
        binding.activityViewModel = activityViewModel
        binding.backHandler = this
        super.onActivityCreated(savedInstanceState)
    }

    var offSet: Int = -1
    lateinit var mLayoutManager:GridLayoutManager
    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        StatusBarUtil.setIconColor(requireActivity(), activityViewModel.theme.value!!.colorPrimary)
        offSet = DensityUtil.dip2px(requireContext(), 20f)
        binding.recyclerView.apply {
            mLayoutManager = GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
            this.layoutManager=mLayoutManager
            this.addItemDecoration(
                GridDividerItemDecoration(
                    DensityUtil.dip2px(requireContext(), 1f),
                    DensityUtil.dip2px(requireContext(), 1f),
                    getColor(R.color.white)
                )
            )

        }
    }

    lateinit var downloads: LinkedList<DownloadEntity>
    lateinit var shareImageInfo: ShareElementInfo<SimpleImage>
    lateinit var adapter:BaseQuickAdapter<DownloadEntity, CustomBaseViewHolder>
    override fun initData(savedInstanceState: Bundle?) {
        launch(Dispatchers.IO) {
            downloads = LinkedList(downloadDao.getDownloadByType(IMAGE))
            launch(Dispatchers.Main) {
                adapter = object :
                    BaseQuickAdapter<DownloadEntity, CustomBaseViewHolder>(R.layout.item_download_image, downloads) {
                    override fun convert(helper: CustomBaseViewHolder, item: DownloadEntity) {
                        if (item.path.isNotBlank()){
                            helper.itemView.findViewById<ImageView>(R.id.image).apply {
                                transitionName=item.path
                            }
                            helper.loadLocalImage(R.id.image, item.path)
                        }else if(item.url.isNotBlank()){
                            helper.itemView.findViewById<ImageView>(R.id.image).apply {
                                transitionName=item.url
                            }
                            helper.loadImage(R.id.image, item.path,item.referer)
                        }
                    }
                }
                adapter.setOnItemClickListener { _, view, position ->
                    val selectedItem = downloads[position]
                    val imageView = view.findViewById<ImageView>(R.id.image)
                    shareImageInfo = ShareElementInfo(
                        imageView,
                        SimpleImage(url = selectedItem.url, path = selectedItem.path, referer = selectedItem.referer)
                    )
                    val intent = Intent(requireActivity(), ImageDisplayActivity::class.java)
                    //设置参数
                    intent.putExtra(ImageDisplayActivity.POSITION, position)

                    intent.putParcelableArrayListExtra(
                        ImageDisplayActivity.IMAGES,
                        ArrayList<SimpleImage>(downloads.map {
                            SimpleImage(url = it.url, path = it.path, referer = it.referer)
                        })
                    )
                    val options = YcShareElement.buildOptionsBundle(requireActivity(), this@DownloadImageFragment)
                    startActivityForResult(intent, MainActivity.REQUEST_IMAGE_TRANSITION, options)
                }
                initDownloadItemDialog(adapter)
                binding.recyclerView.adapter = adapter

            }

        }
    }

    var downloadItemDialog: Dialog? = null
    private fun initDownloadItemDialog(adapter: BaseQuickAdapter<DownloadEntity, CustomBaseViewHolder>?) {
        adapter?.setOnItemLongClickListener { _, _, position ->
            downloadItemDialog = DialogBuilder()
                .setLayoutId(R.layout.dialog_context_menu)
                .setHeightParent(1f)
                .setWidthPercent(1f)
                .setOnViewCreateListener(object : DialogBuilder.OnViewCreateListener {
                    override fun onViewCreated(view: View) {
                        initDownloadItemDialogView(view, position)
                    }
                })
                .setGravity(Gravity.TOP)
                .build(requireContext())
            downloadItemDialog?.show()
            true
        }
    }

    private fun initDownloadItemDialogView(view: View, position: Int) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            this.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            this.adapter = CommonContextMenuAdapter(
                R.layout.mozac_feature_contextmenu_item, listOf(
                    MenuItem(label = getString(R.string.menu_delete), action = object : Action<MenuItem> {
                        override fun execute(data: MenuItem) {
                            val item = downloads[position]
                            deleteDownloadItem(item)
                            downloads.remove(item)
                            binding.recyclerView.adapter?.notifyItemRemoved(position)
                            downloadItemDialog?.dismiss()
                        }
                    }),
                    MenuItem(label = getString(R.string.menu_share), action = object : Action<MenuItem> {
                        override fun execute(data: MenuItem) {
                            downloadItemDialog?.dismiss()
                            val item = downloads[position]
                            if (!requireContext().shareLink(title = item.fileName, url = item.url)) {
                                requireContext().showToast(getString(R.string.tip_share_fail))
                            }
                        }
                    }),
                    MenuItem(label = getString(R.string.menu_copy_link), action = object : Action<MenuItem> {
                        override fun execute(data: MenuItem) {
                            downloadItemDialog?.dismiss()
                            val item = downloads[position]
                            requireContext().copyText(getString(R.string.link), item.url)
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
    fun deleteDownloadItem(downloadItem: DownloadEntity) {
        launch(Dispatchers.IO) {
            try {
                downloadDao.delete(downloadItem)
                FileUtil.deleteFile(downloadItem.path)
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    requireContext().showToast(getString(R.string.tip_delete_fail))
                }
            } finally {
                launch(Dispatchers.Main) {
                    requireContext().showToast(getString(R.string.tip_delete_success))
                }
            }
        }
    }

    override fun getShareElements(): Array<ShareElementInfo<SimpleImage>> {
        return arrayOf(shareImageInfo)
    }

    fun selectShareElement(simpleImage: SimpleImage) {
        var index = 0
        for (download in downloads){
            if (download.path.isNotBlank() && download.path==simpleImage.path){
                index=downloads.indexOf(download)
                break
            } else if(download.url.isNotBlank() && download.url==simpleImage.url){
                index=downloads.indexOf(download)
                break
            }
        }
        if (index>=0){
            adapter.getViewByPosition(binding.recyclerView,index,R.id.image)?.apply {
                shareImageInfo=ShareElementInfo(this,simpleImage)
            }
            mLayoutManager.scrollToPosition(index)
        }
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onChangeRecyclerViewItemIndex(event:ChangeRecyclerViewIndexEvent){
        mLayoutManager.scrollToPosition(event.message)
    }
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }
    override fun onStop(){
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
}