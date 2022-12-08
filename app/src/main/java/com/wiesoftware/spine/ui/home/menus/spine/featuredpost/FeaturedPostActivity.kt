package com.wiesoftware.spine.ui.home.menus.spine.featuredpost

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Size
import android.view.Gravity
import android.view.View
import android.webkit.WebView
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wiesoftware.spine.BuildConfig
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.AdDurationAdapter
import com.wiesoftware.spine.data.adapter.AdsTypeAdapter
import com.wiesoftware.spine.data.net.reponses.AdDurationData
import com.wiesoftware.spine.data.net.reponses.adsmanagment.AdsTypeData
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityFeaturedPost1Binding
import com.wiesoftware.spine.ui.home.camera.CustomCameraActivity
import com.wiesoftware.spine.ui.home.menus.events.TimezoneData
import com.wiesoftware.spine.ui.home.menus.spine.featuredpost.previewfeatured_ad.PreviewAdActivity
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.UriPathHelper
import com.wiesoftware.spine.util.toast
import kotlinx.android.synthetic.main.activity_featured_post1.*
import kotlinx.android.synthetic.main.ad_duration_layout.view.*
import kotlinx.android.synthetic.main.ad_type.view.*
import kotlinx.android.synthetic.main.bottomsheet_picker.view.*
import kotlinx.android.synthetic.main.select_ad_type_layout.view.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class FeaturedPostActivity : AppCompatActivity(), KodeinAware, FeaturedPostEventListener,
    AdDurationAdapter.OnAdDurationSelectedListener, SpineWebViewClient.SpineWebEventListener,
    AdsTypeAdapter.onAdTypeSelectedListner, AdapterView.OnItemSelectedListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    lateinit var binding: ActivityFeaturedPost1Binding
    val homeRepositry: HomeRepositry by instance()
    lateinit var userId: String
    var adDurationDataList: ArrayList<AdDurationData>? = arrayListOf()
    var adsType: ArrayList<AdsTypeData>? = arrayListOf()
    var currentPhotoPath: String? = null
    lateinit var photoURI: Uri
    var adType = 0
    var adDuration = ""
    var startDateSlot = ""
    var startTimeSlot = ""
    var amount = "0"
    var currency = "$"
    var durationId = ""
    var type = ""
    var eventStartDate = ""
    var eventEndDate = ""
    var eventStartTime = ""
    var eventEndTime = ""
    var timezone = ""
    var locationAddress = ""
    private val AUTOCOMPLETE_REQUEST_CODE = 111
    private var isEvent = false
    private var isImagevideo = false
    private var isPoscast = false
    var timeZone: String = ""


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    var lat: Double = 0.0
    var lon: Double = 0.0
    var language = "1"
    lateinit var progressDialog: ProgressDialog


    private fun getLocationUpdates() {
        locationRequest.interval = 50000
        locationRequest.fastestInterval = 50000
        locationRequest.smallestDisplacement = 170f // 170 m = 0.1 mile
        locationRequest.priority =
            LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        lat = location.latitude
                        lon = location.longitude
                        Log.e("loc::", "$lat , $lon")
                        val address: String? = getAddress(lat, lon)
                        Log.e("address2::", "" + address)
                    }
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }

    private fun stopLocationUpdates() {
        locationCallback.let {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                    Log.e("loc::", "$lat , $lon")
                    val address: String? = getAddress(lat, lon)
                    Log.e("address::", "" + address)
                }
            }

    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    private fun getAddress(latitude: Double, longitude: Double): String? {
        val result = StringBuilder()
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses.size > 0) {
                val address: Address = addresses[0]
                result.append(address.locality).append(", ")
                result.append(address.countryName).append(", ")
                result.append(address.subLocality).append(", ")
                result.append(address.subAdminArea)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("tag", e.message!!)
        }
        locationAddress = result.toString()
        binding.et104.setText(locationAddress)
        return locationAddress
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_featured_post1)
        val viewmodel = ViewModelProvider(this).get(FeaturedPostViewmodel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.featuredPostEventListener = this
        progressDialog = ProgressDialog(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest()
        if (hasPermissions(this, permissions)) {
            getCurrentLocation()
            getLocationUpdates()
            startLocationUpdates()
        } else {
            makeRequest()
        }

        val apiKey = getString(R.string.google_maps_key)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
        val placesClient: PlacesClient = Places.createClient(this)
        binding.et104.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                stopLocationUpdates()
                val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this)
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
            }
        }

        homeRepositry.getUser().observe(this, androidx.lifecycle.Observer { user ->
            userId = user.users_id!!
            //  setDurationData()
            getAdDurations()
            mNetworkCallAdsType()
            getTimeSlot()
        })
        binding.editTextTextPersonName34.setOnClickListener {
            openLinkView()
        }
        binding.editTextTextPersonName34.setOnFocusChangeListener { view, b ->
            if (b) {
                openLinkView()
            }
        }
        binding.editTextTextPersonName36.setOnClickListener {
            openLinkView()
        }
        binding.editTextTextPersonName36.setOnFocusChangeListener { view, b ->
            if (b) {
                openLinkView()
            }
        }

        binding.editTextTextPersonName33.setOnClickListener {
            openLinkView()
        }
        binding.editTextTextPersonName33.setOnFocusChangeListener { view, b ->
            if (b) {
                openLinkView()
            }
        }

        val mPictureAdTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.tvAdCounter.text = (90 - s.length).toString()
                //This sets a textview to the current length
