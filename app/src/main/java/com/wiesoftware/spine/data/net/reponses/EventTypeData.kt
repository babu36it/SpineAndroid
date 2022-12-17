package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class EventTypeData(
    val id: String,
    @SerializedName("type_name")
    val typename: String,
    val description: String,
    val status: String,
    @SerializedName("type_image")
    val typeimage: String,
    @SerializedName("created_at")
    val createdat: String,
    @SerializedName("updated_at")
    val updatedat: String,
): Serializable
