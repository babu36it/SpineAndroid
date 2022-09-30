package com.wiesoftware.spine.data.net.reponses

data class EventRequestData(
    val bio: String,
    val booking_status: String,
    val email: String,
    val event_id: String,
    val event_user_id: String,
    val `file`: String,
    val id: String,
    val message: String,
    val multiple: String,
    val name: String,
    val profile_pic: String,
    val title: String,
    val town: String,
    val type: String,
    val users_id: String
)