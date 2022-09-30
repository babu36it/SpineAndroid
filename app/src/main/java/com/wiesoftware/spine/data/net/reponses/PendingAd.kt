package com.wiesoftware.spine.data.net.reponses

data class PendingAd(
    val ad_type: String,
    val created_on: String,
    val duration: String,
    val duration_amount: String,
    val duration_currency: String,
    val duration_length: String,
    val duration_type: String,
    val event_end_date: Any,
    val event_end_time: Any,
    val event_location: Any,
    val event_start_date: Any,
    val event_start_time: Any,
    val event_timezone: Any,
    val event_title: Any,
    val event_type: String,
    val `file`: String,
    val file_type: String,
    val id: String,
    val latitude: Any,
    val longitude: Any,
    val pay_by: String,
    val payment_details: String,
    val promote_your_ad: String,
    val status: String,
    val timeslot_date: String,
    val timeslot_time: String,
    val updated_on: String,
    val user_id: String,
    val website: String
)