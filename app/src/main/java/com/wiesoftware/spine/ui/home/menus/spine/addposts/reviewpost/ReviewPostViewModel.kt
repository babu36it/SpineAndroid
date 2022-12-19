package com.wiesoftware.spine.ui.home.menus.spine.addposts.reviewpost

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 12/29/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class ReviewPostViewModel(val homeRepositry: HomeRepository): ViewModel() {
    var thoughts: String=""
    var hashtags: String=""

    var reviewPostEventListener: ReviewPostEventListener?=null

    fun getLoggedInUser()=homeRepositry.getUser()

    fun onBack(view: View){
        reviewPostEventListener?.onBack()
    }
    fun onPost(view: View){
        reviewPostEventListener?.onPost()

    }
    fun onThoughtEdit(view: View){
        reviewPostEventListener?.onThoughtEdit(thoughts)
    }
    fun onHashtagEdit(view: View){
        reviewPostEventListener?.onHashtagEdit(hashtags)
    }
    fun onCheckedChange(on: Boolean){
        reviewPostEventListener?.onCheckedChange(on)
    }
    fun onPreview(view: View){
        reviewPostEventListener?.onPreview()
    }
    fun onDelete(view: View){
        reviewPostEventListener?.onDelete()
    }
}