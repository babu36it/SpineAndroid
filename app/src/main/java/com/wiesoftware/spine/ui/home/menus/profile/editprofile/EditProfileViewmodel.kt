package com.wiesoftware.spine.ui.home.menus.profile.editprofile

import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 11/25/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class EditProfileViewmodel(val homeRepositry: HomeRepositry): ViewModel() {

    var user_name:String=""
    var display_name: String=""
    var short_bio: String=""

    var editProfileEventListener: EditProfileEventListener?= null

    fun getLoggedInUser()=homeRepositry.getUser()

    fun onBack(view: View){
        editProfileEventListener?.onBack()
    }
    fun addProfilePic(view: View){
        editProfileEventListener?.addProfilePic()
    }

    fun onSave(view: View){
        if (display_name.isNullOrEmpty()){
            Toast.makeText(view.context,"Enter Display Name",Toast.LENGTH_SHORT).show()
        }else if (short_bio.isNullOrEmpty()){
            Toast.makeText(view.context,"Enter Short Bio",Toast.LENGTH_SHORT).show()
        }else{
            editProfileEventListener?.onSaveProfile(user_name,display_name,short_bio)
        }


    }
    fun onSwitchAccount(view: View){
        editProfileEventListener?.switchAccount()
    }
    fun onCategorySelect(view: View){
        editProfileEventListener?.onCategorySelect()
    }
}