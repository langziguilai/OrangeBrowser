package com.dev.orangebrowser.bloc.tabs


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.dev.browser.session.Session
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.view.extension.loadLocalImage
import java.util.*

class TabAdapter(
    var sessions: LinkedList<Session>,
    var cardWidth: Int,
    var cardHeight: Int,
    var activityViewModel: MainViewModel,
    var onSelect: (session: Session) -> Unit,
    var onClose: (session: Session) -> Unit
) : RecyclerView.Adapter<TabViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tab_display_item, null)
        val container = view.findViewById<FrameLayout>(R.id.container)
        //此处必须设置容器指定的大小
        (container.layoutParams as FrameLayout.LayoutParams).apply {
            val params = this
            params.height = cardHeight
            params.width = cardWidth
            container.layoutParams = params
        }
        return TabViewHolder(view)
    }

    override fun getItemCount(): Int {
        return sessions.size
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        val session = sessions[position]
        if (session.tmpThumbnail != null && session.tmpThumbnail!!.get() != null) {
            holder.thumbnail.setImageBitmap(session.tmpThumbnail!!.get())
            Log.d("onBindViewHolder","session id: "+session.id+" holder.tmpThumbnail.setImageBitmap(session.tmpThumbnail!!.get())")
        } else if (session.thumbnailPath != null) {
            holder.thumbnail.loadLocalImage(session.thumbnailPath!!)
            Log.d("onBindViewHolder","session id: "+session.id+" holder.thumbnail.loadLocalImage(session.thumbnailPath!!)")
        }else{
            Log.d("onBindViewHolder","session id: "+session.id+" holder.thumbnail not set")
        }
        if (session.title.isNotBlank()) {
            holder.title.text = session.title
        } else if (session.url.isNotBlank()) {
            holder.title.text = session.url
        }
        holder.bottomBar.setBackgroundColor(activityViewModel.theme.value!!.colorPrimary)
        holder.container.setOnClickListener {
            onSelect(session)
        }
        holder.closeIcon.setOnClickListener {
            sessions.remove(session)
            onClose(session)
            notifyItemRemoved(position)
        }
    }

    fun deleteItem(position: Int): Session {
        val session = sessions.removeAt(position)
        notifyItemRemoved(position)
        return session
    }
}