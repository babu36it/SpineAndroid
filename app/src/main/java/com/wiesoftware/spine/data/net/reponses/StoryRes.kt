package com.wiesoftware.spine.data.net.reponses

data class StoryRes(
    val `data`: List<StoryData>,
    val image: String,
    val message: String,
    val status: Boolean
)