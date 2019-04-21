package com.dev.orangebrowser.bloc.tabs

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.BaseFragment
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.home.HomeFragment
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.databinding.FragmentTabBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.util.DensityUtil
import java.util.*
import javax.inject.Inject


class TabFragment : BaseFragment() {
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

    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        super.initViewWithDataBinding(savedInstanceState)
        initViewPager()
        //清空
        binding.clear.setOnClickListener {
            sessionManager.removeSessions()
            RouterActivity?.loadHomeFragment(HomeFragment.NO_SESSION_ID)
        }
        //新增
        binding.add.setOnClickListener {
            RouterActivity?.loadHomeFragment(HomeFragment.NO_SESSION_ID)
        }
        //返回
        binding.back.setOnClickListener {
            RouterActivity?.loadHomeOrBrowserFragment(sessionId)
        }
    }

    lateinit var data: LinkedList<Session>
    private fun initViewPager() {
        //更新高度
        val cardHeight = (DensityUtil.dip2px(requireContext(), 252f) * arguments!!.getFloat(RATIO)).toInt()
        val cardWidth=DensityUtil.dip2px(requireContext(), 252f)
        data = sessionManager.all
        val currentIndex = sessionManager.getSessionIndex(sessionId)
        (binding.viewpager.layoutParams as? FrameLayout.LayoutParams)?.apply {
            val params=this
            params.height=cardHeight
            binding.viewpager.layoutParams=params
        }
        val layoutManager=LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
        binding.viewpager.layoutManager=layoutManager
        var adapter: TabAdapter?
        adapter =TabAdapter(
            cardHeight= cardHeight,
            cardWidth= cardWidth,
            sessions = data,
            activityViewModel = activityViewModel,
            onSelect = fun(session:Session){
                RouterActivity?.loadHomeOrBrowserFragment(session.id)
            },
            onClose = fun(session:Session){
                sessionManager.remove(session)
                if (sessionManager.size==0){
                    RouterActivity?.loadHomeFragment(HomeFragment.NO_SESSION_ID)
                    return
                }
                binding.viewpager.postDelayed({
                    updateTitle(layoutManager)
                },50)
            }
        )
        PagerSnapHelper().attachToRecyclerView(binding.viewpager)
        ItemTouchHelper(SwipeUpItemTouchHelperCallback(fun(position:Int){
               if (sessionManager.size<=1){
                   sessionManager.removeSessions()
                   RouterActivity?.loadHomeFragment(HomeFragment.NO_SESSION_ID)
                   return
               }
               val session=adapter.deleteItem(position)
               sessionManager.remove(session)
               binding.viewpager.postDelayed({
                   updateTitle(layoutManager)
               },50)
        })).attachToRecyclerView(binding.viewpager)
        binding.viewpager.adapter=adapter

        binding.viewpager.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState==RecyclerView.SCROLL_STATE_IDLE){
                    val position=layoutManager.findFirstCompletelyVisibleItemPosition()
                    if (position>=0){
                        updateTitle(position)
                    }
                }
            }
        })
        binding.back.setOnClickListener {
            RouterActivity?.loadHomeOrBrowserFragment(sessionId)
        }
        binding.clear.setOnClickListener {
            sessionManager.removeSessions()
            RouterActivity?.loadHomeFragment(HomeFragment.NO_SESSION_ID)
        }
        binding.add.setOnClickListener {
            RouterActivity?.loadHomeFragment(HomeFragment.NO_SESSION_ID)
        }
        updateTitle(currentIndex)
        binding.viewpager.scrollToPosition(currentIndex)
    }
    private fun updateTitle(layoutManager:LinearLayoutManager){
         val index= layoutManager.findFirstCompletelyVisibleItemPosition()
         if (index>=0){
             if (data[index].title.isNotBlank()) {
                 binding.title.text = data[index].title
             } else {
                 binding.title.text = data[index].url
             }
         }
    }

    private fun updateTitle(index: Int) {
        if (index>=0){
            if (data[index].title.isNotBlank()) {
                binding.title.text = data[index].title
            } else {
                binding.title.text = data[index].url
            }
        }
    }


    override fun initData(savedInstanceState: Bundle?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

class SwipeUpItemTouchHelperCallback(var onSwipe: (position:Int)->Unit) : ItemTouchHelper.Callback() {
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