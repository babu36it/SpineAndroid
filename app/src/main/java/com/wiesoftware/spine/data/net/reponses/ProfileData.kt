package com.wiesoftware.spine.data.net.reponses

import com.google.gson.annotations.SerializedName

data class ProfileData(
    val account: String,
    val account_mode: Any,
    val address: Any,
    val bg_image: Any,
    val bio: String,
    val business_address: String,
    val business_phone: String,
    val category: String,
    val contact_email: String,
    val created_on: String,
    val device_token: String,
    val display_name: String?,
    val email: String,
    val facebook_id: String,
    val facebook_image: String,
    val last_login: String,
    val name: String?,
    val notify_device_token: String,
    val notify_device_type: String,
    val password: String,
    val profile_pic: String?,
    val referral_code: String,
    val social_login: String,
    val status: String,
    val town: String,
    val updated_on: String,
    val user_latitude: String,
    val user_longitude: String,
    val users_id: String,
    val verification_pin: String,
    val verify_email: String,
    val website: String,
    val followers_records_count: String,
    val following_records_count: String,
    val post_records_count: String,
    val event_records_count: String,
    val pod_records_count: String,
    val allowMessage:String,
    @SerializedName("impulse_follow")
    val impulseFollow: String,
    @SerializedName("category_name")
    val categoryName: String,
    val userBlocked:String
)