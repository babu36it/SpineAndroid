package com.wiesoftware.spine.data.net

import com.google.gson.GsonBuilder
import com.wiesoftware.spine.data.net.reponses.RssResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

/**
 * Created by Vivek kumar on 4/8/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
interface RssApi {

    companion object{
        const val RSS_BASE_URL="https://api.rss2json.com/"
        private const val H1="Content-Type: application/json"

        operator fun invoke(networkConnectionInterceptor: NetworkConnectionInterceptor):RssApi{
            val gson = GsonBuilder()
                .setLenient()
                .create()

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(networkConnectionInterceptor)
                .build()
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(RSS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(/*gson*/))
                .build()
                .create(RssApi::class.java)
        }
    }




    @GET("v1/api.json")
    suspend fun getPodsFromRss(
        @Query("rss_url") rss_url: String
    ):Response<RssResponse>
}