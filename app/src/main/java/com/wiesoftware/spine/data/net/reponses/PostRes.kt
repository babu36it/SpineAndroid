package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class PostRes(
    val current_page: String,
    val current_per_page: String,
    val `data`: MutableList<PostData>,
    val image: String,
    @SerializedName("profil_image")
    val profilImage: String,
    val message: String,
    val status: Boolean
)