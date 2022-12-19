package com.wiesoftware.spine

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.cometchat.pro.core.AppSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.wiesoftware.spine.data.db.AppDatabase
import com.wiesoftware.spine.data.net.*
import com.wiesoftware.spine.data.repo.*
import com.wiesoftware.spine.ui.auth.AuthViewModelFactory
import com.wiesoftware.spine.ui.auth.WelcomeViewModelFactory
import com.wiesoftware.spine.ui.auth.fb.FbEmailViewModelFactory
import com.wiesoftware.spine.ui.auth.login.LoginViewModelFactory
import com.wiesoftware.spine.ui.auth.number.NumberViewModelFactory
import com.wiesoftware.spine.ui.auth.otp.OtpViewModelFactory
import com.wiesoftware.spine.ui.home.menus.activities.following.FollowingActivityViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.activities.you.YouActivityViewModelFactory
import com.wiesoftware.spine.ui.home.menus.events.EventFragmentViewModelFactory
import com.wiesoftware.spine.ui.home.menus.events.addevents.AddEventsViewModelFactory
import com.wiesoftware.spine.ui.home.menus.events.event_details.EventDetailViewModelFactory
import com.wiesoftware.spine.ui.home.menus.events.eventcomment.EventCommentViewModelFactory
import com.wiesoftware.spine.ui.home.menus.events.eventcomment.eventreply.EventReplyViewModelFactory
import com.wiesoftware.spine.ui.home.menus.events.filter.FilterEventViewModelFactory
import com.wiesoftware.spine.ui.home.menus.events.maps.MapviewViewModelFactory
import com.wiesoftware.spine.ui.home.menus.events.preview_event.PreviewEventViewModelFactory
import com.wiesoftware.spine.ui.home.menus.events.select_users.SelectUsersViewModelFactory
import com.wiesoftware.spine.ui.home.menus.podcasts.addpodcasts.AddPodcastViewModelFactory
import com.wiesoftware.spine.ui.home.menus.podcasts.listen.ListenPodcastViewModelFactory
import com.wiesoftware.spine.ui.home.menus.podcasts.podcastdetails.PodcastDetailsViewModelFactory
import com.wiesoftware.spine.ui.home.menus.podcasts.userpodcast.UserPodcastViewModelFactory
import com.wiesoftware.spine.ui.home.menus.podcasts.watch.WtachPodcastViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.ProfileFragmentViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.chat.ChatActivityViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.editprofile.EditProfileViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.follow.FollowViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.follow.followers.FollowersFragmentViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.follow.following.FollowingFragmentViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.masseges.eve_request.EventRequestViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.masseges.msg.MsgFragmentViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.myprofile.MyProfileViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.SettingViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.AccountSettingViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.AccountSettingsActivity
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.change_email.ChangeEmailViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.deactiveaccount.DeactivateViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.deleteAccount.DeleteAccountViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.language.SelectLanguageViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.messaging.MessagingViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.saveEventCalendar.SaveEventViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.currency.CurrencyViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.help.HelpViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.help.privacy.PrivacySettingViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.notifications.NotificationsViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.notifications.email.EmailNotificationViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.notifications.mobile.MobileNotificationViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SomeoneProfileViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.followers.FollowersActivityViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.tabs.bookmark.BookmarkViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.tabs.events.EventsViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.tabs.posts.PostViewModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.addposts.hashtags.HashtagViewModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postmedia.PostMediaViewModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.addposts.poststory.AddStoryViewModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postthought.PostThoughtViewModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.addposts.reviewpost.ReviewPostViewModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.categories.TrendingCatViewModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.comment.impulsecomment.ImpulseCommentViewModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.comment.postcomment.PostCommentViewModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.comment.storycomment.StoryCommentViewModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.following.SpineFollowingViewModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.foryou.SpineForYouViewModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.impulse.ImpulseViewModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.practicioners.PracticionerModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.rec_followers.RecommendedFollowersViewModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.selectfollowers.SelectFollowersViewModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.story.StoryViewModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.story.viewstories.ViewStoryViewModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.welcome.ViewWelcomeViewModelFactory
import com.wiesoftware.spine.ui.home.menus.voice_over.VoiceViewModelFactory
import com.wiesoftware.spine.util.AppConfig
import com.wiesoftware.spine.util.Prefs
import com.wiesoftware.spine.util.toast
import constant.StringContract
import listeners.CometChatCallListener
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton


