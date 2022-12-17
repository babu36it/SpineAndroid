package com.wiesoftware.spine.data.net

import android.util.Log
import com.google.gson.GsonBuilder
import com.wiesoftware.spine.data.net.reponses.*
import com.wiesoftware.spine.ui.home.menus.events.TimeZoneResponse
import com.wiesoftware.spine.util.Prefs
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface EventApi {

    companion object {
        private const val BASE_LINK = "http://thespiritualnetwork.com/api/v1/"
        private const val NewBASE_LINK = "http://162.214.165.52/~pirituc5/"

        private const val BASE_URL = BASE_LINK

        operator fun invoke(): EventApi {
            val gson = GsonBuilder()
                .setLenient()
                .create()

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client: OkHttpClient = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor(Interceptor { chain ->
                    val request: Request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + Prefs.getString("AuthToken", ""))
                        .build()
                    Log.e("AuthorizationToken", "=" + "Bearer " + Prefs.getString("AuthToken", ""))
                    chain.proceed(request)
                }).build()

            return Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(EventApi::class.java)
        }
    }

    @Multipart
    @POST("events/publishEvent")
    suspend fun addUserEvents(
        @Part("status") status: Int,
        @Part("type") type: RequestBody,
        @Part("allow_comments") allow_comments: Int,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("start_time") start_time: RequestBody,
        @Part("start_date") start_date: RequestBody,
        @Part("end_time") end_time: RequestBody,
        @Part("end_date") end_date: RequestBody,
        @Part("timezone") timezone: RequestBody,
        @Part("location") location: RequestBody,
        @Part("link_of_event") link_of_event: RequestBody,
        @Part("join_event_link") join_event_link: RequestBody,
        @Part("event_categories") event_categories: RequestBody,
        @Part("fee") fee: Int,
        @Part("fee_currency") fee_currency: RequestBody,
        @Part("max_attendees") max_attendees: RequestBody,
        @Part("language") language: Int,
        @Part("accept_participants") accept_participants: Int,
        @Part("multiple") multiple: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("booking_url") booking_url: RequestBody,
        @Part("event_subcategories") event_subcategories: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Response<SingleRes>

    @Multipart
    @POST("post/addFeaturedAds")
    suspend fun addEventFeaturedAds(
        @Part file: MultipartBody.Part,
        @Part("user_id") user_id: RequestBody,
        @Part("duration") duration: RequestBody,
        @Part("timeslot_date") timeslot_date: RequestBody,
        @Part("timeslot_time") timeslot_time: RequestBody,
        @Part("ad_type") ad_type: RequestBody,
        @Part("file_type") file_type: RequestBody,
        @Part("website") website: RequestBody,
        @Part("promote_your_ad") promote_your_ad: RequestBody,
        @Part("payment_details") payment_details: RequestBody,
        @Part("pay_by") pay_by: RequestBody,

        @Part("event_title") event_title: RequestBody,
        @Part("event_type") event_type: RequestBody,
        @Part("event_start_date") event_start_date: RequestBody,
        @Part("event_start_time") event_start_time: RequestBody,
        @Part("event_end_time") event_end_time: RequestBody,
        @Part("event_end_date") event_end_date: RequestBody,
        @Part("event_timezone") event_timezone: RequestBody,
        @Part("event_location") event_location: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody
    ): Response<SingleRes>



    @GET("events/getUserEvents/")
    suspend fun getOwnEvents(
    ): Response<OwnEventsRes>

    @FormUrlEncoded
    @POST("events/getAllEvents")
    suspend fun getAllEventsList(
        @Field("page") page: Int,
        @Field("per_page") perpage: Int,
        @Field("type") type: String,
        @Field("event_type_id") eventtypeid: String
    ): Response<EventsRes>

    @FormUrlEncoded
    @POST("events/spineEventsComment")
    suspend fun spineEventsComment(
        @Field("spine_event_id") spine_event_id: String,
        @Field("user_id") user_id: String,
        @Field("comment_id") comment_id: String,
        @Field("comment") comment: String
    ): Response<SingleRes>

    @GET("events/getSpineEventsReplys/{EventCommentId}")
    suspend fun getSpineEventReplys(
        @Path("EventCommentId") EventCommentId: String
    ): Response<EventCommentRes>

    @GET("events/getSpineEventsComment/{EventId}")
    suspend fun getSpineEventComments(
        @Path(value = "EventId") your_events_id: String
    ): Response<EventCommentRes>

    @GET("events/eventSave/{UserId}/{EventId}")
    suspend fun saveEvents(
        @Path(value = "UserId") UserId: String,
        @Path(value = "EventId") EventId: String
    ): Response<SingleRes>

    @FormUrlEncoded
    @POST("events/getEventsListFilter")
    suspend fun getFilteredEvents(
        @Field("page") page: String,
        @Field("per_page") per_page: String,
        @Field("lat") lat: String,
        @Field("lon") lon: String,
        @Field("distance") distance: String,
        @Field("start_date") start_date: String,
        @Field("end_date") end_date: String,
        @Field("category") category: String
    ): Response<EventsRes>

    @FormUrlEncoded
    @POST("events/addEventBooking/{EventId}")
    suspend fun bookEvent(
        @Path("EventId") type: String,
        @Field("message") message: String,
        @Field("amount") amount: String
    ): Response<SingleRes>

    @GET("events/getEventBookingRequestList/{Page}/{PerPage}")
    suspend fun getEventRequestUserList(
        @Path("Page") page: Int,
        @Path("PerPage") perPage: Int,
        ): Response<EventRequestResponse>


    @GET("events/changeEventBookingStatus/{BookingId}/{Status}")
    suspend fun changeBookingStatus(
        @Path("BookingId") BookingId: String,
        @Path("Status") Status: String
    ): Response<SingleRes>

    @GET("events/getEventDetail/{EventId}")
    suspend fun getEventDetails(
        @Path("EventId") event_id: String
    ): Response<EventDetailsRes>

    @GET("events/getDistanceUsersEventsList/{Latitude}/{Longitude}/{Distance}")
    suspend fun getNearbyEvents(
        @Path("Latitude") latitude: Double,
        @Path("Longitude") longitude: Double,
        @Path("Distance") distance: Int
    ): Response<EventsRes>

    @FormUrlEncoded
    @POST("events/setSpineEventsMessage")
    suspend fun sendEventMessage(
        @Field("event_id") event_id: String,
        @Field("event_user_id") event_user_id: String,
        @Field("second_user_id") second_user_id: String,
        @Field("message") message: String,
        @Field("type") type: String
    ): Response<SingleRes>


    @GET("events/removeEventSave/{UserId}/{EventId}")
    suspend fun removeEventSave(
        @Path("UserId") UserId: String,
        @Path("EventId") EventId: String
    ): Response<SingleRes>


    @FormUrlEncoded
    @POST("events/spineEventsShare")
    suspend fun shareSpineEvents(
        @FieldMap data: Map<String, String>
    ): Response<SingleRes>

    @GET("events/getEventTypes")
    suspend fun getEventType(
    ): Response<EventTypeRes>

    @GET("events/getEventsCategory")
    suspend fun getEventCategories(
        @Query("searchText") searchText: String
    ):Response<EventCatRes>

    @FormUrlEncoded
    @POST("podcasts/addPodcastsSubcategory")
    suspend fun addPodcastSubcategory(
        @Field("parent_id") parent_id: String,
        @Field("subcategory_name") subcategory_name: String,
    ): Response<SingleRes>

    @FormUrlEncoded
    @POST("podcasts/getPodcastsSubcategoryByIds")
    suspend fun getPodcastSubcategory(
        @Field("categoryIds") categoryIds: String,
    ): Response<PodcastSubCategoryRes>

    @GET("events/getUserGoingEventsList/{page}/{perPage}/{userId}/{goingPast}")//0 for going
    suspend fun getGoingPastEventsList(
        @Path("page") page: Int,
        @Path("perPage") perPage: Int,
        @Path("userId") userId: String,
        @Path("goingPast") goingPast: Int
    ): Response<EventsRes>

    @FormUrlEncoded
    @POST("spineReportUserPostStory")
    suspend fun spineReportUserPostStory(
        @Field("user_id") user_id: String,
        @Field("spine_report_id") spine_report_id: String,
        @Field("type") type: String,//{1-User, 2-Post, 3-Story, 4- event,5-podcast}
        @Field("report_title") report_title: String,
        @Field("report_issue") report_issue: String,
        @Field("report_msg") report_msg: String
    ): Response<SingleRes>

    @FormUrlEncoded
    @POST("stories/spineStoriesComment")
    suspend fun spineStoriesComment(
        @Field("spine_story_id") spine_story_id: String,
        @Field("user_id") user_id: String,
        @Field("comment_id") comment_id: String,
        @Field("comment") comment: String
    ): Response<SingleRes>


    @GET("timezones")
    suspend fun getAllTimezones(): Response<TimeZoneResponse>


    @GET("events/getEventBookingUserList/{page}/{perPage}/{eventId}")
    suspend fun getGoingUsers(
        @Path("page") page: Int,
        @Path("perPage") perPage: Int,
        @Path("eventId") eventId: String
    ): Response<GoingUsersRes>

    @GET("stories/storySave/{user_id}/{story_id}")
    suspend fun saveStory(
        @Path("user_id") user_id: String,
        @Path("story_id") story_id: String
    ): Response<SingleRes>

    @GET("events/getSpineEventsSave/{page}/{per_page}/{user_id}")
    suspend fun getAllSavedEvents(
        @Path("page") page: Int,
        @Path("per_page") per_page: Int,
        @Path("user_id") user_id: String
    ): Response<EventsRes>

    @GET("events/getFollowingUsersEventsList/{page}/{per_page}/{your_user_id}")
    suspend fun getFollowingUsersEventList(
        @Path(value = "page") page: Int,
        @Path(value = "per_page") per_page: Int,
        @Path(value = "your_user_id") your_user_id: String
    ): Response<EventsRes>

    @GET("follow/getFollowersList/{page}/{per_page}/{user_id}")
    suspend fun getFollowersList(
        @Path(value = "page") page: Int,
        @Path(value = "per_page") per_page: Int,
        @Path(value = "user_id") user_id: String
    ): Response<FollowersRes>

    @GET("events/getOnlineUsersEventsList/{page}/{per_page}/{your_user_id}")
    suspend fun getOnlineEventsList(
        @Path("page") page: Int,
        @Path("per_page") per_page: Int,
        @Path("your_user_id") your_user_id: String
    ): Response<EventsRes>

    @GET("events/getEventsCategory")
    suspend fun getSpinEventCategories(
    ): Response<EventCatRes>


}