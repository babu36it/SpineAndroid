package com.wiesoftware.spine.data.net.reponses

data class StoryMonthRes(
    val `data`: List<StoryMothData>,
    val message: String,
    val status: Boolean
)