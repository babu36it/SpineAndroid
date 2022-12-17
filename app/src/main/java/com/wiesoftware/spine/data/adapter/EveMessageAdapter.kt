package com.wiesoftware.spine.data.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
//        holder.msgUserItemBinding.model=list[position]//2021-01-18 11:46:53
//        val simpleDateFormat = SimpleDateFormat("yyyy-M-dd hh:mm:ss", Locale.getDefault())
//        val time=list[position].created_on
//        try {
//            val date1: Date = simpleDateFormat.parse(time)
//            val dd= SimpleDateFormat("hh:mm a", Locale.getDefault())
//            val ss:String=dd.format(date1)
//            Log.e("fmtTime: ",ss)
//            holder.msgUserItemBinding.textView142.text=ss
//        } catch (e: ParseException) {
//            e.printStackTrace()
//            Log.e("fmtTime: ",e.message.toString())
//        }
//        holder.msgUserItemBinding.root.setOnClickListener {
//            listener.onUserChat(list[position])
//        }

        if(position == 0){
            holder.msgUserItemBinding.textView140.text="Harsh Patel"
            holder.msgUserItemBinding.textView141.text="Hii there"
            Glide.with(holder.itemView.context)
                .load( "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSKMjeeornJdOe6FD8JTzqih-CByVmSWpSD0g&usqp=CAU")
                .into( holder.msgUserItemBinding.imageView16)
        }
        if(position == 1){
            holder.msgUserItemBinding.textView140.text="Edwin Daniel"
            holder.msgUserItemBinding.textView141.text="Hi man, how are you? I saw your..."
            Glide.with(holder.itemView.context)
                .load( "https://t4.ftcdn.net/jpg/04/31/64/75/360_F_431647519_usrbQ8Z983hTYe8zgA7t1XVc5fEtqcpa.jpg")
                .into( holder.msgUserItemBinding.imageView16)
        }
        if(position == 2){
            holder.msgUserItemBinding.textView140.text="Harriet Pena"
            holder.msgUserItemBinding.textView141.text="Hi man, how are you? I saw your..."
            Glide.with(holder.itemView.context)
                .load( "https://pixinvent.com/demo/vuexy-bootstrap-laravel-admin-template/demo-1/images/profile/user-uploads/user-13.jpg")
                .into( holder.msgUserItemBinding.imageView16)
        }
        if(position == 3){
            holder.msgUserItemBinding.textView140.text="Estella Ray"
            holder.msgUserItemBinding.textView141.text="Hi man, how are you? I saw your..."
            Glide.with(holder.itemView.context)
                .load( "https://pixinvent.com/demo/vuexy-bootstrap-laravel-admin-template/demo-6/images/portrait/small/avatar-s-20.jpg")
                .into( holder.msgUserItemBinding.imageView16)
        }



    }

    override fun getItemCount() = 4
}