package com.dev.orangebrowser.bloc.tabs

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dev.orangebrowser.R
import kotlinx.android.synthetic.main.fragment_tab.view.*
import kotlinx.android.synthetic.main.item_tab_display_item.view.*

class TabViewHolder(parentView: View):RecyclerView.ViewHolder(parentView){
    var title:TextView=parentView.findViewById(R.id.title)
    var thumbnail: ImageView =parentView.findViewById(R.id.thumbnail)
    var container:FrameLayout=parentView.findViewById(R.id.container)
    var bottomBar:View=parentView.findViewById(R.id.bottom_bar)
    var closeIcon:View=parentView.findViewById(R.id.close)
}
