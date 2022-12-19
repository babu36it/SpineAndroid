package com.wiesoftware.spine.ui.home.menus.spine.story.viewstories.storyscreen

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 2/26/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class StoryDisplayViewmodel():ViewModel() {

    var storyDisplayEventListener: StoryDisplayEventListener? =null
    fun onBackPressed(view: View){
        storyDisplayEventListener?.onBack()
    }
    fun onLikeStory(view: View){
        storyDisplayEventListener?.onLikeStory()
    }
    fun onCommentStory(view: View){
        storyDisplayEventListener?.onCommentStory()
    }
    fun onShareStory(view: View){
        storyDisplayEventListener?.onShareStory()
    }
    fun onSaveStory(view: View){
        storyDisplayEventListener?.onSaveStory()
    }
    fun onThreeDotStory(view: View){
        storyDisplayEventListener?.onThreeDotStory()
    }
    fun onSelectTime(view: View){
        storyDisplayEventListener?.onSelectTime()
    }
    fun onStoryProfile(view: View){
        storyDisplayEventListener?.onStoryProfile()
    }
}
interface StoryDisplayEventListener{
    fun onBack()
    fun onLikeStory()
    fun onCommentStory()
    fun onShareStory()
    fun onSaveStory()
    fun onThreeDotStory()
    fun onSelectTime()
    fun onStoryProfile()
}