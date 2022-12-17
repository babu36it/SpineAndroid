package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class AllUsersData(
    @SerializedName("profile_pic")
    val profilePic: String?,
    val town: String,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("users_id")
    val usersId: String,

    val category: String,
    @SerializedName("category_name")
    val categoryName: String,
    @SerializedName("account_mode")
    val accountMode: String,
    @SerializedName("user_display_name")
    val displayName: String,
    @SerializedName("follow_status")
    val followStatus: String
)