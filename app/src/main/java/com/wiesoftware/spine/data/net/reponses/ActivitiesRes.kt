package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class ActivitiesRes(
    @SerializedName("current_page")
    val currentPage: String,
    @SerializedName("current_per_page")
    val currentPerPage: String,
    val `data`: List<ActivitiesData>,
    @SerializedName("impulse-image")
    val impulseImage: String,
    val message: String,
    @SerializedName("post-image")
    val postImage: String,
    val status: Boolean,
    @SerializedName("profil_image")
    val profilImage: String
)