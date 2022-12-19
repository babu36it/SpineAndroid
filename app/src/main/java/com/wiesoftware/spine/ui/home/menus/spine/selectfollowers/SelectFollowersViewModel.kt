package com.wiesoftware.spine.ui.home.menus.spine.selectfollowers

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 12/21/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class SelectFollowersViewModel(val homeRepositry: HomeRepository): ViewModel() {

    var selectFollowersEventListener: SelectFollowersEventListener?= null

    fun getLoggedInUser()=homeRepositry.getUser()

    fun onBack(view: View){
        selectFollowersEventListener?.onBack()
    }
    fun onNext(view: View){
        selectFollowersEventListener?.onNext()
    }
}