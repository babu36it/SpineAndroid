package com.wiesoftware.spine.data.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.EveMsgUserData
import com.wiesoftware.spine.databinding.MsgUserItemBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Vivek kumar on 1/18/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class EveMessageAdapter(val list: List<EveMsgUserData>,val listener: OnUserChatListener): RecyclerView.Adapter<EveMessageAdapter.EveUserHolder>() {
    class EveUserHolder(val msgUserItemBinding: MsgUserItemBinding): RecyclerView.ViewHolder(msgUserItemBinding.root) {

    }
    interface OnUserChatListener{
        fun onUserChat(eveMsgUserData: EveMsgUserData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        EveUserHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.msg_user_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: EveUserHolder, position: Int) {
        holder.msgUserItemBinding.model=list[position]//2021-01-18 11:46:53
        val simpleDateFormat = SimpleDateFormat("yyyy-M-dd hh:mm:ss", Locale.getDefault())
        val time=list[position].created_on
        try {
            val date1: Date = simpleDateFormat.parse(time)
            val dd= SimpleDateFormat("hh:mm a", Locale.getDefault())
            val ss:String=dd.format(date1)
            Log.e("fmtTime: ",ss)
            holder.msgUserItemBinding.textView142.text=ss
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("fmtTime: ",e.message.toString())
        }
        holder.msgUserItemBinding.root.setOnClickListener {
            listener.onUserChat(list[position])
        }
    }

    override fun getItemCount() = list.size
}