//                binding.tvNameCounter.setText(40-s.length);
            }

            override fun afterTextChanged(s: Editable) {
            }
        }

        binding.editTextTextMultiLine2.addTextChangedListener(mPictureAdTextWatcher)


        val mPostedAdTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.tvAdPodcastCounter.text = (90 - s.length).toString()
                //This sets a textview to the current length
//                binding.tvNameCounter.setText(40-s.length);
            }

            override fun afterTextChanged(s: Editable) {
            }
        }

        binding.editTextTextMultiLine3.addTextChangedListener(mPostedAdTextWatcher)


        val mEventNameTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.tvNameCounter.text = (40 - s.length).toString()
                //This sets a textview to the current length
//                binding.tvNameCounter.setText(40-s.length);
            }

            override fun afterTextChanged(s: Editable) {
            }
        }

        binding.edtName.addTextChangedListener(mEventNameTextWatcher)


        val mEventAdTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.tvAdEventCounter.text = (90 - s.length).toString()
                //This sets a textview to the current length
//                binding.tvNameCounter.setText(40-s.length);
            }

            override fun afterTextChanged(s: Editable) {
            }
        }

        binding.editTextTextPersonName35.addTextChangedListener(mEventAdTextWatcher)

    }


    private fun getAdDurations() {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                adDurationDataList!!.clear()
                val res = homeRepositry.getAdDuration()
                if (res.status) {
                    dismissProgressDailog()
                    adDurationDataList = res.data
                } else {
                    dismissProgressDailog()
                    Toast.makeText(this@FeaturedPostActivity, res.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dismissProgressDailog()
            }
        }
    }

    private fun mNetworkCallAdsType() {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                adsType!!.clear()
                val res = homeRepositry.getAdsType()
                if (res.status) {
                    dismissProgressDailog()
                    adsType = res.data

                } else {
                    dismissProgressDailog()
                    Toast.makeText(this@FeaturedPostActivity, res.message, Toast.LENGTH_SHORT)
                        .show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                dismissProgressDailog()
            }
        }
    }

    var timeData: List<TimezoneData> = ArrayList<TimezoneData>()
    private fun getTimeSlot() {
        lifecycleScope.launch {
            try {
                val res = homeRepositry.getTimeZoneResponse()
                if (res.status) {
                    timeData = res.data
                    setTimeZone(timeData)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }


    private fun setTimeZone(timeData: List<TimezoneData>) {
        val list: MutableList<String> = ArrayList()
        list.add(getString(R.string.select))
        for (data in timeData) {
            list.add(data.timezone + " GMT " + data.gmt_offset)
        }
        val aa = ArrayAdapter(this, R.layout.item_spinner, R.id.tvSpinnerItem, list)
        with(spinnertimezone) {
            adapter = aa
            setSelection(0, false)
            onItemSelectedListener = this@FeaturedPostActivity
            prompt = getString(R.string.select)
        }
        //  timeZone = timeData.first().id
    }


    override fun onBack() {
        onBackPressed()
    }

    override fun selectAdDuration() {
        selectAdDurationBottom()
    }

    var dialog: BottomSheetDialog? = null
    private fun selectAdDurationBottom() {
        val view: View = layoutInflater.inflate(R.layout.ad_duration_layout, null)
        dialog = BottomSheetDialog(this)
        dialog!!.setContentView(view)
        dialog!!.setOnShowListener {
            val dialogTmp: BottomSheetDialog = it as BottomSheetDialog
            val bottomSheet: FrameLayout =
                dialogTmp.findViewById(R.id.design_bottom_sheet) as FrameLayout?
                    ?: return@setOnShowListener
            bottomSheet.background = null
        }
        dialog!!.window?.let {
            it.setGravity(Gravity.BOTTOM)
            it.setBackgroundDrawableResource(android.R.color.transparent)
            dialog!!.setCancelable(true)
        }
        if (adDurationDataList != null) {
            view.rvAdDuration.also {
                it.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                it.setHasFixedSize(true)
                val adapter = AdDurationAdapter(adDurationDataList!!, this)
                it.adapter = adapter
            }
        }
        dialog!!.show()
    }

    override fun startDate() {
        showDatePicker(2)
    }

    fun showDatePicker(i: Int) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, day)
        val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.time)
        //binding.textView97.text=date
        timezone = c.timeZone.id
        binding.textView102.setText(timezone)
        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val mm: Int = monthOfYear
                c.set(Calendar.YEAR, year)
                c.set(Calendar.MONTH, mm)
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                startDateSlot = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.time)
                if (i == 1) {
                    binding.textView97.text = startDateSlot
                    eventStartDate = startDateSlot
                } else if (i == 2) {
                    binding.textView296.text = startDateSlot
                } else {
                    eventEndDate = startDateSlot
                    binding.textView98.text = startDateSlot

                }
            },
            year,
            month,
            day
        )
        dpd.datePicker.minDate = c.timeInMillis
        dpd.show()
    }

    override fun selectTime() {
        showTimePicker(2)
    }

    fun showTimePicker(i: Int) {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            val time: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(cal.time)
            startTimeSlot = SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time)
            if (i == 1) {
                binding.textView99.text = startTimeSlot
                eventStartTime = startTimeSlot
            } else if (i == 2) {
                binding.textView297.text = startTimeSlot
            } else {
                binding.textView100.text = startTimeSlot
                eventEndTime = startTimeSlot
            }
        }
        TimePickerDialog(
            this, 2,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    override fun selectAdType() {
        selectAdTypeBottom()
    }


    var adTypeDialog: BottomSheetDialog? = null
    private fun selectAdTypeBottom() {
        val view: View = layoutInflater.inflate(R.layout.ad_type, null)
        adTypeDialog = BottomSheetDialog(this)
        adTypeDialog!!.setContentView(view)
        adTypeDialog!!.setOnShowListener {
            val dialogTmp: BottomSheetDialog = it as BottomSheetDialog
            val bottomSheet: FrameLayout =
                dialogTmp.findViewById(R.id.design_bottom_sheet) as FrameLayout?
                    ?: return@setOnShowListener
            bottomSheet.background = null
        }
        adTypeDialog!!.window?.let {
            it.setGravity(Gravity.BOTTOM)
            it.setBackgroundDrawableResource(android.R.color.transparent)
            adTypeDialog!!.setCancelable(true)
        }
        if (adsType != null) {
            view.rvAdType.also {
                it.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                it.setHasFixedSize(true)
                val adapter = AdsTypeAdapter(adsType!!, this)
                it.adapter = adapter
            }
        }




        adTypeDialog!!.show()
    }

    override fun previewAd(
        picVidWebLink: String,
        picVidAdditionalLine: String,
        eventTitle: String,
        eventTimeZone: String,
        eventLocation: String,
        eventWebLink: String,
        eventAdditionalLine: String,
        podWebLink: String,
        podAdditionalLine: String
    ) {
        if (adDuration.isEmpty()) {
            "Please select ad duration.".toast(this); return
        } else if (startDateSlot.isEmpty()) {
            "Please select start date.".toast(this); return
        } else if (startTimeSlot.isEmpty()) {
            "Please select start Time.".toast(this); return
        } else if (adType == 0) {
            "Please select ad type.".toast(this); return
        }

        when (adType) {
            1 -> {
                if (currentPhotoPath == null) {
                    "Please select media.".toast(this)
                    return
                } else if (picVidWebLink.isEmpty()) {
                    "Please enter destination website.".toast(this); return
                }
                addPicVidAd(picVidWebLink, picVidAdditionalLine)
            }
            2 -> {
                if (currentPhotoPath == null) {
                    "Please select media.".toast(this)
                    return
                } else if (eventTitle.isEmpty()) {
                    "Please enter event title.".toast(this); return
                } else if (type.isEmpty()) {
                    "Please select event type".toast(this); return
                } else if (eventStartDate.isEmpty() || eventEndDate.isEmpty()) {
                    "Please select both start and end date.".toast(this); return
                } else if (eventStartTime.isEmpty() || eventEndTime.isEmpty()) {
                    "Please select both start and end time.".toast(this); return
                } else if (timeZone.isEmpty()) {
                    "Please select timezone".toast(this);return
                } else if (binding.et104.text.toString().trim().isEmpty()) {
                    "Please select location".toast(this);return
                } else if (eventWebLink.isEmpty()) {
                    "Please enter destination website.".toast(this); return
                }
                addEventAd(eventWebLink, eventAdditionalLine, eventTitle)
            }
            3 -> {
                if (currentPhotoPath == null) {
                    "Please select media.".toast(this)
                    return
                } else if (podWebLink.isEmpty()) {
                    "Please enter destination website.".toast(this); return
                }
                addPodAd(podWebLink, podAdditionalLine)
            }
        }

    }

    private fun addPodAd(podWebLink: String, podAdditionalLine: String) {

        var ftype = 1
        val mediaType = contentResolver.getType(photoURI)
        if (mediaType != null && mediaType.contains("video")) {
            ftype = 2
        } else {
            ftype = 1
        }
        val podAdData = PodAdData(
            userId,
            currentPhotoPath!!,
            photoURI.toString(),
            ftype.toString(),
            podWebLink,
            podAdditionalLine,
            startDateSlot,
            startTimeSlot,
            adType.toString(),
            adDuration,
            amount,
            currency,
            durationId,
            lat.toString(),
            lon.toString()
        )
        val intent = Intent(this, PreviewAdActivity::class.java)
        intent.putExtra(AD_TYPE, adType)
        intent.putExtra(POD_AD_DATA, podAdData)
        startActivity(intent)
    }

    private fun addEventAd(eventWebLink: String, eventAdditionalLine: String, eventTitle: String) {


        var ftype = 1
        val mediaType = contentResolver.getType(photoURI)
        if (mediaType != null && mediaType.contains("video")) {
            ftype = 2
        } else {
            ftype = 1
        }
        val eventAdData = EventAdData(
            userId, currentPhotoPath!!, photoURI.toString(),
            ftype.toString(), eventWebLink, eventAdditionalLine, startDateSlot,
            startTimeSlot, adType.toString(), adDuration, amount, currency, durationId,
            eventTitle, lat.toString(), lon.toString(), type.toString(), eventStartDate,
            eventEndDate, eventStartTime, eventEndTime, timezone, locationAddress
        )
        val intent = Intent(this, PreviewAdActivity::class.java)
        intent.putExtra(AD_TYPE, adType)
        intent.putExtra(EVENT_AD_DATA, eventAdData)
        startActivity(intent)

    }

    private fun addPicVidAd(picVidWebLink: String, picVidAdditionalLine: String) {

        var ftype = 1
        val mediaType = contentResolver.getType(photoURI)
        if (mediaType != null && mediaType.contains("video")) {
            ftype = 2
        } else {
            ftype = 1
        }
        val picVidAdData = PicVidAdData(
            userId,
            currentPhotoPath!!,
            photoURI.toString(),
            ftype.toString(),
            picVidWebLink,
            picVidAdditionalLine,
            startDateSlot,
            startTimeSlot,
            adType.toString(),
            adDuration,
            amount,
            currency,
            durationId,
            lat.toString(),
            lon.toString()
        )
        val intent = Intent(this, PreviewAdActivity::class.java)
        intent.putExtra(AD_TYPE, adType)
        intent.putExtra(PIC_VID_AD_DATA, picVidAdData)
        startActivity(intent)
    }

    override fun onPicVidSelect() {
        isEvent = false
        isPoscast = false
        isImagevideo = true

        showPicker()
    }

    private fun showPicker() {
        val view: View = layoutInflater.inflate(R.layout.bottomsheet_picker, null)
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
            dialog.setCancelable(false)
        }
        view.btnCan.setOnClickListener {
            dialog.dismiss()
        }
        if (isImagevideo) {
            view.btnFollow.visibility = View.GONE
            view.textView64.text = "Add image(s) or video(s)"
            view.btnOnline.text = "Choose existing photo or video"
        } else if (isEvent) {
            view.btnFollow.visibility = View.VISIBLE
            view.textView64.text = "Add image(s)"
            view.btnOnline.text = "Choose existing photo"
        } else if (isPoscast) {
            view.btnFollow.visibility = View.VISIBLE
            view.textView64.text = "Add image(s)"
            view.btnOnline.text = "Choose existing photo"

        }

        view.btnFollow.setOnClickListener {
            if (hasPermissions(this, permissions)) {
                //openCustomPicker()
                dispatchTakePictureIntent()
            } else {
                makeRequest()
            }
            dialog.dismiss()
        }
        view.btnOnline.setOnClickListener {
            //startActivity(Intent(this, CustomCameraActivity::class.java))
            if (hasPermissions(this, permissions)) {
                if (isImagevideo) {
                    openGallery()

                } else if (isEvent) {
                    openEvent()
                } else if (isPoscast) {
                    openPoscast()
                }
            } else {
                makeRequest()
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    photoURI = FileProvider.getUriForFile(
                        this,
                        "${BuildConfig.APPLICATION_ID}.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/* , video/*"
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, GALLERY_REQ)
    }

    private fun openEvent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, GALLERY_REQ)
    }

    private fun openPoscast() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, GALLERY_REQ)
    }

    val REQUEST_TAKE_PHOTO = 1
    val GALLERY_REQ = 2
    val PERMISSION_REQUEST_CODE = 94
    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (p in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    p
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun makeRequest() {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
    }

    val permissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private fun openCustomPicker() {
        //Prefs.putAny(IS_FROM, POST_MEDIA)
        startActivity(Intent(this, CustomCameraActivity::class.java))

    }

    override fun onEventImageSelect() {
        isEvent = true
        isPoscast = false
        isImagevideo = false
        showPicker()
    }

    override fun onEventType() {
        pickEventType()
    }

    private fun pickEventType() {
        val view: View = layoutInflater.inflate(R.layout.bottom_event_picker, null)
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
            dialog.setCancelable(false)
        }
        view.btnCan.setOnClickListener {
            dialog.dismiss()
        }
        view.btnFollow.setOnClickListener {
            binding.textView94.setText(R.string.local_event)
            type = "0"
            dialog.dismiss()
        }
        view.btnOnline.setOnClickListener {
            binding.textView94.setText(R.string.online_event)
            type = "1"
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onEventStartDate() {
        showDatePicker(1)
    }

    override fun onEventEndDate() {
        showDatePicker(3)
    }

    override fun onEventStartTime() {
        showTimePicker(1)
    }

    override fun onEventEndTime() {
        showTimePicker(3)
    }

    override fun onPodImageSelect() {
        isEvent = false
        isPoscast = true
        isImagevideo = false
        showPicker()
    }

    var wvAdLink: WebView? = null
    lateinit var pbLink: ProgressBar
    fun openLinkView() {
        val view: View = layoutInflater.inflate(R.layout.bottomsheet_ad_destionation_url, null)
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
        val ibClose = view.findViewById<ImageButton>(R.id.imageButton82)
        val etLink = view.findViewById<EditText>(R.id.textView256)
        val btnAddLink = view.findViewById<TextView>(R.id.button120)
        pbLink = view.findViewById<ProgressBar>(R.id.progressBar8)
        wvAdLink = view.findViewById<WebView>(R.id.wvAdLink)
        wvAdLink?.webViewClient = SpineWebViewClient(this)

        ibClose.setOnClickListener {
            dialog.dismiss()
        }
        btnAddLink.setOnClickListener {
            val link = etLink.text
            binding.editTextTextPersonName33.text = link
            binding.editTextTextPersonName34.text = link
            binding.editTextTextPersonName36.text = link
            dialog.dismiss()
        }
        etLink.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                pbLink.visibility = View.VISIBLE
                loadWebView(p0)
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        dialog.show()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView(p0: CharSequence?) {
        wvAdLink!!.settings.loadsImagesAutomatically = true
        wvAdLink!!.settings.javaScriptEnabled = true
        wvAdLink!!.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        wvAdLink!!.loadUrl(p0.toString())
    }

    override fun onAdDurationSelected(adDurationData: AdDurationData) {
        durationId = adDurationData.id
        val durationType = if (adDurationData.duration_type.equals("1")) {
            "Week"
        } else {
            "Month"
        }
        amount = adDurationData.amount
        currency = adDurationData.currency
        adDuration = adDurationData.duration + " " + durationType + " - " + currency + amount
        binding.button113.text = adDuration
        dialog!!.dismiss()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.e("Place", "Place: ${place.name}, ${place.id}")
                        lat = place.latLng!!.latitude
                        lon = place.latLng!!.longitude
                        binding.et104.setText(place.name)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.e("Place", "" + status.statusMessage)
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                var mImageBitmap: Bitmap? = null
                if (currentPhotoPath!!.endsWith(".mp4")) {
                    mImageBitmap = ThumbnailUtils.createVideoThumbnail(
                        File(currentPhotoPath),
                        Size(120, 120),
                        null
                    )
                } else {
                    mImageBitmap =
                        BitmapFactory.decodeFile(currentPhotoPath) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))
                }

                when (adType) {
                    1 -> binding.imageView42.setImageBitmap(mImageBitmap)
                    2 -> binding.imageView43.setImageBitmap(mImageBitmap)
                    3 -> binding.imageView44.setImageBitmap(mImageBitmap)
                }


            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (requestCode == GALLERY_REQ && resultCode == RESULT_OK) {
            if (data?.clipData != null) {
                val clipData: ClipData = data.clipData!!
                val count = (clipData.itemCount - 1)
                for (i in 0..count) {
                    val imgUri = clipData.getItemAt(i).uri
                    photoURI = imgUri/*data?.data!!*/
                    val uriPathHelper = UriPathHelper()
                    currentPhotoPath = uriPathHelper.getPath(this, photoURI)

                    try {
                        var mImageBitmap: Bitmap? = null
                        if (currentPhotoPath!!.endsWith(".mp4")) {
                            mImageBitmap = ThumbnailUtils.createVideoThumbnail(
                                File(currentPhotoPath),
                                Size(120, 120),
                                null
                            )
                        } else {
                            mImageBitmap =
                                BitmapFactory.decodeFile(currentPhotoPath) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))
                        }
                        when (adType) {
                            1 -> binding.imageView42.setImageBitmap(mImageBitmap)
                            2 -> binding.imageView43.setImageBitmap(mImageBitmap)
                            3 -> binding.imageView44.setImageBitmap(mImageBitmap)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } else {
                photoURI = data?.data!!
                val uriPathHelper = UriPathHelper()
                currentPhotoPath = uriPathHelper.getPath(this, photoURI)
                try {
                    var mImageBitmap: Bitmap? = null
                    if (currentPhotoPath!!.endsWith(".mp4")) {
                        mImageBitmap = ThumbnailUtils.createVideoThumbnail(
                            File(currentPhotoPath),
                            Size(120, 120),
                            null
                        )
                    } else {
                        mImageBitmap =
                            BitmapFactory.decodeFile(currentPhotoPath) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))
                    }
                    when (adType) {
                        /*  1 -> Glide.with(this).load(selectedUri.toString()).into(binding.imageView42)
                          2 -> Glide.with(this).load(selectedUri.toString()).into(binding.imageView43)
                          3 -> Glide.with(this).load(selectedUri.toString()).into(binding.imageView44)*/

                        1 -> binding.imageView42.setImageBitmap(mImageBitmap)
                        2 -> binding.imageView43.setImageBitmap(mImageBitmap)
                        3 -> binding.imageView44.setImageBitmap(mImageBitmap)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun showProgressDialog() {
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun dismissProgressDailog() {
        progressDialog.dismiss()
    }

    override fun onPageLoaded() {
        pbLink.visibility = View.INVISIBLE
    }

    companion object {
        const val PIC_VID_AD_DATA = "picVidAdData"
        const val EVENT_AD_DATA = "eventAdData"
        const val POD_AD_DATA = "podAdData"
        const val AD_TYPE = "adType"
    }

    override fun onAdTypeSelected(adsTypeData: AdsTypeData) {

        if (adsTypeData.id.equals("1")) {
            //picture or video
            isImagevideo = true
            isEvent = false
            isPoscast = false

            binding.textView300.text = getString(R.string.picture_or_video)
            binding.picVid.visibility = View.VISIBLE
            binding.event.visibility = View.GONE
            binding.pod.visibility = View.GONE
            adType = 1
            currentPhotoPath = null
            adTypeDialog!!.dismiss()

        } else if (adsTypeData.id.equals("2")) {

            //event
            isImagevideo = false
            isEvent = true
            isPoscast = false

            binding.textView300.text = getString(R.string.event)
            binding.picVid.visibility = View.GONE
            binding.event.visibility = View.VISIBLE
            binding.pod.visibility = View.GONE
            adType = 2
            currentPhotoPath = null
            adTypeDialog!!.dismiss()

        } else if (adsTypeData.id.equals("3")) {
            //pod
            isImagevideo = false
            isEvent = false
            isPoscast = true


            binding.textView300.text = getString(R.string.podcast)
            binding.picVid.visibility = View.GONE
            binding.event.visibility = View.GONE
            binding.pod.visibility = View.VISIBLE
            adType = 3
            currentPhotoPath = null
            adTypeDialog!!.dismiss()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var ppppp = parent!!.id

        if (ppppp == binding.spinnertimezone.id) {
            try {

                var time = timeData[position]
                timeZone = time.id
//                binding.et104.setText(" ")
            } catch (e: Exception) {

            }
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}

data class PicVidAdData(
    val uid: String,
    val currentPhotoPath: String,
    val photoURI: String,
    val ftype: String,
    val picVidWebLink: String,
    val picVidAdditionalLine: String,
    val startDateSlot: String,
    val startTimeSlot: String,
    val adType: String,
    val adDuration: String,
    val amount: String,
    val curency: String,
    val durationId: String,
    val latitude: String,
    val longitude: String
) : Serializable

data class PodAdData(
    val uid: String,
    val currentPhotoPath: String,
    val photoURI: String,
    val ftype: String,
    val podWebLink: String,
    val podAdditionalLine: String,
    val startDateSlot: String,
    val startTimeSlot: String,
    val adType: String,
    val adDuration: String,
    val amount: String,
    val curency: String,
    val durationId: String,
    val latitude: String,
    val longitude: String
) : Serializable

data class EventAdData(
    val uid: String,
    val currentPhotoPath: String,
    val photoURI: String,
    val ftype: String,
    val eventWebLink: String,
    val eventAdditionalLine: String,
    val startDateSlot: String,
    val startTimeSlot: String,
    val adType: String,
    val adDuration: String,
    val amount: String,
    val curency: String,
    val durationId: String,
    val eventTitle: String,
    val latitude: String,
    val longitude: String,
    val eventType: String,
    val eventStartDate: String,
    val eventEndDate: String,
    val eventStartTime: String,
    val eventEndTime: String,
    val timezone: String,
    val location: String
) : Serializable