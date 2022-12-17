package com.wiesoftware.spine.ui.home.menus.spine.comment.postcomment

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 12/25/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class PostCommentViewmodel(
    val homeRepositry: HomeRepository
): ViewModel() {

    var comment: String?=null
    var postCommentEventListener: PostCommentEventListener?=null

    fun getLoggedInUser()=homeRepositry.getUser()

    fun onBack(view: View){
        postCommentEventListener?.onBack()
    }
    fun onPost(view: View){
        if (!comment.isNullOrEmpty()){
            postCommentEventListener?.onPost(comment!!)
        }
    }
}