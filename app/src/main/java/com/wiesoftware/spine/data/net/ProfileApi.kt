package com.wiesoftware.spine.data.net

import android.util.Log
import com.google.gson.GsonBuilder
import com.wiesoftware.spine.data.net.reponses.SingleRes
import com.wiesoftware.spine.util.Prefs
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ProfileApi {

    companion object {
        private const val BASE_LINK = "http://thespiritualnetwork.com/api/v1/"
        private const val NewBASE_LINK = "http://162.214.165.52/~pirituc5/"

        private const val BASE_URL = BASE_LINK

        operator fun invoke(): ProfileApi {
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
                .create(ProfileApi::class.java)
        }
    }

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
        @Field("business_phone_code") business_phone_code: String,
        @Field("business_mobile_code") business_mobile_code: String

    ): Response<SingleRes>

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
}