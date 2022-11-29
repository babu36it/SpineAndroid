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
import com.wiesoftware.spine.data.net.Api
import com.wiesoftware.spine.data.net.NetworkConnectionInterceptor
import com.wiesoftware.spine.data.net.RssApi
import com.wiesoftware.spine.data.repo.AuthRepositry
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.data.repo.RssRepository
import com.wiesoftware.spine.ui.auth.AuthViewModelFactory
import com.wiesoftware.spine.ui.auth.WelcomeViewModelFactory
import com.wiesoftware.spine.ui.auth.fb.FbEmailViewModelFactory
import com.wiesoftware.spine.ui.auth.login.LoginViewModelFactory
import com.wiesoftware.spine.ui.auth.number.NumberViewModelFactory
import com.wiesoftware.spine.ui.auth.otp.OtpViewModelFactory
import com.wiesoftware.spine.ui.home.menus.activities.following.FollowingActivityViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.activities.you.YouActivityViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.events.EventFragmentViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.events.addevents.AddEventsViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.events.event_details.EventDetailViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.events.eventcomment.EventCommentViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.events.eventcomment.eventreply.EventReplyViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.events.filter.FilterEventViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.events.maps.MapviewViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.events.preview_event.PreviewEventViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.events.select_users.SelectUsersViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.podcasts.addpodcasts.AddPodcastViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.podcasts.listen.ListenPodcastViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.podcasts.podcastdetails.PodcastDetailsViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.podcasts.userpodcast.UserPodcastViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.podcasts.watch.WtachPodcastViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.ProfileFragmentViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.chat.ChatActivityViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.editprofile.EditProfileViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.follow.FollowViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.follow.followers.FollowersFragmentViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.follow.following.FollowingFragmentViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.masseges.eve_request.EventRequestViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.masseges.msg.MsgFragmentViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.myprofile.MyProfileViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.SettingViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.AccountSettingViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.AccountSettingsActivity
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.change_email.ChangeEmailViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.deactiveaccount.DeactivateViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.deleteAccount.DeleteAccountViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.language.SelectLanguageViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.messaging.MessagingViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.saveEventCalendar.SaveEventViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.currency.CurrencyViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.help.HelpViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.help.privacy.PrivacySettingViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.notifications.NotificationsViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.notifications.email.EmailNotificationViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.setting.notifications.mobile.MobileNotificationViewModelFactory
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SomeoneProfileViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.followers.FollowersActivityViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.tabs.bookmark.BookmarkViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.tabs.events.EventsViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.profile.tabs.posts.PostViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.spine.addposts.hashtags.HashtagViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postmedia.PostMediaViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.spine.addposts.poststory.AddStoryViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postthought.PostThoughtViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.spine.addposts.reviewpost.ReviewPostViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.spine.categories.TrendingCatViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.spine.comment.impulsecomment.ImpulseCommentViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.spine.comment.postcomment.PostCommentViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.spine.comment.storycomment.StoryCommentViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.spine.following.SpineFollowingViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.spine.foryou.SpineForYouViewModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.impulse.ImpulseViewModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.practicioners.PracticionerModelFactory
import com.wiesoftware.spine.ui.home.menus.spine.rec_followers.RecommendedFollowersViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.spine.selectfollowers.SelectFollowersViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.spine.story.StoryViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.spine.story.viewstories.ViewStoryViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.spine.welcome.ViewWelcomeViewmodelFactory
import com.wiesoftware.spine.ui.home.menus.voice_over.VoiceViewmodelFactory
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
class SpineApplication : Application(),KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@SpineApplication))



        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { Api(instance()) }
        bind() from singleton { AppDatabase(instance()) }
        bind() from singleton { AuthRepositry(instance(), instance()) }
        bind() from singleton { HomeRepositry(instance(), instance()) }
        bind() from singleton { RssApi(instance()) }
        bind() from singleton { RssRepository(instance()) }
        bind() from provider { AuthViewModelFactory(instance()) }
        bind() from provider { OtpViewModelFactory(instance()) }
        bind() from provider { NumberViewModelFactory(instance()) }
        bind() from provider { LoginViewModelFactory(instance()) }
        bind() from provider { WelcomeViewModelFactory(instance()) }
        bind() from provider { FbEmailViewModelFactory(instance()) }
        bind() from provider { SpineForYouViewModelFactory(instance()) }
        bind() from provider { ImpulseViewModelFactory(instance()) }
        bind() from provider { StoryViewmodelFactory(instance()) }
        bind() from provider { SettingViewmodelFactory(instance()) }
        bind() from provider { ProfileFragmentViewModelFactory(instance()) }
        bind() from provider { ImpulseCommentViewmodelFactory(instance()) }
        bind() from provider { ViewWelcomeViewmodelFactory(instance()) }
        bind() from provider { AddStoryViewmodelFactory(instance()) }
        bind() from provider { EventFragmentViewmodelFactory(instance()) }
        bind() from provider { YouActivityViewmodelFactory(instance()) }
        bind() from provider { PostViewmodelFactory(instance()) }
        bind() from provider { FilterEventViewmodelFactory(instance()) }
        bind() from provider { TrendingCatViewmodelFactory(instance()) }
        bind() from provider { EditProfileViewmodelFactory(instance()) }
        bind() from provider { RecommendedFollowersViewmodelFactory(instance()) }
        bind() from provider { ViewStoryViewmodelFactory(instance()) }
        bind() from provider { SelectFollowersViewmodelFactory(instance()) }
        bind() from provider { PostCommentViewmodelFactory(instance()) }
        bind() from provider { SpineFollowingViewmodelFactory(instance()) }
        bind() from provider { PostThoughtViewmodelFactory(instance()) }
        bind() from provider { HashtagViewmodelFactory(instance()) }
        bind() from provider { ReviewPostViewmodelFactory(instance()) }
        bind() from provider { AddEventsViewmodelFactory(instance()) }
        bind() from provider { FollowingActivityViewmodelFactory(instance()) }
        bind() from provider { EventsViewmodelFactory(instance()) }
        bind() from provider { EventDetailViewmodelFactory(instance()) }
        bind() from provider { SelectUsersViewmodelFactory(instance()) }
        bind() from provider { PreviewEventViewmodelFactory(instance()) }
        bind() from provider { MsgFragmentViewmodelFactory(instance()) }
        bind() from provider { ChatActivityViewmodelFactory(instance()) }
        bind() from provider { MyProfileViewmodelFactory(instance()) }
        bind() from provider { FollowViewmodelFactory(instance()) }
        bind() from provider { FollowersFragmentViewmodelFactory(instance()) }
        bind() from provider { FollowingFragmentViewmodelFactory(instance()) }
        bind() from provider { BookmarkViewmodelFactory(instance()) }
        bind() from provider { NotificationsViewmodelFactory(instance()) }
        bind() from provider { AccountSettingViewmodelFactory(instance()) }
        bind() from provider { MapviewViewmodelFactory(instance()) }
        bind() from provider { ChangeEmailViewmodelFactory(instance()) }
        bind() from provider { MessagingViewmodelFactory(instance()) }
        bind() from provider { SaveEventViewmodelFactory(instance()) }
        bind() from provider { AddPodcastViewmodelFactory(instance()) }
        bind() from provider { EventRequestViewmodelFactory(instance()) }
        bind() from provider { SelectLanguageViewmodelFactory(instance()) }
        bind() from provider { DeactivateViewmodelFactory(instance()) }
        bind() from provider { CurrencyViewmodelFactory(instance()) }
        bind() from provider { HelpViewmodelFactory(instance()) }
        bind() from provider { PostMediaViewmodelFactory(instance()) }
        bind() from provider { ListenPodcastViewmodelFactory(instance()) }
        bind() from provider { WtachPodcastViewmodelFactory(instance()) }
        bind() from provider { UserPodcastViewmodelFactory(instance()) }
        bind() from provider { PodcastDetailsViewmodelFactory(instance()) }
        bind() from provider { PrivacySettingViewmodelFactory(instance()) }
        bind() from provider { MobileNotificationViewModelFactory(instance()) }
        bind() from provider { EmailNotificationViewModelFactory(instance()) }
        bind() from provider { SomeoneProfileViewmodelFactory(instance()) }
        bind() from provider { FollowersActivityViewmodelFactory(instance()) }
        bind() from provider { EventCommentViewmodelFactory(instance()) }
        bind() from provider { EventReplyViewmodelFactory(instance()) }
        bind() from provider { DeleteAccountViewmodelFactory(instance()) }
        bind() from provider { StoryCommentViewmodelFactory(instance()) }
        bind() from provider { PracticionerModelFactory(instance()) }
        bind() from provider { VoiceViewmodelFactory(instance()) }
    }

    override fun onCreate() {
        super.onCreate()


        FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(this)
        Prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val isDarkEnable= Prefs.getBoolean(AccountSettingsActivity.IS_DARK_ENABLED, false)
        if (isDarkEnable){
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

    fun createNotificationChannel(){
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

    companion object{
        val TAG="SpineApplication"
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