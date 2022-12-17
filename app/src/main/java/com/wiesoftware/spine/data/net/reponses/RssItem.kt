package com.wiesoftware.spine.data.net.reponses

data class RssItem(
    val author: String,
    val categories: List<Any>,
    val content: String,
    val description: String,
    val enclosure: RssEnclosure,
    val guid: String,
    val link: String,
    val pubDate: String,
    val thumbnail: String,
    val title: String,
    val time: String,
    val like: String,
    val favourite: String
)