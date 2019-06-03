package com.dev.orangebrowser.bloc.tabs

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.BaseFragment
import com.dev.base.support.BackHandler
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.bloc.tabs.integration.*
import com.dev.orangebrowser.databinding.FragmentTabBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.util.DensityUtil
import javax.inject.Inject


class TabFragment : BaseFragment(),BackHandler {
    override fun onBackPressed(): Boolean {
        sessionManager.selectedSession?.apply {
            RouterActivity?.loadHomeOrBrowserFragment(this.id)
        }
        return true
    }

    @Inject
    lateinit var sessionManager: SessionManager
    val sessionId: String
        get() = arguments?.getString(BrowserFragment.SESSION_ID) ?: ""
    lateinit var viewModel: TabViewModel
    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentTabBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(TabViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(activity!!, factory).get(MainViewModel::class.java)
        binding.activityViewModel = activityViewModel
        super.onActivityCreated(savedInstanceState)
    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_tab
    }

    override fun useDataBinding(): Boolean {
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTabBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
        return binding.root
    }

    private lateinit var topBarIntegration: TopBarIntegration
    private lateinit var bottomBarAnimateIntegration: BottomBarAnimateIntegration
    private lateinit var titleIntegration: TitleIntegration
    private lateinit var bottomBarIntegration: BottomBarIntegration
    private lateinit var thumbnailPlaceHolderIntegration: ThumbnailPlaceHolderIntegration
    private lateinit var  viewPagerIntegration:ViewPagerIntegration
      var cardHeight:Int=0
      var cardWidth:Int=0
    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        super.initViewWithDataBinding(savedInstanceState)
        //更新高度
         cardHeight = (DensityUtil.dip2px(requireContext(), 252f) * arguments!!.getFloat(TabFragment.RATIO)).toInt()
         cardWidth= DensityUtil.dip2px(requireContext(), 252f)
         Log.d("cardHeight",""+cardHeight+"px")
         Log.d("cardWidth",""+cardWidth+"px")
        val session = sessionManager.findSessionById(sessionId)
        session?.apply {
            topBarIntegration = TopBarIntegration(
                binding = binding,
                fragment = this@TabFragment,
                savedInstanceState = savedInstanceState,
                session = this
            )
            bottomBarAnimateIntegration = BottomBarAnimateIntegration(
                binding = binding,
                fragment = this@TabFragment,
                savedInstanceState = savedInstanceState,
                sessionManager = sessionManager,
                session = this
            )
            titleIntegration = TitleIntegration(binding = binding)
            bottomBarIntegration = BottomBarIntegration(
                binding = binding,
                sessionManager = sessionManager,
                fragment = this@TabFragment,
                sessionId = sessionId
            )
            viewPagerIntegration = ViewPagerIntegration(
                binding = binding,
                sessionManager = sessionManager,
                fragment = this@TabFragment
            )
            thumbnailPlaceHolderIntegration= ThumbnailPlaceHolderIntegration(binding=binding,session = session,fragment = this@TabFragment)
            enterAnimate(runnable = Runnable {  })
        }
    }

    private fun enterAnimate(runnable: Runnable){
        topBarIntegration.hide()
        titleIntegration.show()
        bottomBarAnimateIntegration.hide()
        bottomBarIntegration.show()
        thumbnailPlaceHolderIntegration.hide(runnable)

    }
    fun exitAnimate(runnable: Runnable){
        val selectSession=viewPagerIntegration.getCurrentSession()
        //选定session
        sessionManager.select(selectSession)
        thumbnailPlaceHolderIntegration.setImage(selectSession)
        topBarIntegration.show(selectSession)
        titleIntegration.hide()
        bottomBarAnimateIntegration.show(selectSession)
        bottomBarIntegration.hide()
        //加载bitmap到session
        thumbnailPlaceHolderIntegration.show(runnable,selectSession)
    }
    override fun initData(savedInstanceState: Bundle?) {

    }

    companion object {
        const val Tag = "TabFragment"
        const val RATIO = "RATIO"
        fun newInstance(sessionId: String, ratio: Float) = TabFragment().apply {
            arguments = Bundle().apply {
                putString(BrowserFragment.SESSION_ID, sessionId)
                putFloat(RATIO, ratio)
            }
        }
    }

}

interface ItemTouchHelperAdapter {
    fun onItemDismiss(position: Int)
}

class SwipeUpItemTouchHelperCallback(var onSwipe: (position: Int) -> Unit) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(0, ItemTouchHelper.UP)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onSwipe(viewHolder.adapterPosition)
    }
}