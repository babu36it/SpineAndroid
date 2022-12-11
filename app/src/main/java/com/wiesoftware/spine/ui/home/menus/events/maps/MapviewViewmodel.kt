package com.wiesoftware.spine.ui.home.menus.events.maps

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.EventRepositry
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 1/22/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class MapviewViewmodel(val eventRepositry: EventRepositry): ViewModel() {

    fun getLoggedInUser()=eventRepositry.getUser()

    var mapviewEventListener: MapviewEventListener?=null

    fun onBack(view: View){
        mapviewEventListener?.onBack()
    }
    fun onFilter(view: View){
        mapviewEventListener?.onFilter()
    }
}