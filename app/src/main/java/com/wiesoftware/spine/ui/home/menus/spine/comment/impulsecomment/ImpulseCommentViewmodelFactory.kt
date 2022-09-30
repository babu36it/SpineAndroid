package com.wiesoftware.spine.ui.home.menus.spine.comment.impulsecomment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 10/7/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class ImpulseCommentViewmodelFactory(
    val homeRepositry: HomeRepositry
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ImpulseCommentViewmodel(homeRepositry) as T
    }
}