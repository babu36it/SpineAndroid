package com.wiesoftware.spine.data.net.reponses

import java.io.Serializable

data class StoryMothData(
    val created_on: String,
    val month: String,
    val month_id: String,
    val year: String
): Serializable