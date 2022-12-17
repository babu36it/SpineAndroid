package com.wiesoftware.spine.data.net

import com.google.gson.GsonBuilder
import com.wiesoftware.spine.data.net.reponses.*
import com.wiesoftware.spine.data.net.reponses.episode.EpisodeModel
import com.wiesoftware.spine.util.Prefs
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


/**
 * Created by Vivek kumar on 4/8/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
interface PodcastApi {

    companion object {
        private const val RSS_BASE_URL = "http://thespiritualnetwork.com/api/v1/"

        private const val H1 = "Content-Type: application/json"

        operator fun invoke(): PodcastApi {
            val gson = GsonBuilder()
                .setLenient()
                .create()

            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client: OkHttpClient = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60,TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor(Interceptor { chain ->
                    val request: Request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer ${Prefs.getString("AuthToken","")}")
                        .build()
                    chain.proceed(request)
                }).build()


          return  Retrofit.Builder()
                .client(client)
                .baseUrl(RSS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(PodcastApi::class.java)
        }
    }

    @FormUrlEncoded
    @POST("podcasts/getRssData")
    suspend fun getFeedRssData(
        @Field("link") rss_url: String,
    ): Response<RssResponse>

    @FormUrlEncoded
    @POST("podcasts/validateRss")
    suspend fun getPodsFromRss(
        @Field("link") rss_url: String,
    ): Response<RssResponse>

    @GET("podcasts/getPodcastsCustom")
    suspend fun getAllPodcasts(): Response<PodRes>

    @GET("podcasts/getPodcastsEpisodeCustom")
    suspend fun getAllEpisodeCustom(): Response<EpisodeModel>


    @GET("podcasts/podcastsView/{PodcastId}")
    suspend fun increasePodcastViews(
        @Path("PodcastId") podcast_id: String
    ): Response<SingleRes>

    @GET("podcasts/gePodcastsDetailCustom/{PodcastId}")
    suspend fun getPodcastDetails(
        @Path("PodcastId") podId: String
    ): Response<PodcastDetailsRes>

    @FormUrlEncoded
    @POST("podcasts/podcastShare")
    suspend fun sharePodcasts(
        @FieldMap data: Map<String, String>
    ): Response<SingleRes>

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


}