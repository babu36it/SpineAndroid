package com.wiesoftware.spine.ui.home.menus.voice_over

import android.view.View
import androidx.lifecycle.ViewModel

import com.wiesoftware.spine.data.repo.HomeRepositry
import java.io.File

class VoiceOverViewModel(val homeRepositry: HomeRepositry) : ViewModel(){

    var voiceOverListener: VoiceOverListner?=null

    fun onBack(view : View){
         voiceOverListener?.onBack()
    }
    fun addPhotoOrImage(view : View){
         voiceOverListener?.addImageOrVideo()
    }
    fun onImageClose(view : View){
        voiceOverListener?.onImageCLose()
    }
    fun onDeleteVoice(view : View){
          voiceOverListener?.onDeleteVoice()
    }
    fun onPreviewClick(view : View){
        voiceOverListener?. onPreviewClick()
    }
     fun onPaused(view : View){
        voiceOverListener?. onPaused()
    }
    fun onPlay(view : View){
        voiceOverListener?. onPlay()
    }
    fun onStartRecording(view : View){
        voiceOverListener?. onStartRecordings()
    }





}