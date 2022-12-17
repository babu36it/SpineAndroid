package com.wiesoftware.spine.data.net.reponses

/**
 * Created by Vivek kumar on 2/4/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
data class EventDetailsRes(
    val `data`: EventsRecord,
    val image: String,
    val message: String,
    val status: Boolean
)
