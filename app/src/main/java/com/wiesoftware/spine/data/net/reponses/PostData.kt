package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PostData(
    val created_on: String,
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
    val total_comment: String,
    val total_like: String,
    val total_save: String,
    val total_share: String,
    val user_like_status: String,
    val user_save_status: String,
    @SerializedName("profile_pic")
    val profilePic: String,
    @SerializedName("post_user_display_name")
    val displayName: String,
    @SerializedName("account_mode")
    val accountMode: String,
    @SerializedName("feature_post")
    val featurePost: String,
    @SerializedName("feature_admin_approve")
    val featureAdminApprove: String,
    @SerializedName("event_post")
    val eventPost: String,
    @SerializedName("event_id")
    val eventId: String,
    @SerializedName("follow_status")
    val followStatus: String,
    @SerializedName("event_location")
    val eventLocation: String,
    @SerializedName("promote_your_ad")
    val promoteYourAd: String,
    val website: String,
    val file:String,
    var files: String
): Serializable