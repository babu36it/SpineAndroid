package com.wiesoftware.spine.ui.home.menus.voice_over

interface VoiceOverListner {
    fun onBack()
    fun addImageOrVideo()
    fun onImageCLose()
    fun onPlay()
    fun onPaused()
    fun onDeleteVoice()
    fun onPreviewClick()
    fun onStartRecordings()
}