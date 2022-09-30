package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.PostData
import com.wiesoftware.spine.databinding.SearchForUContentItemBinding

/**
 * Created by Vivek kumar on 4/8/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class SearchForUContentAdapter(val postList: List<PostData>, val listener: SearchForUContentListener):RecyclerView.Adapter<SearchForUContentAdapter.ContentHolder>() {
    class ContentHolder(val searchForUContentItemBinding: SearchForUContentItemBinding):RecyclerView.ViewHolder(searchForUContentItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ContentHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.search_for_u_content_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ContentHolder, position: Int) {
        holder.searchForUContentItemBinding.model=postList[position]
        holder.searchForUContentItemBinding.root.setOnClickListener { v->
            listener.onPostDetails(postList[position])
        }
    }

    override fun getItemCount() = postList.size

    interface SearchForUContentListener{
        fun onPostDetails(postData: PostData)
    }
}