package com.wiesoftware.spine.data.net.reponses

data class RssEnclosure(
    val duration: Int,
    val length: Int,
    val link: String,
    val rating: RssRating,
    val type: String
)