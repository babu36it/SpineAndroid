package com.wiesoftware.spine.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.util.AppConfig
import com.wiesoftware.spine.util.hideKeyboard
import kotlinx.android.synthetic.main.activity_home.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import androidx.navigation.fragment.NavHostFragment


class HomeActivity : AppCompatActivity(), KodeinAware {

    private val BROADCAST_DEFAULT_ALBUM_CHANGED: Int = 0
    override val kodein by kodein()
    val homeRepositry: HomeRepositry by instance()

    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment?

        navController = navHostFragment!!.navController
        bottomNavigationView.setupWithNavController(navController)


        val nextscreen = intent.getStringExtra("next_screen")

        if (nextscreen != null && nextscreen.isNotEmpty() && nextscreen == "event") {
            bottomNavigationView.selectedItemId = R.id.eventFragment
        }


        homeRepositry.getUser().observe(this, Observer { user ->
            if (user != null) {
                getCometChatUser(user)
            }
        })
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        hideKeyboard()
        return super.dispatchTouchEvent(ev)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { RuntimeLocaleChanger.wrapContext(it) })
    }


    private fun getCometChatUser(user: com.wiesoftware.spine.data.db.entities.User) {
        val uid = user.users_id!!
        val name = user.display_name ?: user.name!!
        CometChat.getUser(uid, object : CometChat.CallbackListener<User>() {
            override fun onSuccess(p0: User?) {
            }

            override fun onError(p0: CometChatException?) {
                createChatUserWithComet(uid, name)
            }
        })
    }

    private fun createChatUserWithComet(user_id: String, userName: String) {
        val user = User()
        user.uid = user_id
        user.name = userName

        CometChat.createUser(
            user,
            AppConfig.AppDetails.AUTH_KEY,
            object : CometChat.CallbackListener<User>() {
                override fun onSuccess(user: User) {
                    Log.e("userIDCommet:", user.uid)
                }

                override fun onError(e: CometChatException) {
                    e.printStackTrace()
                    Log.e("uError", "" + e.printStackTrace())
                }
            })

    }
}


