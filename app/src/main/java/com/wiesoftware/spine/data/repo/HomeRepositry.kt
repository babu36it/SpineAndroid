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
 * Created by Vivek kumar on 8/13/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class HomeRepositry(
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

    suspend fun getAdDuration(user_id: String):AdDurationRes{
        return apiRequest { api.getAdDuration(user_id) }
    }

    suspend fun addRssfeedPodcast(user_id:String,title:String,description:String,language:String,category: String,subcategory:String,allow_comment: String,media_file:String,rss_feed:String):SingleRes{
        return apiRequest { api.addRssfeedPodcast(user_id, title, description, language, category, subcategory, allow_comment, media_file, rss_feed) }
    }

    suspend fun addPodcastSubcategory(parent_id: String,subcategory_name:String,user_id:String):SingleRes{
        return apiRequest { api.addPodcastSubcategory(parent_id, subcategory_name,user_id) }
    }
    suspend fun getPodcastSubcategory(parentId:String,user_id: String):PodcastSubCategoryRes{
        return apiRequest { api.getPodcastSubcategory(parentId,user_id) }
    }

    suspend fun sendVerificationCodeOnEmail(email: String):EmailVerificationRes{
        return apiRequest { api.sendVerificationCodeOnEmail(email) }
    }

    suspend fun verifyEmailVerificationCode(email: String,otp:String):EmailVerificationRes{
        return apiRequest { api.verifyEmailVerificationCode(email,otp) }
    }

    suspend fun sharePodcasts(data: Map<String,String>):SingleRes{
        return apiRequest { api.sharePodcasts(data) }
    }

    suspend fun getUserHashtags(): HashtagRes{
        return apiRequest { api.getUserHashtags() }
    }

    suspend fun getGoingPastEventsList(page:Int,perPage:Int,userId:String,goingPast:Int):EventsRes{
        return apiRequest { api.getGoingPastEventsList(page, perPage, userId, goingPast) }
    }

    suspend fun getStoryListByMonthWithYear(userId:String,monthId:String,year:String):FollowingStoriesRes{
        return apiRequest { api.getStoryListByMonthWithYear(userId, monthId, year) }
    }

    suspend fun getStoriesMonthYear(userId: String): StoryMonthRes{
        return apiRequest { api.getStoriesMonthYear(userId) }
    }

    suspend fun removeStoryCommentReply(story_comment_or_reply_id:String):SingleRes{
        return apiRequest { api.removeStoryCommentReply(story_comment_or_reply_id) }
    }

    suspend fun storyCommentReplyList(comment_id: String):StoriesCommentRes{
        return  apiRequest { api.storyCommentReplyList(comment_id) }
    }

    suspend fun spineReportUserPostStory(user_id: String,spine_report_id: String,type: String,report_title: String,report_issue: String,report_msg: String): SingleRes{
        return apiRequest { api.spineReportUserPostStory(user_id, spine_report_id, type, report_title, report_issue, report_msg) }
    }

    suspend fun storyCommentsList(storyId: String): StoriesCommentRes{
        return apiRequest { api.storyCommentsList(storyId) }
    }

    suspend fun spineStoriesComment(spine_story_id: String,user_id:String,comment_id:String,comment:String):SingleRes{
        return apiRequest { api.spineStoriesComment(spine_story_id, user_id, comment_id, comment) }
    }

    suspend fun spineStoriesShare(data:Map<String,String>):SingleRes{
        return apiRequest { api.spineStoriesShare(data) }
    }

    suspend fun spineStoriesLike(story_id:String,user_id:String):SingleRes{
        return apiRequest { api.spineStoriesLike(story_id, user_id) }
    }

    suspend fun removeCommentOnImpluse(comment_or_reply_id:String):SingleRes{
        return apiRequest { api.removeCommentOnImpluse(comment_or_reply_id) }
    }

    suspend fun spinePostCommentRemove(post_comment_id: String):SingleRes{
        return apiRequest { api.spinePostCommentRemove(post_comment_id) }
    }

    suspend fun recommendedFollowersListByCategories(page:Int,per_page:Int,your_users_id:String):AllUsersRes{
        return apiRequest { api.recommendedFollowersListByCategories(page, per_page, your_users_id) }
    }
    suspend fun getSpinePostReplys(your_post_comment_id: String): CommentReplyRes{
        return apiRequest { api.getSpinePostReplys(your_post_comment_id) }
    }

    suspend fun followUnfollowImpluse(user_id: String,status: Int): SingleRes{
        return apiRequest { api.followUnfollowImpluse(user_id, status) }
    }

    suspend fun deactivateAccount(user_id: String): SingleRes{
        return apiRequest { api.deactivateAccount(user_id) }
    }

    suspend fun deleteSpineAccount(user_id: String): SingleRes{
        return apiRequest { api.deactivateAccount(user_id) }
    }

    suspend fun increasePodcastViews(podcast_id:String): SingleRes{
        return apiRequest { api.increasePodcastViews(podcast_id) }
    }

    suspend fun getPodcastDetails(userId: String,podId: String): PodcastDetailsRes{
        return apiRequest { api.getPodcastDetails(userId, podId) }
    }

    suspend fun getUserPodcast(user_id: String): PodcastRes{
        return apiRequest { api.getUserPodcasts(user_id) }
    }

    suspend fun managePodcastBookmarks(userId: String,podId: String): SingleRes{
        return  apiRequest { api.managePodcastBookmarks(userId, podId) }
    }
    suspend fun managePodcastLikes(userId: String,podId: String): SingleRes{
        return  apiRequest { api.managePodcastLikes(userId, podId) }
    }

    suspend fun getAllPodcasts(user_id: String): PodRes{
        return apiRequest { api.getAllPodcasts(user_id) }
    }

    suspend fun addPodcasts(user_id: RequestBody,type: RequestBody,title: RequestBody,description: RequestBody,language: RequestBody,category: RequestBody,allow_comment: RequestBody,media_file: MultipartBody.Part,thumbnail: MultipartBody.Part): SingleRes{
        return apiRequest { api.addPodcasts(user_id, type, title, description, language, category, allow_comment,media_file, thumbnail) }
    }

    suspend fun getCurrency():CurrencyRes{
        return apiRequest { api.getCurrency() }
    }

    suspend fun getEventDetails(event_id: String,userId: String): EventDetailsRes{
        return apiRequest { api.getEventDetails(event_id) }
    }
    suspend fun saveStatusToCalendarStatus(user_id: String,calender_status: String):SingleRes{
        return apiRequest { api.saveStatusToCalendarStatus(user_id, calender_status) }
    }

    suspend fun whoCanMessage(user_id: String,message_auth: String): SingleRes{
        return apiRequest { api.whoCanMessage(user_id, message_auth) }
    }

    suspend fun requestToChangeEmail(user_id: String,email: String): SingleRes{
        return apiRequest { api.requestToChangeEmail(user_id, email) }
    }

    suspend fun getEventRequestUserList(page: Int,perPage: Int,userId: String): EventRequestResponse{
        return apiRequest { api.getEventRequestUserList(page, perPage) }
    }

    suspend fun changeBookingStatus(event_booking_id: String,status: String): SingleRes{
        return apiRequest { api.changeBookingStatus(event_booking_id, status) }
    }
    suspend fun getPodcastLanguage():LangRes{
        return apiRequest { api.getPodcastLanguage() }
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

    suspend fun getUserNotifications(user_id: String): NotificationsRes{
        return apiRequest { api.getUserNotifications(user_id) }
    }

    suspend fun deleteAccount(user_id:String): SingleRes{
        return apiRequest { api.deleteAccount(user_id) }
    }

    suspend fun changePassword(user_id: String,old_password: String,new_password: String):SingleRes{
        return apiRequest { api.changePassword(user_id, old_password, new_password) }
    }

    suspend fun getAllSavedStory(user_id: String):StoryRes{
        return apiRequest { api.getAllSavedStory(user_id) }
    }
    suspend fun saveStory(user_id:String,story_id:String): SingleRes{
        return apiRequest { api.saveStory(user_id, story_id) }
    }
    suspend fun getAllSavedEvents(page: Int,per_page: Int,user_id: String): EventsRes{
        return apiRequest { api.getAllSavedEvents(page, per_page, user_id) }
    }

    suspend fun getAllSavedPost(user_id: String):PostRes{
        return apiRequest { api.getAllSavedPosts(user_id) }
    }

    suspend fun unFollowUser(user_id: String,unfollow_user_id: String): SingleRes{
        return apiRequest { api.unFollowUser(user_id, unfollow_user_id) }
    }
    suspend fun getFollowingList(page:Int,per_page:Int,user_id: String):FollowersRes{
        return apiRequest { api.getFollowingList(page, per_page, user_id) }
    }

    suspend fun getEventChatMsg(page: Int,per_page: Int,event_user_id: String,second_user_id: String):ChatMsgRes{
        return apiRequest { api.getEventChatMsg(page, per_page, event_user_id, second_user_id) }
    }

    suspend fun getEventMsgUsers(page: Int,per_page: Int,user_id: String):EveMsgUserRes{
        return apiRequest { api.getEventsMsgUsers(page, per_page, user_id) }
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

    suspend fun getSpineComments(post_id:String):SpineCommentRes{
        return apiRequest { api.getSpinePostComments(post_id) }
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

    suspend fun getActivities(page: Int,per_page: Int,user_id: String,followers: Int):ActivitiesRes{
        return apiRequest { api.getActivities(page, per_page, user_id, followers) }
    }

    suspend fun getUserDetails(your_user_id:String):ProfileRes{
        return apiRequest { api.getUserDetails(Prefs.getString("AuthToken", "")!! ) }
    }

    suspend fun getuserDetailsMSGPermision(detailsUser:String,LoginUserId:String):ProfileRes{
        return apiRequest { api.getuserDetailsMSGPermision(detailsUser,LoginUserId) }
    }

    suspend fun updateUserProfilePic(image: MultipartBody.Part?,user_id: RequestBody):SingleRes{
        return apiRequest { api.updateUserProfilePic(image, user_id) }
    }

    suspend fun updateUserBgProfilePic(image: MultipartBody.Part,user_id: RequestBody): SingleRes{
        return apiRequest { api.updateUserBgProfilePic(image, user_id) }
    }

    suspend fun profileEdit(user_id:String,account_type:String,name: String,display_name:String,bio:String,category: String,website:String,contact_email:String,business_phone:String,business_address:String,address:String):SingleRes{
        return apiRequest { api.profileEdit(user_id, account_type, name, display_name, bio, category, website, contact_email, business_phone, business_address, address) }
    }

    suspend fun addUserPost(type: RequestBody,user_id:RequestBody,title: RequestBody,hashtag_ids: RequestBody,post_backround_color_id:RequestBody,multiplity:RequestBody,feature_post: RequestBody):SingleRes{
        return apiRequest { api.addUserPost(type, user_id, title, hashtag_ids, post_backround_color_id, multiplity,feature_post) }
    }
    suspend fun addUserImgVideoPost(feature_post: RequestBody,type: RequestBody,user_id:RequestBody,title: RequestBody,hashtag_ids: RequestBody,post_backround_color_id:RequestBody,multiplity:RequestBody,files: List<MultipartBody.Part>):SingleRes{
        return apiRequest { api.addUserImgVideoPost(feature_post,type, user_id, title, hashtag_ids, post_backround_color_id, multiplity,files) }
    }
    suspend fun getFollowingUsersStoryList(page: Int,per_page: Int,your_user_id: String): FollowingStoriesRes{
        return apiRequest { api.getFollowingUsersStorieList(page, per_page) }
    }

    suspend fun addUserFollow(user_id: String,follow_user_id: String):SingleRes{
        return apiRequest { api.addUserFollow(user_id, follow_user_id) }
    }

    suspend fun onPostSave(your_user_id:String,your_spine_post_id:String):SingleRes{
        return apiRequest { api.onPostSave(your_user_id, your_spine_post_id) }
    }

    suspend fun unlikeImpulse(your_user_id: String,your_spine_impluse_id:String):SingleRes{
        return apiRequest { api.unlikeImpulse(your_user_id, your_spine_impluse_id) }
    }

    suspend fun getAllUsers(page:Int,per_page:Int,your_users_id:String):AllUsersRes{
        return apiRequest { api.getAllUsers(page, per_page, your_users_id) }
    }

    suspend fun sharePost(data: Map<String,String>):SingleRes{
        return apiRequest { api.sharePost(data) }
    }

    suspend fun postComment(spine_post_id: String,user_id:String,comment_id:String,comment:String):SingleRes{
        return apiRequest { api.postComment(spine_post_id, user_id, comment_id, comment) }
    }

    suspend fun likePost(your_post_id: String,user_id: String):SingleRes{
        return apiRequest { api.likePost(your_post_id, user_id) }
    }

    suspend fun getHashtagList():HashtagRes{
        return apiRequest { api.getHashtagList() }
    }

    suspend fun getFollowers(page:Int,per_page:Int,user_id:String):FollowersRes{
        return apiRequest { api.getFollowersList(page, per_page, user_id) }
    }

    suspend fun getYourStories(your_user_id: String):StoryRes{
        return apiRequest { api.getYourStories(your_user_id) }
    }

    suspend fun getAllPosts(page:Int,per_page:Int,your_user_id:String,followers:Int,only_user_post:Int):PostRes{
        return apiRequest { api.getAllPosts(page,per_page, your_user_id,followers,only_user_post) }
    }

    suspend fun postAStory(file: List<MultipartBody.Part>, user_id: RequestBody,title: RequestBody,type: RequestBody,allow_comment: RequestBody,delete_story_after_24_hr: RequestBody):SingleRes{
        return apiRequest { api.addStory(file, user_id, title, type, allow_comment, delete_story_after_24_hr) }
    }

    suspend fun likeSpineImpulse(your_user_id:String,your_spine_impluse_id:String): ResponseBody{
        return apiRequest { api.likeImpulse(your_user_id,your_spine_impluse_id) }
    }

    suspend fun getComments(your_page_id: Int,your_per_page_no: Int,your_spine_id:String): CommentResponse {
        return apiRequest { api.getComments(your_page_id,your_per_page_no,your_spine_id) }
    }
    suspend fun saveImpulseComment(parent_comment_id: String,spine_impluse_id:String,user_id:String,comment:String): ResponseBody{
        return apiRequest { api.saveImpulseComment(parent_comment_id,spine_impluse_id,user_id,comment) }
    }
    suspend fun getWelcomeData():WelcomeResponse{
        return apiRequest { api.getWelcomeData(Prefs.getString("AuthToken", "")!!) }
    }
    suspend fun getSpineImpulse( page_no: Int, page_item_count: Int,user_id:String): SpineImpulseResponse{
        return apiRequest { api.getSpineImpulseData(page_no,page_item_count,user_id) }
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

    suspend fun logoutUser(c_user: User) =db.getUserDao().deleteUser(c_user)

    suspend fun getWelcomePages(): WelcomePageReponse {
        return apiRequest { api.getWelcomePages() }
    }

    suspend fun userBlock(user_id:String, block_user_id:String) : SingleRes {
        return apiRequest { api.blockuser(user_id,block_user_id)
        }
    }
}