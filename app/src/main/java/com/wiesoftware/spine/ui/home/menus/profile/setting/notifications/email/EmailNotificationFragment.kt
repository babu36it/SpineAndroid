package com.wiesoftware.spine.ui.home.menus.profile.setting.notifications.email

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.FragmentEmailNotificationBinding
import com.wiesoftware.spine.util.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class EmailNotificationFragment : Fragment(), KodeinAware, EmailNotificationEventListener {


    override val kodein by kodein()
    val factory: EmailNotificationViewModelFactory by instance()
    val homeRepositry: HomeRepository by instance()
    lateinit var binding: FragmentEmailNotificationBinding
    lateinit var userId: String
    lateinit var progressDialog: ProgressDialog
    private var emailUpdateStatus: String = "";
    private var eventAttachStatus: String = "";
    private var messageAttachStatus: String = "";
    private var commentStatus: String = "";
    private var eventPodStatus: String = "";
    private var spineHqStatus: String = "";
    private var spineServeyStatus: String = "";

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_email_notification,
            container,
            false
        )
        val viewModel = ViewModelProvider(this, factory).get(EmailNotificationViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel.emailNotiFicationEventListener = this
        progressDialog = ProgressDialog(context)

        val emailUpdateStatus = Prefs.getString(EmailNotificationFragment.emailUpdateStatus, "Off")
        if (emailUpdateStatus.equals("On")) {
            binding.switch13.isChecked = true
            getAllNotifications()
        } else {
            offAllNotifications()

        }

        val eventAttachStatus = Prefs.getString(EmailNotificationFragment.eventAttachStatus, "Off")
        if (eventAttachStatus.equals("On")) {
            binding.switch14.isChecked = true

        }

        val messageAttachStatus =
            Prefs.getString(EmailNotificationFragment.messageAttachStatus, "Off")
        if (messageAttachStatus.equals("On")) {
            binding.switch15.isChecked = true
        }

        val commentStatus = Prefs.getString(EmailNotificationFragment.commentStatus, "Off")
        if (commentStatus.equals("On")) {
            binding.switch16.isChecked = true
        }

        val eventPodStatus = Prefs.getString(EmailNotificationFragment.eventPodStatus, "Off")
        if (eventPodStatus.equals("On")) {
            binding.switch17.isChecked = true
        }

        val spineHqStatus = Prefs.getString(EmailNotificationFragment.spineHqStatus, "Off")
        if (spineHqStatus.equals("On")) {
            binding.switch18.isChecked = true
        }

        val spineServeyStatus = Prefs.getString(EmailNotificationFragment.spineServeyStatus, "Off")
        if (spineServeyStatus.equals("On")) {
            binding.switch19.isChecked = true
        }

        return binding.root
    }

    override fun onEmailUpdateChanged(isChecked: Boolean) {

        if (isChecked) {
            emailUpdateStatus = "1";
        } else {
            emailUpdateStatus = "0";
        }
        mNetworkCallEmailUpdateStatus(emailUpdateStatus)

        /*  if (isChecked) {
              getAllNotifications()
          } else {
              offAllNotifications()
          }*/
    }

    fun getAllNotifications() {
        binding.switch13.isChecked = true
        binding.switch14.isChecked = true
        binding.switch15.isChecked = true
        binding.switch16.isChecked = true
        binding.switch17.isChecked = true
        binding.switch18.isChecked = true
        binding.switch19.isChecked = true
    }

    fun offAllNotifications() {
        binding.switch13.isChecked = false
        binding.switch14.isChecked = false
        binding.switch15.isChecked = false
        binding.switch16.isChecked = false
        binding.switch17.isChecked = false
        binding.switch18.isChecked = false
        binding.switch19.isChecked = false
    }

    override fun onICalChanged(checked: Boolean) {
        if (checked) {
            eventAttachStatus = "1";
        } else {
            eventAttachStatus = "0";
        }
        mNetworkCallEventAttechStatus(eventAttachStatus)
    }

    override fun onHQSurveyChanged(checked: Boolean) {
        if (checked) {
            spineServeyStatus = "1";
        } else {
            spineServeyStatus = "0";
        }
        mNetworkCallSpineServeyStatus(spineServeyStatus)
    }

    override fun onSpineHQChanged(checked: Boolean) {
        if (checked) {
            spineHqStatus = "1";
        } else {
            spineHqStatus = "0";
        }
        mNetworkCallSpineHQStatus(spineHqStatus)
    }

    override fun onEveAndPodChanged(checked: Boolean) {
        if (checked) {
            eventPodStatus = "1";
        } else {
            eventPodStatus = "0";
        }
        mNetworkCallEventPodStatus(eventPodStatus)
    }

    override fun onRepliesChanged(checked: Boolean) {
        if (checked) {
            commentStatus = "1";
        } else {
            commentStatus = "0";
        }
        mNetworkCallCommentStatus(commentStatus)
    }

    override fun onMessageChanged(checked: Boolean) {
        if (checked) {
            messageAttachStatus = "1";
        } else {
            messageAttachStatus = "0";
        }
        mNetworkCallMessageStatus(messageAttachStatus)
    }


    private fun mNetworkCallEmailUpdateStatus(status: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.getAllEmailNotification(status, "", "", "", "", "", "")
                if (res.status) {
                    dismissProgressDailog()
                    val emailstatus = if (status == "0") {
                        offAllNotifications()
                        "Off"
                    } else {
                        getAllNotifications()
                        "On"
                    }
                    Prefs.putAny(EmailNotificationFragment.emailUpdateStatus, emailstatus)
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


    private fun mNetworkCallEventAttechStatus(status: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.getAllEmailNotification("", status, "", "", "", "", "")
                if (res.status) {
                    dismissProgressDailog()
                    val eventAttachStatus = if (status == "0") {
                        "Off"
                    } else {
                        "On"
                    }
                    Prefs.putAny(EmailNotificationFragment.eventAttachStatus, eventAttachStatus)
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


    private fun mNetworkCallMessageStatus(status: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.getAllEmailNotification("", "", status, "", "", "", "")
                if (res.status) {
                    dismissProgressDailog()
                    val messageAttachStatus = if (status == "0") {
                        "Off"
                    } else {
                        "On"
                    }
                    Prefs.putAny(EmailNotificationFragment.messageAttachStatus, messageAttachStatus)
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


    private fun mNetworkCallCommentStatus(status: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.getAllEmailNotification("", "", "", status, "", "", "")
                if (res.status) {
                    dismissProgressDailog()
                    val commentStatus = if (status == "0") {
                        "Off"
                    } else {
                        "On"
                    }
                    Prefs.putAny(EmailNotificationFragment.commentStatus, commentStatus)
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


    private fun mNetworkCallEventPodStatus(status: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.getAllEmailNotification("", "", "", "", status, "", "")
                if (res.status) {
                    dismissProgressDailog()
                    val eventPodStatus = if (status == "0") {
                        "Off"
                    } else {
                        "On"
                    }
                    Prefs.putAny(EmailNotificationFragment.eventPodStatus, eventPodStatus)
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


    private fun mNetworkCallSpineHQStatus(status: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.getAllEmailNotification("", "", "", "", "", status, "")
                if (res.status) {
                    dismissProgressDailog()
                    val spineHqStatus = if (status == "0") {
                        "Off"
                    } else {
                        "On"
                    }
                    Prefs.putAny(EmailNotificationFragment.spineHqStatus, spineHqStatus)
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

    private fun mNetworkCallSpineServeyStatus(status: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.getAllEmailNotification("", "", "", "", "", "", status)
                if (res.status) {
                    dismissProgressDailog()
                    val spineServeyStatus = if (status == "0") {
                        "Off"
                    } else {
                        "On"
                    }
                    Prefs.putAny(EmailNotificationFragment.spineServeyStatus, spineServeyStatus)
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
        val emailUpdateStatus = "emailUpdateStatus"
        val eventAttachStatus = "eventAttachStatus"
        val messageAttachStatus = "messageAttachStatus"
        val commentStatus = "commentStatus"
        val eventPodStatus = "eventPodStatus"
        val spineHqStatus = "spineHqStatus"
        val spineServeyStatus = "spineServeyStatus"
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