package com.wiesoftware.spine.data.net.reponses

data class RssResponse(
    val feed: RssFeed,
    val items: List<RssItem>,
    val status: String
)