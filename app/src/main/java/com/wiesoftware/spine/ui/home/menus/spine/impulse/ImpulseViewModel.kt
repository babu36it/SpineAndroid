package com.wiesoftware.spine.ui.home.menus.spine.impulse

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 9/24/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class ImpulseViewModel(
    private val homeRepositry: HomeRepository
): ViewModel() {

    var impulseEventListener: ImpulseEventListener?= null

    fun onBack(view: View){
        impulseEventListener?.onBack()
    }
    fun follow(view: View){
        impulseEventListener?.onFollow()
    }
}