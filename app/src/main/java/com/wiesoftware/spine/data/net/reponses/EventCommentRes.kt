package com.wiesoftware.spine.data.net.reponses

data class EventCommentRes(
    val `data`: List<EventCommentData>,
    val message: String,
    val status: Boolean,
    val user_image:String
)