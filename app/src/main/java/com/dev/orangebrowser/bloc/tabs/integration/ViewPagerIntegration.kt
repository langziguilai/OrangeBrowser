package com.dev.orangebrowser.bloc.tabs.integration

import android.widget.FrameLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.bloc.home.HomeFragment
import com.dev.orangebrowser.bloc.tabs.SwipeUpItemTouchHelperCallback
import com.dev.orangebrowser.bloc.tabs.TabAdapter
import com.dev.orangebrowser.bloc.tabs.TabFragment
import com.dev.orangebrowser.databinding.FragmentTabBinding
import com.dev.orangebrowser.extension.RouterActivity
import java.util.*

class ViewPagerIntegration(
    var binding: FragmentTabBinding,
    var sessionManager: SessionManager,
    var fragment: TabFragment
) {
    lateinit var layoutManager: LinearLayoutManager

    init {
        initViewPager()
    }

    lateinit var data: LinkedList<Session>
    private fun initViewPager() {

        data = sessionManager.all
        val currentIndex = sessionManager.getSessionIndex(fragment.sessionId)
        (binding.viewpager.layoutParams as? FrameLayout.LayoutParams)?.apply {
            val params = this
            params.height = fragment.cardHeight
            binding.viewpager.layoutParams = params
        }
        layoutManager = LinearLayoutManager(fragment.requireContext(), RecyclerView.HORIZONTAL, false)
        binding.viewpager.layoutManager = layoutManager
        var adapter: TabAdapter?
        adapter = TabAdapter(
            cardHeight = fragment.cardHeight,
            cardWidth = fragment.cardWidth,
            sessions = data,
            activityViewModel = fragment.activityViewModel,
            onSelect = fun(session: Session) {
                fragment.exitAnimate(Runnable {
                    fragment.RouterActivity?.loadHomeOrBrowserFragment(session.id)
                })
            },
            onClose = fun(session: Session) {
                sessionManager.remove(session)
                if (sessionManager.size == 0) {
                    fragment.RouterActivity?.loadHomeFragment(HomeFragment.NO_SESSION_ID)
                    return
                }
                binding.viewpager.postDelayed({
                    updateTitle(layoutManager)
                }, 50)
            }
        )
        PagerSnapHelper().attachToRecyclerView(binding.viewpager)
        ItemTouchHelper(SwipeUpItemTouchHelperCallback(fun(position: Int) {
            if (sessionManager.size <= 1) {
                sessionManager.removeSessions()
                fragment.RouterActivity?.loadHomeFragment(HomeFragment.NO_SESSION_ID)
                return
            }
            val session = adapter.deleteItem(position)
            sessionManager.remove(session)
            binding.viewpager.postDelayed({
                updateTitle(layoutManager)
            }, 50)
        })).attachToRecyclerView(binding.viewpager)
        binding.viewpager.adapter = adapter

        binding.viewpager.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                    if (position >= 0) {
                        updateTitle(position)
                    }
                }
            }
        })

        updateTitle(currentIndex)
        binding.viewpager.scrollToPosition(currentIndex)
    }

    private fun updateTitle(layoutManager: LinearLayoutManager) {
        val index = layoutManager.findFirstCompletelyVisibleItemPosition()
        if (index >= 0) {
            if (data[index].title.isNotBlank()) {
                binding.title.text = data[index].title
            } else {
                binding.title.text = data[index].url
            }
        }
    }

    private fun updateTitle(index: Int) {
        if (index >= 0) {
            if (data[index].title.isNotBlank()) {
                binding.title.text = data[index].title
            } else {
                binding.title.text = data[index].url
            }
        }
    }

    fun getCurrentSession(): Session {
        val index = layoutManager.findFirstCompletelyVisibleItemPosition()
        return data[index]
    }
}