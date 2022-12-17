package com.wiesoftware.spine.ui.home.menus.spine.addposts.hashtags

/**
 * Created by Vivek kumar on 12/28/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
interface HashtagEventListener {
    fun onBack()
    fun onNext(hashtags: String)
    fun changeEditTextColor(s: CharSequence)
}