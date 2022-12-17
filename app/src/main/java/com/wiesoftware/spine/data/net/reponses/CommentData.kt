package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class CommentData(
    val comment: String?,
    val created_on: String,
    val id: String,
    val parent_comment_id: String,
    val spine_impluse_id: String,
    val user_id: String,
    val name: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("profile_pic")
    val profilePic: String
)