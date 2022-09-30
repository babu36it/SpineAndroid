package com.wiesoftware.spine.data.net.reponses

import java.io.Serializable

data class EventCommentData(
    val comment: String,
    val created_on: String,
    val id: String,
    val parent_comment_id: String,
    val post_user_name: String,
    val spine_event_id: String,
    val user_id: String,
    val profile_pic:String
): Serializable