package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.SpineImpulseAdapter.*
import com.wiesoftware.spine.data.net.reponses.SpineImpulseData
import com.wiesoftware.spine.databinding.ImpulseItemBinding


/**
 * Created by Vivek kumar on 8/28/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class SpineImpulseAdapter(
    private val impulseData: List<SpineImpulseData>,
    private val spineImpulseEventListener: SpineImpulseEventListener
): RecyclerView.Adapter<ImpulseDataHolder>() {
    class ImpulseDataHolder(
        val impulseItemBinding: ImpulseItemBinding, val context: Context
    ): RecyclerView.ViewHolder(impulseItemBinding.root) {
        fun setTextViewDrawableColor(textView: TextView) {
            for (drawable in textView.compoundDrawablesRelative) {
                if (drawable != null) {
                    drawable.colorFilter =
                        PorterDuffColorFilter(
                            getColor(context, R.color.color_red),
                            PorterDuff.Mode.SRC_IN
                        )
                }
            }
        }
    }
    interface SpineImpulseEventListener{
        fun onCommentClicked(spineImpulseData: SpineImpulseData)
        fun onLikeComment(spineImpulseData: SpineImpulseData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ImpulseDataHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.impulse_item,
                parent,
                false
            ), parent.context
        )

    override fun onBindViewHolder(holder: ImpulseDataHolder, position: Int) {
        holder.impulseItemBinding.viewModel=impulseData[position]
        if(impulseData[position].user_like_status.equals("1")){
            holder.setTextViewDrawableColor(holder.impulseItemBinding.textView24)
        }
        holder.impulseItemBinding.textView25.setOnClickListener {
            spineImpulseEventListener.onCommentClicked(impulseData[position])
        }
        holder.impulseItemBinding.textView24.setOnClickListener {
            spineImpulseEventListener.onLikeComment(impulseData[position])
        }
    }

    override fun getItemCount() = impulseData.size
}