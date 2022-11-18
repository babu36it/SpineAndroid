package com.wiesoftware.spine.data.net

import android.util.Log
import com.google.gson.GsonBuilder
import com.wiesoftware.spine.data.net.reponses.*
import com.wiesoftware.spine.data.net.reponses.episode.EpisodeModel
import com.wiesoftware.spine.data.net.reponses.welcompageresponse.WelcomePageReponse
import com.wiesoftware.spine.ui.home.menus.events.TimeZoneResponse
import com.wiesoftware.spine.util.Prefs
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


interface Api {

    companion object {
        private const val HEADER_1 = "X-API-KEY: 123run"
        private const val HEADER_2 = "Authorization: Basic ZGV2cGFua2FqOmRldnBhbmthag=="
        private const val HEADER_3 = "Content-Type: application/json"
        const val BASE_LINK = "http://thespiritualnetwork.com/api/v1/"
        const val NewBASE_LINK = "http://162.214.165.52/~pirituc5/"

        //        const val BASE_LINK="http://162.214.165.52/~pirituc5/apisecure/"
//        const val BASE_URL=BASE_LINK+"apisecure/"
        const val BASE_URL = BASE_LINK
        const val BASE_URL_VIDEO = BASE_LINK + "assets/upload/welcome/"

        const val ABOUT_SPINE = NewBASE_LINK + "about-us"
        const val HELP_SPINE = NewBASE_LINK + "spine-help"
        const val GUIDELINE_SPINE = NewBASE_LINK + "spine-guidelines"
        const val TERMS_SPINE = NewBASE_LINK + "spine-terms"
        const val PRIVACY_SPINE = NewBASE_LINK + "spine-privacy"

        operator fun invoke(
            networkConnectionInterceptor: NetworkConnectionInterceptor
        ): Api {
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
                .create(Api::class.java)
        }
    }

    //http://162.214.165.52/~pirituc5/apisecure/post/addFeaturedAds
    @Multipart
    @POST("post/getAdsListViaStatus")
    suspend fun getMyAdList(): Response<MyAdRes>


