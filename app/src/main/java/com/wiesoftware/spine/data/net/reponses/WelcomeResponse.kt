package com.wiesoftware.spine.data.net.reponses

data class WelcomeResponse(
    val `data`: List<WelcomeData>?,
    val image: String?,
    val message: String?,
    val status: Boolean?
)