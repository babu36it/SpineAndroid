package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.SpineImpulseData
import com.wiesoftware.spine.databinding.ForYouContentItemBinding
import com.wiesoftware.spine.databinding.ImpulseContentItemBinding

/**
 * Created by Vivek kumar on 9/24/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class ImpulseAdapter(
    private val impulseData: List<SpineImpulseData>,private val impulseEventListener: ImpulseEventListener
): RecyclerView.Adapter<ImpulseAdapter.ImpulseHolder>() {
    class ImpulseHolder( val impulseContentItemBinding: ImpulseContentItemBinding):
        RecyclerView.ViewHolder(impulseContentItemBinding.root){
    }
    interface ImpulseEventListener{
        fun onComment(spineImpulseData: SpineImpulseData)
        fun onLike(spineImpulseData: SpineImpulseData)
        fun omShare(spineImpulseData: SpineImpulseData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ImpulseHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.impulse_content_item,
                parent,
                false
            )
        )
    override fun onBindViewHolder(holder: ImpulseHolder, position: Int) {
        holder.impulseContentItemBinding.viewModel=impulseData[position]
        holder.impulseContentItemBinding.textView32.setOnClickListener {
            impulseEventListener.onComment(impulseData[position])
        }
        holder.impulseContentItemBinding.textView31.setOnClickListener {
            impulseEventListener.onLike(impulseData[position])
        }
        holder.impulseContentItemBinding.imageButton3.setOnClickListener { impulseEventListener.omShare(impulseData[position]) }
    }

    override fun getItemCount() = impulseData.size
}