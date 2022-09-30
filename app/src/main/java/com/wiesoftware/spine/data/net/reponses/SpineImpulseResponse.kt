package com.wiesoftware.spine.data.net.reponses

data class SpineImpulseResponse(
    val current_page: String?,
    val current_per_page: String?,
    val `data`: List<SpineImpulseData>?,
    val image: String?,
    val message: String?,
    val status: Boolean

)