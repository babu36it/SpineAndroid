package com.wiesoftware.spine.data.net.reponses

data class RecommendedFollowersRes(
    val current_page: String,
    val current_per_page: String,
    val `data`: List<RecommendedFollowersData>,
    val image: String,
    val message: String,
    val status: Boolean,
    val total: String
)