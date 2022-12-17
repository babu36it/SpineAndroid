package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class EventsData(
    val records: List<EventsRecord>,
    @SerializedName("start_date")
    val startDate: String
)