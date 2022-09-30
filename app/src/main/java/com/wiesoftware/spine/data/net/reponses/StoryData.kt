package com.wiesoftware.spine.data.net.reponses

data class StoryData(
    val allow_comment: String,
    val created_on: String,
    val delete_story_after_24_hr: String,
    val id: String,
    val media_file: String,
    val removed_time: String,
    val title: String,
    val type: String,
    val user_id: String
)