package com.dev.orangebrowser.bloc.found.creator

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.dev.base.BaseFragment
import com.dev.base.extension.showToast
import com.dev.base.support.BackHandler
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.config.ErrorCode
import com.dev.orangebrowser.data.model.MainPageSite
import com.dev.orangebrowser.databinding.FragmentSiteCreatorBinding
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.extension.appData
import com.dev.orangebrowser.extension.getColor
import com.dev.util.ColorKitUtil
import com.dev.util.DensityUtil
import com.dev.view.StatusBarUtil
import com.noober.background.drawable.DrawableCreator
import java.lang.Exception
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

class SiteCreatorFragment : BaseFragment(), BackHandler {
    @Inject
    lateinit var sessionManager: SessionManager
    val sessionId: String
        get() = arguments?.getString(SESSION_ID) ?: ""

    override fun onBackPressed(): Boolean {
        fragmentManager?.popBackStack()
        return true
    }

    companion object {
        val Tag = "SiteCreatorFragment"
        const val SESSION_ID = "session_id"
        fun newInstance(sessionId: String) = SiteCreatorFragment().apply {
            arguments = Bundle().apply {
                putString(SESSION_ID, sessionId)
            }
        }
    }

    lateinit var viewModel: SiteCreatorViewModel
    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentSiteCreatorBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(SiteCreatorViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSiteCreatorBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(activity!!, factory).get(MainViewModel::class.java)
        binding.activityViewModel = activityViewModel
        binding.fragment = this
        super.onActivityCreated(savedInstanceState)
    }

    override fun useDataBinding(): Boolean {
        return true
    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_site_creator
    }

    private var color:Int=0xffffff
    private var textColor:Int=0x111111
    private var themes:LinkedList<Int> = LinkedList()
    lateinit var random: Random
    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        StatusBarUtil.setIconColor(requireActivity(), activityViewModel.theme.value!!.colorPrimary)
        val session = sessionManager.findSessionById(sessionId)
        color = if (session != null) {
            binding.url.setText(session.url)
            binding.siteTitle.setText(session.title)
            binding.siteDomain.setText(Uri.parse(session.url).host)
            binding.siteName.setText(session.title)
            session.themeColorMap[Uri.parse(session.url).host ?: ""] ?: activityViewModel.theme.value!!.colorPrimary
        }else{
            activityViewModel.theme.value!!.colorPrimary
        }
        //更新主题
        updateTheme(color)
        viewModel.errorCodeLiveData.observe(this, androidx.lifecycle.Observer {
            when (it) {
                ErrorCode.LOAD_FAIL -> {
                    requireContext().showToast(getString(R.string.load_fail))
                }
            }
        })
        viewModel.saveStatusLiveData.observe(this,androidx.lifecycle.Observer {
            if (it==true){
                requireContext().apply {
                    showToast(getString(R.string.save_success))
                }
                onBackPressed()
            }
        })
        parseThemes()
    }
    private fun parseThemes(){
        try {
            themes.addAll(appData.themes.themeSources.map{ Color.parseColor(it.colorPrimary) })
            themes.add(0,color)
            random= Random(themes.size)
        }catch (e:Exception){
            requireContext().showToast(getString(R.string.get_theme_color_failed))
        }
    }
    fun nextTheme() {
        val index=themes.indexOf(color)
        color= themes[(index+1) % themes.size]
        updateTheme(color)
    }

    fun previousTheme() {
        val index=themes.indexOf(color)
        color= themes[(index-1+themes.size) % themes.size]
        updateTheme(color)
    }

    fun randomTheme() {
        color= themes[Math.abs(random.nextInt()) % themes.size]
        updateTheme(color)
    }

    private fun updateTheme(color: Int) {
        val drawable = DrawableCreator.Builder()
            .setSolidColor(color)
            .setCornersRadius(DensityUtil.dip2px(requireContext(), 2f).toFloat())
            .setStrokeWidth(1f)
            .setStrokeColor(0xdfdfdf).build()
        binding.styleContainer.background = drawable
        if (ColorKitUtil.isBackGroundWhiteMode(color)) {
            textColor=getColor(R.color.colorBlack)
            binding.siteTitle.setTextColor(getColor(R.color.colorBlack))
            binding.siteTitle.setHintTextColor(getColor(R.color.colorBlack))
            binding.siteDomain.setTextColor(getColor(R.color.colorBlack))
            binding.siteDomain.setHintTextColor(getColor(R.color.colorBlack))
        } else {
            textColor=getColor(R.color.colorWhite)
            binding.siteTitle.setTextColor(getColor(R.color.colorWhite))
            binding.siteTitle.setHintTextColor(getColor(R.color.colorWhite))
            binding.siteDomain.setTextColor(getColor(R.color.colorWhite))
            binding.siteDomain.setHintTextColor(getColor(R.color.colorWhite))
        }
    }

    fun save() {
        val mainPageSite=MainPageSite(
            url=binding.url.text.toString(),
            name = binding.siteName.text.toString(),
            backgroundColor = color,
            description = binding.siteDomain.text.toString(),
            textColor = textColor,
            textIcon = binding.siteName.text.toString()
        )
        viewModel.addToHomePage(mainPageSite)
    }

    override fun initData(savedInstanceState: Bundle?) {

    }
}
