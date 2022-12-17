package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class GoingUsersRes(
    val `data`: GoingUsersData,
    val image: String,
    val message: String,
    @SerializedName("profile_image")
    val profileImage: String,
    val status: Boolean
)