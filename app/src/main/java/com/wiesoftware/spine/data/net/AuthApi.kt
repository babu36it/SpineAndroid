package com.wiesoftware.spine.data.net

import android.util.Log
import com.google.gson.GsonBuilder
import com.wiesoftware.spine.data.net.reponses.ProfileRes
import com.wiesoftware.spine.data.net.reponses.SignupResponse
import com.wiesoftware.spine.data.net.reponses.WelcomeResponse
import com.wiesoftware.spine.data.net.reponses.welcompageresponse.WelcomePageReponse
import com.wiesoftware.spine.util.Prefs
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface AuthApi {

    companion object {
        private const val BASE_LINK = "http://thespiritualnetwork.com/api/v1/"
        private const val NewBASE_LINK = "http://162.214.165.52/~pirituc5/"

        private const val BASE_URL = BASE_LINK


        operator fun invoke(): AuthApi {
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
                .create(AuthApi::class.java)
        }
    }

    @FormUrlEncoded
    @POST("login/loginUsers")
    suspend fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("notify_device_token") notify_device_token: String,
        @Field("notify_device_type") notify_device_type: String
    ): Response<SignupResponse>

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


    @GET("login/accountVerify/{UserId}")
    suspend fun verifyAccount(
        @Path(value = "UserId", encoded = true) user_id: String
    ): Response<SignupResponse>

    @GET("login/reSendOtp/{UserId}")
    suspend fun reSendCode(
        @Path(value = "UserId", encoded = true) user_id: String
    ): Response<SignupResponse>


    @FormUrlEncoded
    @POST("login/verifyMobile")
    suspend fun verificationCodeOnMobile(
        @Field("mobile_no") mobile_no: String,
        @Field("user_id") user_id: String
    ): Response<SignupResponse>

    @GET("userDetails")
    suspend fun getUserDetails(
    ): Response<ProfileRes>

    @GET("getWelcomeData")
    suspend fun getWelcomeData(): Response<WelcomeResponse>

    @GET("splash/splashScreens")
    suspend fun getWelcomePages(): Response<WelcomePageReponse>

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
    @POST("login/forgetPassword")
    suspend fun forgotPassword(@Field("email") email: String): Response<ResponseBody>

}