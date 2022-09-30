package com.wiesoftware.spine.data.net.reponses

data class CommentResponse(
    val `data`: ArrayList<CommentData>,
    val message: String,
    val status: Boolean,
    val image: String
)