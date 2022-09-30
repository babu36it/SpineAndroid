package com.wiesoftware.spine.data.repo

import android.util.Log
import com.wiesoftware.spine.data.net.RssApi
import com.wiesoftware.spine.data.net.SafeApiRequest
import com.wiesoftware.spine.data.net.reponses.RssResponse

/**
 * Created by Vivek kumar on 4/8/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class RssRepository (private val rssApi: RssApi):SafeApiRequest(){

    suspend fun getPodsFromRss(url:String):RssResponse{
        Log.e("rss::",""+"url")
        return apiRequest { rssApi.getPodsFromRss(url) }
    }
}