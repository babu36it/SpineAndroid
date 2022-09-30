package com.wiesoftware.spine.ui.home.menus.spine.welcome

import android.content.Context
import android.net.Uri
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
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.Api
import com.wiesoftware.spine.data.net.reponses.WelcomeData
import com.wiesoftware.spine.databinding.ViewWelcomeItemBinding

/**
 * Created by Vivek kumar on 10/20/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
public class ViewWelcomeAdapter(
    private val list: List<WelcomeData>,val context: Context
): RecyclerView.Adapter<ViewWelcomeAdapter.VidHolder>() {
    public class VidHolder(
        val viewWelcomeItemBinding: ViewWelcomeItemBinding,val context: Context
    ): RecyclerView.ViewHolder(viewWelcomeItemBinding.root), Player.EventListener {

        companion object{
            public lateinit var simpleExoplayer: SimpleExoPlayer
        }
        private var playbackPosition: Long = 0
        private val dataSourceFactory: DataSource.Factory by lazy {
            DefaultDataSourceFactory(context, "spine-player")
        }

        fun setVideo(url: String){
            /*viewWelcomeItemBinding.progressBar2.visibility=View.VISIBLE
            viewWelcomeItemBinding.videoView2.setVideoPath(url)
            viewWelcomeItemBinding.videoView2.setOnPreparedListener { mp->
                viewWelcomeItemBinding.progressBar2.visibility=View.INVISIBLE
                mp.start()
                val vidRatio = mp.videoWidth / mp.videoHeight.toFloat()
                val screenRatio: Float = viewWelcomeItemBinding.videoView2.getWidth() / viewWelcomeItemBinding.videoView2.getHeight()
                    .toFloat()
                val scale = vidRatio / screenRatio
                if (scale >= 1f) {
                    viewWelcomeItemBinding.videoView2.setScaleX(scale)
                } else {
                    viewWelcomeItemBinding.videoView2.setScaleY(1f / scale)
                }
            }
            viewWelcomeItemBinding.videoView2.setOnCompletionListener {mp->
                mp.start()
            }*/


        }

        fun initializePlayer(url: String) {
            simpleExoplayer = ExoPlayerFactory.newSimpleInstance(context)
            preparePlayer(url)
            viewWelcomeItemBinding.exoplayerView.player = simpleExoplayer
            simpleExoplayer.seekTo(playbackPosition)
            simpleExoplayer.playWhenReady = true
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
            return  ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(uri)
            //}
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            error.printStackTrace()
        }
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (playbackState == Player.STATE_BUFFERING)
                viewWelcomeItemBinding.progressBar.visibility = View.VISIBLE
            else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED)
                viewWelcomeItemBinding.progressBar.visibility = View.INVISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VidHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.view_welcome_item,
                parent,
                false
            ),parent.context
        )

    override fun onBindViewHolder(holder: VidHolder, position: Int) =
        holder.initializePlayer(Api.BASE_URL_VIDEO+list[position].image)
        //holder.setVideo(Api.BASE_URL_VIDEO+list[position].image)


    override fun getItemCount() = list.size
}