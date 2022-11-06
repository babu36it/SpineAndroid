package com.wiesoftware.spine.data.net.reponses


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EventsRecord(
    @SerializedName("accept_participants")
    val acceptParticipants: Int,
    @SerializedName("acctual_end_datetime")
    val acctualEndDatetime: String,
    @SerializedName("acctual_start_datetime")
    val acctualStartDatetime: String,
    @SerializedName("allow_comments")
    val allowComments: Int,
    @SerializedName("created_on")
    val createdOn: String,
    val description: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("end_time")
    val endTime: String,
    @SerializedName("event_categories")
    val eventCategories: String,
    @SerializedName("event_subcategories")
    val eventSubCategories: String,
    val fee: Int,
    @SerializedName("fee_currency")
    val feeCurrency: String,
    val `file`: String,
    val id: String,
    @SerializedName("language")
    val language: Int,
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
    val status: Int,
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
    @SerializedName("hosted_profile_pic")
    val hostedProfilePic:String,
    @SerializedName("languageName")
    val languageName: String,
    @SerializedName("booking_url")
    val booking_url: String,
    var isSelected: Boolean,
    @SerializedName("join_event_link")
    val join_event_link: String,


): Serializable


data class EventRecord (
    @SerializedName("event_id")
    val eventID: String,

    @SerializedName("user_id")
    val userID: String,

    @SerializedName("event_images")
    val eventImages: String,

    @SerializedName("event_date")
    val eventDate: String,

    @SerializedName("event_type")
    val eventType: String,

    @SerializedName("event_title")
    val eventTitle: String,

    @SerializedName("event_startdate")
    val eventStartdate: String,

    @SerializedName("event_starttime")
    val eventStarttime: String,

    @SerializedName("event_enddate")
    val eventEnddate: String,

    @SerializedName("event_endtime")
    val eventEndtime: String,

    @SerializedName("event_location")
    val eventLocation: String,

    @SerializedName("event_hosted_lang")
    val eventHostedLang: String,

    @SerializedName("event_username")
    val eventUsername: String,

    @SerializedName("event_user_profile")
    val eventUserProfile: String,

    @SerializedName("event_about")
    val eventAbout: String,

    val amount: String,

    @SerializedName("currency_symbol")
    val currencySymbol: String,

    @SerializedName("categories_name")
    val categoriesName: String,

    @SerializedName("subcategories_name")
    val subcategoriesName: String,

    @SerializedName("link_of_event")
    val linkOfEvent: Any? = null,

    @SerializedName("join_event_link")
    val joinEventLink: Any? = null,

    @SerializedName("booking_url")
    val bookingURL: String,

    @SerializedName("booking_id")
    val bookingID: String,

    @SerializedName("button_text")
    val buttonText: String,

    @SerializedName("event_going_count")
    val eventGoingCount: Long,

    @SerializedName("event_going_users")
    val eventGoingUsers: List<Any?>,

    @SerializedName("total_comments")
    val totalComments: String,

    val comments: List<Any?>
):Serializable

//data class DemoEventsRecord(
//    val type:String,
//    val title:String,
//    val time:String,
//    val location:String,
//    )