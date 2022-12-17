package com.wiesoftware.spine.data.net.reponses

data class ChatMsgData(
    val created_on: String,
    val id: String,
    val message: String,
    val message_id: String,
    val type: String,
    val updated_on: String,
    val message_by: String
)