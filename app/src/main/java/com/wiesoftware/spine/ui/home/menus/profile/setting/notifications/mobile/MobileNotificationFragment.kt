package com.wiesoftware.spine.ui.home.menus.profile.setting.notifications.mobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.FragmentMobileNotificationBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class MobileNotificationFragment : Fragment(),KodeinAware, MobileNotificationEventListener {

    override val kodein by kodein()
    val homeRepositry: HomeRepositry by instance()
    val factory: MobileNotificationViewModelFactory by instance()
    lateinit var binding: FragmentMobileNotificationBinding
    lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_mobile_notification, container, false)
        val viewModel=ViewModelProvider(this,factory).get(MobileNotificationViewModel::class.java)
        binding.viewmodel=viewModel
        viewModel.mobileNotificationEventListener=this
        viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user->
            userId=user.users_id!!
        })

        return binding.root//inflater.inflate(R.layout.fragment_mobile_notification, container, false)
    }

    override fun onPushNotificationChanged(isChecked: Boolean) {
        if (isChecked){
            getAllNotifications()
        }else{
            removeAllNotifications()
        }
    }

    private fun removeAllNotifications() {
        binding.switch14.isChecked=false
        binding.switch15.isChecked=false
        binding.switch16.isChecked=false
        binding.switch17.isChecked=false
        binding.switch18.isChecked=false
        binding.switch19.isChecked=false
        binding.switch20.isChecked=false
        binding.switch21.isChecked=false
    }

    private fun getAllNotifications() {
        binding.switch14.isChecked=true
        binding.switch15.isChecked=true
        binding.switch16.isChecked=true
        binding.switch17.isChecked=true
        binding.switch18.isChecked=true
        binding.switch19.isChecked=true
        binding.switch20.isChecked=true
        binding.switch21.isChecked=true

    }

    override fun onEveryMemberNotificationChanged(isChecked: Boolean) {

    }

    override fun onImpulseNotificationChanged(isChecked: Boolean) {

    }

    override fun onActivityFollowNotificationChanged(isChecked: Boolean) {

    }

    override fun onMessageNotificationChanged(isChecked: Boolean) {

    }

    override fun onSavedEventNotificationChanged(isChecked: Boolean) {

    }

    override fun onEventUpdateNotificationChanged(isChecked: Boolean) {

    }

    override fun onCommentNotificationChanged(isChecked: Boolean) {

    }

    override fun onLikeNotificationChanged(isChecked: Boolean) {

    }


}