    @Multipart
    @POST("post/addFeaturedAds")
    suspend fun addFeaturedAds(
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
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody
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


    @FormUrlEncoded
    @POST("post/getAdsDuration")
    suspend fun getAdDuration(
        @Field("user_id") user_id: String
    ): Response<AdDurationRes>


    @FormUrlEncoded
    @POST("podcasts/addPodcasts")
    suspend fun addRssfeedPodcast(
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("language") language: String,
        @Field("category") category: String,
        @Field("subcategory") subcategory: String,
        @Field("allow_comment") allow_comment: String,
        @Field("rss_feed") rss_feed: String,
        @Field("media_file") media_file: String,
        @Field("thumbnail") thumbnail: String,
    ): Response<SingleRes>

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


    @FormUrlEncoded
    @POST("podcasts/sendEmailOTP")
    suspend fun sendVerificationCodeOnEmail(
        @Field("email") email: String
    ): Response<EmailVerificationRes>

    @FormUrlEncoded
    @POST("podcasts/sendEmailOTPVerification")
    suspend fun verifyEmailVerificationCode(
        @Field("email") email: String,
        @Field("otp") otp: String
    ): Response<EmailVerificationRes>


    @FormUrlEncoded
    @POST("podcasts/podcastShare")
    suspend fun sharePodcasts(
        @FieldMap data: Map<String, String>
    ): Response<SingleRes>


    @GET("post/getUserHashtags")
    suspend fun getUserHashtags(): Response<HashtagRes>

    @GET("events/getUserGoingEventsList/{page}/{perPage}/{userId}/{goingPast}")//0 for going
    suspend fun getGoingPastEventsList(
        @Path("page") page: Int,
        @Path("perPage") perPage: Int,
        @Path("userId") userId: String,
        @Path("goingPast") goingPast: Int
    ): Response<EventsRes>

    @GET("stories/getStoryListByMonthWithYear/{userId}/{monthId}/{year}")
    suspend fun getStoryListByMonthWithYear(
        @Path("userId") userId: String,
        @Path("monthId") monthId: String,
        @Path("year") year: String
    ): Response<FollowingStoriesRes>


    @GET("stories/storyMonthWithYear/{userId}")
    suspend fun getStoriesMonthYear(
        @Path("userId") userId: String
    ): Response<StoryMonthRes>


    @GET("stories/removeStoriesCommentReply/{story_comment_or_reply_id}")
    suspend fun removeStoryCommentReply(
        @Path("story_comment_or_reply_id") story_comment_or_reply_id: String
    ): Response<SingleRes>


    @GET("stories/storyCommentReplyList/{story_comment_id}")
    suspend fun storyCommentReplyList(
        @Path("story_comment_id") story_comment_id: String
    ): Response<StoriesCommentRes>


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


    @GET("stories/storyCommentsList/{storyId}")
    suspend fun storyCommentsList(
        @Path("storyId") storyId: String
    ): Response<StoriesCommentRes>

    @FormUrlEncoded
    @POST("stories/spineStoriesComment")
    suspend fun spineStoriesComment(
        @Field("spine_story_id") spine_story_id: String,
        @Field("user_id") user_id: String,
        @Field("comment_id") comment_id: String,
        @Field("comment") comment: String
    ): Response<SingleRes>

    @FormUrlEncoded
    @POST("stories/spineStoriesShare")
    suspend fun spineStoriesShare(
        @FieldMap data: Map<String, String>
    ): Response<SingleRes>

    @GET("stories/spineStoriesLike/{story_id}/{user_id}")
    suspend fun spineStoriesLike(
        @Path("story_id") story_id: String,
        @Path("user_id") user_id: String
    ): Response<SingleRes>

    @GET("impluse/removeCommentOnImpluse/{comment_or_reply_id}")
    suspend fun removeCommentOnImpluse(
        @Path("comment_or_reply_id") comment_or_reply_id: String
    ): Response<SingleRes>

    @GET("post/spinePostCommentRemove/{post_comment_id}")
    suspend fun spinePostCommentRemove(
        @Path("post_comment_id") post_comment_id: String
    ): Response<SingleRes>

    @GET("follow/recommendedFollowersListByCategories/{page}/{per_page}/{your_users_id}")
    suspend fun recommendedFollowersListByCategories(
        @Path("page") page: Int,
        @Path("per_page") per_page: Int,
        @Path("your_users_id") your_users_id: String
    ): Response<AllUsersRes>

    @GET("post/getSpinePostReplys/{your_post_comment_id}")
    suspend fun getSpinePostReplys(
        @Path("your_post_comment_id") your_post_comment_id: String
    ): Response<CommentReplyRes>

    @GET("impluse/followUnfollowImpluse/{user_id}/{status}")
    suspend fun followUnfollowImpluse(
        @Path("user_id") user_id: String,
        @Path("status") status: Int
    ): Response<SingleRes>

    @GET("podcasts/podcastsView/{podcast_id}")
    suspend fun increasePodcastViews(
        @Path("podcast_id") podcast_id: String
    ): Response<SingleRes>

    @GET("podcasts/gePodcastDetail/{userId}/{podId}")
    suspend fun getPodcastDetails(
        @Path("userId") userId: String,
        @Path("podId") podId: String
    ): Response<PodcastDetailsRes>

    @GET("podcasts/manageBookmark/{userId}/{podId}")
    suspend fun managePodcastBookmarks(
        @Path("userId") userId: String,
        @Path("podId") podId: String
    ): Response<SingleRes>

    @GET("podcasts/manageLikeUnlike/{userId}/{podId}")
    suspend fun managePodcastLikes(
        @Path("userId") userId: String,
        @Path("podId") podId: String
    ): Response<SingleRes>


    @GET("podcasts/getPodcastsCustom}")
    suspend fun getAllPodcasts(): Response<PodRes>

    @GET("podcasts/getPodcastsEpisodeCustom")
    suspend fun getAllEpisodeCustom(): Response<EpisodeModel>


    @GET("podcasts/getUserPodcasts/{user_id}")
    suspend fun getUserPodcasts(
        @Path("user_id") user_id: String
    ): Response<PodcastRes>

    @Multipart
    @POST("podcasts/addPodcasts")
    suspend fun addPodcasts(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("language") language: RequestBody,
        @Part("category") category: RequestBody,
        @Part("subcategory") subcategory: RequestBody,
        @Part("rss_feed") rss_feed: RequestBody,
        @Part("allow_comment") allow_comment: RequestBody,
        @Part("media_file") media_file: RequestBody,
        @Part("thumbnail") thumbnail: RequestBody
    ): Response<SingleRes>


    @GET("deactiveAccount")
    suspend fun deactivateAccount(): Response<SingleRes>


    @GET("deleteSpineAccount/{user_id}")
    suspend fun deleteSpineAccount(
        @Path("user_id") user_id: String
    ): Response<SingleRes>


//    //@Headers(HEADER_1, HEADER_2)
//    @GET("eventCurrency")
//    suspend fun getCurrency():Response<CurrencyRes>

//    Harsh: chnage for new api
    //@Headers(HEADER_1, HEADER_2)

    @GET("currency")
    suspend fun getCurrency(): Response<CurrencyRes>

    @GET("events/getEventDetail/{EventId}")
    suspend fun getEventDetails(
        @Path("EventId") event_id: String
    ): Response<EventDetailsRes>

    @FormUrlEncoded
    @POST("saveEventToCalender")
    suspend fun saveStatusToCalendarStatus(
        @Field("calender_status") calender_status: String
    ): Response<SingleRes>

    @FormUrlEncoded
    @POST("eventMessagingAutho")
    suspend fun whoCanMessage(
        @Field("message_auth") message_auth: String
    ): Response<SingleRes>

    @FormUrlEncoded
    @POST("requestToChangeEmail")
    suspend fun requestToChangeEmail(
        @Field("email") email: String
    ): Response<SingleRes>

    @GET("events/getEventBookings/{page}/{perPage}/{userId}")
    suspend fun getEventRequestUserList(
        @Path("page") page: Int,
        @Path("perPage") perPage: Int,
        @Path("userId") userId: String
    ): Response<EventRequestResponse>

    @GET("events/changeEventBookingStatus/{event_booking_id}/{status}")
    suspend fun changeBookingStatus(
        @Path("event_booking_id") event_booking_id: String,
        @Path("status") status: String
    ): Response<SingleRes>

//    //@Headers(HEADER_1, HEADER_2)
//    @GET("podcasts/getPodcastsLanguage")
//    suspend fun getPodcastLanguage():Response<LangRes>

//    < Harsh: Changed Endpoint of time zone api: 16/10/22>
    //@Headers(HEADER_1, TestToken)

    @GET("languages")
    suspend fun getPodcastLanguage(): Response<LangRes>

//    //@Headers(HEADER_1, HEADER_2)
//    @GET("getAllTimezones")
//    suspend fun getAllTimezones():Response<TimeZoneResponse>

    //    < Harsh: Changed Endpoint of time zone api: 16/10/22>
    //@Headers(HEADER_1, TestToken)
    @GET("timezones")
    suspend fun getAllTimezones(): Response<TimeZoneResponse>

    @GET("events/getEventBookingUserList/{page}/{perPage}/{eventId}")
    suspend fun getGoingUsers(
        @Path("page") page: Int,
        @Path("perPage") perPage: Int,
        @Path("eventId") eventId: String
    ): Response<GoingUsersRes>

    @FormUrlEncoded
    @POST("events/addEventBooking/{EventId}")
    suspend fun bookEvent(
        @Path("EventId") type: String,
        @Field("message") message: String,
        @Field("amount") amount: String
    ): Response<SingleRes>

    @FormUrlEncoded
    @POST("userNotification")
    suspend fun getUserNotifications(
        @Field("user_id") user_id: String
    ): Response<NotificationsRes>


    @DELETE("deleteAccount")
    suspend fun deleteAccount(): Response<SingleRes>

    @FormUrlEncoded
    @POST("changePassword")
    suspend fun changePassword(
        @Field("user_id") user_id: String,
        @Field("old_password") old_password: String,
        @Field("new_password") new_password: String
    ): Response<SingleRes>

    @GET("stories/getSpineStorySave/{user_id}")
    suspend fun getAllSavedStory(
        @Path("user_id") user_id: String
    ): Response<StoryRes>

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

    @GET("post/getSpinePostSave/{user_id}")
    suspend fun getAllSavedPosts(
        @Path("user_id") user_id: String
    ): Response<PostRes>

    @GET("follow/removeSpineUserFollowing/{user_id}/{unfollow_user_id}")
    suspend fun unFollowUser(
        @Path("user_id") user_id: String,
        @Path("unfollow_user_id") unfollow_user_id: String
    ): Response<SingleRes>

    @GET("follow/getFollowingList/{page}/{per_page}/{user_id}")
    suspend fun getFollowingList(
        @Path("page") page: Int,
        @Path("per_page") per_page: Int,
        @Path("user_id") user_id: String
    ): Response<FollowersRes>

    @GET("events/getSpineEventsMessage/{page}/{per_page}/{event_user_id}/{second_user_id}")
    suspend fun getEventChatMsg(
        @Path("page") page: Int,
        @Path("per_page") per_page: Int,
        @Path("event_user_id") event_user_id: String,
        @Path("second_user_id") second_user_id: String
    ): Response<ChatMsgRes>

    @GET("events/getSpineEventsMessageUsers/{page}/{per_page}/{user_id}")
    suspend fun getEventsMsgUsers(
        @Path("page") page: Int,
        @Path("per_page") per_page: Int,
        @Path("user_id") user_id: String
    ): Response<EveMsgUserRes>

    @FormUrlEncoded
    @POST("events/setSpineEventsMessage")
    suspend fun sendEventMessage(
        @Field("event_id") event_id: String,
        @Field("event_user_id") event_user_id: String,
        @Field("second_user_id") second_user_id: String,
        @Field("message") message: String,
        @Field("type") type: String
    ): Response<SingleRes>

//    //@Headers(HEADER_1, HEADER_2)
//    @GET("events/getOwnEventsList/{userID}")
//    suspend fun getOwnEvents(
//        @Path(value = "userID") userID: String
//    ):Response<OwnEventsRes>

    //    Harsh: changed endporint for new api:17-10-22
    //@Headers(HEADER_1, TestToken)
    @GET("events/getUserEvents/")
    suspend fun getOwnEvents(

    ): Response<OwnEventsRes>

    @FormUrlEncoded
    @POST("events/getEventsListFilter")
    suspend fun getFilteredEvents(
        @Field("page") page: Int,
        @Field("per_page") per_page: Int,
        @Field("user_id") user_id: String,
        @Field("lat") lat: String,
        @Field("lon") lon: String,
        @Field("distance") distance: Int,
        @Field("start_date") start_date: String,
        @Field("end_date") end_date: String,
        @Field("category") category: String
    ): Response<EventsRes>

    @GET("events/getDistanceUsersEventsList/{latitude}/{longitude}/{distance}")
    suspend fun getNearbyEvents(
//        @Path("page") page: Int,
//        @Path("per_page") per_page: Int,
//        @Path("your_user_id") your_user_id: String,
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double,
        @Path("distance") distance: Int
    ): Response<EventsRes>

    @GET("events/getOnlineUsersEventsList/{page}/{per_page}/{your_user_id}")
    suspend fun getOnlineEventsList(
        @Path("page") page: Int,
        @Path("per_page") per_page: Int,
        @Path("your_user_id") your_user_id: String
    ): Response<EventsRes>

    @GET("events/getSpineEventsReplys/{your_events_comment_id}")
    suspend fun getSpineEventReplys(
        @Path("your_events_comment_id") your_events_comment_id: String
    ): Response<EventCommentRes>

    @GET("events/getUsersEventsList/{page}/{perpage}/{user_id}/{type}")
    suspend fun getAllUserList(
        @Path("page") page: Int,
        @Path("perpage") perpage: Int,
        @Path("user_id") user_id: String,
        @Path("type") type: String
    ): Response<EventsRes>

    @FormUrlEncoded
    @POST("events/getAllEvents")
    suspend fun getAllEventsList(
        @Field("page") page: Int,
        @Field("per_page") perpage: Int,
        @Field("type") type: String,
        @Field("event_type_id") eventtypeid: String
    ): Response<EventsRes>

    @GET("events/removeSpineEvents/{your_event_id}")
    suspend fun removeSpineEvent(
        @Path("your_event_id") your_event_id: String
    ): Response<SingleRes>

    @GET("events/removeEventSave/{your_user_id}/{your_spine_event_id}")
    suspend fun removeEventSave(
        @Path("your_user_id") your_user_id: String,
        @Path("your_spine_event_id") your_spine_event_id: String
    ): Response<SingleRes>

    @GET("events/getSpineEventsComment/{your_events_id}")
    suspend fun getSpineEventComments(
        @Path(value = "your_events_id") your_events_id: String
    ): Response<EventCommentRes>

    @FormUrlEncoded
    @POST("events/spineEventsComment")
    suspend fun spineEventsComment(
        @Field("spine_event_id") spine_event_id: String,
        @Field("user_id") user_id: String,
        @Field("comment_id") comment_id: String,
        @Field("comment") comment: String
    ): Response<SingleRes>


    @FormUrlEncoded
    @POST("events/spineEventsShare")
    suspend fun shareSpineEvents(
        @FieldMap data: Map<String, String>
    ): Response<SingleRes>

    @GET("post/getSpinePostComment/{post_id}")
    suspend fun getSpinePostComments(
        @Path(value = "post_id") post_id: String
    ): Response<SpineCommentRes>

    @GET("events/getEventsCategory")
    suspend fun getEventCategories(
        @Query("searchText") searchText: String
    ): Response<EventCatRes>

    @GET("events/getEventsCategory")
    suspend fun getSpinEventCategories(
    ): Response<EventCatRes>


    @GET("podcasts/getPodcastsCategory")
    suspend fun getProdcastCategories(
        @Query("searchText") searchText: String
    ): Response<EventCatRes>


    @GET("post/getSpineUserActivityList/{page}/{per_page}/{user_id}/{followers}")
    suspend fun getActivities(
        @Path(value = "page") page: Int,
        @Path(value = "per_page") per_page: Int,
        @Path(value = "user_id") user_id: String,
        @Path(value = "followers") followers: Int
    ): Response<ActivitiesRes>

    @GET("userDetails")
    suspend fun getUserDetails(
    ): Response<ProfileRes>

    @GET("userDetails")
    suspend fun getUserDetailAuth(
    ): Response<SignupResponse>

    @GET("userDetailsMSGPermision/{detailsUser}/{LoginUserId}")
    suspend fun getuserDetailsMSGPermision(
        @Path(value = "detailsUser") detailsUser: String,
        @Path(value = "LoginUserId") LoginUserId: String
    ): Response<ProfileRes>

    @Multipart
    @POST("profile/userProfilePic")
    suspend fun updateUserProfilePic(
        @Part image: MultipartBody.Part?
    ): Response<SingleRes>

    @Multipart
    @POST("profile/userBgProfilePic")
    suspend fun updateUserBgProfilePic(
        @Part image: MultipartBody.Part,
    ): Response<SingleRes>

    @FormUrlEncoded
    @POST("profile/profileEdit")
    suspend fun profileEdit(
        @Field("account_type") account_type: String,
        @Field("name") name: String,
        @Field("display_name") display_name: String,
        @Field("bio") bio: String,
        @Field("category") category: String,
        @Field("website") website: String,
        @Field("contact_email") contact_email: String,
        @Field("business_phone") business_phone: String,
        @Field("business_address") business_address: String,
        @Field("address") address: String
    ): Response<SingleRes>

    @FormUrlEncoded
    @POST("profile/profileEdit")
    suspend fun editProfile(
        @Field("account_type") account_type: String,
        @Field("listing_type") listing_type: String,
        @Field("name") name: String,
        @Field("display_name") display_name: String,
        @Field("bio") bio: String,
        @Field("category") category: String,
        @Field("interested") interested: String,
        @Field("offer_desciption") offer_desciption: String,
        @Field("key_perfomance") key_perfomance: String,
        @Field("desease_pattern") desease_pattern: String,
        @Field("languages") languages: String,
        @Field("qualification") qualification: String,
        @Field("company_name") company_name: String,
        @Field("street_1") street_1: String,
        @Field("street_2") street_2: String,
        @Field("street_3") street_3: String,
        @Field("city") city: String,
        @Field("postcode") postcode: String,
        @Field("country") country: String,
        @Field("address") address: String,
        @Field("metaverse_address") metaverse_address: String,
        @Field("website") website: String,
        @Field("contact_email") contact_email: String,
        @Field("business_phone") business_phone: String,
        @Field("business_mobile") business_mobile: String,
        @Field("business_phone_code")business_phone_code:String,
        @Field("business_mobile_code")business_mobile_code:String

        ): Response<SingleRes>


    @Multipart
    @POST("post/addUserPost")
    suspend fun addUserPost(
        @Part("type") type: RequestBody,
        @Part("user_id") user_id: RequestBody,
        @Part("title") title: RequestBody,
        @Part("hashtag_ids") hashtag_ids: RequestBody,
        @Part("post_backround_color_id") post_backround_color_id: RequestBody,
        @Part("multiplity") multiplity: RequestBody, //feature_post
        @Part("feature_post") feature_post: RequestBody
    ): Response<SingleRes>


    @Multipart
    @POST("post/addUserPost")
    suspend fun addUserImgVideoPost(
        @Part("feature_post") feature_post: RequestBody,
        @Part("type") type: RequestBody,
        @Part("user_id") user_id: RequestBody,
        @Part("title") title: RequestBody,
        @Part("hashtag_ids") hashtag_ids: RequestBody,
        @Part("post_backround_color_id") post_backround_color_id: RequestBody,
        @Part("multiplity") multiplity: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Response<SingleRes>

//    //@Headers(HEADER_1, HEADER_2)
//    @GET("stories/getFollowingUsersStorieList/{page}/{per_page}/{your_user_id}")
//    suspend fun getFollowingUsersStorieList(
//        @Path(value = "page") page: Int,
//        @Path(value = "per_page") per_page: Int,
//        @Path(value = "your_user_id") your_user_id: String
//    ):Response<FollowingStoriesRes>


    //    Harsh: change fro new api:18-10-22
    @GET("stories/getFollowingUsersStorieList/{page}/{per_page}")
    suspend fun getFollowingUsersStorieList(
        @Path(value = "page") page: Int,
        @Path(value = "per_page") per_page: Int,
    ): Response<FollowingStoriesRes>

    @FormUrlEncoded
    @POST("follow/addUserFollow")
    suspend fun addUserFollow(
        @Field("user_id") user_id: String,
        @Field("follow_user_id") follow_user_id: String
    ): Response<SingleRes>

    @GET("post/postSave/{your_user_id}/{your_spine_post_id}")
    suspend fun onPostSave(
        @Path(value = "your_user_id") your_user_id: String,
        @Path(value = "your_spine_post_id") your_spine_post_id: String
    ): Response<SingleRes>

    @GET("impluse/removeImpluseLike/{your_user_id}/{your_spine_impluse_id}")
    suspend fun unlikeImpulse(
        @Path(value = "your_user_id") your_user_id: String,
        @Path(value = "your_spine_impluse_id") your_spine_impluse_id: String
    ): Response<SingleRes>

    @GET("follow/allUserList/{page}/{per_page}/{your_users_id}")
    suspend fun getAllUsers(
        @Path(value = "page") page: Int,
        @Path(value = "per_page") per_page: Int,
        @Path(value = "your_users_id") your_users_id: String
    ): Response<AllUsersRes>


    @FormUrlEncoded
    @POST("post/spinePostShare")
    suspend fun sharePost(
        @FieldMap data: Map<String, String>
    ): Response<SingleRes>

    @FormUrlEncoded
    @POST("post/spinePostComment")
    suspend fun postComment(
        @Field("spine_post_id") spine_post_id: String,
        @Field("user_id") user_id: String,
        @Field("comment_id") comment_id: String,
        @Field("comment") comment: String
    ): Response<SingleRes>

    @GET("post/spinePostLike/{your_post_id}/{user_id}")
    suspend fun likePost(
        @Path(value = "your_post_id") your_post_id: String,
        @Path(value = "user_id") user_id: String
    ): Response<SingleRes>

    @GET("hashtag/hashtagList")
    suspend fun getHashtagList(): Response<HashtagRes>

    @GET("follow/getFollowersList/{page}/{per_page}/{user_id}")
    suspend fun getFollowersList(
        @Path(value = "page") page: Int,
        @Path(value = "per_page") per_page: Int,
        @Path(value = "user_id") user_id: String
    ): Response<FollowersRes>

    @GET("stories/getOwnStorieList/{your_user_id}")
    suspend fun getYourStories(
        @Path(value = "your_user_id") your_user_id: String
    ): Response<StoryRes>

    @GET("post/getSpineUserFollowersPostList/{Page}/{PerPage}/{UserId}/{Followers}/{OnlyUserPost}")
    suspend fun getAllPosts(
        @Path(value = "Page") page: Int,
        @Path(value = "PerPage") per_page: Int,
        @Path(value = "UserId") your_user_id: String,
        @Path(value = "Followers") followers: Int,
        @Path(value = "OnlyUserPost") only_user_post: Int
    ): Response<PostRes>

    @Multipart
    @POST("stories/addUserStories")
    suspend fun addStory(
        @Part media_file: List<MultipartBody.Part>,
        @Part("user_id") user_id: RequestBody,
        @Part("title") title: RequestBody,
        @Part("type") type: RequestBody,
        @Part("allow_comment") allow_comment: RequestBody,
        @Part("delete_story_after_24_hr") delete_story_after_24_hr: RequestBody
    ): Response<SingleRes>

    @GET("post/getSpineUserActivityList/{page}/{per_page}/{your_user_id}/{followers}")
    suspend fun getActivitiesList(
        @Path(value = "page") page: Int,
        @Path(value = "per_page") per_page: Int,
        @Path(value = "your_user_id") your_user_id: String,
        @Path(value = "followers") followers: Int
    ): Response<ResponseBody>

    @GET("impluse/impluseLike/{your_user_id}/{your_spine_impluse_id}")
    suspend fun likeImpulse(
        @Path(value = "your_user_id") your_user_id: String,
        @Path(value = "your_spine_impluse_id") your_spine_impluse_id: String
    ): Response<ResponseBody>

    @GET("impluse/impluseComments/{your_page_id}/{your_per_page_no}/{your_spine_id}")
    suspend fun getComments(
        @Path(value = "your_page_id", encoded = true) your_page_id: Int,
        @Path(value = "your_per_page_no", encoded = true) your_per_page_no: Int,
        @Path(value = "your_spine_id", encoded = true) your_spine_id: String
    ): Response<CommentResponse>

    @FormUrlEncoded
    @POST("impluse/saveCommentOnImpluse")
    suspend fun saveImpulseComment(
        @Field("parent_comment_id") parent_comment_id: String,
        @Field("spine_impluse_id") spine_impluse_id: String,
        @Field("user_id") user_id: String,
        @Field("comment") comment: String
    ): Response<ResponseBody>

    @GET("impluse/impluseList/{page_no}/{page_item_count}/{user_id}")
    suspend fun getSpineImpulseData(
        @Path(value = "page_no", encoded = true) page_no: Int,
        @Path(value = "page_item_count", encoded = true) page_item_count: Int,
        @Path(value = "user_id", encoded = true) user_id: String
    ): Response<SpineImpulseResponse>

    @GET("other/getWelcomeData")
    suspend fun getWelcomeData(): Response<WelcomeResponse>

    @FormUrlEncoded
    @POST("login/socialLogin")
    suspend fun socialLoigin(
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("facebook_id") facebook_id: String,
        @Field("device_token") device_token: String,
        @Field("user_latitude") user_latitude: String,
        @Field("user_longitude") user_longitude: String,
        @Field("notify_device_token") notify_device_token: String,
        @Field("notify_device_type") notify_device_type: String
    ): Response<SignupResponse>

    @FormUrlEncoded
    @POST("login/loginUsers")
    suspend fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("notify_device_token") notify_device_token: String,
        @Field("notify_device_type") notify_device_type: String
    ): Response<SignupResponse>

    @FormUrlEncoded
    @POST("login/forgetPassword")
    suspend fun forgotPassword(@Field("email") email: String): Response<ResponseBody>


    @FormUrlEncoded
    @POST("login/registerUsers")
    suspend fun userSignup(
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("town") town: String,
        @Field("password") password: String,
        @Field("user_ip") user_ip: String,
        @Field("user_latitude") user_latitude: String,
        @Field("user_longitude") user_longitude: String,
        @Field("category") category: String
    ): Response<SignupResponse>


    @GET("login/accountVerify/{user_id}")
    suspend fun verifyAccount(
        @Header("Authorization") token: String,
        @Path(value = "user_id", encoded = true) user_id: String
    ): Response<SignupResponse>

    @GET("login/accountVerify/{user_id}")
    suspend fun reSendCode(
        @Header("Authorization") token: String,
        @Path(value = "user_id", encoded = true) user_id: String
    ): Response<SignupResponse>


    @FormUrlEncoded
    @POST("login/verificationCodeOnMobile")
    suspend fun verificationCodeOnMobile(
        @Field("mobile_no") mobile_no: String,
        @Field("user_id") user_id: String
    ): Response<SignupResponse>

//    //@Headers(HEADER_1, HEADER_2)
//    @Multipart
//    @POST("events/addUserEvents")
//    suspend fun addUserEvents(
//        @Part("user_id") user_id: RequestBody,
//        @Part("type") type: RequestBody,
//        @Part("allow_comments") allow_comments: RequestBody,
//        @Part("title") title: RequestBody,
//        @Part("description") description: RequestBody,
//        @Part("start_time") start_time: RequestBody,
//        @Part("start_date") start_date: RequestBody,
//        @Part("end_time") end_time: RequestBody,
//        @Part("end_date") end_date: RequestBody,
//        @Part("timezone") timezone: RequestBody,
//        @Part("location") location: RequestBody,
//        @Part("link_of_event") link_of_event: RequestBody,
//        @Part("event_categories") event_categories: RequestBody,
//        @Part("fee") fee: RequestBody,
//        @Part("fee_currency") fee_currency: RequestBody,
//        @Part("max_attendees") max_attendees: RequestBody,
//        @Part("language") language: RequestBody,
//        @Part("accept_participants") accept_participants: RequestBody,
//        @Part("multiple") multiple: RequestBody,
//        @Part("latitude") latitude: RequestBody,
//        @Part("longitude") longitude: RequestBody,
//        @Part("booking_url") booking_url: RequestBody,
//        @Part("event_subcategories") event_subcategories: RequestBody,
//        @Part files: List<MultipartBody.Part>
//    ):Response<SingleRes>

    //    <Harsh : change endpoint for new apu:16-10-22>
    //@Headers(HEADER_1, TestToken)
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


    @GET("events/getEventTypes")
    suspend fun getEventType(
    ): Response<EventTypeRes>

    @GET("events/getFollowingUsersEventsList/{page}/{per_page}/{your_user_id}")
    suspend fun getFollowingUsersEventList(
        @Path(value = "page") page: Int,
        @Path(value = "per_page") per_page: Int,
        @Path(value = "your_user_id") your_user_id: String
    ): Response<EventsRes>

    @GET("events/eventSave/{your_user_id}/{your_spine_event_id}")
    suspend fun saveEvents(
        @Path(value = "your_user_id") your_user_id: String,
        @Path(value = "your_spine_event_id") your_spine_event_id: String
    ): Response<SingleRes>

    @GET("splash/splashScreens")
    suspend fun getWelcomePages(): Response<WelcomePageReponse>

    @FormUrlEncoded
    @POST("spineBlockUsers")
    suspend fun blockuser(
        @Field("user_id") user_id: String,
        @Field("blocked_user_id") title: String
    ): Response<SingleRes>

    @FormUrlEncoded
    @POST("PrivacyAllSetting")
    suspend fun getAllPrivarcySettings(
        @Field("p_findability") p_findability: String,
        @Field("p_customization") p_customization: String,
        @Field("p_necessary") p_necessary: String,
        @Field("p_personalized") p_personalized: String
    ): Response<SingleRes>

    @FormUrlEncoded
    @POST("emailAllNotification")
    suspend fun getAllEmailNotification(
        @Field("e_notify_status") e_notify_status: String,
        @Field("e_event_attach_status") e_event_attach_status: String,
        @Field("e_message_status") e_message_status: String,
        @Field("e_comment_reply_status") e_comment_reply_status: String,
        @Field("e_event_podcast_status") e_event_podcast_status: String,
        @Field("e_update_from_spine_status") e_update_from_spine_status: String,
        @Field("e_spine_surveys_status") e_spine_surveys_status: String
    ): Response<SingleRes>

    @FormUrlEncoded
    @POST("mobileAllNotification")
    suspend fun getAllMobileNotifications(
        @Field("m_notify_status") m_notify_status: String,
        @Field("m_like_notify_status") m_like_notify_status: String,
        @Field("m_comment_notify_status") m_comment_notify_status: String,
        @Field("m_update_and_reminders_status") m_update_and_reminders_status: String,
        @Field("m_save_event_reminders_status") m_save_event_reminders_status: String,
        @Field("m_message_status") m_message_status: String,
        @Field("m_follow_status") m_follow_status: String,
        @Field("m_spine_impulse_status") m_spine_impulse_status: String,
        @Field("m_any_post_status") m_any_post_status: String
    ): Response<SingleRes>

}