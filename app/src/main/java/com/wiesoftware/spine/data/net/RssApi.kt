package com.wiesoftware.spine.data.net

import com.google.gson.GsonBuilder
import com.wiesoftware.spine.data.net.reponses.RssResponse
import com.wiesoftware.spine.util.Prefs
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * Created by Vivek kumar on 4/8/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
interface RssApi {

    companion object {
        private const val RSS_BASE_URL = "http://thespiritualnetwork.com/api/v1/"

        //        const val RSS_BASE_URL="http://162.214.165.52/~pirituc5/apisecure/"
        private const val H1 = "Content-Type: application/json"

        operator fun invoke(networkConnectionInterceptor: NetworkConnectionInterceptor): RssApi {
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
                .create(RssApi::class.java)
        }
    }


    @FormUrlEncoded
    @POST("podcasts/validateRss")
    suspend fun getPodsFromRss(
        @Field("link") rss_url: String,
    ): Response<RssResponse>

    @FormUrlEncoded
    @POST("podcasts/getRssData")
    suspend fun getFeedRssData(
        @Field("link") rss_url: String,
    ): Response<RssResponse>
}