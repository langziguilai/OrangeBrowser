package com.dev.orangebrowser.bloc.setting.fragments.download

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.BaseFragment
import com.dev.base.extension.*
import com.dev.base.support.BackHandler
import com.dev.browser.feature.downloads.DownloadManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.bloc.setting.adapter.Adapter
import com.dev.orangebrowser.bloc.setting.viewholder.*
import com.dev.orangebrowser.bloc.setting.viewholder.base.Action
import com.dev.orangebrowser.databinding.FragmentSettingDownloadPathBinding
import com.dev.orangebrowser.extension.*
import com.dev.util.DensityUtil
import com.evernote.android.state.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class DownloadPathSettingFragment : BaseFragment(), BackHandler {


    companion object {
        const val Tag = "DownloadPathSettingFragment"
        val START_PATH: String = Environment.getExternalStorageDirectory().absolutePath
        fun newInstance() = DownloadPathSettingFragment()
    }

    @State
    var currentDirectoryPath = ""
    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentSettingDownloadPathBinding
    override fun onBackPressed(): Boolean {
        if (currentDirectoryPath != START_PATH) {
            onPathSelect(
                PathItem(
                    title = "",
                    path = getParentPath(currentDirectoryPath),
                    bgResId = -1,
                    action = object : Action<PathItem> {
                        override fun invoke(data: PathItem) {

                        }
                    })
            )
            return true
        }
        RouterActivity?.loadDownloadSettingFragment(R.anim.holder,R.anim.slide_right_out)
        return true

    }

    private fun getParentPath(path: String): String {
        val end = path.lastIndexOf("/")
        return path.substring(0, end)
    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_setting_download_path
    }

    override fun useDataBinding(): Boolean {
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingDownloadPathBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
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

    private val pathLinkedList = LinkedList<Any>()

    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.parentPath.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.addFolderContainer.onGlobalLayoutComplete{
            it.translationY=(it.measuredHeight+DensityUtil.dip2px(requireContext(),8f)).toFloat()
            binding.bottomOverlay.hide()
        }

    }
    fun chooseDirectory(){
        launch(Dispatchers.IO) {
            DownloadManager.getInstance(requireContext().applicationContext).setCustomDownloadPath(currentDirectoryPath)
            //setSpString(R.string.pref_setting_download_relative_path, currentDirectoryPath.removePrefix(START_PATH))
            launch(Dispatchers.Main) {
                fragmentManager?.popBackStack()
            }
        }
    }
    fun addFolder(){
        val text=binding.addFolderText.text.toString()
        if (!text.isBlank()){
            addFolder(text)
            hideAddFolderDialog()
        }else{
            Toast.makeText(requireContext(),getString(R.string.tip_correct_folder_name),Toast.LENGTH_SHORT).show()
        }
    }
     fun showAddFolderDialog(){
        binding.bottomOverlay.show()
        binding.addFolderContainer.animate().alpha(1f).translationY(0f)
            .setInterpolator(DEFAULT_INTERPOLATOR).setDuration(NORMAL_ANIMATION).start()
    }
     fun hideAddFolderDialog(){
        binding.addFolderText.hideKeyboard()
        val view=binding.addFolderContainer
        view.animate().alpha(0f).translationY((view.measuredHeight+ DensityUtil.dip2px(requireContext(),8f)).toFloat())
            .withEndAction {
                binding.bottomOverlay.hide()
            }
            .setInterpolator(DEFAULT_INTERPOLATOR).setDuration(NORMAL_ANIMATION).start()
    }

    private lateinit var dataList: LinkedList<Any>

    override fun initData(savedInstanceState: Bundle?) {
        currentDirectoryPath =
            START_PATH
        launch(Dispatchers.IO) {
            dataList = getData(START_PATH)
            pathLinkedList.addAll(getParentPathData(START_PATH))
            launch(Dispatchers.Main) {
                binding.recyclerView.adapter = Adapter(dataList)
                binding.parentPath.adapter = Adapter(pathLinkedList)
            }
        }
    }

    private fun onSelect(data: FolderItem) {
        updateCurrentPath(data.path)
    }
    private fun  updateCurrentPath(path:String){
        launch(Dispatchers.IO) {
            pathLinkedList.clear()
            pathLinkedList.addAll(getParentPathData(path))
            dataList.clear()
            dataList.addAll(getData(path))
            currentDirectoryPath = path
            launch(Dispatchers.Main) {
                binding.parentPath.adapter?.notifyDataSetChanged()
                binding.recyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }
    private fun getParentPathData(path: String): List<Any> {
        val list = LinkedList<Any>()
        val relativePath = path.removePrefix(START_PATH)
        val pathArray = relativePath.split("/")
        for (i in 0 until pathArray.size) {
            val subPath = START_PATH + pathArray.subList(0, i + 1).joinToString("/")
            var title = pathArray[i]
            if (i == 0) {
                title = "/"
            }
            list.add(PathItem(title = title, action = object : Action<PathItem> {
                override fun invoke(data: PathItem) {
                    onPathSelect(data)
                }
            }, path = subPath, bgResId = R.drawable.bg_grident_right_round))
        }
        return list.reversed()
    }

    private fun onPathSelect(item: PathItem) {
        updateCurrentPath(item.path)
    }

    private fun getData(path: String): LinkedList<Any> {
        val list = LinkedList<Any>()
        val dirs = getDirs(path)
        dirs.forEach {
            val title = it.name
            val folderPath = it.absolutePath
            list.add(
                FolderItem(iconText = getString(R.string.ic_folder_fill),
                    title = title,
                    icon = getString(R.string.ic_right),
                    path = folderPath,
                    color = requireContext().resources.getColor(R.color.colorPrimary),
                    action = object : Action<FolderItem> {
                        override fun invoke(data: FolderItem) {
                            onSelect(data)
                        }
                    })
            )
        }

        return list
    }

    //
    private fun getDirs(path: String): List<File> {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PERMISSION_DENIED
        ) {
            Toast.makeText(
                requireContext(),
                getString(R.string.tip_permission_write_external_storage),
                Toast.LENGTH_SHORT
            ).show()
            return listOf()
        }
        val currentFile = File(path)
        currentFile.listFiles { file -> file?.isDirectory ?: false }?.apply {
            return this.toList().sorted()
        }
        return listOf()
    }

    //创建文件夹
    private fun addFolder(folderName: String):Boolean {
        try {
            val file = File("$currentDirectoryPath/$folderName")
            if (!file.exists()) {
                if (!file.mkdirs()){
                    requireContext().showToast(getString(R.string.tip_create_download_foloder_error))
                    return false
                }
            }
        } catch (e: SecurityException) {
            requireContext().showToast(getString(R.string.tip_create_download_foloder_error))
            return false
        }
        return true
    }
}
