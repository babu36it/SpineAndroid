package com.wiesoftware.spine.ui.home.menus.spine.viewmedia

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
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
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.databinding.ActivityViewMediaInLargeBinding


class ViewMediaInLargeActivity : AppCompatActivity(), ViewMediaEventListener, Player.EventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    lateinit var binding: ActivityViewMediaInLargeBinding

    private var mScaleGestureDetector: ScaleGestureDetector? = null

    private var playbackPosition: Long = 0
    private val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(this, "spine-player")
    }
    companion object {
        var mScaleFactor = 1.0f
        var mImageView: ImageView? = null

        var simpleExoplayer: SimpleExoPlayer?=null
        val MEDIA_URL="mediaUrl"
        val MEDIA_TYPE="mediaType"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_view_media_in_large)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_view_media_in_large)
        val viewmodel=ViewModelProvider(this).get(ViewMediaViewModel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.viewMediaEventListener=this
        mImageView = binding.imageView31
        val mediaUrl=intent.getStringExtra(MEDIA_URL)
        val mediaType=intent.getStringExtra(MEDIA_TYPE)
        if (mediaType.equals("0")){
            setImage(mediaUrl!!)
        }else{
            binding.exoplayerView.visibility=View.VISIBLE
            setVideo(mediaUrl!!)
        }

        mScaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
    }

    private fun setVideo(mediaUrl: String) {
        initializePlayer(mediaUrl)
    }
    fun initializePlayer(url: String) {
        simpleExoplayer = ExoPlayerFactory.newSimpleInstance(this)
        preparePlayer(url)
        binding.exoplayerView.player = simpleExoplayer
        simpleExoplayer!!.seekTo(playbackPosition)
        simpleExoplayer!!.playWhenReady = true
        simpleExoplayer!!.addListener(this)

    }
    fun preparePlayer(videoUrl: String) {
        val uri = Uri.parse(videoUrl)
        val mediaSource = buildMediaSource(uri)
        simpleExoplayer!!.prepare(mediaSource)
    }

    fun buildMediaSource(uri: Uri): MediaSource {

        return  ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(uri)

    }

    override fun onPlayerError(error: ExoPlaybackException) {
        error.printStackTrace()
    }
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_BUFFERING)
            binding.progressBar.visibility = View.VISIBLE
        else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED)
            binding.progressBar.visibility = View.INVISIBLE
    }


    private fun setImage(mediaUrl: String) {
            Glide.with(mImageView!!).load(mediaUrl).placeholder(R.drawable.demo).into(mImageView!!)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mScaleGestureDetector!!.onTouchEvent(event)
        return true
    }

    private class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            mScaleFactor *= scaleGestureDetector.scaleFactor
            mScaleFactor = Math.max(0.1f,Math.min(mScaleFactor, 10.0f))
            mImageView!!.setScaleX(mScaleFactor)
            mImageView!!.setScaleY(mScaleFactor)
            return true
        }
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        if(simpleExoplayer != null) {
            pausePlayer(simpleExoplayer)
        }
    }

    override fun onStop() {
        super.onStop()
        if(simpleExoplayer != null) {
            pausePlayer(simpleExoplayer)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(simpleExoplayer != null) {
            releaseExoPlayer(simpleExoplayer)
        }


    }
    fun pausePlayer(exoPlayer: SimpleExoPlayer?) {
        if (exoPlayer != null) {
            exoPlayer.playWhenReady = false
        }
    }
    fun releaseExoPlayer(exoPlayer: SimpleExoPlayer?) {
        exoPlayer?.release()
    }

}