package com.wiesoftware.spine.data.net.reponses

data class FollowersRes(
    val current_page: String,
    val current_per_page: String,
    val `data`: List<FollowersData>,
    val image: String,
    val message: String,
    val status: Boolean
)