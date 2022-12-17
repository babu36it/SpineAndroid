package com.wiesoftware.spine.data.repo

import com.wiesoftware.spine.data.db.AppDatabase
import com.wiesoftware.spine.data.net.EventApi
import com.wiesoftware.spine.data.net.SafeApiRequest
import com.wiesoftware.spine.data.net.reponses.*
import com.wiesoftware.spine.ui.home.menus.events.TimeZoneResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class EventRepository(
    private val eventApi: EventApi,
    private val db:AppDatabase
) : SafeApiRequest(){

    suspend fun addEventFeaturedAds(
        file: MultipartBody.Part,
        user_id: RequestBody,
        duration: RequestBody,
        timeslot_date: RequestBody,
        timeslot_time: RequestBody,
        ad_type: RequestBody,
        file_type: RequestBody,
        website: RequestBody,
        promote_your_ad: RequestBody,
        payment_details: RequestBody,
        pay_by: RequestBody,
        event_title: RequestBody,
        event_type: RequestBody,
        event_start_date: RequestBody,
        event_start_time: RequestBody,
        event_end_time: RequestBody,
        event_end_date: RequestBody,
        event_timezone: RequestBody,
        event_location: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody
    ):SingleRes{
        return apiRequest { eventApi.addEventFeaturedAds(file, user_id, duration, timeslot_date, timeslot_time, ad_type, file_type, website, promote_your_ad, payment_details, pay_by, event_title, event_type, event_start_date, event_start_time, event_end_time, event_end_date, event_timezone, event_location, latitude, longitude) }
    }




    suspend fun addPodcastSubcategory(parent_id: String,subcategory_name:String):SingleRes{
        return apiRequest { eventApi.addPodcastSubcategory(parent_id, subcategory_name) }
    }
    suspend fun getPodcastSubcategory(parentId:String):PodcastSubCategoryRes{
        return apiRequest { eventApi.getPodcastSubcategory(parentId) }
    }



    suspend fun getOnLineEventsList(page: Int, per_page: Int, user_id: String): EventsRes {
        return apiRequest { eventApi.getOnlineEventsList(page, per_page, user_id) }
    }


    suspend fun getGoingPastEventsList(page:Int,perPage:Int,userId:String,goingPast:Int):EventsRes{
        return apiRequest { eventApi.getGoingPastEventsList(page, perPage, userId, goingPast) }
    }


    suspend fun spineReportUserPostStory(user_id: String,spine_report_id: String,type: String,report_title: String,report_issue: String,report_msg: String): SingleRes{
        return apiRequest { eventApi.spineReportUserPostStory(user_id, spine_report_id, type, report_title, report_issue, report_msg) }
    }


    suspend fun spineStoriesComment(spine_story_id: String,user_id:String,comment_id:String,comment:String):SingleRes{
        return apiRequest { eventApi.spineStoriesComment(spine_story_id, user_id, comment_id, comment) }
    }


    suspend fun getEventDetails(event_id: String,userId: String): EventDetailsRes{
        return apiRequest { eventApi.getEventDetails(event_id) }
    }

    suspend fun changeBookingStatus(event_booking_id: String,status: String): SingleRes{
        return apiRequest { eventApi.changeBookingStatus(event_booking_id, status) }
    }


    suspend fun getTimeZoneResponse():TimeZoneResponse{
        return apiRequest { eventApi.getAllTimezones() }
    }

    suspend fun getGoingUsers(page: Int,perPage: Int,eventId: String): GoingUsersRes{
        return apiRequest { eventApi.getGoingUsers(page, perPage, eventId) }
    }

    suspend fun bookEvents(user_id: String,type: String,spine_event_id: String,message: String,amount: String): SingleRes{
        return apiRequest { eventApi.bookEvent( spine_event_id, message, amount) }
    }


    suspend fun getEventCatRes(searchText: String): EventCatRes {
        return apiRequest { eventApi.getSpinEventCategories() }
    }
    suspend fun getAllSavedEvents(page: Int,per_page: Int,user_id: String): EventsRes{
        return apiRequest { eventApi.getAllSavedEvents(page, per_page, user_id) }
    }

    suspend fun sendEventMessage(event_id: String,event_user_id:String,second_user_id: String,message: String,type: String):SingleRes{
        return apiRequest { eventApi.sendEventMessage(event_id, event_user_id, second_user_id, message, type) }
    }

    suspend fun getOwnEvents(): OwnEventsRes{
        return apiRequest { eventApi.getOwnEvents() }
    }

    suspend fun getFilteredEventList(page: String,per_page: String,user_id: String,lat: String,lon: String,distance: String,start_date: String,end_date:String,category: String): EventsRes{
        return  apiRequest { eventApi.getFilteredEvents(page, per_page,  lat, lon, distance, start_date, end_date,category) }
    }

    suspend fun getNearbyEvents(page:Int,per_page:Int,your_user_id:String,latitude:Double,longitude:Double,distance:Int):EventsRes{
        return apiRequest { eventApi.getNearbyEvents( latitude, longitude, distance) }
    }



    suspend fun getSpineEventReplys(your_events_comment_id: String):EventCommentRes{
        return apiRequest { eventApi.getSpineEventReplys(your_events_comment_id) }
    }

    suspend fun getAllEvents(page:Int,perpage: Int,type:String,typeID:String):EventsRes{
        return apiRequest { eventApi.getAllEventsList(page, perpage, type,typeID) }
    }


    suspend fun removeEventSave(your_user_id:String,your_spine_event_id:String):SingleRes{
        return apiRequest { eventApi.removeEventSave(your_user_id, your_spine_event_id) }
    }

    suspend fun getSpineEventsComment(your_events_id: String):EventCommentRes{
        return apiRequest { eventApi.getSpineEventComments(your_events_id) }
    }

    suspend fun spineEventsComment(spine_event_id: String,user_id:String,comment_id:String,comment:String):SingleRes{
        return apiRequest { eventApi.spineEventsComment(spine_event_id, user_id, comment_id, comment) }
    }

    suspend fun spineEventShare(data: Map<String, String>):SingleRes{
        return apiRequest { eventApi.shareSpineEvents(data) }
    }

    suspend fun saveEvents(your_user_id: String,your_spine_event_id:String):SingleRes{
        return apiRequest { eventApi.saveEvents(your_user_id, your_spine_event_id) }
    }

    suspend fun getFollowingUsersEventsList(page: Int,per_page: Int,your_user_id: String):EventsRes{
        return apiRequest { eventApi.getFollowingUsersEventList(page, per_page, your_user_id) }
    }

    suspend fun getEventType():EventTypeRes{
        return apiRequest { eventApi.getEventType() }
    }


    suspend fun getFollowers(page:Int,per_page:Int,user_id:String):FollowersRes{
        return apiRequest { eventApi.getFollowersList(page, per_page, user_id) }
    }

    suspend fun addUserEvent(
        status: Int, user_id: RequestBody, type: RequestBody, allow_comments: Int,
        title: RequestBody, description: RequestBody, start_time: RequestBody,
        start_date: RequestBody, end_time: RequestBody, end_date: RequestBody,
        timezone: RequestBody, location: RequestBody, link_of_event: RequestBody,
        event_categories: RequestBody, fee: Int, fee_currency: RequestBody,
        max_attendees: RequestBody, language: Int, accept_participants: Int,
        multiple: RequestBody, latitude: RequestBody, longitude: RequestBody,
        booking_url:RequestBody, event_subcategories:RequestBody,
        files: List<MultipartBody.Part>,join_event_link:RequestBody):SingleRes{
        return apiRequest { eventApi.addUserEvents(status, type, allow_comments, title, description, start_time, start_date, end_time, end_date, timezone, location, link_of_event, join_event_link,event_categories, fee, fee_currency, max_attendees, language, accept_participants, multiple,latitude,longitude,booking_url, event_subcategories,files) }
    }

    suspend fun getEventRequestUserList(Page:Int,PerPage:Int):EventRequestResponse{
        return apiRequest { eventApi.getEventRequestUserList(Page, PerPage) }
    }

    fun getUser() =db.getUserDao().getUser()

}