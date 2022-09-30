package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

/*
data class AdDurationData(
    val amount: String,
    @SerializedName("created_on")
    val createdOn: String,
    val currency: String,
    val duration: String,
    val id: String,
    @SerializedName("modification_on")
    val modificationOn: String,
    val status: String,
    @SerializedName("duration_type")
    val durationType: String
)*/

data class AdDurationData(
    val id: String,
    val amount: String,
    val currency: String,
    val duration: String,
    val durationType: String
)
