package com.wiesoftware.spine.ui.home.menus.spine.story.viewstories

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 12/17/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class ViewStoryViewmodel(val homeRepositry: HomeRepositry): ViewModel() {

    var viewStoryEventListener: ViewStoryEventListener?=null

    fun getLoggedInUser()=homeRepositry.getUser()

    fun onBack(view: View){
        viewStoryEventListener?.onBack()
    }
    fun onStorySave(view: View){
        viewStoryEventListener?.onStorySave()
    }
    fun onStoryMore(view: View){
        viewStoryEventListener?.onStoryMore()
    }

}