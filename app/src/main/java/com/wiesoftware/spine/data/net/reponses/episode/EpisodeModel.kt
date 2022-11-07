package com.wiesoftware.spine.data.net.reponses.episode

data class EpisodeModel(
    val status: Boolean,
    val message: String,
    val data: ArrayList<EpisodeData>
)

data class EpisodeData(

    val id: String,
    val podcast_episodes: ArrayList<ProdcastEpisode>

)

data class ProdcastEpisode(
    val title: String,
    val language: String,
    val pubDate: String,
    val guid: String,
    val author: String,
    val thumbnail: String,
    val mediaLink: String,
    val type: String,
    val duration: String,
    val user_id: String,
    val user_name: String,
    val user_image: String,
    val play_count: String,
    val like: String,
    val favourite_count: String
)