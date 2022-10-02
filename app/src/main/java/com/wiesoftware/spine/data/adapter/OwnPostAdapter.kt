package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.PostData
import com.wiesoftware.spine.databinding.OwnPostItemBinding


/**
 * Created by Vivek kumar on 1/18/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class OwnPostAdapter(val postList: List<PostData>,val listener: OwnPostSelectedListener):RecyclerView.Adapter<OwnPostAdapter.OwnPostHolder>() {
    class OwnPostHolder(val ownPostItemBinding: OwnPostItemBinding): RecyclerView.ViewHolder(ownPostItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        OwnPostHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.own_post_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: OwnPostHolder, position: Int) {
//        holder.ownPostItemBinding.model=postList[position]
//        val postType=postList[position].type
//        val width = 100
//        val height = 200
//
//        holder.itemView.layoutParams = SpanLayoutParams(SpanSize(width, height))
        val postType= "1"
        var url = ""
        if (position == 1){
            url = "https://img.rawpixel.com/private/static/images/website/2022-05/366-mj-7703-fon-jj.jpg?w=800&dpr=1&fit=default&crop=default&q=65&vib=3&con=3&usm=15&bg=F4F4F3&ixlib=js-2.2.1&s=f23359a06a626e56bedb45dac2809feb"
        }
        if (position == 0){
            url = "https://img.freepik.com/free-vector/nature-scene-with-river-hills-forest-mountain-landscape-flat-cartoon-style-illustration_1150-37326.jpg?w=2000"
        }

        if (position == 2){
            url = "https://tableforchange.com/wp-content/uploads/2017/06/yoga-quotes-11-min.png"
        }

        if (position == 3){
            url = "https://miro.medium.com/max/1400/1*61Vk6EDAFTD6j4s7X6y2NA.png"
        }

        if (position == 4){
            url = "https://streetbounty.com/wp-content/uploads/2018/03/100-Street-Photography-Quotes-HelenLevitt.jpg"
        }

        if (position == 5){
            url = "https://iso.500px.com/wp-content/uploads/2015/04/quotes_35.jpg"
        }
        if (position == 6){
            url = "https://i.pinimg.com/736x/69/27/6c/69276ccea9ec5464b9f4c7c69a85390f.jpg"
        }
        if (position == 7){
            url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSsISEKeeDKAl0gAMw0MMVi3gggbna6F62OvA&usqp=CAU"
        }

        if (postType.equals("1")){
            holder.ownPostItemBinding.imgContent.visibility=View.VISIBLE
            Glide.with(holder.itemView.context)
                .load( url)
                .into( holder.ownPostItemBinding.imageView15)
            holder.ownPostItemBinding.textContent.visibility=View.GONE
        }else{
//            holder.ownPostItemBinding.imgContent.visibility=View.GONE
//            holder.ownPostItemBinding.textContent.visibility=View.VISIBLE
        }
//        holder.ownPostItemBinding.imgContent.setOnClickListener {
//            listener.onPostSelected(postList[position])
//        }
//        holder.ownPostItemBinding.textView134.setOnClickListener {
//            listener.onPostSelected(postList[position])
//        }
//        holder.ownPostItemBinding.postContent.setOnClickListener {
//            listener.onPostSelected(postList[position])
//        }
    }

    override fun getItemCount() = 7

    interface OwnPostSelectedListener{
        fun onPostSelected(postData: PostData)
    }

    override fun onViewAttachedToWindow(holder: OwnPostHolder) {
        super.onViewAttachedToWindow(holder)
        val lp: ViewGroup.LayoutParams = holder.itemView.getLayoutParams()
        if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams
            && (holder.getLayoutPosition() === 3 || holder.getLayoutPosition() === 13)
        ) {
            lp.isFullSpan = true
        }
        holder.setIsRecyclable(false)
    }

}