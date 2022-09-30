package com.wiesoftware.spine.data.net.reponses

data class PodcastSubCategoryRes(
    val `data`: List<PodcastSubCategoryData>,
    val message: String,
    val status: Boolean
)