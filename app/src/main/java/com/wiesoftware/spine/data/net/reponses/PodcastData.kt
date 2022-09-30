package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class PodcastData(
    val allow_comment: String,
    val category: String,
    val created_on: String,
    val description: String,
    val id: String,
    val language: String,
    val media_file: String,
    val status: String,
    val thumbnail: String,
    val title: String,
    val type: String,
    val updated_on: String,
    val user_id: String,
    val views: String,
    val likes: String,
    val username: String,
    @SerializedName("user_display_name")
    val displayName:String,
    val profile_pic: String,
    val user_like: String,
    val bookmarks: String,
    @SerializedName("iso_639-1")
    val lang: String,
    val bio: String
)