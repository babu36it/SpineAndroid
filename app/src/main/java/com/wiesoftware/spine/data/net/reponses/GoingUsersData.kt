package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class GoingUsersData(
    val `file`: String,
    val id: String,
    val multiple: String,
    val title: String,
    val type: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("user_list")
    val userList: List<GoingUser>
)