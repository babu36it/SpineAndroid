package com.wiesoftware.spine.ui.home.menus.events.filter

/**
 * Created by Vivek kumar on 12/16/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
interface FilterEventListener {
    fun onclose()
    fun onFindEvent(location: String, date: String,datetwo:String ,category: String)
    fun onOpenDialog()
}