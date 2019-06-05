package com.dev.orangebrowser.bloc.download.html

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
import com.dev.browser.database.download.*
import com.dev.browser.feature.session.SessionUseCases
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.data.dao.SavedFileDao
import com.dev.orangebrowser.data.model.SavedFile
import com.dev.orangebrowser.databinding.FragmentDownloadImageBinding
import com.dev.orangebrowser.extension.RouterActivity
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class DownloadHtmlFragment : BaseFragment(),BackHandler {
    override fun onBackPressed(): Boolean {
        RouterActivity?.loadDownloadFragment(enterAnimationId = R.anim.holder,exitAnimationId = R.anim.slide_right_out)
        return true
    }


    companion object {
        val Tag = "DownloadHtmlFragment"
        fun newInstance() = DownloadHtmlFragment()
    }

    @Inject
    lateinit var savedFileDao: SavedFileDao
    @Inject
    lateinit var sessionUseCases: SessionUseCases
    @Inject
    lateinit var sessionManager:SessionManager
    lateinit var viewModel: DownloadHtmlViewModel
    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentDownloadImageBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(DownloadHtmlViewModel::class.java)
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
                    DensityUtil.dip2px(requireContext(), 1f),
                    getColor(R.color.white)
                )
            )

        }
    }
    lateinit var saveFiles: LinkedList<SavedFile>
    override fun initData(savedInstanceState: Bundle?) {
        launch(Dispatchers.IO) {
            saveFiles = LinkedList(savedFileDao.getAll())
            launch(Dispatchers.Main) {
                val adapter = object :
                    BaseQuickAdapter<SavedFile, CustomBaseViewHolder>(R.layout.item_download_html, saveFiles) {
                    override fun convert(helper: CustomBaseViewHolder, item: SavedFile) {
                        helper.setTextColor(R.id.icon, activityViewModel.theme.value!!.colorPrimary)
                        helper.setText(R.id.title, item.name)
                        helper.setText(R.id.icon,getString(R.string.ic_internet))
                    }
                }
                initDownloadItemDialog(adapter)
                adapter.setOnItemClickListener{
                    _,_,position ->
                    saveFiles[position].path?.apply {
                        val path=this
                        sessionManager.selectedSession?.apply {
                            val session=this
                            sessionUseCases.loadUrl.invoke(url="file://$path",session=session)
                            RouterActivity?.loadHomeOrBrowserFragment(sessionId = session.id)
                        }
                    }
                }
                binding.recyclerView.adapter=adapter
            }

        }
    }
    var downloadItemDialog: Dialog?=null
    private fun initDownloadItemDialog(adapter: BaseQuickAdapter<SavedFile, CustomBaseViewHolder>?) {
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
                            val item=saveFiles[position]
                            deleteDownloadHtmlItem(item)
                            saveFiles.remove(item)
                            binding.recyclerView.adapter?.notifyItemRemoved(position)
                            downloadItemDialog?.dismiss()
                        }
                    }),
                    MenuItem(label = getString(R.string.menu_share),action = object:Action<MenuItem>{
                        override fun execute(data: MenuItem) {
                            downloadItemDialog?.dismiss()
                            val item= saveFiles[position]
                            if(!requireContext().shareLink(title =item.name ?: "",url =item.url ?: "")){
                                requireContext().showToast(getString(R.string.tip_share_fail))
                            }
                        }
                    }),
                    MenuItem(label = getString(R.string.menu_copy_link),action = object:Action<MenuItem>{
                        override fun execute(data: MenuItem) {
                            downloadItemDialog?.dismiss()
                            val item= saveFiles[position]
                            requireContext().copyText(getString(R.string.link),item.url ?: "")
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
    fun deleteDownloadHtmlItem(savedFile:SavedFile){
         launch(Dispatchers.IO) {
             try {
                 savedFileDao.delete(savedFile.uid)
                 FileUtil.deleteFile(savedFile.path)
             }catch (e:Exception){
                  launch(Dispatchers.Main){
                      requireContext().showToast(getString(R.string.tip_delete_fail))
                  }
             }finally {
                 launch(Dispatchers.Main) {
                     requireContext().showToast(getString(R.string.tip_delete_success))
                 }
             }
         }
    }
}