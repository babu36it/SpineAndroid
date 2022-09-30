package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class PodcastRes(
    val `data`: List<PodcastData>,
    val message: String,
    val status: Boolean,
    val image: String,
    @SerializedName("profile_img")
    val profileImg: String
)