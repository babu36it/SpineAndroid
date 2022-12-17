package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.PostData
import com.wiesoftware.spine.databinding.ForYouContentItemBinding
import com.wiesoftware.spine.ui.home.menus.spine.foryou.POST_BASE_IMG_FILE
import com.wiesoftware.spine.util.printDifference
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Vivek kumar on 9/1/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class ForYouContentAdapter(
    val postList: List<PostData>,
    val forYouContentEventListener: ForYouContentEventListener
) : RecyclerView.Adapter<ForYouContentAdapter.ContentHolder>(),
    SliderPostImageAdapter.SliderItemClickListener {
    var isLikedNow = false
    var totalLike = 0
    var likeCliked = false
    val totalLikesCountList: MutableList<Int> = mutableListOf()
    val likeStatusList: MutableList<Boolean> = mutableListOf()

    class ContentHolder(
        val forYouContentItemBinding: ForYouContentItemBinding, val context: Context
    ) : RecyclerView.ViewHolder(forYouContentItemBinding.root), Player.EventListener {

        companion object {
            public lateinit var simpleExoplayer: SimpleExoPlayer
        }

        private var playbackPosition: Long = 0
        private val dataSourceFactory: DataSource.Factory by lazy {
            DefaultDataSourceFactory(context, "spine-player")
        }

        fun initializePlayer(url: String) {
            simpleExoplayer = ExoPlayerFactory.newSimpleInstance(context)
            preparePlayer(url)
            forYouContentItemBinding.exoplayerView.player = simpleExoplayer
            simpleExoplayer.seekTo(playbackPosition)
            simpleExoplayer.playWhenReady = false
            simpleExoplayer.addListener(this)
        }

        fun preparePlayer(videoUrl: String) {
            val uri = Uri.parse(videoUrl)
            val mediaSource = buildMediaSource(uri)
            simpleExoplayer.prepare(mediaSource)
        }

        fun buildMediaSource(uri: Uri): MediaSource {
            /*if (type == "dash") {
               DashMediaSource.Factory(dataSourceFactory)
                   .createMediaSource(uri)
           } else {*/
            return ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
            //}
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            error.printStackTrace()
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (playbackState == Player.STATE_BUFFERING)
                forYouContentItemBinding.progressBar.visibility = View.VISIBLE
            else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED)
                forYouContentItemBinding.progressBar.visibility = View.INVISIBLE
        }


        fun setTextViewDrawableColor(textView: TextView, color: Int) {
            for (drawable in textView.compoundDrawablesRelative) {
                if (drawable != null) {
                    drawable.colorFilter =
                        PorterDuffColorFilter(
                            ContextCompat.getColor(context, color),
                            PorterDuff.Mode.SRC_IN
                        )
                }
            }
        }


        fun onShowPopupMenu(
            postData: PostData,
            forYouContentEventListener: ForYouContentEventListener
        ) {
            val wrapper: Context = ContextThemeWrapper(context, R.style.PopupStyle)
            val popupMenu: PopupMenu = PopupMenu(wrapper, forYouContentItemBinding.imageButton5)
            popupMenu.menuInflater.inflate(R.menu.story_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_share ->
                        forYouContentEventListener.onPostInAppShare(postData)
                }
                true
            })
            popupMenu.show()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ContentHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.for_you_content_item,
                parent,
                false
            ),
            parent.context
        )

    override fun onBindViewHolder(holder: ContentHolder, position: Int) {
        holder.forYouContentItemBinding.model = postList[position]

        val url = POST_BASE_IMG_FILE + postList[position].files
        Glide.with(holder.forYouContentItemBinding.imageView5).load(url).error(R.drawable.ic_photo)
            .into(holder.forYouContentItemBinding.imageView5)
        val event_post = postList[position].eventPost ?: ""
        var postedTime = ""
        val cc: Date = Calendar.getInstance().getTime()
        val simpleDateFormat = SimpleDateFormat("yyyy-M-dd hh:mm:ss", Locale.getDefault())
        try {
            val s = postList[position].created_on
            val date1: Date = simpleDateFormat.parse(s)
            val date: String = simpleDateFormat.format(cc)
            val date2: Date = simpleDateFormat.parse(date)
            if (date1 != null && date2 != null) {
                val d: String = printDifference(date1, date2)!!
                if (d.contains("-")) {
                    holder.forYouContentItemBinding.textView30.text = "Today"
                    postedTime = "Today"
                } else {
                    holder.forYouContentItemBinding.textView30.text = d
                    postedTime = d
                }

            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        if (event_post.equals("1")) {
            holder.forYouContentItemBinding.textView275.visibility = View.VISIBLE
            holder.forYouContentItemBinding.textView76.visibility = View.VISIBLE
            holder.forYouContentItemBinding.textView276.visibility = View.VISIBLE
            val title = postList[position].title
            val tt = title.split(",")
            holder.forYouContentItemBinding.textView275.text =
                tt.get(0)//Start on: 2021-03-31 - 11:43:37
            val startDate = tt.get(1).split(":").get(1).split("-")
            val stDate = "${startDate[0]}-${startDate[1]}-${startDate[2]}"

            val simpleDateFormat = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())
            try {
                val date1: Date = simpleDateFormat.parse(stDate)
                val dd = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
                val ss: String = dd.format(date1)
                Log.e("fmtDate: ", ss)
                val newDay = (ss.split(",")[1]).split(" ")[1]
                val newMonth = (ss.split(",")[1]).split(" ")[2]
                holder.forYouContentItemBinding.textView76.text = newDay + " " + newMonth
            } catch (e: ParseException) {
                e.printStackTrace()
                Log.e("fmtDate: ", e.message.toString())
            }
            val endDate = tt.get(2).split(":").get(1).split("-")
            val etDate = "${endDate[0]}-${endDate[1]}-${endDate[2]}"
            try {
                val date1: Date = simpleDateFormat.parse(stDate)
                val date2: Date = simpleDateFormat.parse(etDate)
                if (date1 != null && date2 != null) {
                    val d: String = printDifference(date1, date2)!!
                    var diff = ""
                    var eveLoc = postList[position].eventLocation
                    if (!eveLoc.isNullOrEmpty()) {
                        if (eveLoc.length > 30) {
                            eveLoc = eveLoc.take(30)
                        }
                    }
                    if (d.contains("-")) {
                        diff = "${eveLoc} | ${d.replace("-", "")}"
                    } else {
                        diff = "${eveLoc} | $d"
                    }
                    holder.forYouContentItemBinding.textView276.text = diff

                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }

        } else {
            holder.forYouContentItemBinding.textView275.visibility = View.INVISIBLE
            holder.forYouContentItemBinding.textView76.visibility = View.INVISIBLE
            holder.forYouContentItemBinding.textView276.visibility = View.INVISIBLE
        }

        val fetureAdminapprove = postList[position].featureAdminApprove ?: ""
        if (fetureAdminapprove.equals("1")) {
            holder.forYouContentItemBinding.tvFeatured.visibility = View.VISIBLE
        } else {
            holder.forYouContentItemBinding.tvFeatured.visibility = View.GONE
        }

        val colorId = postList[position].post_backround_color_id ?: ""
        var file = postList[position].files ?: ""
        val multiplity = postList[position].multiplity ?: ""
        if (multiplity.equals("1")) {
            val fList = postList[position].files.split(",")
            if (fList.size > 0) {
                /* holder.forYouContentItemBinding.recyclerViewMultiImg.visibility = View.VISIBLE
                 val listss: ArrayList<PostImageItem> = ArrayList()
                 fList.forEach { postImageItem ->
                     listss.add(PostImageItem(POST_BASE_IMG_FILE+postImageItem))
                 }
                 val postImgadapter=PostImageAdapter(listss)
                 holder.forYouContentItemBinding.recyclerViewMultiImg.also {
                     it.layoutManager = StaggeredGridLayoutManager(2,RecyclerView.VERTICAL)
                     it.adapter=postImgadapter
                     it.stopScroll()
                 }
 */
                holder.forYouContentItemBinding.cvSlider.visibility = View.VISIBLE
                val listss: ArrayList<PostImageItem> = ArrayList()
                val adapter = SliderPostImageAdapter(holder.context, this, position)
                holder.forYouContentItemBinding.imageSlider.setSliderAdapter(adapter)
                fList.forEach { postImageItem ->
                    //listss.add(PostImageItem(POST_BASE_IMG_FILE+postImageItem))
                    adapter.addItem(POST_BASE_IMG_FILE + postImageItem)
                }
            } else {
                holder.forYouContentItemBinding.cvSlider.visibility = View.INVISIBLE
                holder.forYouContentItemBinding.recyclerViewMultiImg.visibility = View.INVISIBLE
            }
            file = fList[0]
        }
        holder.forYouContentItemBinding.exoplayerView.setOnClickListener {
            val url = POST_BASE_IMG_FILE + file
            forYouContentEventListener.onViewVidInLarge(url, "1", postList[position])
        }
        holder.forYouContentItemBinding.imageView5.setOnClickListener {
            val url = POST_BASE_IMG_FILE + file
            //forYouContentEventListener.onViewImgInLarge(url,"0")
            forYouContentEventListener.onViewVidInLarge(url, "1", postList[position])
        }
        holder.forYouContentItemBinding.imageSlider.setOnClickListener {
            forYouContentEventListener.onViewVidInLarge("url", "", postList[position])
        }
        holder.forYouContentItemBinding.tvPostColor.setOnClickListener {
            forYouContentEventListener.onViewVidInLarge("url", "", postList[position])
        }
        if (postList[position].type.equals("0")) {
            holder.forYouContentItemBinding.imageView5.visibility = View.INVISIBLE
        } else {
            holder.forYouContentItemBinding.imageView5.visibility = View.VISIBLE
        }
        if (!file.isNullOrEmpty()) {
            if (file.endsWith("mov", true) || file.endsWith("mp4", true) ||
                file.endsWith("3gp", true) || file.endsWith("mkv", true) || file.endsWith(
                    "avi",
                    true
                )
            ) {
                holder.forYouContentItemBinding.exoplayerView.visibility = View.VISIBLE
                holder.forYouContentItemBinding.exoplayerView.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.context,
                        R.color.text_black
                    )
                )
                val url = POST_BASE_IMG_FILE + file
                Log.e("vid::", url)
                holder.initializePlayer(url)
            } else {
                val url = POST_BASE_IMG_FILE + file
                Log.e("vidImgvsking::", url)
                holder.forYouContentItemBinding.imageView5.visibility = View.VISIBLE
                holder.forYouContentItemBinding.exoplayerView.visibility = View.INVISIBLE
                Glide.with(holder.forYouContentItemBinding.imageView5).load(url)
                    .error(R.drawable.ic_photo).into(holder.forYouContentItemBinding.imageView5)
            }

        } else {
            holder.forYouContentItemBinding.exoplayerView.visibility = View.INVISIBLE
        }
        if (colorId.contains("#")) {
            try {
                holder.forYouContentItemBinding.tvPostColor.setBackgroundColor(
                    Color.parseColor(
                        colorId
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val user_like_status = postList[position].user_like_status ?: ""
        if (user_like_status.equals("1")) {
            holder.setTextViewDrawableColor(
                holder.forYouContentItemBinding.textView31,
                R.color.color_red
            )
            likeStatusList.add(true)
            isLikedNow = true
        } else {
            holder.setTextViewDrawableColor(
                holder.forYouContentItemBinding.textView31,
                R.color.gray_tint
            )
            isLikedNow = false
            likeStatusList.add(false)
        }
        val user_save_status = postList[position].user_save_status ?: ""
        if (user_save_status.equals("1")) {
            holder.forYouContentItemBinding.imageButton4.setImageResource(R.drawable.ic_saved)
        }

        try {
            totalLike = postList[position].total_like.toInt()
            totalLikesCountList.add(totalLike)
        } catch (e: Exception) {
            totalLike = 0
            totalLikesCountList.add(totalLike)
            e.printStackTrace()
        }
        holder.forYouContentItemBinding.textView31.setOnClickListener {

            totalLike = totalLikesCountList[position]
            isLikedNow = likeStatusList[position]

            if (isLikedNow) {
                holder.setTextViewDrawableColor(
                    holder.forYouContentItemBinding.textView31,
                    R.color.gray_tint
                )
                if (totalLike != 0) {
                    totalLike = totalLike - 1
                    totalLikesCountList[position] = totalLike
                }
                isLikedNow = false
                likeStatusList[position] = isLikedNow
            } else {
                holder.setTextViewDrawableColor(
                    holder.forYouContentItemBinding.textView31,
                    R.color.color_red
                )
                totalLike = totalLike + 1
                totalLikesCountList[position] = totalLike
                isLikedNow = true
                likeStatusList[position] = isLikedNow
            }
            holder.forYouContentItemBinding.textView31.text = "" + totalLike
            forYouContentEventListener.onPostLike(postList[position], isLikedNow)
        }
        /*holder.forYouContentItemBinding.textView32.setOnClickListener {
            holder.forYouContentItemBinding.postComment.visibility= View.VISIBLE
        }*/


        holder.forYouContentItemBinding.textView32.setOnClickListener(object :
            DoubleClickListener() {
            override fun onSingleClick(v: View?) {
                forYouContentEventListener.onPostCommentDoubleClick(postList[position])
                holder.forYouContentItemBinding.postComment.visibility = View.VISIBLE
            }

            override fun onDoubleClick(v: View?) {
                forYouContentEventListener.onPostCommentDoubleClick(postList[position])
            }
        })
        holder.forYouContentItemBinding.sendComment.setOnClickListener {
            val comment = holder.forYouContentItemBinding.typeComment.text
            if (!comment.isNullOrEmpty()) {
                forYouContentEventListener.onPostComment(postList[position], comment.toString())
                holder.forYouContentItemBinding.typeComment.text.clear()
            }
        }
        holder.forYouContentItemBinding.imageButton4.setOnClickListener {
            forYouContentEventListener.onPostSaved(postList[position])
        }
        holder.forYouContentItemBinding.imageButton3.setOnClickListener {
            forYouContentEventListener.onPostShare(postList[position])
        }
        holder.forYouContentItemBinding.imageView4.setOnClickListener {
            forYouContentEventListener.onSomeonesProfileView(postList[position])
        }
        holder.forYouContentItemBinding.textView29.setOnClickListener {
            //forYouContentEventListener.onViewVidInLarge("url","",postList[position])
            forYouContentEventListener.onSomeonesProfileView(postList[position])
        }
        holder.forYouContentItemBinding.imageButton5.setOnClickListener {
            //holder.onShowPopupMenu(postList[position],forYouContentEventListener)
            forYouContentEventListener.onPostInAppShare(postList[position])
        }
        if (postList[position].type.equals("2")) {
            holder.forYouContentItemBinding.textView320.visibility = View.VISIBLE
            holder.forYouContentItemBinding.imageButton85.visibility = View.VISIBLE
            val uri = postList[position].file
            Glide.with(holder.forYouContentItemBinding.imageView5).load(uri)
                .error(R.drawable.ic_photo).into(holder.forYouContentItemBinding.imageView5)
        } else {
            holder.forYouContentItemBinding.textView320.visibility = View.INVISIBLE
            holder.forYouContentItemBinding.imageButton85.visibility = View.INVISIBLE
        }
        holder.forYouContentItemBinding.imageButton85.setOnClickListener {
            forYouContentEventListener.onAdLinkClicked(postList[position].website)
        }
    }

    override fun getItemCount() = postList.size


    interface ForYouContentEventListener {
        fun onPostLike(postData: PostData, isLikedNow: Boolean)
        fun onPostComment(postData: PostData, comment: String)
        fun onPostShare(postData: PostData)
        fun onPostInAppShare(postData: PostData)
        fun onPostSaved(postData: PostData)
        fun onPostCommentDoubleClick(postData: PostData)
        fun onSomeonesProfileView(postData: PostData)
        fun onViewImgInLarge(url: String, type: String)
        fun onViewVidInLarge(url: String, type: String, postData: PostData)
        fun onAdLinkClicked(url: String)
    }

    abstract class DoubleClickListener : View.OnClickListener {
        var lastClickTime: Long = 0
        override fun onClick(v: View?) {
            onSingleClick(v)
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                onDoubleClick(v)
            }
            lastClickTime = clickTime
        }

        abstract fun onSingleClick(v: View?)
        abstract fun onDoubleClick(v: View?)

        companion object {
            private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds
        }
    }

    override fun onSliderItemClicked(vPosition: Int) {
        forYouContentEventListener.onViewVidInLarge("url", "", postList[vPosition])
    }
}

data class PostImageItem(val image: String)