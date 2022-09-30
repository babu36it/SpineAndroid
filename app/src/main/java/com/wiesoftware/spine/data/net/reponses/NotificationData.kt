package com.wiesoftware.spine.data.net.reponses

data class NotificationData(
    val bio: String,
    val created_on: String,
    val table_id: String,
    val title: String,
    val user_action: String,
    val username: String,
    val users_id: String
    )