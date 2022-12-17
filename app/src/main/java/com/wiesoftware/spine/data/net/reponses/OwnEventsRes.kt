package com.wiesoftware.spine.data.net.reponses

data class OwnEventsRes(
    val `data`: MutableList<EventsRecord>,
    val image: String,
    val message: String,
    val status: Boolean
)