package com.wiesoftware.spine.data.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.ChatMsgData
import com.wiesoftware.spine.databinding.ChatItemBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Vivek kumar on 1/19/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class EveChatAdapter(val dataList: List<ChatMsgData>,val eveUserImg:String,val userId: String): RecyclerView.Adapter<EveChatAdapter.EveChatHolder>() {
    class EveChatHolder(val chatItemBinding: ChatItemBinding): RecyclerView.ViewHolder(chatItemBinding.root) {

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        EveChatHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.chat_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: EveChatHolder, position: Int) {
        holder.chatItemBinding.model=dataList[position]
        val chatDate=dataList[position].created_on
        val uId: String=dataList[position].message_by
        if (uId.equals(userId)){
            holder.chatItemBinding.receiverChat.visibility=View.GONE
            holder.chatItemBinding.senderChat.visibility=View.VISIBLE
        }else{
            holder.chatItemBinding.receiverChat.visibility=View.VISIBLE
            holder.chatItemBinding.senderChat.visibility=View.GONE
        }
        val simpleDateFormat = SimpleDateFormat("yyyy-M-dd hh:mm:ss", Locale.getDefault())
        try {
            val date1: Date = simpleDateFormat.parse(chatDate)
            val dd= SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
            val ss:String=dd.format(date1)
            Log.e("fmtDate: ",ss)
            holder.chatItemBinding.dateT.text=ss
            holder.chatItemBinding.dT.text=ss
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("fmtDate: ",e.message.toString())
        }
        Glide.with(holder.chatItemBinding.imageView17)
            .load(eveUserImg)
            .placeholder(R.drawable.ic_profile)
            .into(holder.chatItemBinding.imageView17)
    }

    override fun getItemCount() = dataList.size
}