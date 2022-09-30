package com.wiesoftware.spine.ui.home.menus.events.addordup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.EventsRecord
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityAddOrDupEventBinding
import com.wiesoftware.spine.ui.home.menus.events.addevents.AddEventActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import kotlinx.android.synthetic.main.bottom_eventtype_picker.view.*
import kotlinx.android.synthetic.main.bottomsheet_picker.view.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AddOrDupEventActivity : AppCompatActivity(), KodeinAware, AddOrDupEventListener,
    DupEventAdapter.DupEveEventListener {

    override val kodein by kodein()
    val homeRepositry: HomeRepositry by instance()
    lateinit var binding: ActivityAddOrDupEventBinding
    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_or_dup_event)
        val viewModel = ViewModelProvider(this).get(AddOrDupEventViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel.addOrDupEventListener = this
        homeRepositry.getUser().observe(this, Observer { user ->
            userId = user.users_id!!
            getOwnEvents()
        })
    }

    override fun onBack() {
        onBackPressed()
    }

    private fun getOwnEvents() {
        lifecycleScope.launch {
            try {
                val res = homeRepositry.getOwnEvents(userId)
                if (res.status) {
                    BASE_IMAGE = res.image
                    val data = res.data
                    binding.rvDupEvent.also {
                        it.layoutManager =
                            LinearLayoutManager(
                                this@AddOrDupEventActivity,
                                RecyclerView.VERTICAL,
                                false
                            )
                        it.setHasFixedSize(true)
                        it.adapter = DupEventAdapter(data, this@AddOrDupEventActivity)
                    }
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    override fun addNewEvent() {
        showEventPicker()

    }

    override fun onDupEventClick(eveRecord: EventsRecord) {
        val intent = Intent(this, AddEventActivity::class.java)
        intent.putExtra(duplicateEvent, eveRecord)
        intent.putExtra(duplicateImage, BASE_IMAGE)
        intent.putExtra("event_type", eveRecord.type)
        startActivity(intent)
    }

    companion object {
        const val duplicateEvent = "DUPLICATE_EVENT"
        const val duplicateImage = "DUPLICATE_IMAGE"
    }

    private fun showEventPicker() {
        val view: View = layoutInflater.inflate(R.layout.bottom_eventtype_picker, null)
        val dialog: BottomSheetDialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        dialog.setOnShowListener {
            val dialogTmp: BottomSheetDialog = it as BottomSheetDialog
            val bottomSheet: FrameLayout =
                dialogTmp.findViewById(R.id.design_bottom_sheet) as FrameLayout?
                    ?: return@setOnShowListener
            bottomSheet.background = null
        }

        dialog.window?.let {
            it.setGravity(Gravity.BOTTOM)
            it.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(true)
        }
        view.rl_local.setOnClickListener {

            goonAddEventsActivity("0")
            dialog.dismiss()
        }

        view.rl_online.setOnClickListener {
            goonAddEventsActivity("1")
            dialog.dismiss()
        }

        view.rl_retreat.setOnClickListener {
            goonAddEventsActivity("2")
            dialog.dismiss()
        }

        view.rl_metaverse.setOnClickListener {
            goonAddEventsActivity("3")
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun goonAddEventsActivity(valueEvent: String) {
        startActivity(Intent(this, AddEventActivity::class.java).putExtra("event_type", valueEvent))
    }
}