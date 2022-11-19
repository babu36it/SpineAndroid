package com.wiesoftware.spine.ui.home.menus.spine.addposts.poststory

import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 10/19/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class AddStoryViewmodel(val homeRepositry: HomeRepositry) : ViewModel() {


    var allowComments: Boolean = true
    var title: String? = null
    var addStoryEventListener: AddStoryEventListener? = null

    fun onBAck(view: View) {
        addStoryEventListener?.onBack()
    }

    fun onAdd(view: View) {
        addStoryEventListener?.onAdd()
    }

    fun onPreview(view: View) {
        addStoryEventListener?.onPreview()
    }

    fun onDelete(view: View) {
        addStoryEventListener?.onDelete()
    }

    fun onPostStory(view: View) {
        addStoryEventListener?.onPostStory(allowComments)
    }

}