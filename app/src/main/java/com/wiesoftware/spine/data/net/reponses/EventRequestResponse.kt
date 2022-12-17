package com.wiesoftware.spine.data.net.reponses

data class EventRequestResponse(
    val `data`: List<EventRequestData>,
    val image: String,
    val message: String,
    val profile_image: String,
    val status: Boolean
)