/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.feature.contextmenu

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.BaseTransparentFullScreenDialogFragment
import com.dev.base.extension.onGlobalLayoutComplete
import com.dev.browser.R
import com.dev.browser.session.Session
import com.dev.util.DensityUtil
import com.dev.util.Keep

private const val KEY_TITLE = "title"
private const val KEY_SESSION_ID = "session_id"
private const val KEY_IDS = "ids"
private const val KEY_LABELS = "labels"
private const val LONG_CLICK_X = "long_click_x"
private const val LONG_CLICK_Y = "long_click_y"

/**
 * [DialogFragment] implementation to display the actual context menu dialog.
 */
@Keep
class ContextMenuFragment : BaseTransparentFullScreenDialogFragment() {


    internal var feature: ContextMenuFeature? = null

    @VisibleForTesting
    internal val itemIds: List<String> by lazy { arguments!!.getStringArrayList(KEY_IDS)!! }
    @VisibleForTesting internal val itemLabels: List<String> by lazy { arguments!!.getStringArrayList(KEY_LABELS)!! }
    @VisibleForTesting internal val sessionId: String by lazy { arguments!!.getString(KEY_SESSION_ID)!! }
    @VisibleForTesting internal val title: String by lazy { arguments!!.getString(KEY_TITLE)!! }
    @VisibleForTesting internal val longClickX: Int by lazy { arguments!!.getInt(LONG_CLICK_X) }
    @VisibleForTesting internal val longClickY: Int by lazy { arguments!!.getInt(LONG_CLICK_Y) }

    override fun getLayoutId(): Int {
        return R.layout.mozac_feature_contextmenu_dialog
    }

    override fun initViewAndData(rootView: View, bundle: Bundle?) {
        initDialogContentView(rootView, LayoutInflater.from(requireContext()))
    }

    var offSet:Int=-1
    @SuppressLint("InflateParams")
    internal fun initDialogContentView(container:View,inflater: LayoutInflater) {
        offSet=DensityUtil.dip2px(container.context,20f)
        container.setOnClickListener {
            dismiss()
        }
        val recyclerView=container.findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = ContextMenuAdapter(this@ContextMenuFragment, inflater)
        }
        recyclerView.onGlobalLayoutComplete {
            (it.layoutParams as? FrameLayout.LayoutParams)?.apply {
                this.leftMargin=calculateRecyclerViewLeftMargin(container.width,it.width,longClickX)
                this.topMargin=calculateRecyclerViewTopMargin(container.height,it.height,longClickY)
                it.layoutParams=this
            }
        }
    }
    //计算左边的距离
    private fun calculateRecyclerViewLeftMargin(containerWidth: Int, childWidth: Int, x:Int):Int{
        return if (x+childWidth+offSet>containerWidth){
            x-childWidth-offSet
        }else{
            x+offSet
        }
    }
    //计算左边的距离
    private fun calculateRecyclerViewTopMargin(containerHeight: Int, childHeight: Int, y:Int):Int{
        return when {
            y+childHeight/2+offSet>containerHeight -> containerHeight-childHeight-offSet
            y-childHeight/2-offSet<0 -> offSet
            else -> y-childHeight/2
        }
    }
    internal fun onItemSelected(position: Int) {
        feature?.onMenuItemSelected(sessionId, itemIds[position])
        dismiss()
    }

    companion object {
        /**
         * Create a new [ContextMenuFragment].
         */
        fun create(
            session: Session,
            title: String,
            ids: List<String>,
            labels: List<String>,
            longClickX:Int,
            longClickY:Int
        ): ContextMenuFragment {
            val arguments = Bundle()
            arguments.putString(KEY_TITLE, title)
            arguments.putStringArrayList(KEY_IDS, ArrayList(ids))
            arguments.putStringArrayList(KEY_LABELS, ArrayList(labels))
            arguments.putString(KEY_SESSION_ID, session.id)
            arguments.putInt(LONG_CLICK_X, longClickX)
            arguments.putInt(LONG_CLICK_Y,longClickY)
            val fragment = ContextMenuFragment()
            fragment.arguments = arguments
            return fragment
        }
    }
}

/**
 * RecyclerView adapter for displayig the context menu.
 */
class ContextMenuAdapter(
    private val fragment: ContextMenuFragment,
    private val inflater: LayoutInflater
) : RecyclerView.Adapter<ContextMenuViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int) = ContextMenuViewHolder(
        inflater.inflate(R.layout.mozac_feature_contextmenu_item, parent, false))

    override fun getItemCount(): Int = fragment.itemIds.size

    override fun onBindViewHolder(holder: ContextMenuViewHolder, position: Int) {
        val label = fragment.itemLabels[position]
        holder.labelView.text = label

        holder.itemView.setOnClickListener { fragment.onItemSelected(position) }
    }
}

/**
 * View holder for a context menu item.
 */
class ContextMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    internal val labelView = itemView.findViewById<TextView>(R.id.label)
}
