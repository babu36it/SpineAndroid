package com.wiesoftware.spine.data.net.reponses

data class SpineCommentRes(
    val `data`: List<SpineCommentData>,
    val message: String,
    val status: Boolean,
    val image: String
)