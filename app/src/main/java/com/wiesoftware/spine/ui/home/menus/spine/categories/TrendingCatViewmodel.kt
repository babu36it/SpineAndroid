package com.wiesoftware.spine.ui.home.menus.spine.categories

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 12/16/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class TrendingCatViewmodel(val homeRepositry: HomeRepositry):ViewModel() {

    var trendingCatEventListener: TrendingCatEventListener?=null

    fun onBack(view: View){
        trendingCatEventListener?.onBack()
    }
    fun allCategory(view: View){
        trendingCatEventListener?.allCategory()
    }
    fun trendingCategory(view: View){
        trendingCatEventListener?.trendingCategory()
    }
}