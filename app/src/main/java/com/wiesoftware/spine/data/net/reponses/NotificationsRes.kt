package com.wiesoftware.spine.data.net.reponses

data class NotificationsRes(
    val `data`: List<NotificationData>,
    val message: String,
    val status: Boolean
)