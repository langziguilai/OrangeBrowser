package com.dev.orangebrowser.bloc.download

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.BaseFragment
import com.dev.browser.feature.downloads.DownloadManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.databinding.FragmentDownloadBinding
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.extension.getColor
import com.dev.orangebrowser.utils.FileSizeHelper
import com.dev.orangebrowser.utils.FileTypeDetect
import com.dev.util.DensityUtil
import com.dev.view.StatusBarUtil
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.GridDividerItemDecoration
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class DownloadFragment : BaseFragment() {


    companion object {
        val Tag="DownloadFragment"
        fun newInstance() = DownloadFragment()
    }

    lateinit var viewModel: DownloadViewModel
    lateinit var activityViewModel: MainViewModel
    lateinit var binding:FragmentDownloadBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel=ViewModelProviders.of(this,factory).get(DownloadViewModel::class.java)
    }

    override fun useDataBinding(): Boolean {
        return true
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding= FragmentDownloadBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
        return binding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel=ViewModelProviders.of(this.requireActivity(),factory).get(MainViewModel::class.java)
        binding.activityViewModel=activityViewModel
        super.onActivityCreated(savedInstanceState)
    }
    var allItems:LinkedList<Item> = LinkedList()
    var displayItems:LinkedList<Item> = LinkedList()
    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        StatusBarUtil.setIconColor(requireActivity(),activityViewModel.theme.value!!.colorPrimary)
        binding.recyclerView.apply {
            this.layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            this.addItemDecoration(GridDividerItemDecoration(0,DensityUtil.dip2px(requireContext(),0.5f),getColor(R.color.material_grey_300)))
            this.adapter=object:BaseQuickAdapter<Item,CustomBaseViewHolder>(R.layout.item_resource,displayItems){
                override fun convert(helper: CustomBaseViewHolder, item: Item) {
                    helper.setTextColor(R.id.icon,activityViewModel.theme.value!!.colorPrimary)
                    helper.setText(R.id.title,item.name)
                    helper.setText(R.id.size,item.size)
                    when(item.type){
                        Type.VIDEO->{

                        }
                        Type.IMAGE->{

                        }
                        Type.WEB->{

                        }
                        Type.OTHER->{

                        }
                    }
                }

            }
        }
        binding.savedVideos.setOnClickListener {

        }
        binding.downloadPath.setOnClickListener {

        }
        binding.downloadHtml.setOnClickListener {

        }
        binding.downloadImage.setOnClickListener {

        }
    }

    override fun initData(savedInstanceState: Bundle?) {

        launch(Dispatchers.IO) {
             val downloadPath=DownloadManager.getInstance(requireContext().applicationContext).downloadPath
             val dir=File(downloadPath)
             if(dir.isDirectory){
                 val items= dir.listFiles().filter { !it.isDirectory }.map {
                     var type=Type.OTHER
                     if (FileTypeDetect.isImage(it.path)){
                         type=Type.IMAGE
                     }else if(FileTypeDetect.isVideo(it.path)){
                         type=Type.VIDEO
                     }
                     Item(
                         name=it.name,
                         path = it.absolutePath,
                         size=FileSizeHelper.ShowLongFileSize(it.totalSpace),
                         date = Date(it.lastModified()),
                         type = type
                     )

                 }
                 allItems.addAll(items)
                 displayItems.addAll(items)
                 launch(Dispatchers.Main) {
                     binding.recyclerView.adapter?.notifyDataSetChanged()
                 }
             }
        }
    }

}
data class Item(var name:String,var path:String,var size:String,var type:Type,var date: Date)
enum class Type{
    VIDEO,IMAGE,WEB,OTHER
}