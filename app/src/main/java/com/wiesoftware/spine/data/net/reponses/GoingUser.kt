package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class GoingUser(
    val bio: String,
    val email: String,
    val name: String,
    @SerializedName("profile_pic")
    val profilePic: String,
    val town: String,
    @SerializedName("users_id")
    val usersId: String
)