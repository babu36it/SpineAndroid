package com.wiesoftware.spine.data.net

import android.util.Log
import com.google.gson.GsonBuilder
import com.wiesoftware.spine.data.net.reponses.*
import com.wiesoftware.spine.data.net.reponses.adsmanagment.AdsTypeModel
import com.wiesoftware.spine.data.net.reponses.episode.EpisodeModel
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
        private const val BASE_LINK = "http://thespiritualnetwork.com/api/v1/"
        private const val NewBASE_LINK = "http://162.214.165.52/~pirituc5/"

        private const val BASE_URL = BASE_LINK
        const val BASE_URL_VIDEO = BASE_LINK + "assets/upload/welcome/"

        const val ABOUT_SPINE = NewBASE_LINK + "about-us"
        const val HELP_SPINE = NewBASE_LINK + "spine-help"
        const val GUIDELINE_SPINE = NewBASE_LINK + "spine-guidelines"
        const val TERMS_SPINE = NewBASE_LINK + "spine-terms"
        const val PRIVACY_SPINE = NewBASE_LINK + "spine-privacy"

        operator fun invoke(): Api {
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


    @GET("userDetails")
    suspend fun getUserDetailAuth(
    ): Response<SignupResponse>

    @FormUrlEncoded
    @POST("post/getAdsDuration")
    suspend fun getAdDuration(
        @Field("user_id") user_id: String
    ): Response<AdDurationRes>

    @GET("post/getUserHashtags")
    suspend fun getUserHashtags(): Response<HashtagRes>

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

    @GET("stories/storyCommentsList/{storyId}")
    suspend fun storyCommentsList(
        @Path("storyId") storyId: String
    ): Response<StoriesCommentRes>

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


    @FormUrlEncoded
    @POST("userNotification")
    suspend fun getUserNotifications(
        @Field("user_id") user_id: String
    ): Response<NotificationsRes>

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



    @GET("post/getSpinePostSave/{user_id}")
    suspend fun getAllSavedPosts(
        @Path("user_id") user_id: String
    ): Response<PostRes>

    @GET("follow/removeSpineUserFollowing/{user_id}/{unfollow_user_id}")
    suspend fun unFollowUser(
        @Path("user_id") user_id: String,
        @Path("unfollow_user_id") unfollow_user_id: String
    ): Response<SingleRes>

    @GET("follow/addUserFollowUnfollow/{FollowUserId}")
    suspend fun addFollowunFollow(
        @Path("FollowUserId") unfollow_user_id: String
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


    @GET("post/getSpinePostComment/{post_id}")
    suspend fun getSpinePostComments(
        @Path(value = "post_id") post_id: String
    ): Response<SpineCommentRes>



    @GET("post/getSpineUserActivityList/{page}/{per_page}/{user_id}/{followers}")
    suspend fun getActivities(
        @Path(value = "page") page: Int,
        @Path(value = "per_page") per_page: Int,
        @Path(value = "user_id") user_id: String,
        @Path(value = "followers") followers: Int
    ): Response<ActivitiesRes>

    @GET("userDetailsMSGPermision/{detailsUser}/{LoginUserId}")
    suspend fun getuserDetailsMSGPermision(
        @Path(value = "detailsUser") detailsUser: String,
        @Path(value = "LoginUserId") LoginUserId: String
    ): Response<ProfileRes>


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

    @Multipart
    @POST("post/addPost")
    suspend fun UserImgVideoPost(
        @Part("title") title: RequestBody,
        @Part("type") type: RequestBody,
        @Part("hashtags") hashtag_ids: RequestBody,
        @Part("mark_friends") mark_friends: RequestBody,
        @Part("place_link") place_link: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Response<SingleRes>



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
        @Part("title") title: RequestBody,
        @Part("type") type: RequestBody,
        @Part("allow_comment") allow_comment: RequestBody,
    ): Response<SingleRes>

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

    @FormUrlEncoded
    @POST("spineBlockUsers")
    suspend fun blockuser(
        @Field("user_id") user_id: String,
        @Field("blocked_user_id") title: String
    ): Response<SingleRes>


    @FormUrlEncoded
    @POST("post/addPost")
    suspend fun addQuestionThought(
        @Field("title") title: String,
        @Field("type") type: String,
        @Field("hashtags") hashtags: String
    ): Response<SingleRes>

    @GET("post/getAdsDuration")
    suspend fun getAdDuration(
    ): Response<AdDurationRes>

    @GET("post/getAdsType")
    suspend fun getAdsType(

    ): Response<AdsTypeModel>

    @GET("post/getPaymentSetting")
    suspend fun getPaymentSettings():Response<PaymentResponses>
}