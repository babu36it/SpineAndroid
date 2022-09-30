package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class PodDatas(
    val allow_comment: String,
    val bio: String,
    val bookmarks: String,
    val category: List<PodCategory>,
    val created_on: String,
    val description: String,
    val duration: String,
    val id: String,
    @SerializedName("iso_639-1")
    val iso_639_1: String,
    val language: String,
    val likes: String,
    val media_file: String,
    val name: String,
    val profile_pic: String,
    val rss_feed: String,
    val status: String,
    val subcategory: List<PodSubcategory>,
    val thumbnail: String,
    val title: String,
    val type: Any,
    val updated_on: String,
    val user_display_name: String,
    val user_id: String,
    val user_like: String,
    val username: String,
    val views: String
)