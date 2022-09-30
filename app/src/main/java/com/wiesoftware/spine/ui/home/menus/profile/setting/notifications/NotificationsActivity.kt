package com.wiesoftware.spine.ui.home.menus.profile.setting.notifications

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.NotificationAdapter
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityNotificationsBinding
import com.wiesoftware.spine.ui.home.menus.profile.masseges.MessagesTabadapter
import com.wiesoftware.spine.ui.home.menus.profile.masseges.eve_request.EveRequestFragment
import com.wiesoftware.spine.ui.home.menus.profile.masseges.msg.MsgFragment
import com.wiesoftware.spine.ui.home.menus.profile.setting.notifications.email.EmailNotificationFragment
import com.wiesoftware.spine.ui.home.menus.profile.setting.notifications.mobile.MobileNotificationFragment
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class NotificationsActivity : AppCompatActivity(),KodeinAware, NotificationsEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val homeRepositry: HomeRepositry by instance()
    val factory: NotificationsViewmodelFactory by instance()
    lateinit var binding: ActivityNotificationsBinding
    var userId: String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_notifications)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_notifications)
        val viewmodel=ViewModelProvider(this,factory).get(NotificationsViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.notificationsEventListener=this
        viewmodel.getLoggedInUser().observe(this, Observer { user->
            userId=user.users_id!!
            //getNotifications()
        })

        setUpViewPager()
        addTabs()
    }
    private fun addTabs() {
        TabLayoutMediator(binding.tabLayout2,binding.viewpager2){tab, position ->
        }.attach()
        binding.tabLayout2.getTabAt(0)?.text = resources.getText(R.string.mobile)
        binding.tabLayout2.getTabAt(1)?.text = resources.getText(R.string.email)
    }

    private fun setUpViewPager() {
        val adapter= MessagesTabadapter(this)
        adapter.addFragment(MobileNotificationFragment())
        adapter.addFragment(EmailNotificationFragment())
        binding.viewpager2.offscreenPageLimit=2
        binding.viewpager2.adapter=adapter
    }
    private fun getNotifications() {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.getUserNotifications(userId)
                if (res.status){
                    val datalist=res.data
                    binding.rvNoti.also {
                        it.layoutManager=LinearLayoutManager(this@NotificationsActivity,RecyclerView.VERTICAL,false)
                        it.setHasFixedSize(true)
                        it.adapter=NotificationAdapter(datalist)
                    }
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onBack() {
        onBackPressed()
    }
}