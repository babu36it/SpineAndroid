package com.wiesoftware.spine.data.net.reponses

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FollwingData(
    val name: String,
    @SerializedName("display_name")
    val displayName:String,
    val stories_data: ArrayList<FollowingStoriesData>,
    val user_id: String,
    @SerializedName("profile_pic")
    val profilePic: String
):Parcelable{

}