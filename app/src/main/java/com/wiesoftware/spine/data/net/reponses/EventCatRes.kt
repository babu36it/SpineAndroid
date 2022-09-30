package com.wiesoftware.spine.data.net.reponses

data class EventCatRes(
    val `data`: List<EventCatData>,
    val message: String,
    val status: Boolean
)