package com.wiesoftware.spine.data.net.reponses

data class CommentReplyRes(
    val `data`: List<CommentReplyData>,
    val image: String,
    val message: String,
    val status: Boolean
)