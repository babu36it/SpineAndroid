package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class LangData(
    val id: String,
    val name: String,
    @SerializedName("iso_639-1")
    val iso_639: String,
    var isSelect:Boolean
)