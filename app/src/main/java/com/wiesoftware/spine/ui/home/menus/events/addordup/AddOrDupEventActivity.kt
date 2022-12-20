package com.wiesoftware.spine.ui.home.menus.events.addordup

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.EventTypeData
import com.wiesoftware.spine.data.net.reponses.EventsData
import com.wiesoftware.spine.data.net.reponses.EventsRecord
import com.wiesoftware.spine.data.repo.EventRepositry
import com.wiesoftware.spine.databinding.*
import com.wiesoftware.spine.ui.home.menus.events.addevents.AddEventActivity
import com.wiesoftware.spine.ui.home.menus.events.filter.FilterEventActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.util.*
import kotlinx.android.synthetic.main.bottom_eventtype_picker.view.*
import kotlinx.android.synthetic.main.dup_event_list_item.view.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class AddOrDupEventActivity : AppCompatActivity(), KodeinAware, AddOrDupEventListener
     {

    override val kodein by kodein()
    val eventRepositry: EventRepositry by instance()
    lateinit var binding: ActivityAddOrDupEventBinding
    lateinit var userId: String
    lateinit var data : MutableList<EventTypeData>
    lateinit var progress : ProgressDialog
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_or_dup_event)
        val viewModel = ViewModelProvider(this).get(AddOrDupEventViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel.addOrDupEventListener = this
        eventRepositry.getUser().observe(this, Observer { user ->
            userId = user.users_id!!

             progress = ProgressDialog(this)
            progress.setTitle("Loading")
            progress.setMessage("Wait while loading...")
            progress.setCancelable(false) // disable dismiss by tapping outside of the dialog

            getEventType()
            getOwnEvents()

        })
    }

    override fun onBack() {
        onBackPressed()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getOwnEvents() {
        lifecycleScope.launch {
            try {
                progress.show()
                val res = eventRepositry.getOwnEvents()
                progress.dismiss()
                if (res.status) {
                    BASE_IMAGE = res.image
                    val data = res.data

                   var  mAdapter = BaseAdapter<EventsRecord>(this@AddOrDupEventActivity)

                    mAdapter!!.listOfItems = res.data.toList()

                    mAdapter!!.expressionViewHolderBinding = {data,viewBinding,context->

                        var holder = viewBinding as DupEventListItemBinding

                        if (data.type == "1") {
                            holder.textView324.text = "Local Event"
//                holder.itemView.textView327.visibility = View.GONE
                        } else if (data.type == "2") {
                            holder.textView324.text = "Online Event"
//                holder.itemView.textView327.visibility = View.GONE
                        } else if(data.type == "3") {
                            holder.textView324.text = "Retreat Event"
//                holder.itemView.textView327.visibility = View.VISIBLE
                        } else if(data.type == "4") {
                            holder.textView324.text = "Metaverse Sessions"
//                holder.itemView.textView327.visibility = View.GONE
                        }

                        if (data.status == 0){
                            holder.textView327.visibility = View.VISIBLE
                        }else{
                            holder.textView327.visibility = View.GONE
                        }


                        Glide.with(this@AddOrDupEventActivity)
                            .load("https://thespiritualnetwork.com/assets/upload/spine-post/"+data.file)
                            .thumbnail(.1f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error( ColorDrawable(ContextCompat.getColor(this@AddOrDupEventActivity, R.color.light_gry)))
                            .into(holder.imageView48);


                        try {
                            holder.model = data
                            holder.root.setOnClickListener {
                                onDupEventClick(data)
                            }
                            var startdate=data.startDate + " "+ data.startTime
                            var enddate=data.endDate + " "+ data.endTime

                            var startvalue= Utils.dateTimeformat(removeSecond(startdate)!!)
                            var endValue= Utils.dateTimeformat(removeSecond(enddate)!!)


                            holder.textView326.text= startvalue.dayOfMonth.toString()+" "+startvalue.month+" - "+
                                    endValue.dayOfMonth.toString()+" "+endValue.month + " " +startvalue.year.toString() + ", " + Utils.dateTimeformat(startvalue)




                        }catch (e:Exception){

                            Log.d("DupEvents", "onBindViewHolder: $e")
                        }


                    }

                    mAdapter!!.expressionOnCreateViewHolder = {viewGroup->
                        DupEventListItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
                    }

//                    binding.rvDupEvent.apply {
//                        layoutManager = layoutManager
//                        adapter = mAdapter
//                    }


                    binding.rvDupEvent.also {
                        it.layoutManager =
                            LinearLayoutManager(
                                this@AddOrDupEventActivity,
                                RecyclerView.VERTICAL,
                                false
                            )
                        it.setHasFixedSize(true)
                        it.adapter = mAdapter
                    }
                }
            } catch (e: ApiException) {
                progress.dismiss()
                e.printStackTrace()
            } catch (e: NoInternetException) {
                progress.dismiss()
                e.printStackTrace()
            }
        }
    }

    private fun getEventType() {
        lifecycleScope.launch {
            try {
                progress.show()
                val res = eventRepositry.getEventType()
                progress.dismiss()
                if (res.status) {

                     data = res.data


                }
            } catch (e: ApiException) {
                e.printStackTrace()
                progress.dismiss()
            } catch (e: NoInternetException) {
                e.printStackTrace()
                progress.dismiss()
            }
        }
    }

    override fun addNewEvent() {
        showEventPicker()

    }

    fun removeSecond(str: String?): String? {
        var str = str
        if (str != null && str.length > 0) {
            str = str.substring(0, str.length - 3)
        }
        return str
    }


     fun onDupEventClick(eveRecord: EventsRecord) {
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

        var  mAdapter = BaseAdapter<EventTypeData>(this@AddOrDupEventActivity)

        mAdapter!!.listOfItems = data.toList()

        mAdapter!!.expressionViewHolderBinding = {data,viewBinding,context->

            var holder = viewBinding as IteamEventTypeBinding

            holder.txtLocal.text = data.typename
            holder.txtDesc.text = data.description

            holder.root.setOnClickListener {
                onEventClick(data.id)
            }


        }

        mAdapter!!.expressionOnCreateViewHolder = {viewGroup->
            IteamEventTypeBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        }


        view.rv_eventtype.also {
            it.layoutManager =
                LinearLayoutManager(
                    this@AddOrDupEventActivity,
                    RecyclerView.VERTICAL,
                    false
                )
            it.setHasFixedSize(true)
            it.adapter = mAdapter
        }

//        view.rl_local.setOnClickListener {
//
//            goonAddEventsActivity("0")
//            dialog.dismiss()
//        }
//
//        view.rl_online.setOnClickListener {
//            goonAddEventsActivity("1")
//            dialog.dismiss()
//        }
//
//        view.rl_retreat.setOnClickListener {
//            goonAddEventsActivity("2")
//            dialog.dismiss()
//        }
//
//        view.rl_metaverse.setOnClickListener {
//            goonAddEventsActivity("3")
//            dialog.dismiss()
//        }

        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun goonAddEventsActivity(valueEvent: String) {

    }

     fun onEventClick(valueEvent: String) {
        startActivity(Intent(this, AddEventActivity::class.java).putExtra("event_type", valueEvent))
    }
}