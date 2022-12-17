package com.wiesoftware.spine.data.net.reponses

data class RssResponse(
    val data: DataFeed,

    val status: Boolean
)

data class DataFeed(
    val feed: RssFeed,
    val items: ArrayList<RssItem>,
)