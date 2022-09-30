package com.wiesoftware.spine.data.net.reponses

data class EventCatData(
    val category_name: String,
    val created_on: String,
    val id: String,
    val status: String,
    val updated_on: String,
    var isSelect:Boolean
)