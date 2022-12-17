package com.wiesoftware.spine.data.net.reponses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FollowingStoriesRes(
    val current_page: String,
    val current_per_page: String,
    val `data`: ArrayList<FollwingData>,
    val image: String,
    val message: String,
    val status: Boolean,
    val user_image: String
):Parcelable