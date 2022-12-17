package com.wiesoftware.spine.ui.home.menus.spine.addposts.postthought

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 11/5/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class PostThoughtViewmodel(val homeRepositry: HomeRepository) : ViewModel() {

    var postThoughtEventListener: PostThoughtEventListener?=null
    fun onBack(view: View){
        postThoughtEventListener?.onBack()
    }

}