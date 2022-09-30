package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SpineCommentData(
    val comment: String,
    val created_on: String,
    val id: String,
    val parent_comment_id: String,
    val post_user_name: String,
    val spine_post_id: String,
    val user_id: String,
    @SerializedName("post_user_display_name")
    val displayName: String,
    @SerializedName("profile_pic")
    val profilePic: String
): Serializable