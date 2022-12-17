package com.wiesoftware.spine.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

const val CURRENT_USER_ID = 0

@Entity
data class User(
    var users_id: String?,
    var name: String?,
    var town: String?,
    var email: String?,
    var verification_pin: String?,
    var status: String?,
    var account: String?,
    var facebook_id: String?,
    var facebook_image: String?,
    var social_login: String?,
    var device_token: String?,
    var last_login: String?,
    var verify_email: String?,
    var referral_code: String?,
    var user_latitude: String?,
    var user_longitude: String?,
    var display_name: String?,
    var user_image:String?
):Serializable
{
    @PrimaryKey(autoGenerate = false)
    var uid: Int = CURRENT_USER_ID
}