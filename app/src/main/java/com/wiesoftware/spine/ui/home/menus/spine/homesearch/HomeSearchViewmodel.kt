package com.wiesoftware.spine.ui.home.menus.spine.homesearch

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 4/7/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class HomeSearchViewmodel():ViewModel() {

    var homeSearchEventListener: HomeSearchEventListener?= null

    fun onPosts(view: View){
        homeSearchEventListener?.onPosts()
    }
    fun onTags(view: View){
        homeSearchEventListener?.onTags()
    }
    fun onPeople(view: View){
        homeSearchEventListener?.onPeople()
    }
    fun onPracticioners(view: View){
        homeSearchEventListener?.onPracticioner()
    }
    fun onCategory(view: View){
        homeSearchEventListener?.onCategories()
    }
}