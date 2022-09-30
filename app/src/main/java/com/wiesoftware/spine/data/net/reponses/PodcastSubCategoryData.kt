package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class PodcastSubCategoryData(
    @SerializedName("created_on")
    val createdOn: String,
    val id: String,
    @SerializedName("parent_id")
    val parentId: String,
    val status: String,
    @SerializedName("subcategory_name")
    val subcategoryName: String,
    @SerializedName("updated_on")
    val updatedOn: String
)