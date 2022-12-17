package com.wiesoftware.spine.data.net.reponses

import java.io.Serializable

data class EveMsgUserData(
    val event_id: String,
    val event_name: String,
    val event_user_id: String,
    val event_user_name: String,
    val id: String,
    val second_user_id: String,
    val second_user_name: String,
    val created_on: String,
    val message: String,
    val event_user_image: String
): Serializable