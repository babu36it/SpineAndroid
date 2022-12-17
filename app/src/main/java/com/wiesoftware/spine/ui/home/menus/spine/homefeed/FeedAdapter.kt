package com.wiesoftware.spine.ui.home.menus.spine.homefeed


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.smarteist.autoimageslider.SliderView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.PostImageItem
import com.wiesoftware.spine.data.adapter.SliderPostImageAdapter
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.impulse_content_item.view.*
import kotlinx.android.synthetic.main.impulse_content_item.view.textView32
import kotlinx.android.synthetic.main.post_event_item.view.*
import kotlinx.android.synthetic.main.post_event_item.view.imageViewPostBig

import kotlinx.android.synthetic.main.post_images_item.view.*
import kotlinx.android.synthetic.main.promoted_by_new.view.*
import kotlinx.android.synthetic.main.promoted_by_new.view.userImage


class FeedAdapter(
    private val dataList: List<HomeFeedModel>,
    val feedEventListener: FeedEventListener
):RecyclerView.Adapter<RecyclerView.ViewHolder>(), SliderPostImageAdapter.SliderItemClickListener {
    private val POST_TYPE_PROMOTED = 1
    private val POST_TYPE_SPINE = 2
    private val POST_TYPE_EVENT = 3
    private val POST_TYPE_IMAGE = 4
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return when (viewType) {
            POST_TYPE_PROMOTED -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.promoted_by_new, parent, false)
                return PromotedPostViewHolder(view)
            }
            POST_TYPE_SPINE -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.impulse_content_item, parent, false)
                return SpineViewHolder(view)
            }
            POST_TYPE_EVENT -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.post_event_item, parent, false)
                return EventViewHolder(view)
            }
            POST_TYPE_IMAGE -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.post_images_item, parent, false)
                return PostForYouViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.for_you_content_item, parent, false)
                return PostForYouViewHolder(view)

            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataModel = dataList[position]
        val context = holder.itemView.context
        when (dataModel.type) {
            POST_TYPE_PROMOTED -> {
                val viewHolder = holder as PromotedPostViewHolder
                // viewHolder.textName.text=context.getString(R.string.oliver)
                viewHolder.userImage.setOnClickListener{
                    feedEventListener.onViewSomeonesProfile(dataModel)
                }
            }
            POST_TYPE_SPINE -> {
                val viewHolder = holder as SpineViewHolder
                viewHolder.imageView4.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.profile_circle
                    )
                )
                viewHolder.description.text = context.getString(R.string.spine_dumy_description)
                viewHolder.titleName.text = context.getString(R.string.spine)
                viewHolder.unknown.text = context.getString(R.string.unkonwn)
                viewHolder.comment.text = "20"
                viewHolder.itemView.setOnClickListener {

                    feedEventListener.viewAllSpineImpulse_(dataModel)
                }

            }
            POST_TYPE_EVENT -> {
                val viewHolder = holder as EventViewHolder
                if (dataModel.text.equals("ONLINE")) {
                    viewHolder.textLocVoice.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_mic
                        ), null, null, null
                    )
                    viewHolder.textLocVoice.text = "En | 18:00,2hrs"
                    viewHolder.textView275.text = "Sahaja Yoga Online\nMeditation"
                    viewHolder.textOnline.visibility = View.VISIBLE
                    viewHolder.textPostedFrom.visibility = View.VISIBLE
                    viewHolder.imageViewPostBig.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.flower
                        )
                    )

                } else {
                    viewHolder.textOnline.visibility = View.GONE
                    viewHolder.textPostedFrom.visibility = View.VISIBLE
                }

            }
            POST_TYPE_IMAGE->{
                val viewHolder = holder as PostForYouViewHolder
                viewHolder.cvSlider.visibility=View.VISIBLE
                val listss: ArrayList<PostImageItem> = ArrayList()
                listss.add(PostImageItem("https://demonuts.com/Demonuts/SampleImages/W-03.JPG"))
                listss.add(PostImageItem("https://demonuts.com/Demonuts/SampleImages/W-08.JPG"))
                listss.add(PostImageItem("https://demonuts.com/Demonuts/SampleImages/W-10.JPG"))
                val adapter= SliderPostImageAdapter(context,this,position)
                listss.forEach { postImageItem ->
                    //listss.add(PostImageItem(POST_BASE_IMG_FILE+postImageItem))
                    adapter.addItem(postImageItem.image)
                }
                viewHolder.imageSlider.setSliderAdapter(adapter)

            }
        }


    }

    override fun getItemViewType(position: Int): Int {
        var type: Int = -1
        when (dataList.get(position).type) {
            1 -> {
                type = POST_TYPE_PROMOTED
            }
            2 -> {
                type = POST_TYPE_SPINE
            }
            3 -> {
                type = POST_TYPE_EVENT
            }
            4 -> {
                type = POST_TYPE_IMAGE
            }
        }
        return type
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


    inner class PromotedPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

         var userImage: CircleImageView =itemView.userImage

    }

    inner class PostForYouViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cvSlider : CardView = itemView.cvSlider
        var imageSlider : SliderView = itemView.imageSlider
    }

    inner class SpineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView4: CircleImageView = itemView.imageView4
        var description: TextView = itemView.textView43
        var titleName: TextView = itemView.textView29
        var unknown: TextView = itemView.textView44
        var comment: TextView = itemView.textView32
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView275: TextView = itemView.textView275
        var textLocVoice: TextView = itemView.textLocVoice
        var textOnline: TextView = itemView.textOnline
        var textPostedFrom: TextView = itemView.textPostedFrom
        var imageViewPostBig: ImageView = itemView.imageViewPostBig
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
        fun viewAllSpineImpulse_(postData: HomeFeedModel)
        fun onViewSomeonesProfile(postData: HomeFeedModel)

    }

    override fun onSliderItemClicked(vPosition: Int) {
      // feedEventListener.onViewVidInLarge_("url", "", postList[vPosition])
    }
}

data class PostImageItem(val image: String)