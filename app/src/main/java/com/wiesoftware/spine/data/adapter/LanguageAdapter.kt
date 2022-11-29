package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.ui.home.menus.podcasts.addpodcasts.AddPodcastActivity

class LanguageAdapter(val addPodcastActivity: AddPodcastActivity, val list: ArrayList<String>) :
    BaseAdapter() {
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val inflater = addPodcastActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row = inflater.inflate(R.layout.search_text_item, parent, false)
        val label = row.findViewById<View>(R.id.tv_title) as TextView
        if (list.get(position) != null) label.setText(
            list.get(position)
        )

        return row
    }

}
