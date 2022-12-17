package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class RecommendedFollowersData(
    val category: String,
    @SerializedName("category_name")
    val categoryName: String,
    @SerializedName("profile_pic")
    val profilePic: String,
    val town: String,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("users_id")
    val usersId: String
)