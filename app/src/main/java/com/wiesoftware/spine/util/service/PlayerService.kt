package com.wiesoftware.spine.util.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * Created by Vivek kumar on 2/22/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class PlayerService : Service(), MediaPlayer.OnSeekCompleteListener {

    companion object{
        const val LOGCAT="PlayerService"
        const val ACTION_PLAYER="playerAction"
        const val ACTION_PLAY="play"
        const val ACTION_PAUSE="pause"
        const val ACTION_FORWARD="forward"
        const val ACTION_REWIND="rewind"
        const val forwardTime : Int=10000
        const val backwardTime : Int=10000
        const val BROADCAST_SEEKBAR_CHANGED="BROADCAST_SEEKBAR_CHANGED"
        const val SEEKBAR_PROGRESS="SEEKBAR_PROGRESS"
        var mediaPlayer: MediaPlayer? = null
    }

    var endTime: Int=0
    var playTime: Int=0


    override fun onCreate() {
        super.onCreate()
        Log.e(LOGCAT,"Service started.")
        if (mediaPlayer == null){
            initPlayer()
        }else if (mediaPlayer!!.isPlaying){
            mediaPlayer!!.pause()
        }
    }

    @SuppressLint("WrongConstant")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //super.onStartCommand(intent, flags, startId)
        if (intent != null){
            try {

            val action=intent.getStringExtra(ACTION_PLAYER)
            if (action!!.equals(ACTION_PLAY)){
                mediaPlayer!!.start()
            }else if (action.equals(ACTION_PAUSE)){
                mediaPlayer!!.pause()
            }else if (action.equals(ACTION_FORWARD)){
                if ((playTime + forwardTime) <= endTime) {
                    playTime += forwardTime
                    mediaPlayer!!.seekTo(playTime)
                }
            }else if (action.equals(ACTION_REWIND)){
                if ((playTime - backwardTime) > 0) {
                    playTime -= backwardTime
                    mediaPlayer!!.seekTo(playTime)
                }
            }

            }catch (e: Exception){
                e.printStackTrace()
            }
        }

        return START_STICKY
    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
        }catch (e: Exception){
         e.printStackTrace()
        }
    }


    fun initPlayer(){
        val audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        mediaPlayer!!.setOnSeekCompleteListener(this)
        try {
            mediaPlayer!!.setDataSource(audioUrl)
            mediaPlayer!!.prepare()
            endTime = mediaPlayer!!.duration
            mediaPlayer!!.start()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onSeekComplete(mp: MediaPlayer?) {
        mp!!.stop()
        mp.release()
    }


}