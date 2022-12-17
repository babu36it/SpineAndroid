package com.wiesoftware.spine.data.net.reponses

data class ChatMsgRes(
    val `data`: List<ChatMsgData>,
    val message: String,
    val status: Boolean
)