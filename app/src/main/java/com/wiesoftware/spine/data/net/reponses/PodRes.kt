package com.wiesoftware.spine.data.net.reponses

data class PodRes(
    val `data`: List<PodDatas>,
    val image: String,
    val message: String,
    val profile_img: String,
    val status: Boolean
)