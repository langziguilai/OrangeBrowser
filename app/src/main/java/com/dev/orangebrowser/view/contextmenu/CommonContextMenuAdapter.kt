package com.dev.orangebrowser.view.contextmenu

import com.dev.orangebrowser.R
import com.dev.util.Keep
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter

@Keep
class CommonContextMenuAdapter(menuItemLayout: Int, dataList: List<MenuItem>) :
    BaseQuickAdapter<MenuItem, CustomBaseViewHolder>(menuItemLayout, dataList) {
    override fun convert(helper: CustomBaseViewHolder, item: MenuItem) {
        helper.setText(R.id.label, item.label)
        if (item.labelColor > 0) {
            helper.setTextColor(R.id.label, item.labelColor)
        }
        item.icon?.apply {
            helper.setText(R.id.icon, this)
            if (item.iconColor > 0) {
                helper.setTextColor(R.id.icon, item.iconColor)
            }
        }
        helper.itemView.apply {
            isClickable = true
            isFocusable = true
            setOnClickListener {
                item.action?.execute(item)
            }
        }
    }

}