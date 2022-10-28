package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class PodcastSubCategoryData(

    val id: String,
    @SerializedName("parent_id")
    val parentId: String,
    val status: String,
    @SerializedName("subcategory_name")
    val subcategoryName: String,
)