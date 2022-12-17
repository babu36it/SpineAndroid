package com.wiesoftware.spine.data.repo

import com.wiesoftware.spine.data.db.AppDatabase
import com.wiesoftware.spine.data.db.entities.User
import com.wiesoftware.spine.data.net.Api
import com.wiesoftware.spine.data.net.ProfileApi
import com.wiesoftware.spine.data.net.SafeApiRequest
import com.wiesoftware.spine.data.net.reponses.*
import com.wiesoftware.spine.data.net.reponses.adsmanagment.AdsTypeModel
import com.wiesoftware.spine.data.net.reponses.episode.EpisodeModel
import com.wiesoftware.spine.data.net.reponses.welcompageresponse.WelcomePageReponse
import com.wiesoftware.spine.ui.home.menus.events.TimeZoneResponse
import com.wiesoftware.spine.util.Prefs
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody


class HomeRepository(
    private val api: Api,
    private val db: AppDatabase
) : SafeApiRequest() {


    suspend fun getAdDuration(user_id: String): AdDurationRes {
        return apiRequest { api.getAdDuration(user_id) }
    }

    suspend fun getUserHashtags(): HashtagRes {
        return apiRequest { api.getUserHashtags() }
    }


    suspend fun getStoryListByMonthWithYear(
        userId: String,
        monthId: String,
        year: String
    ): FollowingStoriesRes {
        return apiRequest { api.getStoryListByMonthWithYear(userId, monthId, year) }
    }

    suspend fun getStoriesMonthYear(userId: String): StoryMonthRes {
        return apiRequest { api.getStoriesMonthYear(userId) }
    }

    suspend fun removeStoryCommentReply(story_comment_or_reply_id: String): SingleRes {
        return apiRequest { api.removeStoryCommentReply(story_comment_or_reply_id) }
    }

    suspend fun storyCommentReplyList(comment_id: String): StoriesCommentRes {
        return apiRequest { api.storyCommentReplyList(comment_id) }
    }

    suspend fun addFeaturedAds(
        file: MultipartBody.Part,
        user_id: RequestBody,
        duration: RequestBody,
        timeslot_date: RequestBody,
        timeslot_time: RequestBody,
        ad_type: RequestBody,
        file_type: RequestBody,
        website: RequestBody,
        promote_your_ad: RequestBody,
        payment_details: RequestBody,
        pay_by: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody
    ): SingleRes{
        return apiRequest { api.addFeaturedAds(file, user_id, duration, timeslot_date, timeslot_time, ad_type, file_type, website, promote_your_ad,payment_details, pay_by,latitude, longitude) }
    }


    suspend fun storyCommentsList(storyId: String): StoriesCommentRes {
        return apiRequest { api.storyCommentsList(storyId) }
    }


    suspend fun spineStoriesShare(data: Map<String, String>): SingleRes {
        return apiRequest { api.spineStoriesShare(data) }
    }

    suspend fun spineStoriesLike(story_id: String, user_id: String): SingleRes {
        return apiRequest { api.spineStoriesLike(story_id, user_id) }
    }

    suspend fun removeCommentOnImpluse(comment_or_reply_id: String): SingleRes {
        return apiRequest { api.removeCommentOnImpluse(comment_or_reply_id) }
    }

    suspend fun spinePostCommentRemove(post_comment_id: String): SingleRes {
        return apiRequest { api.spinePostCommentRemove(post_comment_id) }
    }

    suspend fun recommendedFollowersListByCategories(
        page: Int,
        per_page: Int,
        your_users_id: String
    ): AllUsersRes {
        return apiRequest {
            api.recommendedFollowersListByCategories(
                page,
                per_page,
                your_users_id
            )
        }
    }

    suspend fun getSpinePostReplys(your_post_comment_id: String): CommentReplyRes {
        return apiRequest { api.getSpinePostReplys(your_post_comment_id) }
    }

    suspend fun followUnfollowImpluse(user_id: String, status: Int): SingleRes {
        return apiRequest { api.followUnfollowImpluse(user_id, status) }
    }







    suspend fun getQuetionsThought(title: String, postType: String, hashTags: String): SingleRes {
        return apiRequest {
            api.addQuestionThought(title, postType, hashTags)
        }
    }

    suspend fun getAdDuration(): AdDurationRes {
        return apiRequest { api.getAdDuration() }
    }

    suspend fun getAdsType(): AdsTypeModel {
        return apiRequest {
            api.getAdsType()
        }
    }

    suspend fun getPaymentSettings(): PaymentResponses {
        return apiRequest {
            api.getPaymentSettings()
        }
    }


    suspend fun getUserNotifications(user_id: String): NotificationsRes {
        return apiRequest { api.getUserNotifications(user_id) }
    }


    suspend fun changePassword(
        user_id: String,
        old_password: String,
        new_password: String
    ): SingleRes {
        return apiRequest { api.changePassword(user_id, old_password, new_password) }
    }

    suspend fun getAllSavedStory(user_id: String): StoryRes {
        return apiRequest { api.getAllSavedStory(user_id) }
    }

    suspend fun saveStory(user_id: String, story_id: String): SingleRes {
        return apiRequest { api.saveStory(user_id, story_id) }
    }


    suspend fun getAllSavedPost(user_id: String): PostRes {
        return apiRequest { api.getAllSavedPosts(user_id) }
    }

    suspend fun unFollowUser(user_id: String, unfollow_user_id: String): SingleRes {
        return apiRequest { api.unFollowUser(user_id, unfollow_user_id) }
    }

    suspend fun addFollowunFollow(followUnfollowUserId: String): SingleRes {
        return apiRequest { api.addFollowunFollow(followUnfollowUserId) }
    }

    suspend fun getFollowingList(page: Int, per_page: Int, user_id: String): FollowersRes {
        return apiRequest { api.getFollowingList(page, per_page, user_id) }
    }

    suspend fun getEventChatMsg(
        page: Int,
        per_page: Int,
        event_user_id: String,
        second_user_id: String
    ): ChatMsgRes {
        return apiRequest { api.getEventChatMsg(page, per_page, event_user_id, second_user_id) }
    }

    suspend fun getEventMsgUsers(page: Int, per_page: Int, user_id: String): EveMsgUserRes {
        return apiRequest { api.getEventsMsgUsers(page, per_page, user_id) }
    }







    suspend fun getSpineComments(post_id: String): SpineCommentRes {
        return apiRequest { api.getSpinePostComments(post_id) }
    }




    suspend fun getActivities(
        page: Int,
        per_page: Int,
        user_id: String,
        followers: Int
    ): ActivitiesRes {
        return apiRequest { api.getActivities(page, per_page, user_id, followers) }
    }


    suspend fun getuserDetailsMSGPermision(detailsUser: String, LoginUserId: String): ProfileRes {
        return apiRequest { api.getuserDetailsMSGPermision(detailsUser, LoginUserId) }
    }



    suspend fun addUserPost(
        type: RequestBody,
        user_id: RequestBody,
        title: RequestBody,
        hashtag_ids: RequestBody,
        post_backround_color_id: RequestBody,
        multiplity: RequestBody,
        feature_post: RequestBody
    ): SingleRes {
        return apiRequest {
            api.addUserPost(
                type,
                user_id,
                title,
                hashtag_ids,
                post_backround_color_id,
                multiplity,
                feature_post
            )
        }
    }

    suspend fun addUserImgVideoPost(
        feature_post: RequestBody,
        type: RequestBody,
        user_id: RequestBody,
        title: RequestBody,
        hashtag_ids: RequestBody,
        post_backround_color_id: RequestBody,
        multiplity: RequestBody,
        files: List<MultipartBody.Part>
    ): SingleRes {
        return apiRequest {
            api.addUserImgVideoPost(
                feature_post,
                type,
                user_id,
                title,
                hashtag_ids,
                post_backround_color_id,
                multiplity,
                files
            )
        }
    }

    suspend fun userImageVideoPost(
        title: RequestBody, type: RequestBody, hashtags: RequestBody,
        mark_friends: RequestBody, place_link: RequestBody,
        files: List<MultipartBody.Part>
    ): SingleRes {
        return apiRequest {
            api.UserImgVideoPost(
                title, type, hashtags,
                mark_friends, place_link,
                files
            )
        }
    }

    suspend fun getFollowingUsersStoryList(
        page: Int,
        per_page: Int,
        your_user_id: String
    ): FollowingStoriesRes {
        return apiRequest { api.getFollowingUsersStorieList(page, per_page) }
    }

    suspend fun addUserFollow(user_id: String, follow_user_id: String): SingleRes {
        return apiRequest { api.addUserFollow(user_id, follow_user_id) }
    }

    suspend fun onPostSave(your_user_id: String, your_spine_post_id: String): SingleRes {
        return apiRequest { api.onPostSave(your_user_id, your_spine_post_id) }
    }

    suspend fun unlikeImpulse(your_user_id: String, your_spine_impluse_id: String): SingleRes {
        return apiRequest { api.unlikeImpulse(your_user_id, your_spine_impluse_id) }
    }

    suspend fun getAllUsers(page: Int, per_page: Int, your_users_id: String): AllUsersRes {
        return apiRequest { api.getAllUsers(page, per_page, your_users_id) }
    }

    suspend fun sharePost(data: Map<String, String>): SingleRes {
        return apiRequest { api.sharePost(data) }
    }

    suspend fun postComment(
        spine_post_id: String,
        user_id: String,
        comment_id: String,
        comment: String
    ): SingleRes {
        return apiRequest { api.postComment(spine_post_id, user_id, comment_id, comment) }
    }

    suspend fun likePost(your_post_id: String, user_id: String): SingleRes {
        return apiRequest { api.likePost(your_post_id, user_id) }
    }

    suspend fun getHashtagList(): HashtagRes {
        return apiRequest { api.getHashtagList() }
    }

    suspend fun getYourStories(your_user_id: String): StoryRes {
        return apiRequest { api.getYourStories(your_user_id) }
    }

    suspend fun getAllPosts(
        page: Int,
        per_page: Int,
        your_user_id: String,
        followers: Int,
        only_user_post: Int
    ): PostRes {
        return apiRequest {
            api.getAllPosts(
                page,
                per_page,
                your_user_id,
                followers,
                only_user_post
            )
        }
    }

    suspend fun postAStory(
        file: List<MultipartBody.Part>,
        title: RequestBody,
        type: RequestBody,
        allow_comment: RequestBody,
    ): SingleRes {
        return apiRequest {
            api.addStory(
                file,
                title,
                type,
                allow_comment,
            )
        }
    }

    suspend fun likeSpineImpulse(
        your_user_id: String,
        your_spine_impluse_id: String
    ): ResponseBody {
        return apiRequest { api.likeImpulse(your_user_id, your_spine_impluse_id) }
    }

    suspend fun getComments(
        your_page_id: Int,
        your_per_page_no: Int,
        your_spine_id: String
    ): CommentResponse {
        return apiRequest { api.getComments(your_page_id, your_per_page_no, your_spine_id) }
    }

    suspend fun saveImpulseComment(
        parent_comment_id: String,
        spine_impluse_id: String,
        user_id: String,
        comment: String
    ): ResponseBody {
        return apiRequest {
            api.saveImpulseComment(
                parent_comment_id,
                spine_impluse_id,
                user_id,
                comment
            )
        }
    }


    suspend fun getSpineImpulse(
        page_no: Int,
        page_item_count: Int,
        user_id: String
    ): SpineImpulseResponse {
        return apiRequest { api.getSpineImpulseData(page_no, page_item_count, user_id) }
    }


    fun getUser() = db.getUserDao().getUser()

    suspend fun logoutUser(c_user: User) = db.getUserDao().deleteUser(c_user)

    suspend fun getUserDetails(): SignupResponse {
        return apiRequest { api.getUserDetailAuth() }
    }


    suspend fun userBlock(user_id: String, block_user_id: String): SingleRes {
        return apiRequest {
            api.blockuser(user_id, block_user_id)
        }
    }
}