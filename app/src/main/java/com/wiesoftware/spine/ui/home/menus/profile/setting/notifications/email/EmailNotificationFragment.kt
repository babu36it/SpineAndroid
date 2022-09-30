package com.wiesoftware.spine.ui.home.menus.profile.setting.notifications.email

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.FragmentEmailNotificationBinding
import com.wiesoftware.spine.util.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class EmailNotificationFragment : Fragment(),KodeinAware, EmailNotificationEventListener {


    override val kodein by kodein()
    val factory: EmailNotificationViewModelFactory by instance()
    val homeRepositry: HomeRepositry by instance()
    lateinit var binding:FragmentEmailNotificationBinding
    lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            binding=DataBindingUtil.inflate(inflater,R.layout.fragment_email_notification, container, false)
            val viewModel=ViewModelProvider(this,factory).get(EmailNotificationViewModel::class.java)
            binding.viewmodel=viewModel
            viewModel.emailNotiFicationEventListener=this

            return binding.root
    }

    override fun onEmailUpdateChanged(isChecked: Boolean) {
        if (isChecked){
            getAllNotifications()
        }else{
            offAllNotifications()
        }
    }

    fun getAllNotifications(){
        binding.switch14.isChecked=true
        binding.switch15.isChecked=true
        binding.switch16.isChecked=true
        binding.switch17.isChecked=true
        binding.switch18.isChecked=true
        binding.switch19.isChecked=true
    }
    fun offAllNotifications(){
        binding.switch14.isChecked=false
        binding.switch15.isChecked=false
        binding.switch16.isChecked=false
        binding.switch17.isChecked=false
        binding.switch18.isChecked=false
        binding.switch19.isChecked=false
    }

    override fun onICalChanged(checked: Boolean) {
     //   "ICAL: $checked".toast(requireContext())
    }

    override fun onHQSurveyChanged(checked: Boolean) {

    }

    override fun onSpineHQChanged(checked: Boolean) {

    }

    override fun onEveAndPodChanged(checked: Boolean) {

    }

    override fun onRepliesChanged(checked: Boolean) {

    }

    override fun onMessageChanged(checked: Boolean) {

    }

}