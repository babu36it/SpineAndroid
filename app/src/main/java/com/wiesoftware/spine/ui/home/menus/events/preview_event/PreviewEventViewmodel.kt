package com.wiesoftware.spine.ui.home.menus.events.preview_event

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.EventRepositry
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 1/15/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class PreviewEventViewmodel(val eventRepositry: EventRepositry): ViewModel() {

    var previewEventsEventListener: PreviewEventsEventListener?= null

    fun getUser()= eventRepositry.getUser()
    fun onBack(view: View){
        previewEventsEventListener?.onBack()
    }
    fun  onPost(view: View){
        previewEventsEventListener?.onPost()
    }
}