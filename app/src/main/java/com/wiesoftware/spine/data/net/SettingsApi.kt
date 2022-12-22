package com.wiesoftware.spine.data.net

import android.util.Log
import com.google.gson.GsonBuilder
import com.wiesoftware.spine.data.net.reponses.CurrencyRes
import com.wiesoftware.spine.data.net.reponses.LangRes
import com.wiesoftware.spine.data.net.reponses.SingleRes
import com.wiesoftware.spine.util.Prefs
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface SettingsApi {

    companion object {
        private const val HEADER_1 = "X-API-KEY: 123run"
        private const val HEADER_2 = "Authorization: Basic ZGV2cGFua2FqOmRldnBhbmthag=="
        private const val HEADER_3 = "Content-Type: application/json"
        const val BASE_LINK = "http://thespiritualnetwork.com/api/v1/"
        const val NewBASE_LINK = "http://162.214.165.52/~pirituc5/"

        const val BASE_URL = BASE_LINK
        const val BASE_URL_VIDEO = BASE_LINK + "assets/upload/welcome/"

        const val ABOUT_SPINE = NewBASE_LINK + "about-us"
        const val HELP_SPINE = NewBASE_LINK + "spine-help"
        const val GUIDELINE_SPINE = NewBASE_LINK + "spine-guidelines"
        const val TERMS_SPINE = NewBASE_LINK + "spine-terms"
        const val PRIVACY_SPINE = NewBASE_LINK + "spine-privacy"

        operator fun invoke(): SettingsApi {
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
                .create(SettingsApi::class.java)
        }
    }

    @DELETE("deleteAccount")
    suspend fun deleteAccount(): Response<SingleRes>

    @GET("deactiveAccount")
    suspend fun deactivateAccount(): Response<SingleRes>

    @FormUrlEncoded
    @POST("requestToChangeEmail")
    suspend fun requestToChangeEmail(
        @Field("email") email: String
    ): Response<SingleRes>

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

    @GET("currency")
    suspend fun getCurrency(): Response<CurrencyRes>

    @GET("languages")
    suspend fun getLanguages(): Response<LangRes>

    @FormUrlEncoded
    @POST("emailAllNotification")
    suspend fun getEmailNotification(
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
    suspend fun getMobileNotifications(
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

    @FormUrlEncoded
    @POST("PrivacyAllSetting")
    suspend fun getPrivarcySettings(
        @Field("p_findability") p_findability: String,
        @Field("p_customization") p_customization: String,
        @Field("p_necessary") p_necessary: String,
        @Field("p_personalized") p_personalized: String
    ): Response<SingleRes>

}
