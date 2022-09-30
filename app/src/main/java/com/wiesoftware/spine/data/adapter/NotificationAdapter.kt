package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.NotificationData
import com.wiesoftware.spine.databinding.NotificationItemBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Vivek kumar on 1/22/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class NotificationAdapter(val dataList: List<NotificationData>): RecyclerView.Adapter<NotificationAdapter.NotificationHolder>() {
    class NotificationHolder(val notificationItemBinding: NotificationItemBinding,val context: Context): RecyclerView.ViewHolder(notificationItemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NotificationHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.notification_item,
                parent,
                false
            ),parent.context
        )

    override fun onBindViewHolder(holder: NotificationHolder, position: Int) {
        holder.notificationItemBinding.model=dataList[position]
        val simpleDateFormat = SimpleDateFormat("yyyy-M-dd hh:mm:ss", Locale.getDefault())
        val time=dataList[position].created_on
        try {
            val date1: Date = simpleDateFormat.parse(time)
            val dd= SimpleDateFormat("hh:mm a", Locale.getDefault())
            val ss:String=dd.format(date1)
            Log.e("fmtTime: ",ss)
            holder.notificationItemBinding.textView172.text=ss
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("fmtTime: ",e.message.toString())
        }
    }

    override fun getItemCount() =dataList.size
}