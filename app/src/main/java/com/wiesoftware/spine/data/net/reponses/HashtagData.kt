package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class HashtagData(
    val created_on: String,
    val hash_title: String,
    val id: String,
    val users_id: String,
    @SerializedName("total_count")
    val totalCount: String

)