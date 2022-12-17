package com.wiesoftware.spine.data.net.reponses

data class StoriesCommentRes(
    val `data`: List<StoriesCommentData>,
    val message: String,
    val status: Boolean
)