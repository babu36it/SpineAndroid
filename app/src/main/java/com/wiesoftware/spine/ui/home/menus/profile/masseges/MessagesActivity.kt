package com.wiesoftware.spine.ui.home.menus.profile.masseges

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.tabs.TabLayoutMediator
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.databinding.ActivityMessagesBinding
import com.wiesoftware.spine.ui.home.menus.profile.masseges.eve_request.EveRequestFragment
import com.wiesoftware.spine.ui.home.menus.profile.masseges.feedback.FeedBackFragment
import com.wiesoftware.spine.ui.home.menus.profile.masseges.msg.MsgFragment
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

class MessagesActivity : AppCompatActivity(),KodeinAware, MessagesEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    lateinit var binding: ActivityMessagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_messages)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_messages)
        val viewmodel=ViewModelProvider(this).get(MessagesViewModel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.messagesEventListener=this

        setUpViewPager()
        addTabs()
    }

    private fun addTabs() {
        TabLayoutMediator(binding.tabLayout2,binding.viewpager2){tab, position ->
        }.attach()

//        binding.tabLayout2.getTabAt(0)?.setCustomView(R.layout.tab_badge)
//
//        binding.tabLayout2.getTabAt(0)?.b
        //set the badge
        //set the badge
        val badgeDrawable: BadgeDrawable = binding.tabLayout2.getTabAt(0)?.getOrCreateBadge()!!
        badgeDrawable.setVisible(true)
        badgeDrawable.backgroundColor = getResources().getColor(R.color.color_red);
        badgeDrawable.horizontalOffset = -20

        val badgeDrawable2: BadgeDrawable = binding.tabLayout2.getTabAt(1)?.getOrCreateBadge()!!
        badgeDrawable2.setVisible(true)
        badgeDrawable2.backgroundColor = getResources().getColor(R.color.color_red);
        badgeDrawable2.horizontalOffset = -20
        binding.tabLayout2.getTabAt(0)?.text = resources.getText(R.string.message)
        binding.tabLayout2.getTabAt(1)?.text = resources.getText(R.string.events_requests)
        binding.tabLayout2.getTabAt(2)?.text = resources.getText(R.string.feedback)
    }

    private fun setUpViewPager() {
        val adapter= MessagesTabadapter(this)
        adapter.addFragment(MsgFragment())
        adapter.addFragment(EveRequestFragment())
        adapter.addFragment(FeedBackFragment())
        binding.viewpager2.offscreenPageLimit=2
        binding.viewpager2.adapter=adapter
    }

    override fun onBack() {
        onBackPressed()
    }
}