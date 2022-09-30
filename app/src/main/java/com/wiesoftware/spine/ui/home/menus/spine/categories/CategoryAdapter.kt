package com.wiesoftware.spine.ui.home.menus.spine.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.HashtagData
import com.wiesoftware.spine.databinding.CatItemBinding

/**
 * Created by Vivek kumar on 12/16/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class CategoryAdapter(val hashtagDataList: List<HashtagData>,val listener:HashtagEventListener) : RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {
    class CategoryHolder(val catItemBinding: CatItemBinding):RecyclerView.ViewHolder(catItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CategoryHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.cat_item,
                parent,
                false
                )
        )


    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
            holder.catItemBinding.model=hashtagDataList[position]
            holder.catItemBinding.follow.setOnClickListener {
            listener.onTrendingFollow(hashtagDataList[position])
            }
    }

    override fun getItemCount() = hashtagDataList.size

    interface HashtagEventListener{
        fun onTrendingFollow(hashtag: HashtagData)
    }
}