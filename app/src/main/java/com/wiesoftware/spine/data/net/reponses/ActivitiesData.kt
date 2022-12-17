package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class ActivitiesData(
    @SerializedName("created_on")
    val createdOn: String,
    val files: String,
    @SerializedName("follow_user_id")
    val followUserId: String,
    @SerializedName("hashtag_ids")
    val hashtagIds: String,
    val id: String,
    val multiplity: String,
    @SerializedName("post_backround_color_id")
    val postBackroundColorId: String,
    @SerializedName("private_req")
    val privateReq: String,
    val status: String,
    val tbl: String,
    val title: String,
    val type: String,
    @SerializedName("updated_on")
    val updatedOn: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("user_name_from_id")
    val userNameFromId: String,
    @SerializedName("user_display_name_from_id")
    val displayName: String,
    @SerializedName("u_name")
    val uName: String,
    @SerializedName("profile_pic")
    val profilePic: String,
    @SerializedName("tbl_action")
    val tblAction: String,
    @SerializedName("opp_display_name")
    val oppDisplayName: String,

    @SerializedName("created_on_time_ago")
     val createdOnTimeAgo: String
)