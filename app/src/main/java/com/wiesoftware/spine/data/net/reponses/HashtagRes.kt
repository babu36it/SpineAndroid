package com.wiesoftware.spine.data.net.reponses

data class HashtagRes(
    val `data`: MutableList<HashtagData>,
    val message: String,
    val status: Boolean
)