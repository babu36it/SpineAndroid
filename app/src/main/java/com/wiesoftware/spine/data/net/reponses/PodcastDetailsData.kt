package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class PodcastDetailsData(
    val allow_comment: String,
    val bio: Any,
    val bookmarks: String,
    val category: String,
    val created_on: String,
    val description: String,
    val id: String,
    @SerializedName("iso_639-1")
    val lang: String,
    val language: String,
    val likes: String,
    val media_file: String,
    val name: String,
    val profile_pic: Any,
    val status: String,
    val thumbnail: String,
    val title: String,
    val type: String,
    val updated_on: String,
    val user_id: String,
    val user_like: String,
    val username: String,
    val views: String
)