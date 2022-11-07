package com.wiesoftware.spine.ui.home.menus.profile.setting.notifications.mobile

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.FragmentMobileNotificationBinding
import com.wiesoftware.spine.ui.home.menus.profile.setting.notifications.email.EmailNotificationFragment
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.Prefs
import com.wiesoftware.spine.util.putAny
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class MobileNotificationFragment : Fragment(), KodeinAware, MobileNotificationEventListener {

    override val kodein by kodein()
    val homeRepositry: HomeRepositry by instance()
    val factory: MobileNotificationViewModelFactory by instance()
    lateinit var binding: FragmentMobileNotificationBinding
    lateinit var userId: String
    lateinit var progressDialog: ProgressDialog
    private var likeMyStuffStatus: String = ""
    private var commentStatus: String = ""
    private var eventUpdateStatus: String = ""
    private var eventReminderStatus: String = ""
    private var messagesStatus: String = ""
    private var followStatus: String = ""
    private var impulsesStatus: String = ""
    private var eventMemberStatus: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_mobile_notification,
            container,
            false
        )
        val viewModel =
            ViewModelProvider(this, factory).get(MobileNotificationViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel.mobileNotificationEventListener = this
        progressDialog = ProgressDialog(context)
        viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            userId = user.users_id!!
        })

        val likeMyStuffStatus = Prefs.getString(MobileNotificationFragment.likeMyStuffStatus, "Off")
        if (likeMyStuffStatus.equals("On")) {
            binding.switch14.isChecked = true
        }

        val commentStatus = Prefs.getString(MobileNotificationFragment.commentStatus, "Off")
        if (commentStatus.equals("On")) {
            binding.switch15.isChecked = true
        }

        val eventUpdateStatus = Prefs.getString(MobileNotificationFragment.eventUpdateStatus, "Off")
        if (eventUpdateStatus.equals("On")) {
            binding.switch16.isChecked = true
        }

        val eventReminderStatus =
            Prefs.getString(MobileNotificationFragment.eventReminderStatus, "Off")
        if (eventReminderStatus.equals("On")) {
            binding.switch17.isChecked = true
        }


        val messagesStatus = Prefs.getString(MobileNotificationFragment.messagesStatus, "Off")
        if (messagesStatus.equals("On")) {
            binding.switch18.isChecked = true
        }

        val followStatus = Prefs.getString(MobileNotificationFragment.followStatus, "Off")
        if (followStatus.equals("On")) {
            binding.switch19.isChecked = true
        }

        val impulsesStatus = Prefs.getString(MobileNotificationFragment.impulsesStatus, "Off")
        if (impulsesStatus.equals("On")) {
            binding.switch20.isChecked = true
        }


        val eventMemberStatus = Prefs.getString(MobileNotificationFragment.everyMemberStatus, "Off")
        if (eventMemberStatus.equals("On")) {
            binding.switch21.isChecked = true
        }

        return binding.root//inflater.inflate(R.layout.fragment_mobile_notification, container, false)
    }

    override fun onPushNotificationChanged(isChecked: Boolean) {
        if (isChecked) {
            getAllNotifications()
        } else {
            removeAllNotifications()
        }
    }

    private fun removeAllNotifications() {
        binding.switch14.isChecked = false
        binding.switch15.isChecked = false
        binding.switch16.isChecked = false
        binding.switch17.isChecked = false
        binding.switch18.isChecked = false
        binding.switch19.isChecked = false
        binding.switch20.isChecked = false
        binding.switch21.isChecked = false
    }

    private fun getAllNotifications() {
        binding.switch14.isChecked = true
        binding.switch15.isChecked = true
        binding.switch16.isChecked = true
        binding.switch17.isChecked = true
        binding.switch18.isChecked = true
        binding.switch19.isChecked = true
        binding.switch20.isChecked = true
        binding.switch21.isChecked = true

    }

    override fun onEveryMemberNotificationChanged(isChecked: Boolean) {
        if (isChecked) {
            eventMemberStatus = "1";
        } else {
            eventMemberStatus = "0";
        }
        mNetworkCalleventMemberStatusAPI(eventMemberStatus)
    }

    override fun onImpulseNotificationChanged(isChecked: Boolean) {
        if (isChecked) {
            impulsesStatus = "1";
        } else {
            impulsesStatus = "0";
        }
        mNetworkCallimpulsesStatusAPI(impulsesStatus)
    }

    override fun onActivityFollowNotificationChanged(isChecked: Boolean) {
        if (isChecked) {
            followStatus = "1";
        } else {
            followStatus = "0";
        }
        mNetworkCallMemberFollowAPI(followStatus)
    }

    override fun onMessageNotificationChanged(isChecked: Boolean) {
        if (isChecked) {
            messagesStatus = "1";
        } else {
            messagesStatus = "0";
        }
        mNetworkCallMessagesStatusAPI(messagesStatus)
    }

    override fun onSavedEventNotificationChanged(isChecked: Boolean) {
        if (isChecked) {
            eventReminderStatus = "1";
        } else {
            eventReminderStatus = "0";
        }
        mNetworkCallEventReminderStatusAPI(eventReminderStatus)
    }

    override fun onEventUpdateNotificationChanged(isChecked: Boolean) {
        if (isChecked) {
            eventUpdateStatus = "1";
        } else {
            eventUpdateStatus = "0";
        }
        mNetworkCallEventUpdateStatusAPI(eventUpdateStatus)
    }

    override fun onCommentNotificationChanged(isChecked: Boolean) {
        if (isChecked) {
            commentStatus = "1";
        } else {
            commentStatus = "0";
        }
        mNetworkCallCommnetStatusAPI(commentStatus)
    }

    override fun onLikeNotificationChanged(isChecked: Boolean) {
        if (isChecked) {
            likeMyStuffStatus = "1";
        } else {
            likeMyStuffStatus = "0";
        }
        mNetworkCallLikeAPI(likeMyStuffStatus)
    }

    private fun mNetworkCallLikeAPI(status: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.getAllMobileNotifications(
                    "", status, "", "", "", "", "",
                    "", ""
                )
                if (res.status) {
                    dismissProgressDailog()
                    Toast.makeText(context, res.message, Toast.LENGTH_SHORT)
                        .show()
                    val likeMyStuffStatus = if (status == "0") {
                        "Off"
                    } else {
                        "On"
                    }
                    Prefs.putAny(MobileNotificationFragment.likeMyStuffStatus, likeMyStuffStatus)
                } else {
                    dismissProgressDailog()
                    Toast.makeText(context, res.message, Toast.LENGTH_SHORT)
                        .show()

                }
            } catch (e: ApiException) {
                dismissProgressDailog()
                e.printStackTrace()
            } catch (e: NoInternetException) {
                dismissProgressDailog()
                e.printStackTrace()
            }

        }

    }

    private fun mNetworkCallCommnetStatusAPI(status: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.getAllMobileNotifications(
                    "", "", status, "", "", "", "",
                    "", ""
                )
                if (res.status) {
                    dismissProgressDailog()
                    Toast.makeText(context, res.message, Toast.LENGTH_SHORT)
                        .show()
                    val commentStatus = if (status == "0") {
                        "Off"
                    } else {
                        "On"
                    }
                    Prefs.putAny(MobileNotificationFragment.commentStatus, commentStatus)
                } else {
                    dismissProgressDailog()
                    Toast.makeText(context, res.message, Toast.LENGTH_SHORT)
                        .show()

                }
            } catch (e: ApiException) {
                dismissProgressDailog()
                e.printStackTrace()
            } catch (e: NoInternetException) {
                dismissProgressDailog()
                e.printStackTrace()
            }

        }

    }

    private fun mNetworkCallEventUpdateStatusAPI(status: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.getAllMobileNotifications(
                    "", "", "", status, "", "", "",
                    "", ""
                )
                if (res.status) {
                    dismissProgressDailog()
                    Toast.makeText(context, res.message, Toast.LENGTH_SHORT)
                        .show()
                    val eventUpdateStatus = if (status == "0") {
                        "Off"
                    } else {
                        "On"
                    }
                    Prefs.putAny(MobileNotificationFragment.eventUpdateStatus, eventUpdateStatus)
                } else {
                    dismissProgressDailog()
                    Toast.makeText(context, res.message, Toast.LENGTH_SHORT)
                        .show()

                }
            } catch (e: ApiException) {
                dismissProgressDailog()
                e.printStackTrace()
            } catch (e: NoInternetException) {
                dismissProgressDailog()
                e.printStackTrace()
            }

        }

    }

    private fun mNetworkCallEventReminderStatusAPI(status: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.getAllMobileNotifications(
                    "", "", "", "", status, "", "",
                    "", ""
                )
                if (res.status) {
                    dismissProgressDailog()
                    Toast.makeText(context, res.message, Toast.LENGTH_SHORT)
                        .show()
                    val eventReminderStatus = if (status == "0") {
                        "Off"
                    } else {
                        "On"
                    }
                    Prefs.putAny(
                        MobileNotificationFragment.eventReminderStatus,
                        eventReminderStatus
                    )
                } else {
                    dismissProgressDailog()
                    Toast.makeText(context, res.message, Toast.LENGTH_SHORT)
                        .show()

                }
            } catch (e: ApiException) {
                dismissProgressDailog()
                e.printStackTrace()
            } catch (e: NoInternetException) {
                dismissProgressDailog()
                e.printStackTrace()
            }

        }

    }

    private fun mNetworkCallMessagesStatusAPI(status: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.getAllMobileNotifications(
                    "", "", "", "", "", status, "",
                    "", ""
                )
                if (res.status) {
                    dismissProgressDailog()
                    Toast.makeText(context, res.message, Toast.LENGTH_SHORT)
                        .show()
                    val messagesStatus = if (status == "0") {
                        "Off"
                    } else {
                        "On"
                    }
                    Prefs.putAny(MobileNotificationFragment.messagesStatus, messagesStatus)
                } else {
                    dismissProgressDailog()
                    Toast.makeText(context, res.message, Toast.LENGTH_SHORT)
                        .show()

                }
            } catch (e: ApiException) {
                dismissProgressDailog()
                e.printStackTrace()
            } catch (e: NoInternetException) {
                dismissProgressDailog()
                e.printStackTrace()
            }

        }

    }


    private fun mNetworkCallMemberFollowAPI(status: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.getAllMobileNotifications(
                    "", "", "", "", "", "", status,
                    "", ""
                )
                if (res.status) {
                    dismissProgressDailog()
                    Toast.makeText(context, res.message, Toast.LENGTH_SHORT)
                        .show()
                    val followStatus = if (status == "0") {
                        "Off"
                    } else {
                        "On"
                    }
                    Prefs.putAny(MobileNotificationFragment.followStatus, followStatus)
                } else {
                    dismissProgressDailog()
                    Toast.makeText(context, res.message, Toast.LENGTH_SHORT)
                        .show()

                }
            } catch (e: ApiException) {
                dismissProgressDailog()
                e.printStackTrace()
            } catch (e: NoInternetException) {
                dismissProgressDailog()
                e.printStackTrace()
            }

        }

    }

    private fun mNetworkCallimpulsesStatusAPI(status: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.getAllMobileNotifications(
                    "", "", "", "", "", "", "",
                    status, ""
                )
                if (res.status) {
                    dismissProgressDailog()
                    Toast.makeText(context, res.message, Toast.LENGTH_SHORT)
                        .show()
                    val impulsesStatus = if (status == "0") {
                        "Off"
                    } else {
                        "On"
                    }
                    Prefs.putAny(MobileNotificationFragment.impulsesStatus, impulsesStatus)
                } else {
                    dismissProgressDailog()
                    Toast.makeText(context, res.message, Toast.LENGTH_SHORT)
                        .show()

                }
            } catch (e: ApiException) {
                dismissProgressDailog()
                e.printStackTrace()
            } catch (e: NoInternetException) {
                dismissProgressDailog()
                e.printStackTrace()
            }

        }

    }


    private fun mNetworkCalleventMemberStatusAPI(status: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.getAllMobileNotifications(
                    "", "", "", "", "", "", "",
                    "", status
                )
                if (res.status) {
                    dismissProgressDailog()
                    Toast.makeText(context, res.message, Toast.LENGTH_SHORT)
                        .show()
                    val eventMemberStatus = if (status == "0") {
                        "Off"
                    } else {
                        "On"
                    }
                    Prefs.putAny(MobileNotificationFragment.everyMemberStatus, eventMemberStatus)
                } else {
                    dismissProgressDailog()
                    Toast.makeText(context, res.message, Toast.LENGTH_SHORT)
                        .show()

                }
            } catch (e: ApiException) {
                dismissProgressDailog()
                e.printStackTrace()
            } catch (e: NoInternetException) {
                dismissProgressDailog()
                e.printStackTrace()
            }

        }

    }


    companion object {
        val likeMyStuffStatus = "likeMyStuffStatus"
        val commentStatus = "commentStatus"
        val eventUpdateStatus = "eventUpdateStatus"
        val eventReminderStatus = "eventReminderStatus"
        val messagesStatus = "messagesStatus"
        val followStatus = "followStatus"
        val impulsesStatus = "impulsesStatus"
        val everyMemberStatus = "everyMemberStatus"
    }


    private fun showProgressDialog() {
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun dismissProgressDailog() {
        progressDialog.dismiss()
    }


}