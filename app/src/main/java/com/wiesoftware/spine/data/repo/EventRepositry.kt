package com.wiesoftware.spine.data.repo

import com.wiesoftware.spine.data.db.AppDatabase
import com.wiesoftware.spine.data.db.entities.User
import com.wiesoftware.spine.data.net.Api
import com.wiesoftware.spine.data.net.SafeApiRequest
import com.wiesoftware.spine.data.net.reponses.*
import com.wiesoftware.spine.data.net.reponses.welcompageresponse.WelcomePageReponse
import com.wiesoftware.spine.ui.home.menus.events.TimeZoneResponse
import com.wiesoftware.spine.util.Prefs
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody

/**
 * Created by Harsh Patel on 11/12/2022.
 * E-mail:- ptlhrs1998r@gmail.com
 */
class EventRepositry(
    private val api: Api,
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
        return apiRequest { api.addEventFeaturedAds(file, user_id, duration, timeslot_date, timeslot_time, ad_type, file_type, website, promote_your_ad, payment_details, pay_by, event_title, event_type, event_start_date, event_start_time, event_end_time, event_end_date, event_timezone, event_location, latitude, longitude) }
    }

    suspend fun addFeaturedAds(
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
        latitude: RequestBody,
        longitude: RequestBody
    ): SingleRes{
        return apiRequest { api.addFeaturedAds(file, user_id, duration, timeslot_date, timeslot_time, ad_type, file_type, website, promote_your_ad,payment_details, pay_by,latitude, longitude) }
    }



    suspend fun addPodcastSubcategory(parent_id: String,subcategory_name:String):SingleRes{
        return apiRequest { api.addPodcastSubcategory(parent_id, subcategory_name) }
    }
    suspend fun getPodcastSubcategory(parentId:String):PodcastSubCategoryRes{
        return apiRequest { api.getPodcastSubcategory(parentId) }
    }


    suspend fun sharePodcasts(data: Map<String,String>):SingleRes{
        return apiRequest { api.sharePodcasts(data) }
    }


    suspend fun getGoingPastEventsList(page:Int,perPage:Int,userId:String,goingPast:Int):EventsRes{
        return apiRequest { api.getGoingPastEventsList(page, perPage, userId, goingPast) }
    }


    suspend fun spineReportUserPostStory(user_id: String,spine_report_id: String,type: String,report_title: String,report_issue: String,report_msg: String): SingleRes{
        return apiRequest { api.spineReportUserPostStory(user_id, spine_report_id, type, report_title, report_issue, report_msg) }
    }


    suspend fun spineStoriesComment(spine_story_id: String,user_id:String,comment_id:String,comment:String):SingleRes{
        return apiRequest { api.spineStoriesComment(spine_story_id, user_id, comment_id, comment) }
    }


    suspend fun getEventDetails(event_id: String,userId: String): EventDetailsRes{
        return apiRequest { api.getEventDetails(event_id) }
    }

    suspend fun changeBookingStatus(event_booking_id: String,status: String): SingleRes{
        return apiRequest { api.changeBookingStatus(event_booking_id, status) }
    }


    suspend fun getTimeZoneResponse():TimeZoneResponse{
        return apiRequest { api.getAllTimezones() }
    }

    suspend fun getGoingUsers(page: Int,perPage: Int,eventId: String): GoingUsersRes{
        return apiRequest { api.getGoingUsers(page, perPage, eventId) }
    }

    suspend fun bookEvents(user_id: String,type: String,spine_event_id: String,message: String,amount: String): SingleRes{
        return apiRequest { api.bookEvent( spine_event_id, message, amount) }
    }


    suspend fun deleteAccount(): SingleRes{
        return apiRequest { api.deleteAccount() }
    }

    suspend fun saveStory(user_id:String,story_id:String): SingleRes{
        return apiRequest { api.saveStory(user_id, story_id) }
    }
    suspend fun getAllSavedEvents(page: Int,per_page: Int,user_id: String): EventsRes{
        return apiRequest { api.getAllSavedEvents(page, per_page, user_id) }
    }

    suspend fun sendEventMessage(event_id: String,event_user_id:String,second_user_id: String,message: String,type: String):SingleRes{
        return apiRequest { api.sendEventMessage(event_id, event_user_id, second_user_id, message, type) }
    }

    suspend fun getOwnEvents(): OwnEventsRes{
        return apiRequest { api.getOwnEvents() }
    }

    suspend fun getFilteredEventList(page: String,per_page: String,user_id: String,lat: String,lon: String,distance: String,start_date: String,end_date:String,category: String): EventsRes{
        return  apiRequest { api.getFilteredEvents(page, per_page,  lat, lon, distance, start_date, end_date,category) }
    }

    suspend fun getNearbyEvents(page:Int,per_page:Int,your_user_id:String,latitude:Double,longitude:Double,distance:Int):EventsRes{
        return apiRequest { api.getNearbyEvents( latitude, longitude, distance) }
    }

    suspend fun getOnLineEventsList(page: Int,per_page: Int,user_id: String):EventsRes{
        return apiRequest { api.getOnlineEventsList(page,per_page,user_id) }
    }

    suspend fun getSpineEventReplys(your_events_comment_id: String):EventCommentRes{
        return apiRequest { api.getSpineEventReplys(your_events_comment_id) }
    }

    suspend fun getAllEvents(page:Int,perpage: Int,type:String,typeID:String):EventsRes{
        return apiRequest { api.getAllEventsList(page, perpage, type,typeID) }
    }

    suspend fun removeSpineEvent(your_event_id: String):SingleRes{
        return apiRequest { api.removeSpineEvent(your_event_id) }
    }

    suspend fun removeEventSave(your_user_id:String,your_spine_event_id:String):SingleRes{
        return apiRequest { api.removeEventSave(your_user_id, your_spine_event_id) }
    }

    suspend fun getSpineEventsComment(your_events_id: String):EventCommentRes{
        return apiRequest { api.getSpineEventComments(your_events_id) }
    }

    suspend fun spineEventsComment(spine_event_id: String,user_id:String,comment_id:String,comment:String):SingleRes{
        return apiRequest { api.spineEventsComment(spine_event_id, user_id, comment_id, comment) }
    }

    suspend fun spineEventShare(data: Map<String, String>):SingleRes{
        return apiRequest { api.shareSpineEvents(data) }
    }

    suspend fun saveEvents(your_user_id: String,your_spine_event_id:String):SingleRes{
        return apiRequest { api.saveEvents(your_user_id, your_spine_event_id) }
    }

    suspend fun getFollowingUsersEventsList(page: Int,per_page: Int,your_user_id: String):EventsRes{
        return apiRequest { api.getFollowingUsersEventList(page, per_page, your_user_id) }
    }

    suspend fun getEventType():EventTypeRes{
        return apiRequest { api.getEventType() }
    }

    suspend fun getEventCatRes(searchText:String):EventCatRes{
        return apiRequest { api.getSpinEventCategories() }
    }

    suspend fun getUserDetails():ProfileRes{
        return apiRequest { api.getUserDetails() }
    }

    suspend fun addUserFollow(user_id: String,follow_user_id: String):SingleRes{
        return apiRequest { api.addUserFollow(user_id, follow_user_id) }
    }

    suspend fun sharePost(data: Map<String,String>):SingleRes{
        return apiRequest { api.sharePost(data) }
    }

    suspend fun postComment(spine_post_id: String,user_id:String,comment_id:String,comment:String):SingleRes{
        return apiRequest { api.postComment(spine_post_id, user_id, comment_id, comment) }
    }

    suspend fun getFollowers(page:Int,per_page:Int,user_id:String):FollowersRes{
        return apiRequest { api.getFollowersList(page, per_page, user_id) }
    }

    suspend fun getComments(your_page_id: Int,your_per_page_no: Int,your_spine_id:String): CommentResponse {
        return apiRequest { api.getComments(your_page_id,your_per_page_no,your_spine_id) }
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
        return apiRequest { api.addUserEvents(status, type, allow_comments, title, description, start_time, start_date, end_time, end_date, timezone, location, link_of_event, join_event_link,event_categories, fee, fee_currency, max_attendees, language, accept_participants, multiple,latitude,longitude,booking_url, event_subcategories,files) }
    }

    fun getUser() =db.getUserDao().getUser()

}