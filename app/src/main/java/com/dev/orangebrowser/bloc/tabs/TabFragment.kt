package com.dev.orangebrowser.bloc.tabs

import android.content.Context
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
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.mozac_feature_choice_dialogs.*
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
    private fun initViewPager(){
        val cardHeight=(DensityUtil.dip2px(requireContext(),252f)*arguments!!.getFloat(RATIO)).toInt()
        //更新高度
//        (binding.viewpager.layoutParams as? FrameLayout.LayoutParams)?.apply {
//                 val params=this
//                 params.height=(DensityUtil.dip2px(requireContext(),252f)*arguments!!.getFloat(RATIO)).toInt()
//                 binding.viewpager.layoutParams=params
//        }

        val currentIndex= sessionManager.getSessionIndex(sessionId)
        val adapter =
            object : BaseQuickAdapter<Session, CustomBaseViewHolder>(R.layout.item_tab_display_item, sessionManager.all) {
                override fun convert(helper: CustomBaseViewHolder, item: Session) {
                    helper.getView<FrameLayout>(R.id.container)?.apply {
                        val view=this
                        (this.layoutParams as? FrameLayout.LayoutParams)?.apply {
                            val params=this
                            params.height=cardHeight
                            view.layoutParams=params
                        }
                    }
                    if (item.tmpThumbnail!=null){
                        helper.loadBitmapToImageView(R.id.thumbnail,item.tmpThumbnail!!)
                    }else if (item.thumbnailPath!=null){
                        helper.loadLocalImage(R.id.thumbnail, item.thumbnailPath!!)
                    }
                    helper.getView<View>(R.id.bottom_bar)
                        .setBackgroundColor(activityViewModel.theme.value!!.colorPrimary)
                    if (item.title.isNotBlank()) {
                        helper.setText(R.id.title, item.title)
                    } else {
                        helper.setText(R.id.title, item.url)
                    }
                    helper.addOnClickListener(R.id.close)
                }
            }
        adapter.setHasStableIds(true)
        adapter.setOnItemChildClickListener { _, view, position ->
            if (view.id == R.id.close) {
                sessionManager.findSessionById(sessionManager.all[position].id)?.apply {
                    sessionManager.remove(session = this)
                    adapter.remove(position)
                    if (sessionManager.size==0){
                        RouterActivity?.loadHomeFragment(HomeFragment.NO_SESSION_ID)
                    }
                }
            }
        }
        adapter.setOnItemClickListener { _, _, position ->
            val session=sessionManager.all[position]
            sessionManager.select(session)
            RouterActivity?.loadHomeOrBrowserFragment(session.id)
        }
        binding.viewpager.adapter = adapter
        binding.viewpager.layoutManager=LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
        val helper=PagerSnapHelper()
        helper.attachToRecyclerView(binding.viewpager)
        binding.viewpager.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState==RecyclerView.SCROLL_STATE_IDLE){
                  helper.findSnapView(binding.viewpager.layoutManager)?.apply {
                      val view=this
                      binding.viewpager.layoutManager?.getPosition(view)?.apply {
                          updateTitle(this)
                      }
                  }
                }
            }
        })
        binding.viewpager.scrollToPosition(currentIndex)
        //
        ItemTouchHelper(SwipeUpItemTouchHelperCallback(object:ItemTouchHelperAdapter{
            override fun onItemDismiss(position: Int) {
                sessionManager.remove(sessionManager.all[position])
                binding.viewpager.adapter?.notifyItemRemoved(position)
            }
        })).attachToRecyclerView(binding.viewpager)
        updateTitle(currentIndex)
    }

    private fun updateTitle(index:Int){
        if (sessionManager.all[index].title.isNotBlank()) {
            binding.title.text = sessionManager.all[index].title
        } else {
            binding.title.text = sessionManager.all[index].url
        }
    }


    override fun initData(savedInstanceState: Bundle?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        const val Tag = "TabFragment"
        const val RATIO="RATIO"
        fun newInstance(sessionId: String,ratio:Float) = TabFragment().apply {
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
class SwipeUpItemTouchHelperCallback(var adapter:ItemTouchHelperAdapter):ItemTouchHelper.Callback(){
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
       return  makeMovementFlags(0,ItemTouchHelper.UP)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onItemDismiss(viewHolder.adapterPosition)
    }
}