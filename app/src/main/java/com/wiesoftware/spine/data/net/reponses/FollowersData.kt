package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

/**
 * Created by Vivek kumar on 12/18/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
data class FollowersData(
    val created_on: String,
    val image: String,
    val hashtag_ids: String,
    val id: String,
    val multiplity: String,
    val post_backround_color_id: String,
    val post_user_name: String,
    val status: String,
    val title: String,
    val type: String,
    val updated_on: String,
    val user_id: String,
    val tbl_users_user_name: String,
    @SerializedName("tbl_users_user_display_name")
    val displayName: String,
    val profile_pic: String,
    val tbl_users_user_id: String,
    val bio: String,
    val is_follow: String
)

