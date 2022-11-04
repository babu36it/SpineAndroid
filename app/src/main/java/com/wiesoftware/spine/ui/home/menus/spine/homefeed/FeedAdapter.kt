package com.wiesoftware.spine.ui.home.menus.spine.homefeed


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.wiesoftware.spine.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.impulse_content_item.view.*


class FeedAdapter(
    private val dataList: List<HomeFeedModel>,
    val feedEventListener: FeedEventListener
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val POST_TYPE_PROMOTED = 1
    private val POST_TYPE_SPINE = 2
    private val POST_TYPE_EVENT = 3
    private val POST_TYPE_POST = 4
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return when (viewType) {
            POST_TYPE_PROMOTED -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.for_you_content_item, parent, false)
                return PromotedPostViewHolder(view)
            }
            POST_TYPE_SPINE -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.impulse_content_item, parent, false)
                return SpineViewHolder(view)
            }
            POST_TYPE_EVENT -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.for_you_content_item, parent, false)
                return EventViewHolder(view)
            }
            POST_TYPE_POST -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.for_you_content_item, parent, false)
                return PostForYouViewHolder(view)
            }
            else -> {

              view= LayoutInflater.from(parent.context)
                    .inflate(R.layout.for_you_content_item, parent, false)
                return PostForYouViewHolder(view)

            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataModel = dataList[position]
        val context = holder.itemView.context
         when(dataModel.type){
             POST_TYPE_PROMOTED->{
                 val viewHolder = holder as PromotedPostViewHolder
                 viewHolder.textName.text=context.getString(R.string.oliver)
             }
             POST_TYPE_SPINE->{
                 val viewHolder = holder as SpineViewHolder
                 Glide.with(context).load(R.drawable.ic_spine_home)
                     .error(R.drawable.ic_photo) .transform(RoundedCorners(80)).into(viewHolder.imageView4)

                 viewHolder.description.text=context.getString(R.string.spine_dumy_description)
                 viewHolder.titleName.text=context.getString(R.string.spine)
                 viewHolder.unknown.text=context.getString(R.string.unkonwn)
             }
         }


    }

    override fun getItemViewType(position: Int): Int {
        var type :Int=-1
        when (dataList.get(position).type) {
             1->{
                 type =POST_TYPE_PROMOTED
             }
           2->{
               type =POST_TYPE_SPINE
             }
           3->{
               type =POST_TYPE_EVENT
             }
            4->{
                type= POST_TYPE_POST
             }
        }
        return type
    }
    override fun getItemCount(): Int {
        return dataList.size
    }


   inner class PromotedPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

       var textName: TextView =itemView.textView29

    }
    inner class PostForYouViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
    inner class SpineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView4: CircleImageView =itemView.imageView4
        var description: TextView =itemView.textView43
        var titleName: TextView =itemView.textView29
        var unknown: TextView =itemView.textView44
    }
    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    interface FeedEventListener {
        /*fun onPostLike(postData: PostData, isLikedNow: Boolean)
        fun onPostComment(postData: PostData, comment: String)
        fun onPostShare(postData: PostData)
        fun onPostInAppShare(postData: PostData)
        fun onPostSaved(postData: PostData)
        fun onPostCommentDoubleClick(postData: PostData)
        fun onSomeonesProfileView(postData: PostData)
        fun onViewImgInLarge(url: String, type: String)
        fun onViewVidInLarge(url: String, type: String, postData: PostData)
        fun onAdLinkClicked(url: String)*/
        fun onPromotedClicked(postData: HomeFeedModel)
    }

}