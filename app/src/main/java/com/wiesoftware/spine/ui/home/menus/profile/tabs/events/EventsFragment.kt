package com.wiesoftware.spine.ui.home.menus.profile.tabs.events

import android.app.ProgressDialog
import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.OwnEventAdapter
import com.wiesoftware.spine.data.net.reponses.EventsRecord
import com.wiesoftware.spine.data.repo.EventRepository
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.FragmentEventsBinding
import com.wiesoftware.spine.ui.home.menus.events.B_IMG_URL
import com.wiesoftware.spine.ui.home.menus.events.EVE_RECORD
import com.wiesoftware.spine.ui.home.menus.events.addordup.AddOrDupEventActivity
import com.wiesoftware.spine.ui.home.menus.events.event_details.EventDetailActivity
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

val EVE_RECORD = "eve_record"
val B_IMG_URL = "base_url"
val IS_FROM_EVENT_DETAILS = "isFromEventDetails"
var PROFILE_PIC_URL = ""

class EventsFragment : Fragment(), KodeinAware, EventsEventListener,
    OwnEventAdapter.OnEventDetailsListener {
    override val kodein by kodein()
    val factory: EventsViewModelFactory by instance()
    val homeRepositry: HomeRepository by instance()
    val eventRepository: EventRepository by instance()
    lateinit var binding: FragmentEventsBinding
    lateinit var userId: String
    var PROFILE_PIC_URL = ""
    var BASE_IMAGE: String = ""
    lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_events, container, false)
        val viewmodel = ViewModelProvider(this, factory).get(EventsViewModel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.eventsEventListener = this
        progressDialog = ProgressDialog(context)
        viewmodel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            userId = user.users_id!!
            getOwnEvents()
        })
        return binding.root
    }

    private fun getOwnEvents() {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = eventRepository.getOwnEvents()
                if (res.status) {
                    dismissProgressDailog()
                    val data = res.data
                    if (data.size > 0) {
                        binding.tvNoEvent.visibility = View.GONE
                    } else {
                        binding.tvNoEvent.visibility = View.VISIBLE
                    }

                    binding.rvOwnEvents.also {
                        it.layoutManager =
                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                        it.setHasFixedSize(true)
                        it.adapter = OwnEventAdapter(data, this@EventsFragment)
                        it.adapter!!.notifyDataSetChanged()
                    }
                } else {
                    dismissProgressDailog()
                    binding.tvNoEvent.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), res.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                dismissProgressDailog()
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
                dismissProgressDailog()
            }
        }
    }

    override fun onAddevent() {
        startActivity(Intent(requireContext(), AddOrDupEventActivity::class.java))
    }

    override fun onEventDetails(ownEventData: EventsRecord) {
        val intent = Intent(requireContext(), EventDetailActivity::class.java)
        intent.putExtra(EVE_RECORD, ownEventData)
        intent.putExtra(B_IMG_URL, BASE_IMAGE)
        intent.putExtra("event_id", ownEventData.id)
        startActivity(intent)
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