package com.wiesoftware.spine.data.net.reponses

data class PodcastDetailsRes(
    val `data`: PodcastDetailsData,
    val image: String,
    val message: String,
    val profile_img: String,
    val status: Boolean
)