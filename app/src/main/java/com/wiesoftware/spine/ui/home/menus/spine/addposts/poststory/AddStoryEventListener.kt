package com.wiesoftware.spine.ui.home.menus.spine.addposts.poststory

/**
 * Created by Vivek kumar on 10/19/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
interface AddStoryEventListener {
    fun onBack()
    fun onAdd()
    fun onPreview()
    fun onDelete()
    fun onPostStory(thoughts: String, allowComments: Boolean, story_time: Boolean)
}