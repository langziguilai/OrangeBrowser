package com.dev.orangebrowser.bloc.found

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.BaseFragment
import com.dev.base.extension.showToast
import com.dev.base.support.BackHandler
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.config.ErrorCode
import com.dev.orangebrowser.data.model.SiteCategory
import com.dev.orangebrowser.databinding.FragmentFoundBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.extension.getColor
import com.dev.util.DensityUtil
import com.dev.view.StatusBarUtil
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.GridDividerItemDecoration
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import java.util.*
import javax.inject.Inject

class FoundFragment : BaseFragment(), BackHandler {
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onBackPressed(): Boolean {
        sessionManager.selectedSession?.apply {
            RouterActivity?.loadHomeOrBrowserFragment(this.id, R.anim.holder, R.anim.slide_right_out)
        }
        return true
    }

    companion object {
        val Tag = "FoundFragment"
        fun newInstance() = FoundFragment()
    }

    lateinit var viewModel: FoundViewModel
    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentFoundBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(FoundViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFoundBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(activity!!, factory).get(MainViewModel::class.java)
        binding.activityViewModel = activityViewModel
        binding.backHandler = this
        super.onActivityCreated(savedInstanceState)
    }

    override fun useDataBinding(): Boolean {
        return true
    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_found
    }

    private var data: LinkedList<SiteCategory> = LinkedList()
    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        StatusBarUtil.setIconColor(requireActivity(), activityViewModel.theme.value!!.colorPrimary)
        binding.addNew.setOnClickListener {
            RouterActivity?.loadSiteCreatorFragment()
        }
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        val spanWidth = DensityUtil.dip2px(requireContext(), 18f)
        val spanHeight = spanWidth / 2
        binding.recyclerView.addItemDecoration(
            GridDividerItemDecoration(
                spanWidth,
                spanHeight,
                getColor(R.color.colorWhite)
            )
        )
        val adapter = object : BaseQuickAdapter<SiteCategory, CustomBaseViewHolder>(R.layout.item_site_category, data) {
            override fun convert(helper: CustomBaseViewHolder, item: SiteCategory) {
                helper.loadImage(R.id.icon, item.icon ?: "", strategy = CustomBaseViewHolder.CENTER_INSIDE)
                helper.setText(R.id.name, item.name ?: "")
                helper.setText(R.id.description, item.description ?: "")
            }
        }
        adapter.setOnItemClickListener { _, _, position ->
            val category=data[position]
            if (category.name!=null && category.url!=null){
                RouterActivity?.loadSiteListFragment(categoryName=category.name!!,url = category.url!!)
            }
        }
        binding.recyclerView.adapter = adapter
        viewModel.categoryListLiveData.observe(this, androidx.lifecycle.Observer {
            data.clear()
            data.addAll(it)
            adapter.notifyDataSetChanged()
        })
        viewModel.errorCodeLiveData.observe(this, androidx.lifecycle.Observer {
            when (it) {
                ErrorCode.LOAD_FAIL -> {
                    requireContext().showToast(getString(R.string.load_fail))
                }
            }
        })
    }

    override fun initData(savedInstanceState: Bundle?) {
        viewModel.loadCategoryList()
        viewModel.loadFromRemote()
    }
//看看是否添加头部
//    private fun getHeaderView(): View {
//        val view =
//            LayoutInflater.from(requireContext()).inflate(R.layout.view_category_header, binding.recyclerView, false)
//        view.findViewById<ImageView>(R.id.image)?.apply {
//            //GlideHelper.loadRemoteImage()
//        }
//        return view
//    }
}
