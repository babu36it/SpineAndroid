package com.wiesoftware.spine.ui.home.menus.podcasts.addrss

/**
 * Created by Vivek kumar on 4/6/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
interface AddRssEventListener {
    fun onBack()
    fun validateRss(rssLink: String)
    fun onCloseInfo()
}