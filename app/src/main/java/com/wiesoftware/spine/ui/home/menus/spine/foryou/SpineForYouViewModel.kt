package com.wiesoftware.spine.ui.home.menus.spine.foryou

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 8/12/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class SpineForYouViewModel(
    private val homeRepositry: HomeRepositry
): ViewModel(){


   fun getLoggedInUser()= homeRepositry.getUser()
    var spineForYouEventListener: SpineForYouEventListener?= null

    fun viewAllImpulse(view: View){
        spineForYouEventListener?.viewAllSpineImpulse()
    }

    fun viewAllStories(view: View){
        spineForYouEventListener?.viewAllStories()
    }
    fun trendingCategories(view: View){
        spineForYouEventListener?.trendingCategories()
    }
    fun recommendedFollowers(view: View){
        spineForYouEventListener?.recommendedFollowers()
    }

}