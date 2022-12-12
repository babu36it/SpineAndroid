package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.EventCommentData
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.EventCommentDataItemBinding
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.Coroutines
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.printDifference
import kotlinx.android.synthetic.main.event_comment_data_item.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Vivek kumar on 1/13/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class EventCommentAdapter(
    val list: List<EventCommentData>,
    val listener: OnEventCommentEveListener,
    val repo: HomeRepository,
    val  userImage: String,
):RecyclerView.Adapter<EventCommentAdapter.CommentHolder>() {
    class CommentHolder(
        val eventCommentDataItemBinding: EventCommentDataItemBinding,
        val context: Context,
        val repo: HomeRepository
    ):RecyclerView.ViewHolder(eventCommentDataItemBinding.root) {
        fun getReplys(comment_id: String): List<EventCommentData>{
            var list: List<EventCommentData> = ArrayList<EventCommentData>()
            Coroutines.main {
              try {
                  val res=repo.getSpineEventReplys(comment_id)
                  if (res.status){
                      list=res.data
                  }
                }catch (e: ApiException){
                    e.printStackTrace()
                }catch (e: NoInternetException){
                    e.printStackTrace()
                }

            }
            return list
        }
    }
    interface  OnEventCommentEveListener{
        fun onReply(eventCommentData: EventCommentData,comment: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CommentHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.event_comment_data_item,
                parent,
                false
            ),parent.context,repo
        )

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        holder.eventCommentDataItemBinding.model=list[position]

        val cc: Date = Calendar.getInstance().getTime()
        val simpleDateFormat = SimpleDateFormat("yyyy-M-dd hh:mm:ss", Locale.getDefault())
        try {
            val s=list[position].created_on
            val date1: Date = simpleDateFormat.parse(s)
            val date: String = simpleDateFormat.format(cc)
            val date2: Date = simpleDateFormat.parse(date)
            if (date1 != null && date2 != null) {
                val d: String = printDifference(date1,date2)!!
                if (d.contains("-")){
                    holder.eventCommentDataItemBinding.textView133.text="Today"
                }else{
                    holder.eventCommentDataItemBinding.textView133.text=d
                }

            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        var userpic=userImage+list[position].profile_pic


        Glide
            .with(holder.context)
            .load(userpic)
            .centerCrop()
            .placeholder(R.drawable.ic_profile)
            .into(holder.itemView.circleImageView8);



        holder.eventCommentDataItemBinding.button50.setOnClickListener {
            val etCmnt=holder.eventCommentDataItemBinding.etCmnt.text.toString()
            /*if (etCmnt.isEmpty()){
                return@setOnClickListener
            }*/
            listener.onReply(list[position],etCmnt)
            holder.eventCommentDataItemBinding.etCmnt.setText("")
            val comment_id=list[position].id
            val lists=holder.getReplys(comment_id)
            lists.let {
                holder.eventCommentDataItemBinding.rvReply.also {
                    it.layoutManager=LinearLayoutManager(holder.context,RecyclerView.VERTICAL,false)
                    it.setHasFixedSize(true)
                    it.adapter=EventReplyAdapter(lists)
                }
            }
        }
        holder.eventCommentDataItemBinding.button51.setOnClickListener {
            listener.onReply(list[position],"comment")
            /*holder.eventCommentDataItemBinding.replyLayout.visibility=View.VISIBLE
            val comment_id=list[position].id
            val lists=holder.getReplys(comment_id)
            lists.let {
                holder.eventCommentDataItemBinding.rvReply.also {
                    it.layoutManager=LinearLayoutManager(holder.context,RecyclerView.VERTICAL,false)
                    it.setHasFixedSize(true)
                    it.adapter=EventReplyAdapter(lists)
                }
            }*/
        }
    }

    override fun getItemCount() = list.size
}