package com.wiesoftware.spine.ui.home.menus.spine.featuredpost

/**
 * Created by Vivek kumar on 4/27/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
interface FeaturedPostEventListener {
    fun onBack()
    fun selectAdDuration()
    fun startDate()
    fun selectTime()
    fun selectAdType()
    fun previewAd(
        picVidWebLink: String,
        picVidAdditionalLine: String,
        eventTitle: String,
        eventTimeZone: String,
        eventLocation: String,
        eventWebLink: String,
        eventAdditionalLine: String,
        podWebLink: String,
        podAdditionalLine: String
    )
    fun onPicVidSelect()
    fun onEventImageSelect()
    fun onEventType()
    fun onEventStartDate()
    fun onEventEndDate()
    fun onEventStartTime()
    fun onEventEndTime()
    fun onPodImageSelect()
}