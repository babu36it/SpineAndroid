package com.wiesoftware.spine.ui.home.menus.events.preview_event

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.net.reponses.EventsRecord
import com.wiesoftware.spine.data.net.reponses.ImageData
import com.wiesoftware.spine.data.repo.EventRepository
import com.wiesoftware.spine.databinding.ActivityPreviewEventBinding
import com.wiesoftware.spine.ui.home.menus.events.B_IMG_URL
import com.wiesoftware.spine.ui.home.menus.events.EVE_RECORD
import com.wiesoftware.spine.ui.home.menus.events.addevents.AddEventSuccessActivity
import com.wiesoftware.spine.ui.home.menus.profile.setting.currency.CurrencyActivity
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.Prefs
import com.wiesoftware.spine.util.Utils
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PreviewEventActivity : AppCompatActivity(), KodeinAware, PreviewEventsEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val factory: PreviewEventViewmodelFactory by instance()
    val eventRepositry: EventRepository by instance()
    lateinit var binding: ActivityPreviewEventBinding

    var record: EventsRecord? = null

    lateinit var photoURI: Uri
    lateinit var progress : ProgressDialog

    var currentImgPath: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_preview_event)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_preview_event)
        val viewmodel = ViewModelProvider(this, factory).get(PreviewEventViewmodel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.previewEventsEventListener = this
        viewmodel.getUser().observe(this, {

            binding.textView122.text = it.name.toString()

            Glide
                .with(this)
                .load("https://thespiritualnetwork.com/assets/upload/profile/"+it.user_image)
                .centerCrop()
                .placeholder(R.drawable.ic_account)
                .into(binding.circleImageView7);
            //Log.e("namenidhi", it.profile_image.toString())
        })

        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Wait while loading...")
        progress.setCancelable(false) // disable dismiss by tapping outside of the dialog

        setPreviewData()
    }

    private fun setPreviewData() {
        currentImgPath = intent.getStringArrayListExtra(B_IMG_URL)!!
        photoURI = Uri.parse(intent.getStringExtra("imageUri"))
        record = intent.getSerializableExtra(EVE_RECORD) as EventsRecord
        val eve_date = record!!.startDate
        val start_time = record!!.startTime
        val end_time = record!!.endTime
        val location = record!!.location
        val title = record!!.title
        val link = record!!.linkOfEvent
        val about = record!!.description
        //  binding.textView125.text=about
        binding.textView119.text = location
        binding.textView115.text = title
        binding.textView121.text = record!!.languageName
        binding.textView125.text = about
        Log.e("lang", "${record!!.languageName} $about")
        binding.textView117.text = start_time + " - " + end_time + " " + "GMT"
        val type = record!!.type
//        if (type.equals("1")) {
//
//        } else {
//            binding.textView108.text = getString(R.string.local_event)
//            binding.textView118.text = getString(R.string.local)
//        }

        if (type == "0") {
            binding.textView108.text = getString(R.string.local_event)

        } else if (type =="1") {
            binding.textView108.text = getString(R.string.local_event)

        } else if(type == "2") {
            binding.textView108.text = getString(R.string.online_event)
            binding.textView118.text = getString(R.string.online)

        } else if(type == "3"){
            binding.textView108.text = getString(R.string.retreat_event)


        }else{
            binding.textView108.text = getString(R.string.metaverse_event)
            binding.textView118.text = getString(R.string.online)
        }

        if(record!!.linkOfEvent == ""){
            binding.textView521.visibility = View.GONE
        } else {
            binding.textView521.visibility = View.VISIBLE
        }

        if(record!!.fee == 0) {
            binding.textView522.text = "FREE"
        } else {
            binding.textView522.text =  record!!.symbol + " " + record!!.fee
        }




        val simpleDateFormat = SimpleDateFormat("dd-M-yyy", Locale.getDefault())
        try {
            val date1: Date = simpleDateFormat.parse(eve_date)
            val dd = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
            val ss: String = dd.format(date1)
            Log.e("fmtDate: ", ss)
            binding.textView116.text = ss
            val newDay = (ss.split(",")[1]).split(" ")[1]
            val newMonth = (ss.split(",")[1]).split(" ")[2]
            binding.textView76.text = newDay + " " + newMonth
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("fmtDate: ", e.message.toString())
        }


        val imgList: MutableList<ImageData> = ArrayList<ImageData>()

        for (file in currentImgPath) {
            imgList.add(ImageData(file))
        }

        val adapter = PreviewEventImageAdapter(imgList)
        binding.vpSlider.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.vpSlider) { tab, position ->

        }.attach()

        if(imgList.size < 2) {
            binding.tabLayout.visibility = View.INVISIBLE
        }

        binding.vpSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
