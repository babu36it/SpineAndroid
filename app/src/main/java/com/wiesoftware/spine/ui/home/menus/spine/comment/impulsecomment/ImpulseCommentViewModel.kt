package com.wiesoftware.spine.ui.home.menus.spine.comment.impulsecomment

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 10/7/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class ImpulseCommentViewModel(
    val homeRepositry: HomeRepository
): ViewModel() {

    var comment:String?=null
    var impulseCommentEventListener: ImpulseCommentEventListener?=null

    fun getLoggedInUser()= homeRepositry.getUser()

    fun onBack(view: View){
    impulseCommentEventListener?.onBack()
    }

    fun onPost(view: View){
        if (comment.isNullOrEmpty()){
           return
        }
        impulseCommentEventListener?.onPost(comment!!)
    }

}