package com.wiesoftware.spine.ui.home.menus.events

data class TimeZoneResponse(
    val `data`: List<TimezoneData>,
    val message: String,
    val status: Boolean
)

data class TimezoneData(
    val id: String,
    val country_code: String,
    val timezone: String,
    val gmt_offset: String,
    val dst_offset: String,
    val raw_offset: String,
)
