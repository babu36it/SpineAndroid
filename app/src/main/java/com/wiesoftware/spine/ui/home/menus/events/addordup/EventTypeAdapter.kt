package com.wiesoftware.spine.ui.home.menus.events.addordup

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.EventTypeData
import com.wiesoftware.spine.data.net.reponses.EventsRecord
import com.wiesoftware.spine.databinding.DupEventListItemBinding
import com.wiesoftware.spine.databinding.IteamEventTypeBinding
import com.wiesoftware.spine.databinding.IteamEventTypeBindingImpl
import com.wiesoftware.spine.ui.home.menus.events.addevents.AddEventActivity
import com.wiesoftware.spine.util.Utils
import kotlinx.android.synthetic.main.dup_event_list_item.view.*

class EventTypeAdapter(
    val data: MutableList<EventTypeData>,
    val evetclickEventListener: EventCliclListener
) : RecyclerView.Adapter<EventTypeAdapter.EventTypeHolder>()  {
    class EventTypeHolder(val dupEventListItemBinding: IteamEventTypeBinding): RecyclerView.ViewHolder(dupEventListItemBinding.root) {

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = EventTypeHolder(

        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.iteam_event_type,
            parent,
            false
        )
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: EventTypeHolder, position: Int) {

            holder.dupEventListItemBinding.txtLocal.text = data[position].typename
            holder.dupEventListItemBinding.txtDesc.text = data[position].description

        holder.dupEventListItemBinding.root.setOnClickListener {
            evetclickEventListener.onEventClick(data[position].id)
        }
    }



    override fun getItemCount() = data.size

    interface EventCliclListener{
        fun onEventClick(valueEvent: String)
    }
}