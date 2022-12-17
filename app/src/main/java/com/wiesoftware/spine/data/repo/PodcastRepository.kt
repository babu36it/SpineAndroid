package com.wiesoftware.spine.data.repo

import android.util.Log
import com.wiesoftware.spine.data.net.PodcastApi
import com.wiesoftware.spine.data.net.SafeApiRequest
import com.wiesoftware.spine.data.net.reponses.*
import com.wiesoftware.spine.data.net.reponses.episode.EpisodeModel
import okhttp3.RequestBody


class PodcastRepository(private val podcastApi: PodcastApi) : SafeApiRequest() {

    suspend fun getPodsFromRss(url: String): RssResponse {
        Log.e("rss::", "=$url")
        return apiRequest { podcastApi.getPodsFromRss(url) }
    }

    suspend fun getRssFeedData(url: String): RssResponse {
        return apiRequest { podcastApi.getFeedRssData(url) }
    }

    suspend fun getAllPodcasts(): PodRes {
        return apiRequest { podcastApi.getAllPodcasts() }
    }

    suspend fun getAllEpisodePodcast(): EpisodeModel {
        return apiRequest { podcastApi.getAllEpisodeCustom() }
    }

    suspend fun increasePodcastViews(podcast_id: String): SingleRes {
        return apiRequest { podcastApi.increasePodcastViews(podcast_id) }
    }

    suspend fun getPodcastDetails(podId: String): PodcastDetailsRes {
        return apiRequest { podcastApi.getPodcastDetails(podId) }
    }

    suspend fun sharePodcasts(data: Map<String, String>): SingleRes {
        return apiRequest { podcastApi.sharePodcasts(data) }
    }

    suspend fun managePodcastBookmarks(userId: String, podId: String): SingleRes {
        return apiRequest { podcastApi.managePodcastBookmarks(userId, podId) }
    }

    suspend fun managePodcastLikes(userId: String, podId: String): SingleRes {
        return apiRequest { podcastApi.managePodcastLikes(userId, podId) }
    }

    suspend fun addPodcasts(
        title: RequestBody,
        description: RequestBody,
        language: RequestBody,
        category: RequestBody,
        subcategory: RequestBody,
        rssFeed: RequestBody,
        allow_comment: RequestBody,
        media_file: RequestBody,
        thumbnail: RequestBody
    ): SingleRes {
        return apiRequest {
            podcastApi.addPodcasts(
                title,
                description,
                language,
                category,
                subcategory,
                rssFeed,
                allow_comment,
                media_file,
                thumbnail
            )
        }
    }

    suspend fun addRssfeedPodcast(
        title: String,
        description: String,
        language: String,
        category: String,
        subcategory: String,
        allow_comment: String,
        rss_feed: String,
        media_file: String,
        thumbnail: String
    ): SingleRes {
        return apiRequest {
            podcastApi.addRssfeedPodcast(
                title,
                description,
                language,
                category,
                subcategory,
                allow_comment,
                rss_feed,
                media_file,
                thumbnail
            )
        }
    }


    suspend fun sendVerificationCodeOnEmail(email: String): EmailVerificationRes {
        return apiRequest { podcastApi.sendVerificationCodeOnEmail(email) }
    }

    suspend fun verifyEmailVerificationCode(email: String, otp: String): EmailVerificationRes {
        return apiRequest { podcastApi.verifyEmailVerificationCode(email, otp) }
    }
}