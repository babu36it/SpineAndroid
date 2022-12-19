package com.wiesoftware.spine.ui.home.menus.events.filter

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.EventRepository

/**
 * Created by Vivek kumar on 12/16/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class FilterEventViewModel(val eventRepositry: EventRepository): ViewModel() {

    var location: String=""
    var date: String=""
    var datetwo:String=""
    var category: String=""
    var filterEventListener: FilterEventListener?= null

    fun onCloseFilter(view: View){
        filterEventListener?.onclose()
    }
    fun onFindEvent(view: View){
        filterEventListener?.onFindEvent(location,date,datetwo,category)
    }
    fun openDialog(view: View){
        filterEventListener?.onOpenDialog()
    }
}