//                val rBtnId = binding.rgBtn.getChildAt(position).id
//                binding.rgBtn.check(rBtnId)
            }
        })

    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onPost() {
        addEventsData()
    }


    private fun addEventsData() {

        val imgList: MutableList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()

        for ((potion, path) in currentImgPath.withIndex()) {
            val file: File = File(path.toString())
            val requestFile: RequestBody = RequestBody.create(
                contentResolver.getType(photoURI)?.let { it.toMediaTypeOrNull() },
                file
            )
            val img_file: MultipartBody.Part = MultipartBody.Part.createFormData(
                "files[$potion]",
                file.name,
                requestFile
            )

            imgList.add(img_file)
        }


        val uid: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), record!!.userId)
        val title: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), record!!.title)
        val description: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), record!!.description)
        val timeZone: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), record!!.timezone)
        val location: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), record!!.location)
        val link: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), record!!.linkOfEvent)
//        val fee: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), record!!.fee)
        val attendees: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), record!!.maxAttendees)
//        val allow_cmnt: RequestBody =
//            RequestBody.create("multipart/form-data".toMediaTypeOrNull(),  record!!.allowComments)
        val types: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "" + record!!.type)
        val sDate: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "" + record!!.startDate)
        val eDate: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "" + record!!.endDate)
        val sTime: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "" + record!!.startTime)
        val eTime: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "" + record!!.endTime)
        val multiple: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "0")
        val eveCat: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), record!!.eventCategories)
//        val language: RequestBody =
//            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), record!!.language)
        val feeCurency: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), Prefs.getString(
                CurrencyActivity.CURRENCY_ID, "0").toString())
//        val paticipants: RequestBody =
//            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), record!!.acceptParticipants)
        val latitude: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), record!!.latitude)
        val longitude: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), record!!.longitude)

        val bookingurl: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), record!!.booking_url)

        val event_subcategories: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), record!!.eventSubCategories)
        val join_event_link: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), record!!.join_event_link)

//        val status: RequestBody =
//            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), record!!.status)
        lifecycleScope.launch {
            try {
                progress.show()
                val res = eventRepositry.addUserEvent(
                    record!!.status,
                    uid,
                    types,
                    record!!.allowComments,
                    title,
                    description,
                    sTime,
                    sDate,
                    eTime,
                    eDate,
                    timeZone,
                    location,
                    link,

                    eveCat,
                    record!!.fee,
                    feeCurency,
                    attendees,
                    record!!.language,
                    record!!.acceptParticipants,
                    multiple,
                    latitude,
                    longitude,
                    bookingurl,
                    event_subcategories,
                    imgList,
                    join_event_link
                )
                Log.e("eveRes::", "" + res)
                progress.dismiss()
                if (res.status) {
                    Utils.showToast(this@PreviewEventActivity, res.message)
                    //  "Event added successfully.".toast(this@AddEventActivity)
                    startActivity(
                        Intent(
                            this@PreviewEventActivity,
                            AddEventSuccessActivity::class.java
                        )
                    )
                    finish()
                    /// onBackPressed()
                } else {
                    Utils.showToast(this@PreviewEventActivity, res.message)
                    //  "Oops! Something went wrong.".toast(this@AddEventActivity)
                }
            } catch (e: ApiException) {
                progress.dismiss()
                Utils.showToast(this@PreviewEventActivity, e.message.toString())
                e.printStackTrace()
            } catch (e: NoInternetException) {
                progress.dismiss()
                Utils.showToast(this@PreviewEventActivity, e.message.toString())
                e.printStackTrace()
            }
        }
    }
}