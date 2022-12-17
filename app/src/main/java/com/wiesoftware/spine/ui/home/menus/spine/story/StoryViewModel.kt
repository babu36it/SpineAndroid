package com.wiesoftware.spine.ui.home.menus.spine.story

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 10/1/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class StoryViewModel(
    val homeRepositry: HomeRepository
): ViewModel() {

    var storyEventListener : StoryEventListener? = null

    fun getLoggedInUser()=homeRepositry.getUser()

    fun onBack(view: View){
        storyEventListener?.onBack()
    }
}