package com.wiesoftware.spine.ui.home.menus.spine.featuredpost

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 4/27/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class FeaturedPostViewmodel():ViewModel() {
    var featuredPostEventListener: FeaturedPostEventListener? = null
    var picVidWebLink = ""; var picVidAdditionalLine = ""
    var eventTitle = ""; var eventTimeZone = ""; var eventLocation = "" ;var eventWebLink = ""; var eventAdditionalLine = ""
    var podWebLink = ""; var podAdditionalLine = ""

    fun onBack(view: View){
        featuredPostEventListener?.onBack()
    }
    fun selectAdDuration(view: View){
        featuredPostEventListener?.selectAdDuration()
    }
    fun startDate(view: View){
        featuredPostEventListener?.startDate()
    }
    fun selectTime(view: View){
        featuredPostEventListener?.selectTime()
    }
    fun selectAdType(view: View){
        featuredPostEventListener?.selectAdType()
    }
    fun previewAd(view: View){
        featuredPostEventListener?.previewAd(
            picVidWebLink,picVidAdditionalLine,
            eventTitle,eventTimeZone,eventLocation,eventWebLink,eventAdditionalLine,
            podWebLink,podAdditionalLine
        )
    }
    fun onPicVidSelect(view: View){
        featuredPostEventListener?.onPicVidSelect()
    }
    fun onEventImageSelect(view: View){
        featuredPostEventListener?.onEventImageSelect()
    }

    fun onEventType(view: View){
        featuredPostEventListener?.onEventType()
    }
    fun onEventStartDate(view: View){
        featuredPostEventListener?.onEventStartDate()
    }
    fun onEventEndDate(view: View){
        featuredPostEventListener?.onEventEndDate()
    }
    fun onEventStartTime(view: View){
        featuredPostEventListener?.onEventStartTime()
    }
    fun onEventEndTime(view: View){
        featuredPostEventListener?.onEventEndTime()
    }
    fun onPodImageSelect(view: View){
        featuredPostEventListener?.onPodImageSelect()
    }
}