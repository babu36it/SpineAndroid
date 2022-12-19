package com.wiesoftware.spine.ui.home.menus.events.addevents

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.EventRepository

/**
 * Created by Vivek kumar on 11/13/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class AddEventViewModel(val eventRepositry: EventRepository):ViewModel() {

    var addEventsListener: AddEventsListener?=null
    var title: String=""
    var about: String=""
    var timeZone: String=""
    var location: String=""
    var link: String=""
    var fee:String="0"
    var attendees: String=""
    var addNewCat: String? = null
    var bookeventurl:String=""
    var checkcomment:Boolean=true

    fun getLoggedInUser()=eventRepositry.getUser()

    fun onCheckedChange(on:Boolean){
        addEventsListener?.onAllowComments(on)
    }
    fun onAllowParticipantsCheckedChange(on:Boolean){
        addEventsListener?.onAllowParticipants(on)
    }

    fun onPaidAmountCheckedChange(on:Boolean){
        addEventsListener?.onAllowPaid(on)
    }

    fun selectEventType(view: View){
        addEventsListener?.selectEventType()
    }

    fun onBack(view: View){
        addEventsListener?.onBack()
    }
    fun onPost(view: View){
        addEventsListener?.onPost(title,timeZone,location,link,fee,attendees,bookeventurl)
    }
    fun onAddPic(view: View){
        addEventsListener?.onAdPic()
    }
    fun onDelete(view: View){
        addEventsListener?.onDelete()
    }
    fun onPreview(view: View){
        addEventsListener?.onPreview(title,about,location,link,fee,attendees,bookeventurl)
    }
    fun onStartDate(view: View){
        addEventsListener?.onStartDate()
    }
    fun onEndDate(view: View){
        addEventsListener?.onEndDate()
    }
    fun onStartTime(view: View){
        addEventsListener?.onStartTime()
    }
    fun onEndTime(view: View){
        addEventsListener?.onEndTime()
    }
    fun onCurency(view: View){
        addEventsListener?.onCurency()
    }

    fun onAddAditionalCategory(view: View){
        addEventsListener?.onAddAditionalCategory()
    }
    fun onNewCategoryAdd(view: View){
        if (addNewCat.isNullOrEmpty()){
            return
        }
        addEventsListener?.onAddNewCategory(addNewCat!!)
    }


    fun goToHome(view: View){
        addEventsListener?.goToHome()
    }

    fun addNewEvent(view: View){
        addEventsListener?.addNewEvent()
    }



}