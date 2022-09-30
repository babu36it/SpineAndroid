package com.wiesoftware.spine.ui.home.menus.spine.addposts.reviewpost

/**
 * Created by Vivek kumar on 12/29/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
interface ReviewPostEventListener {
    fun onBack()
    fun onPost()
    fun onThoughtEdit(thought: String)
    fun onHashtagEdit(hashtag: String)
    fun onCheckedChange(on: Boolean)
    fun onPreview()
    fun onDelete()
}