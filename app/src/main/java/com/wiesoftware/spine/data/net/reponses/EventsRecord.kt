package com.wiesoftware.spine.data.net.reponses


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EventsRecord(
    @SerializedName("accept_participants")
    val acceptParticipants: String,
    @SerializedName("acctual_end_datetime")
    val acctualEndDatetime: String,
    @SerializedName("acctual_start_datetime")
    val acctualStartDatetime: String,
    @SerializedName("allow_comments")
    val allowComments: String,
    @SerializedName("created_on")
    val createdOn: String,
    val description: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("end_time")
    val endTime: String,
    @SerializedName("event_categories")
    val eventCategories: String,
    val fee: String,
    @SerializedName("fee_currency")
    val feeCurrency: String,
    val `file`: String,
    val id: String,
    val language: String,
    @SerializedName("link_of_event")
    val linkOfEvent: String,
    val location: String,
    @SerializedName("max_attendees")
    val maxAttendees: String,
    val multiple: String,

    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("start_time")
    val startTime: String,
    val status: String,
    val timezone: String,
    val title: String,
    val type: String,
    @SerializedName("updated_on")
    val updatedOn: String,
    @SerializedName("use_name")
    val useName: String,
    @SerializedName("use_display_name")
    val displayName:String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("total_comment")
    val totalComment:String,
    @SerializedName("total_save")
    val totalSave: String,
    @SerializedName("total_share")
    val totalShare: String,
    @SerializedName("user_share_status")
    val userShareStatus: String,
    @SerializedName("user_save_status")
    val userSaveStatus: String,
    val latitude: String,
    val longitude: String,
    @SerializedName("booking_status")
    val bookingStatus: String,
    @SerializedName("booking_id")
    val bookingId: String,
    @SerializedName("symbol")
    val  symbol: String,
    @SerializedName("hostedProfilePic")
    val hostedProfilePic:String,
    @SerializedName("languageName")
    val languageName: String,
    @SerializedName("booking_url")
    val booking_url: String,
    var isSelected: Boolean

): Serializable

//data class DemoEventsRecord(
//    val type:String,
//    val title:String,
//    val time:String,
//    val location:String,
//    )