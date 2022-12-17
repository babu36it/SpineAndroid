package com.wiesoftware.spine.data.net.reponses

data class EventCatRes(
    val `data`: ArrayList<EventCatData>,
    val message: String,
    val status: Boolean
)