package com.wiesoftware.spine.data.net.reponses

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FollowingStoriesData(
    val allow_comment: String,
    val created_on: String,
    val delete_story_after_24_hr: String,
    val id: String,
    val media_file: String,
    val removed_time: String,
    val title: String,
    val type: String,
    val user_id: String,
    @SerializedName("total_likes")
    val totalLikes: Int?,
    @SerializedName("total_comments")
    val totalComments: Int?,
    @SerializedName("like_status")
    val likeStatus:Int,
    @SerializedName("save_status")
    val saveStatus:Int
):Parcelable{
    fun isVideo() =  media_file?.contains(".mp4",true) ?: false ||
            media_file?.contains(".mov",true) ?: false ||
            media_file?.contains(".3gp",true) ?: false ||
            media_file?.contains(".avi",true) ?: false
}