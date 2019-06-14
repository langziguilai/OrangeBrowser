package com.dev.orangebrowser.bloc.found.category

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.BaseFragment
import com.dev.base.extension.showToast
import com.dev.base.support.BackHandler
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.config.ErrorCode
import com.dev.orangebrowser.data.model.CommonSite
import com.dev.orangebrowser.databinding.FragmentSiteListBinding
import com.dev.orangebrowser.extension.appComponent
import com.dev.view.StatusBarUtil
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import java.util.*
import javax.inject.Inject

class SiteListFragment : BaseFragment(), BackHandler {
    @Inject
    lateinit var sessionManager: SessionManager
    val category:String
        get() = arguments?.getString(SITE_CATEGORY_NAME) ?: ""
    val url:String
        get() = arguments?.getString(SITE_CATEGORY_URL) ?: ""
    override fun onBackPressed(): Boolean {
        sessionManager.selectedSession?.apply {
            fragmentManager?.popBackStack()
        }
        return true
    }

    companion object {
        val Tag = "SiteListFragment"
        const val SITE_CATEGORY_NAME = "site_category_name"
        const val SITE_CATEGORY_URL = "site_category_url"
        fun newInstance(categoryName: String, url: String) = SiteListFragment().apply {
            arguments = Bundle().apply {
                putString(SITE_CATEGORY_NAME, categoryName)
                putString(SITE_CATEGORY_URL, url)
            }
        }
    }

    lateinit var viewModel: SiteListViewModel
    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentSiteListBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(SiteListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSiteListBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
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
        return R.layout.fragment_site_list
    }

    private var data: LinkedList<CommonSite> = LinkedList()
    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        StatusBarUtil.setIconColor(requireActivity(), activityViewModel.theme.value!!.colorPrimary)
        binding.title.text=category
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(),  RecyclerView.VERTICAL, false)
        val adapter = object : BaseQuickAdapter<CommonSite, CustomBaseViewHolder>(R.layout.item_site_common, data) {
            override fun convert(helper: CustomBaseViewHolder, item: CommonSite) {
                helper.addOnClickListener(R.id.add)
                helper.loadImage(R.id.icon, item.icon ?: "", strategy = CustomBaseViewHolder.CENTER_OUTSIDE)
                helper.setText(R.id.name, item.name ?: "")
                if (item.added){
                    helper.setText(R.id.add,getString(R.string.added))
                }else{
                    helper.setText(R.id.add,getString(R.string.add_home_page))
                }
            }
        }
        adapter.setOnItemChildClickListener { _, view, position ->
            if (view.id==R.id.add){
                addSiteToMainPage(view,data[position])
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

    private fun addSiteToMainPage(view:View,site:CommonSite){
          if(!site.added){
              site.added=true
              view.findViewById<TextView>(R.id.add)?.text=getString(R.string.added)
              viewModel.addToHomePage(site)
          }
    }
    override fun initData(savedInstanceState: Bundle?) {
        viewModel.loadCommonSites(category)
        viewModel.loadFromRemote()
    }
}
