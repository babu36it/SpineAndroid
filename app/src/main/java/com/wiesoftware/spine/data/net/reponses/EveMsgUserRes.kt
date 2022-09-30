package com.wiesoftware.spine.data.net.reponses

data class EveMsgUserRes(
    val `data`: List<EveMsgUserData>,
    val message: String,
    val status: Boolean,
    val image: String
)