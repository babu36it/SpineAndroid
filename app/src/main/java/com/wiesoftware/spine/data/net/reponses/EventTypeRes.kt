package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class EventTypeRes (
    val `data`: MutableList<EventTypeData>,
    val message: String,
    val status: Boolean
        )