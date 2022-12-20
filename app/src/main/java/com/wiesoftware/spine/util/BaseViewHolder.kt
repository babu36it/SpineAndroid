package com.wiesoftware.spine.util

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class BaseViewHolder<T> internal constructor(private val binding: ViewBinding, private val experssion: (T, ViewBinding, Context) -> Unit)
    : RecyclerView.ViewHolder(binding.root){
    fun bind(item:T,context: Context){
        experssion(item,binding,context)
    }
}