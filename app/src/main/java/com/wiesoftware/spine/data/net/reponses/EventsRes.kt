package com.wiesoftware.spine.data.net.reponses

data class EventsRes(
    val current_page: String,
    val current_per_page: String,
    val `data`: MutableList<EventsData>,
    val image: String,
    val message: String,
    val user_image:String,
    val status: Boolean
)