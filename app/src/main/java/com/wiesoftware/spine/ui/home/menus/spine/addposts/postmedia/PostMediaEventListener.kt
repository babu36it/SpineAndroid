package com.wiesoftware.spine.ui.home.menus.spine.addposts.postmedia

/**
 * Created by Vivek kumar on 11/5/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
interface PostMediaEventListener {

    fun onBack()
    fun onPost()
    fun onAdd()
    fun onPreview()
    fun onDelete()
    fun onCheckedChange(isFeatured: Boolean)
}