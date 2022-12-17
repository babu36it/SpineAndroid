package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StoriesCommentData(
    val comment: String,
    val created_on: String,
    val id: String,
    @SerializedName("parent_comment_id")
    val parentCommentId: String,
    @SerializedName("spine_story_id")
    val spineStoryId: String,
    @SerializedName("user_id")
    val userId: String,
    val name: String,
    @SerializedName("profile_pic")
    val profilePic: String
): Serializable