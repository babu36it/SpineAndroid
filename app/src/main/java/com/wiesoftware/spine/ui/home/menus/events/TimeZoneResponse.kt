package com.wiesoftware.spine.ui.home.menus.events

data class TimeZoneResponse(
    val `data`: List<String>,
    val message: String,
    val status: Boolean
)