/**
 * Created by Vivek kumar on 8/4/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("DEPRECATION")
class SpineApplication : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@SpineApplication))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { Api() }
        //    bind() from singleton { SettingsApi(instance()) }
        bind() from singleton { AppDatabase(instance()) }
        bind() from singleton { AuthRepository(instance(), instance()) }
        bind() from singleton { HomeRepository(instance(), instance()) }
        bind() from singleton { EventRepository(instance(), instance()) }
        bind() from singleton { SettingsRepository(instance()) }
        bind() from singleton { ProfileRepository(instance()) }
        bind() from singleton { PodcastApi() }
        bind() from singleton { SettingsApi() }
        bind() from singleton { EventApi() }
        bind() from singleton { AuthApi() }
        bind() from singleton { ProfileApi() }
        bind() from singleton { PodcastRepository(instance()) }
        bind() from provider { AuthViewModelFactory(instance()) }
        bind() from provider { OtpViewModelFactory(instance()) }
        bind() from provider { NumberViewModelFactory(instance()) }
        bind() from provider { LoginViewModelFactory(instance(), instance()) }
        bind() from provider { WelcomeViewModelFactory(instance()) }
        bind() from provider { FbEmailViewModelFactory(instance()) }
        bind() from provider { SpineForYouViewModelFactory(instance()) }
        bind() from provider { ImpulseViewModelFactory(instance()) }
        bind() from provider { StoryViewModelFactory(instance()) }
        bind() from provider { SettingViewModelFactory(instance()) }
        bind() from provider { ProfileFragmentViewModelFactory(instance()) }
        bind() from provider { ImpulseCommentViewModelFactory(instance()) }
        bind() from provider { ViewWelcomeViewModelFactory(instance()) }
        bind() from provider { AddStoryViewModelFactory(instance()) }
        bind() from provider { EventFragmentViewModelFactory(instance()) }
        bind() from provider { YouActivityViewModelFactory(instance()) }
        bind() from provider { PostViewModelFactory(instance()) }
        bind() from provider { FilterEventViewModelFactory(instance()) }
        bind() from provider { TrendingCatViewModelFactory(instance()) }
        bind() from provider { EditProfileViewModelFactory(instance()) }
        bind() from provider { RecommendedFollowersViewModelFactory(instance()) }
        bind() from provider { ViewStoryViewModelFactory(instance()) }
        bind() from provider { SelectFollowersViewModelFactory(instance()) }
        bind() from provider { PostCommentViewModelFactory(instance()) }
        bind() from provider { SpineFollowingViewModelFactory(instance()) }
        bind() from provider { PostThoughtViewModelFactory(instance()) }
        bind() from provider { HashtagViewModelFactory(instance()) }
        bind() from provider { ReviewPostViewModelFactory(instance()) }
        bind() from provider { AddEventsViewModelFactory(instance()) }
        bind() from provider { FollowingActivityViewmodelFactory(instance()) }
        bind() from provider { EventsViewModelFactory(instance()) }
        bind() from provider { EventDetailViewModelFactory(instance()) }
        bind() from provider { SelectUsersViewModelFactory(instance()) }
        bind() from provider { PreviewEventViewModelFactory(instance()) }
        bind() from provider { MsgFragmentViewModelFactory(instance()) }
        bind() from provider { ChatActivityViewModelFactory(instance()) }
        bind() from provider { MyProfileViewModelFactory(instance()) }
        bind() from provider { FollowViewModelFactory(instance()) }
        bind() from provider { FollowersFragmentViewModelFactory(instance()) }
        bind() from provider { FollowingFragmentViewModelFactory(instance()) }
        bind() from provider { BookmarkViewModelFactory(instance()) }
        bind() from provider { NotificationsViewModelFactory(instance()) }
        bind() from provider { AccountSettingViewModelFactory(instance()) }
        bind() from provider { MapviewViewModelFactory(instance()) }
        bind() from provider { ChangeEmailViewModelFactory(instance()) }
        bind() from provider { MessagingViewModelFactory(instance()) }
        bind() from provider { SaveEventViewModelFactory(instance()) }
        bind() from provider { AddPodcastViewModelFactory(instance()) }
        bind() from provider { EventRequestViewModelFactory(instance()) }
        bind() from provider { SelectLanguageViewModelFactory(instance()) }
        bind() from provider { DeactivateViewModelFactory(instance()) }
        bind() from provider { CurrencyViewModelFactory(instance()) }
        bind() from provider { HelpViewModelFactory(instance()) }
        bind() from provider { PostMediaViewModelFactory(instance()) }
        bind() from provider { ListenPodcastViewModelFactory(instance()) }
        bind() from provider { WtachPodcastViewModelFactory(instance()) }
        bind() from provider { UserPodcastViewModelFactory(instance()) }
        bind() from provider { PodcastDetailsViewModelFactory(instance()) }
        bind() from provider { PrivacySettingViewModelFactory(instance()) }
        bind() from provider { MobileNotificationViewModelFactory(instance()) }
        bind() from provider { EmailNotificationViewModelFactory(instance()) }
        bind() from provider { SomeoneProfileViewModelFactory(instance()) }
        bind() from provider { FollowersActivityViewModelFactory(instance()) }
        bind() from provider { EventCommentViewModelFactory(instance()) }
        bind() from provider { EventReplyViewModelFactory(instance()) }
        bind() from provider { DeleteAccountViewModelFactory(instance()) }
        bind() from provider { StoryCommentViewModelFactory(instance()) }
        bind() from provider { PracticionerModelFactory(instance()) }
        bind() from provider { VoiceViewModelFactory(instance()) }
    }

    override fun onCreate() {
        super.onCreate()


        FacebookSdk.sdkInitialize(getApplicationContext())
        FacebookSdk.setApplicationId(resources.getString(R.string.facebook_app_id));
        AppEventsLogger.activateApp(this)
        Prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val isDarkEnable = Prefs.getBoolean(AccountSettingsActivity.IS_DARK_ENABLED, false)
        if (isDarkEnable) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        val appSettings = AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(
            AppConfig.AppDetails.REGION
        ).build()

        CometChat.init(this, AppConfig.AppDetails.APP_ID, appSettings,
            object : CometChat.CallbackListener<String>() {
                override fun onSuccess(p0: String?) {
                    StringContract.AppInfo.AUTH_KEY = AppConfig.AppDetails.AUTH_KEY
                    CometChat.setSource("ui-kit", "android", "kotlin")
                    Log.d(TAG, "onSuccess: $p0")
                }

                override fun onError(p0: CometChatException?) {
                    p0?.printStackTrace()
                    p0?.message?.toast(applicationContext)
                    Log.d(TAG, "Initialization failed with exception: " + p0?.message)
                }
            })

        addConnectionListener(TAG)
        CometChatCallListener.addCallListener(TAG, this)
        createNotificationChannel()


        val leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(90 * 1024 * 1024)
        val databaseProvider: DatabaseProvider = ExoDatabaseProvider(this)
        if (simpleCache == null) {
            simpleCache = SimpleCache(cacheDir, leastRecentlyUsedCacheEvictor, databaseProvider)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        CometChatCallListener.removeCallListener(TAG)
        CometChat.removeConnectionListener(TAG)
    }

    private fun addConnectionListener(tag: String) {
        CometChat.addConnectionListener(tag, object : CometChat.ConnectionListener {
            override fun onConnected() {
                //Toast.makeText(baseContext, "Connected", Toast.LENGTH_SHORT).show()
            }

            override fun onConnecting() {}
            override fun onDisconnected() {
//                Toast.makeText(baseContext,"You connection has been broken with server." +
//                        "Please wait for a minute or else restart the app.", Toast.LENGTH_LONG).show()
            }

            override fun onFeatureThrottled() {

            }
        })
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.app_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("2", name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        val TAG = "SpineApplication"
        var simpleCache: SimpleCache? = null
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        RuntimeLocaleChanger.overrideLocale(this)
    }
}