package com.wiesoftware.spine.data.net.reponses

data class ProfileRes(
    val `data`: ProfileData,
    val message: String,
    val image: String,
    val status: Boolean
)