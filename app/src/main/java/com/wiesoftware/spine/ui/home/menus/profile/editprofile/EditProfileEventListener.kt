package com.wiesoftware.spine.ui.home.menus.profile.editprofile

/**
 * Created by Vivek kumar on 11/25/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
interface EditProfileEventListener {
    fun onBack()
    fun onSaveProfile(user_name: String, display_name: String, short_bio: String)
    fun switchAccount()
    fun addProfilePic()
    fun onCategorySelect()
}