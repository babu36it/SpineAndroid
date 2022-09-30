package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class AllUsersRes(
    @SerializedName("current_page")
    val currentPage: String,
    @SerializedName("current_per_page")
    val currentPerPage: String,
    val `data`: List<AllUsersData>,
    val image: String,
    val message: String,
    val status: Boolean,
    val total: String
)