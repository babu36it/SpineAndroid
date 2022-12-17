package com.wiesoftware.spine.ui.home.menus.spine.comment.storycomment

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 3/5/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class StoryCommentViewmodel(val homeRepositry: HomeRepository): ViewModel() {
    var comment=""

    var storyCommentEventListener: StoryCommentEventListener?=null

    fun getLoggedInUser()=homeRepositry.getUser()

    fun onBack(view: View){
        storyCommentEventListener?.onBack()
    }
    fun onSendComment(view: View){
        if (comment.isEmpty()){
            return
        }
        storyCommentEventListener?.onSendComment(comment)
    }
}