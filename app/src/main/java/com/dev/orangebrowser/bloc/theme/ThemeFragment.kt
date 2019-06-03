package com.dev.orangebrowser.bloc.theme

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.BaseFragment
import com.dev.base.support.BackHandler
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.data.model.ThemeSource
import com.dev.orangebrowser.databinding.FragmentThemeBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.extension.appData
import com.dev.orangebrowser.extension.getColor
import com.dev.util.DensityUtil
import com.dev.view.StatusBarUtil
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.GridDividerItemDecoration
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import com.noober.background.drawable.DrawableCreator
import javax.inject.Inject


class ThemeFragment : BaseFragment(), BackHandler {
    companion object {
        val Tag = "ThemeFragment"
        fun newInstance() = ThemeFragment()
    }
    @Inject
    lateinit var sessionManager: SessionManager
    lateinit var viewModel: ThemeViewModel
    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentThemeBinding
    override fun onBackPressed(): Boolean {
        sessionManager.selectedSession?.apply {
            RouterActivity?.loadHomeOrBrowserFragment(this.id,R.anim.slide_right_in,R.anim.slide_right_out)
        }
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(ThemeViewModel::class.java)
    }

    override fun useDataBinding(): Boolean {
        return true
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentThemeBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
        binding.lifecycleOwner=this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(activity!!, factory).get(MainViewModel::class.java)
        binding.activityViewModel = activityViewModel
        binding.backHandler=this
        super.onActivityCreated(savedInstanceState)
    }


    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        StatusBarUtil.setIconColor(requireActivity(),activityViewModel.theme.value!!.colorPrimary)
        val decorationWidth=DensityUtil.dip2px(requireContext(),7f)
        binding.recyclerView.addItemDecoration(GridDividerItemDecoration(
            decorationWidth,decorationWidth,getColor(R.color.transparent)))
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_theme
    }

    override fun initData(savedInstanceState: Bundle?) {
        binding.recyclerView.adapter = object : BaseQuickAdapter<ThemeSource, CustomBaseViewHolder>(
            R.layout.item_theme,
            appData.themes.themeSources
        ) {
            override fun convert(helper: CustomBaseViewHolder, item: ThemeSource) {
                val bg = DrawableCreator.Builder()
                    .setCornersRadius(DensityUtil.dip2px(requireContext(),4f).toFloat())
                    .setSolidColor(Color.parseColor(item.colorPrimary))
                    .build()
                helper.itemView.findViewById<View>(R.id.theme_color).background=bg
                helper.itemView.findViewById<TextView>(R.id.title).text=item.name
                helper.itemView.setOnClickListener {
                    val newTheme=item.toTheme()
                    activityViewModel.theme.postValue(newTheme)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        //恢复StatusBar
        sessionManager.selectedSession?.apply {
            val host = Uri.parse(this.url).host ?: ""
            if (this.screenNumber!= Session.HOME_SCREEN && this.themeColorMap.containsKey(host)){
                StatusBarUtil.setIconColor(requireActivity(), this.themeColorMap[host]!!)
            }else{
                StatusBarUtil.setIconColor(requireActivity(),activityViewModel.theme.value!!.colorPrimary)
            }
        }
    }
}
