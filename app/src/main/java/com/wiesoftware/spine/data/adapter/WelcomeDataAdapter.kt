package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.net.Uri
import android.opengl.Visibility
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.Api
import com.wiesoftware.spine.data.net.reponses.WelcomeData
import com.wiesoftware.spine.databinding.WelcomeItemBinding
import com.wiesoftware.spine.ui.auth.WelcomeEventListener

/**
 * Created by Vivek kumar on 8/13/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class WelcomeDataAdapter(
    private val welcomeData: List<WelcomeData>,private val welcomeEventListener: WelcomeEventListener
): RecyclerView.Adapter<WelcomeDataAdapter.DataHolder>() {

    interface WelcomeEventListener {
        fun onViewWelcome()
    }

    inner class DataHolder(
        val welcomeItemBinding: WelcomeItemBinding,val context: Context
    ): RecyclerView.ViewHolder(welcomeItemBinding.root), Player.EventListener {


        private lateinit var simpleExoplayer: SimpleExoPlayer
        private var playbackPosition: Long = 0
        private val dataSourceFactory: DataSource.Factory by lazy {
            DefaultDataSourceFactory(context, "spine-player")
        }

        fun initializePlayer(url: String) {
            simpleExoplayer = ExoPlayerFactory.newSimpleInstance(context)
            preparePlayer(url)
            welcomeItemBinding.exoplayerView.player = simpleExoplayer
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
            return  ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
        }
        override fun onPlayerError(error: ExoPlaybackException) {
            error.printStackTrace()
        }


    }

    override fun onViewAttachedToWindow(holder: DataHolder) {
        super.onViewAttachedToWindow(holder)

    }

    override fun onViewDetachedFromWindow(holder: DataHolder) {
        super.onViewDetachedFromWindow(holder)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.welcome_item,
                parent,
                false
            ),parent.context
        )

    override fun getItemCount() = welcomeData.size

    override fun onBindViewHolder(holder: DataHolder, position: Int) {
        holder.welcomeItemBinding.viewmodel= welcomeData[position]
        val welcomeDataItem: WelcomeData=welcomeData[position]
        holder.welcomeItemBinding.exoplayerView.setOnClickListener {
            welcomeEventListener.onViewWelcome()
        }
        if(welcomeDataItem.type.equals("2")){
            holder.initializePlayer(Api.BASE_URL_VIDEO+welcomeDataItem.image)
            }
            //holder.welcomeItemBinding.videoView.start()


    }

}