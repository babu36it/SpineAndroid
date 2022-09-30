package com.wiesoftware.spine.ui.home.menus.podcasts.addpodcasts

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 2/1/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class AddPodcastViewmodel(val homeRepositry: HomeRepositry): ViewModel() {

    var addPodcastEventListener: AddPodcastEventListener?=null
    var title: String=""
    var description: String=""
    var addNewCat: String? = null

    fun getLoggedInUser()=homeRepositry.getUser()

    fun onBack(view: View){
    addPodcastEventListener?.onBack()
    }
    fun onPost(view: View){
    addPodcastEventListener?.onPost(title,description)
    }
    fun addPodcast(view: View){
        addPodcastEventListener?.onAddPodcast()
    }
    fun addImage(view: View){
        addPodcastEventListener?.onAddImage()
    }

    fun isChecked(isChecked: Boolean){
        addPodcastEventListener?.isAllowComment(isChecked)
    }

    fun onSelectCategories(view: View){
        addPodcastEventListener?.onSelectCategories()
    }
    fun onAddAditionalCategory(view: View){
        addPodcastEventListener?.onAddAditionalCategory()
    }
    fun onNewCategoryAdd(view: View){
        if (addNewCat.isNullOrEmpty()){
            return
        }
        addPodcastEventListener?.onAddNewCategory(addNewCat!!)
    }
    fun onReview(view: View){
        addPodcastEventListener?.onReview(title,description)
    }

}