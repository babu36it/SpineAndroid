package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class CommentReplyData(
    val comment: String,
    @SerializedName("created_on")
    val createdOn: String,
    val id: String,
    @SerializedName("parent_comment_id")
    val parentCommentId: String,
    @SerializedName("post_user_display_name")
    val displayName: String,
    @SerializedName("post_user_name")
    val postUserName: String,
    @SerializedName("profile_pic")
    val profilePic: String,
    @SerializedName("spine_post_id")
    val spinePostId: String,
    @SerializedName("user_id")
    val userId: String
)