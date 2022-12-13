package com.wiesoftware.spine.ui.home.menus.events.addevents


import android.Manifest
import android.R.attr
import android.app.*
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.PodcastSubcategoryAdapter
import com.wiesoftware.spine.data.net.reponses.*
import com.wiesoftware.spine.data.repo.EventRepositry
import com.wiesoftware.spine.data.repo.SettingsRepository
import com.wiesoftware.spine.databinding.ActivityAddEventBinding
import com.wiesoftware.spine.ui.home.menus.events.B_IMG_URL
import com.wiesoftware.spine.ui.home.menus.events.EVE_RECORD
import com.wiesoftware.spine.ui.home.menus.events.TimezoneData
import com.wiesoftware.spine.ui.home.menus.events.addordup.AddOrDupEventActivity
import com.wiesoftware.spine.ui.home.menus.events.preview_event.PreviewEventActivity
import com.wiesoftware.spine.ui.home.menus.events.preview_event.PreviewEventImageAdapter
import com.wiesoftware.spine.ui.home.menus.profile.setting.currency.CurrencyActivity
import com.wiesoftware.spine.ui.home.menus.profile.setting.currency.CurrencyActivity.Companion.CURRENCY_ID
import com.wiesoftware.spine.ui.home.menus.profile.setting.currency.CurrencyActivity.Companion.CURRENCY_SYMBOL
import com.wiesoftware.spine.util.*
import kotlinx.android.synthetic.main.activity_add_event.*
import kotlinx.android.synthetic.main.bottomsheet_picker.view.*
import kotlinx.android.synthetic.main.eve_cat_selection.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.kodein.di.KodeinAware
import org.kodein.di.android.BuildConfig
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddEventActivity : AppCompatActivity(), KodeinAware, AddEventsListener,
    AdapterView.OnItemSelectedListener, SpinerCatAdapter.OnEveItemChecked,
    SpinerCatAdapter.ListValue, PodcastSubcategoryAdapter.OnPodSubCatSelectedListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    private val AUTOCOMPLETE_REQUEST_CODE = 111
    val REQUEST_TAKE_PHOTO = 1
    val GALLERY_REQ = 2
    val PERMISSION_REQUEST_CODE = 94
    var code: Int = 105
    var parent_id = "0"
    val permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )
    var timeZone:String=""
    var currentPhotoPath: String? = null
    lateinit var photoURI: Uri

    override val kodein by kodein()
    val eventRepositry: EventRepositry by instance()
    val settingsRepositry: SettingsRepository by instance()
    val factory: AddEventsViewmodelFactory by instance()
    lateinit var binding: ActivityAddEventBinding
    var peviewlangague: String = ""

    lateinit var user_id: String;
    var type: Int = 0;
    var allow_comments: Int = 1;
    var allow_participants: String = "1"
    var startDate: String = "";
    var endDate: String = "";
    var startTime: String = "";
    var endTime: String = ""
    var category: String = ""
    var categoryIds: String = "";
    var curency: String = "$";
    var currencyId: String = "0"
    var mImageBitmap: Bitmap? = null
    var subCatIds: String = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback



    var lat: Double = 0.0
    var lon: Double = 0.0
    var language = ""
    var about: String = ""
    var languagenname: String = ""
    var viewmodel: AddEventViewmodel? = null

    var imgPaths = ArrayList<String>()

    companion object {
        var isAddNewEvent = false
    }

    var subCatedataList = listOf<PodcastSubCategoryData>()

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

    //Add Events
    var isAdditionalBtnClick = true
    override fun onAddAditionalCategory() {
        if (isAdditionalBtnClick) {
            binding.editTextTextPersonName31.visibility = View.VISIBLE
            binding.button101.visibility = View.VISIBLE
            isAdditionalBtnClick = false
            binding.imageButton73.setImageResource(R.drawable.ic_minus)
        } else {
            binding.editTextTextPersonName31.visibility = View.GONE
            binding.button101.visibility = View.GONE
            isAdditionalBtnClick = true
            binding.imageButton73.setImageResource(R.drawable.ic_add_new)
        }
    }


    override fun onAddNewCategory(category: String) {
        lifecycleScope.launch {
            try {
                val res = eventRepositry.addPodcastSubcategory(parent_id, category)
                if (res.status) {
                    binding.editTextTextPersonName31.setText("")
                    getSubcatgery()
                    "Subcategory added successfully".toast(this@AddEventActivity)
                }
            } catch (e: Exception) {
                e.printStackTrace()
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

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()

        if(isAddNewEvent) {
            finish()
        }

        isAddNewEvent = false
    }

    private fun getAddress(latitude: Double, longitude: Double): String? {
        val result = StringBuilder()
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses.size > 0) {
                val address: Address = addresses[0]
                result.append(address.getLocality()).append(", ")
                result.append(address.getCountryName()).append(", ")
                result.append(address.subLocality).append(", ")
                result.append(address.subAdminArea)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("tag", e.message!!)
        }
        binding.et104.setText(result.toString())
        return result.toString()
    }

    var catData: ArrayList<EventCatData> = ArrayList<EventCatData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_event)
        viewmodel = ViewModelProvider(this, factory).get(AddEventViewmodel::class.java)
        binding.viewmodel = viewmodel
        viewmodel?.addEventsListener = this
        //  about= intent.getStringExtra("about").toString()
        viewmodel?.let { viewmodel ->
            viewmodel.getLoggedInUser()?.observe(this, androidx.lifecycle.Observer { user ->
                user_id = user.users_id!!
                getSubcatgery()
            })
        }

        type = intent.getStringExtra("event_type")!!.toInt()
        if (type == 1) {
            binding.textView90.text = "LOCAL EVENT ( MAX 24H )"
            binding.texlinkToJoin.visibility = View.GONE
            binding.etlinkToJoin.visibility = View.GONE
        } else if (type == 2) {
            binding.textView90.text = "ONLINE EVENT"
            binding.texlinkToJoin.text = "Add link to join online event"
        } else if(type == 3) {
            binding.textView90.text = "RETREAT EVENT"
            binding.texlinkToJoin.visibility = View.GONE
            binding.etlinkToJoin.visibility = View.GONE
        } else if(type == 4) {
            binding.textView90.text = "METAVERSE SESSIONS"
            binding.texlinkToJoin.text = "Add link to join metaverse event"
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest()

        binding.tvSelectCats.setOnClickListener {
            categoryIds = ""
            category = ""
            binding.tvSelectCats.text = category
            openDialog()
        }
        getEventCategories("")
        getLanguages()
        getTimeSlot()

        if (hasPermissions(this, permissions)) {
            getCurrentLocation()
            getLocationUpdates()
            startLocationUpdates()
        } else {
            makeRequest()
        }
        setSartDateTime()

        val apiKey = getString(R.string.google_maps_key)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
        val placesClient: PlacesClient = Places.createClient(this)
        binding.et104.setOnClickListener {
            stopLocationUpdates()
            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
        /*binding.et104.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                stopLocationUpdates()
                val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this)
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
            }
        }*/
        if (intent != null) {
            val eventsRecord =
                intent.getSerializableExtra(AddOrDupEventActivity.duplicateEvent) as EventsRecord?
            if (eventsRecord != null) {
                setDuplicateData(eventsRecord)
            }
        }


        /*if (about!=null){
            aboutevent.setText(about)
        }else{
            aboutevent.text=""
        }*/


        textView501.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to delete this event ??")
                .setTitle("Delete Event")

            builder.setPositiveButton("Delete") { dialog, id ->
                // User clicked OK button

            }

            builder.setNegativeButton(
                "Cancel"
            ) { dialog, id ->
                // User clicked cancel button
                dialog.dismiss()
            }

            val alert: AlertDialog = builder.create()



            alert.show()
        }

        textView109.setOnClickListener {
            var intent = Intent(this, CurrencyActivity::class.java)
            startActivityForResult(intent, code)
        }

        aboutevent.setOnClickListener {
            var intent = Intent(this, AddAboutEventActivity::class.java)
            startActivityForResult(intent, code)
        }

        binding.editTextTextPersonName13.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.textView92.text = (40 - s?.length!!).toString()
            }
        })


    }

    private fun setDuplicateData(eventsRecord: EventsRecord) {
        val baseImage = intent.getStringExtra(AddOrDupEventActivity.duplicateImage)


        val eveType = if ((eventsRecord.type).equals("0")) {
            getString(R.string.local_event)
        } else {
            getString(R.string.online_event)
        }
        Log.d("TAG", "setDuplicateData: ${eventsRecord.title}")
        var title = eventsRecord.title
        viewmodel?.title = title


        binding.textView94.text = eveType
        // binding.textView97.text = eventsRecord.startDate
        //  binding.textView98.text = eventsRecord.endDate
        //  binding.textView99.text = eventsRecord.startTime
        //  binding.textView100.text = eventsRecord.endTime
        binding.textView102.setText(eventsRecord.timezone)
        binding.et104.setText(eventsRecord.location)
        binding.et106.setText(eventsRecord.linkOfEvent)
        binding.aboutevent.setText(eventsRecord.description)
        about = eventsRecord.description
        //   binding.tvSelectCats.text = eventsRecord.eventCategories
        binding.editTextTextPersonName14.setText(eventsRecord.fee.toString())
        binding.textView109.text = eventsRecord.feeCurrency
        viewmodel?.bookeventurl = eventsRecord.booking_url
        viewmodel?.link = eventsRecord.linkOfEvent
        viewmodel?.attendees = eventsRecord.maxAttendees
        allow_participants = eventsRecord.acceptParticipants.toString()
        allow_comments = eventsRecord.allowComments.toInt()

    }

    var lanngData: List<LangData> = ArrayList<LangData>()
    var timeData: List<TimezoneData> = ArrayList<TimezoneData>()
    private fun getLanguages() {
        lifecycleScope.launch {
            try {
                val res = settingsRepositry.getLanguages()
                if (res.status) {
                    lanngData = res.data
                    setLanguages(lanngData)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }
    private fun getTimeSlot() {
        lifecycleScope.launch {
            try {
                val res = eventRepositry.getTimeZoneResponse()
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

    private fun setLanguages(lanngData: List<LangData>) {
        val list: MutableList<String> = ArrayList()
        list.add(getString(R.string.select))
        for (data in lanngData) {
            list.add(data.name)
        }
        val aa = ArrayAdapter(this, R.layout.item_spinner, R.id.tvSpinnerItem, list)
        with(spinnerLang) {
            adapter = aa
            setSelection(0, false)
            onItemSelectedListener = this@AddEventActivity
            prompt = getString(R.string.select)
        }
    }

    private fun setTimeZone(timeData: List<TimezoneData>) {
        val list: MutableList<String> = ArrayList()
//        list.add(getString(R.string.select))
        for (data in timeData) {
            list.add(data.timezone + " GMT "+ data.gmt_offset)
        }
        val aa = ArrayAdapter(this, R.layout.item_spinner, R.id.tvSpinnerItem, list)
        with(spinnertimezone) {
            adapter = aa
            setSelection(0, false)
            onItemSelectedListener = this@AddEventActivity
            prompt = getString(R.string.select)
        }
        timeZone = timeData.first().id
    }

    private fun filter(adpter:SpinerCatAdapter,text: String) {
        // creating a new array list to filter our data.
        val filteredlist: ArrayList<EventCatData> = ArrayList()

        // running a for loop to compare elements.
        for (item in catData) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.category_name.toString().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adpter.filterList(filteredlist)
        }
    }

    private fun getEventCategories(value: String) {
        lifecycleScope.launch {
            try {
                val catRes = eventRepositry.getEventCatRes(value)
                if (catRes.status) {
                    catData = catRes.data
                    setEventCategories(catData)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    private fun setEventCategories(catData: List<EventCatData>) {
        val list: MutableList<String> = ArrayList()
        list.add(getString(R.string.add_categories))
        for (data in catData) {
            list.add(data.category_name)
        }
        val aa = ArrayAdapter(this, R.layout.item_spinner, R.id.tvSpinnerItem, list)
        with(spinnerCat) {
            adapter = aa
            setSelection(0, false)
            onItemSelectedListener = this@AddEventActivity
            prompt = getString(R.string.add_categories)
        }
    }

    private fun setSartDateTime() {
        val c = Calendar.getInstance()
       // val timezone = c.timeZone.id
      //  binding.textView102.setText(timezone)
        val tim: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(c.time)
        val time: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(c.time)
        binding.textView99.text = time
        val date: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(c.time)
        binding.textView97.text = date
        startDate = date
        startTime = tim
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> getCurrentLocation()
                PackageManager.PERMISSION_DENIED -> "Please allow permissions for better experience.".toast(
                    this
                )
            }
        }
    }

    private fun getSubcatgery() {
        lifecycleScope.launch {
            try {
                val res = eventRepositry.getPodcastSubcategory(parent_id)
                if (res.status) {
                     subCatedataList = res.data
                    binding.recyclerView9.also {
                        it.layoutManager = GridLayoutManager(this@AddEventActivity, 2)
                        it.setHasFixedSize(true)
                        it.adapter = PodcastSubcategoryAdapter(this@AddEventActivity,subCatedataList, this@AddEventActivity)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
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

    override fun onBack() {
        onBackPressed()



    }

    var flag: Int = 0
    override fun onPost(
        title: String,
        timeZone: String,
        location: String,
        link: String,
        fee: String,
        attendees: String,
        book_event_url: String
    ) {

        val sdf = SimpleDateFormat("hh:mm")
        val sdfDate = SimpleDateFormat("dd-MM-yyyy")
        //"$startDate, $startTime, $endDate, $endTime".toast(this)
        if (currentPhotoPath.isNullOrEmpty()) {
            "Please add Picture".toast(this)
            return
        } else if (title.equals("")) {
            binding.editTextTextPersonName13.error =
                "Required."; binding.editTextTextPersonName13.requestFocus(); return
        } else if (endDate.equals("")) {
            "Select Start Date and End Date".toast(this)
            return
        }
        else if (sdfDate.parse(startDate).equals(sdfDate.parse(endDate))) {

            if (endTime == "") {
                "Select Start Time And End Time".toast(this)
                return
            } else if (!isTimeAfter(sdf.parse(startTime), sdf.parse(endTime))) {
                "End Time Should be Greater Than Start Time".toast(this)
                return

            } else if (about.equals("")) {
                Utils.showToast(this, "Enter About")
                // binding.et106.error = "Required."; binding.et106.requestFocus(); return
            }
            else if (location.equals("")) {
                Utils.showToast(this, "Enter Location")
                binding.et104.error = "Required.";binding.et104.requestFocus(); return
            }
            else if (language == "") {
                Utils.showToast(this, "Select Language")
                // binding.et104.error = "Required.";binding.et104.requestFocus(); return
            }
            else {
                addEventsData(
                    title,
                    timeZone,
                    location,
                    link,
                    fee,
                    attendees,
                    book_event_url
                )
            }


        }  else if (about.equals("")) {
            Utils.showToast(this, "Enter About")
            // binding.et106.error = "Required."; binding.et106.requestFocus(); return
        }
        else if (location.equals("")) {
            Utils.showToast(this, "Enter Location")
            binding.et104.error = "Required.";binding.et104.requestFocus(); return
        }
        else if (language == "") {
            Utils.showToast(this, "Select Language")
            // binding.et104.error = "Required.";binding.et104.requestFocus(); return
        }
        else {
            addEventsData(
                title,
                timeZone,
                location,
                link,
                fee,
                attendees,
                book_event_url
            )
        }


    }

    fun addEventsData(
        title: String,
        timeZone: String,
        location: String,
        link: String,
        fee: String,
        attendees: String,
        book_event_url: String
    ) {
        val file: File = File(currentPhotoPath!!)
        val requestFile: RequestBody = RequestBody.create(
            contentResolver.getType(photoURI)?.let { it.toMediaTypeOrNull() },
            file
        )
        val img_file: MultipartBody.Part = MultipartBody.Part.createFormData(
            "files[0]",
            file.name,
            requestFile
        )

        val imgList: MutableList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
        imgList.add(img_file)
        peviewlangague = language
        Log.e("datenidhiii",startDate+endDate)


        val uid: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), user_id)
        val title: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), title)
        val description: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), about)
        val timeZone: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), timeZone)
        val location: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), location)
        val link: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), link)
        val fee: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), fee)
        val attendees: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), attendees)
        val allow_cmnt: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "" + allow_comments)
        val types: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "" + type)
        val sDate: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "" + startDate)
        val eDate: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "" + endDate)
        val sTime: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "" + startTime)
        val eTime: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "" + endTime)
        val multiple: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "0")
        val eveCat: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), categoryIds)
        val language: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), language)
        val feeCurency: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), currencyId)
        val paticipants: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), allow_participants)
        val latitude: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), lat.toString())
        val longitude: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), lon.toString())

        val bookingurl: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), book_event_url.toString())

        val event_subcategories: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), subCatIds.toString())

        val status: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "0")
        binding.button42.visibility = View.INVISIBLE
//        if (flag == 0) {
//            flag = 1
//            lifecycleScope.launch {
//                try {
//                    val res = eventRepositry.addUserEvent(
//                        status,
//                        uid,
//                        types,
//                        allow_cmnt,
//                        title,
//                        description,
//                        sTime,
//                        sDate,
//                        eTime,
//                        eDate,
//                        timeZone,
//                        location,
//                        link,
//                        eveCat,
//                        fee,
//                        feeCurency,
//                        attendees,
//                        language,
//                        paticipants,
//                        multiple,
//                        latitude,
//                        longitude,
//                        bookingurl,
//                        event_subcategories,
//                        imgList
//                    )
//                    binding.button42.visibility = View.VISIBLE
//                    Log.e("eveRes::", "" + res)
//                    if (res.status) {
//                        Utils.showToast(this@AddEventActivity, res.message)
//                        //  "Event added successfully.".toast(this@AddEventActivity)
//                        flag = 0
//                        startActivity(Intent(this@AddEventActivity, AddEventSuccessActivity::class.java))
//                        finish()
//                        /// onBackPressed()
//                    } else {
//                        flag = 0
//                        Utils.showToast(this@AddEventActivity, res.message)
//                        //  "Oops! Something went wrong.".toast(this@AddEventActivity)
//                    }
//                } catch (e: ApiException) {
//                    binding.button42.visibility = View.VISIBLE
//                    flag = 0
//                    e.printStackTrace()
//                } catch (e: NoInternetException) {
//                    binding.button42.visibility = View.VISIBLE
//                    flag = 0
//                    e.printStackTrace()
//                }
//            }
//        }
    }

    override fun onDelete() {
        currentPhotoPath = ""
        imgPaths.clear()
//        binding.imageView11.setImageResource(0)
        binding.editTextTextPersonName13.setText("")
        binding.et106.setText("")
        binding.et107.setText("")
        binding.tvSelectCats.text = getString(R.string.select)
        binding.editTextTextPersonName14.setText("")
        binding.editTextTextPersonName15.setText("")

    }

    override fun onPreview(
        title: String,
        description: String,
        location: String,
        link: String,
        fee: String,
        attendees: String,
        book_event_url: String
    ) {
          val  event_title = binding.editTextTextPersonName13.text.toString()
        val  event_location = binding.et104.text.toString()

        val sdf = SimpleDateFormat("hh:mm")
        val sdfDate = SimpleDateFormat("dd-MM-yyyy")
        //"$startDate, $startTime, $endDate, $endTime".toast(this)
        if (currentPhotoPath.isNullOrEmpty()) {
            "Please add Picture".toast(this)
            return
        } else if (event_title.equals("")) {
            binding.editTextTextPersonName13.error =
                "Required."; binding.editTextTextPersonName13.requestFocus(); return
        } else if (endDate.equals("")) {
            "Select Start Date and End Date".toast(this)
            return
        } else if (endTime == "") {
            "Select Start Time And End Time".toast(this)
            return
        }
        else if (!isTimeAfter(sdf.parse(startTime), sdf.parse(endTime))) {
            "End Time Should be Greater Than Start Time".toast(this)

        }  else if (about.equals("")) {
            Utils.showToast(this, "Enter About")
            // binding.et106.error = "Required."; binding.et106.requestFocus(); return
        }
        else if (event_location.equals("")) {
            Utils.showToast(this, "Enter Location")
            binding.et104.error = "Required.";binding.et104.requestFocus(); return
        }
        else if (language == "") {
            Utils.showToast(this, "Select Language")
            // binding.et104.error = "Required.";binding.et104.requestFocus(); return
        } else if (attendees == "" || attendees == "0") {
            Utils.showToast(this, "Enter Max Attendees")
            // binding.et104.error = "Required.";binding.et104.requestFocus(); return
        }
        else {



            var endDateTime = endDate +" "+ endTime
            var startDateTime = startDate +" "+ startTime
            val fuldate = SimpleDateFormat("dd-MM-yyyy hh:mm")
            if(!isTimeAfter(fuldate.parse(startDateTime), fuldate.parse(endDateTime))){
                "End Date Should be Greater Than Start Date".toast(this)
                return
            }

            var join_link = binding.etlinkToJoin.text.toString()


            val records : EventsRecord = EventsRecord(
                allow_participants.toInt(),
                endDateTime,
                startDateTime,
                allow_comments,
                startDate,
                about,
                endDate,
                endTime,
                category,
                subCatIds,
                fee.toInt(),
                currencyId,
                "",
                user_id,
                language.toInt(),
                link,
                event_location,
                attendees,
                categoryIds,
                startDate,
                startTime,
                1,
                timeZone,
                "",
                event_title,
                type.toString(),
                startDate,
                user_id,
                user_id,
                user_id,
                "0",
                "0",
                "0",
                "0",
                "0",
                lat.toString(),
                lon.toString(),
                "",
                "",
                curency,
                "",
                languagenname,
                book_event_url,
                false,
                join_link

            )
//            val record: EventsRecord = EventsRecord(
//                allow_participants,
//                endDate,
//                startDate,
//                allow_comments.toString(),
//                startDate,
//                about,
//                endDate,
//                endTime,
//                category,
//                fee,
//                curency,
//                "",
//                user_id,
//                peviewlangague,
//                link,
//                location,
//                attendees,
//                "0",
//                startDate,
//                startTime,
//                "0",
//                timeZone,
//                title,
//                type.toString(),
//                startDate,
//                user_id,
//                user_id,
//                "0",
//                "0",
//                "0",
//                "0",
//                "0",
//                "",
//                lat.toString(),
//                lon.toString(),
//                "",
//                categoryIds,
//                "$",
//                "",
//                languagenname,
//                et_book_event.text.toString(),
//                false
//            )
            Log.e("Harsh::",records.toString())
            val intent = Intent(this, PreviewEventActivity::class.java)
            intent.putExtra(EVE_RECORD, records)
            intent.putExtra(B_IMG_URL, imgPaths)
            intent.putExtra("imageUri", photoURI.toString())
            startActivity(intent)
        }

    }


    override fun onAdPic() {
        showPhotoPicker()
    }

    override fun selectEventType() {
        showPicker()
    }

    override fun onAllowComments(isChecked: Boolean) {
        if (isChecked) {
            allow_comments = 1
            binding.switch6.setBackgroundTintList(this.getResources().getColorStateList(R.color.colorPrimaryDark));
        } else {
            allow_comments = 0
            binding.switch6.setBackgroundTintList(this.getResources().getColorStateList(R.color.light_gry));
        }
    }

    override fun onAllowPaid(isChecked: Boolean) {
        if (isChecked) {
            binding.rldPaid.visibility = View.VISIBLE
            binding.textPodcast.visibility=View.VISIBLE
            binding.etBookEvent.visibility=View.VISIBLE
            binding.switchPaid.setBackgroundTintList(this.getResources().getColorStateList(R.color.colorPrimaryDark));



        } else {
            binding.rldPaid.visibility = View.GONE
            binding.textPodcast.visibility=View.GONE
           binding.etBookEvent.visibility=View.GONE
            binding.switchPaid.setBackgroundTintList(this.getResources().getColorStateList(R.color.light_gry));
        }
    }

    override fun goToHome() {
    }

    override fun addNewEvent() {
    }


    override fun onAllowParticipants(isChecked: Boolean) {
        if (isChecked) {
            allow_participants = "1"
            binding.switch5.setBackgroundTintList(this.getResources().getColorStateList(R.color.colorPrimaryDark));
        } else {
            allow_participants = "0"

            binding.switch5.setBackgroundTintList(this.getResources().getColorStateList(R.color.light_gry));
        }
    }

    override fun onStartDate() {
        showDatePicker(1)
    }

    override fun onEndDate() {
        if(binding.textView97.text.toString() == "") {
            "Please select start date first".toast(this@AddEventActivity)
        } else if(binding.textView99.text.toString() == ""){
            "Please select start time first".toast(this@AddEventActivity)
        } else {
            showDatePicker(2)
        }

    }

    override fun onStartTime() {
        if(binding.textView97.text.toString() == "") {
            "Please select start date first".toast(this@AddEventActivity)
        } else {
            showTimePicker(1)
        }

    }

    override fun onEndTime() {
        if(binding.textView97.text.toString() == "") {
            "Please select start date first".toast(this@AddEventActivity)
        } else if(binding.textView99.text.toString() == ""){
            "Please select start time first".toast(this@AddEventActivity)
        } else if(binding.textView98.text.toString() == "") {
            "Please select end date first".toast(this@AddEventActivity)
        } else {
            showTimePicker(2)
        }

    }

    override fun onCurency() {
        startActivity(Intent(this, CurrencyActivity::class.java))
    }

    override fun onPostResume() {
        super.onPostResume()
        curency = Prefs.getString(CURRENCY_SYMBOL, "$").toString()
        currencyId = Prefs.getString(CURRENCY_ID, "0").toString()
        binding.textView109.text = curency
        //"$curency".toast(this)

    }

    fun showTimePicker(i: Int) {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            val time: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(cal.time)
            val tim: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time)
            if (i == 1) {
                binding.textView99.text = tim
                startTime = time
            } else {

                var startDate = SimpleDateFormat("dd-MM-yyy HH:mm:ss", Locale.getDefault()).parse(startDate +" " + startTime)
                var endDate = SimpleDateFormat("dd-MM-yyy HH:mm:ss", Locale.getDefault()).parse(endDate +" " + time)


                if(startDate.after(endDate)) {
                    "End date and time must be bigger than Start date and time".toast(this@AddEventActivity)
                } else {
                    binding.textView100.text = tim
                    endTime = time
                }

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

    fun isTimeAfter(startTime: Date?, endTime: Date): Boolean {
        return !endTime.before(startTime)
    }

    /*  fun isDateAfter(startDate:String, endDate:String) :Boolean{
          try
          {
              var myFormatString = "yyyy-MM-dd"
              var  df =  SimpleDateFormat(myFormatString);
              var  date1 = df.parse(endDate)
              var  startingDate = df.parse(startDate);

              return date1.after(startingDate);
          }
          catch ( e:Exception)
          {

              return false;
          }
      }*/

    fun showDatePicker(i: Int) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, day)

        //   binding.textView97.text=SimpleDateFormat("dd-MM-yyyy").format(c.time)
        val timezone = c.timeZone.id
        binding.textView102.setText(timezone)
        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val mm: Int = monthOfYear
                c.set(Calendar.YEAR, year)
                c.set(Calendar.MONTH, mm)
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val d: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(c.time)
                if (i == 1) {
                    binding.textView97.text =
                        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(c.time)
                    startDate = d
                } else {

                    var startDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(startDate)

                    if(startDate.after(c.time)) {
                        "End date must be bigger than Start date".toast(this@AddEventActivity)
                    } else {
                        binding.textView98.text =
                            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(c.time)
                        endDate = d
                    }
                }
            },
            year,
            month,
            day
        )
        val cal = Calendar.getInstance()
        //  cal.add(Calendar.DAY_OF_MONTH,1)
        dpd.getDatePicker().setMinDate(cal.timeInMillis);


        dpd.show()
    }

    private fun showPhotoPicker() {
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
        view.btnFollow.visibility = View.GONE
        view.btnFollow.setOnClickListener {
            if (hasPermissions(this, permissions)) {
                dispatchTakePictureIntent()
            } else {
                makeRequest()
            }
            dialog.dismiss()
        }
        view.btnOnline.setOnClickListener {
            //startActivity(Intent(this, CustomCameraActivity::class.java))
            if (hasPermissions(this, permissions)) {
                openGallery()
            } else {
                makeRequest()
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQ)
    }

    private fun showPicker() {
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
            type = 0
            dialog.dismiss()
        }
        view.btnOnline.setOnClickListener {
            binding.textView94.setText(R.string.online_event)
            type = 1
            dialog.dismiss()
        }
        dialog.show()
    }

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
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                        takePictureIntent.setClipData(ClipData.newRawUri("", photoURI));

                        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)

                    }
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                    //  takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        Log.e("codeee", requestCode.toString())
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

        /* else if (requestCode === 201) {
             if (resultCode === RESULT_OK) {
                  about = intent.getStringExtra("about").toString()
                 if (about!=null){
                     aboutevent.setText(about)
                 }else{
                     aboutevent.text=""
                 }
             }
         }*/

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == code) {
            if (resultCode == Activity.RESULT_OK) {
                about = data?.getStringExtra("about").toString()
                Log.e("aboutt", about)
                if (about != null) {
                    aboutevent.setText(about)
                } else {
                    aboutevent.text = ""
                }
            }
        }


        super.onActivityResult(requestCode, resultCode, data)
        val uriPathHelper = UriPathHelper()
        val imgList: MutableList<ImageData> = ArrayList<ImageData>()
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            try {


                photoURI = data?.data!!
                currentPhotoPath = uriPathHelper.getPath(this, data?.data!!)
                imgPaths.add(currentPhotoPath!!)
                imgList.add(ImageData(currentPhotoPath))


//                mImageBitmap =
//                    BitmapFactory.decodeFile(currentPhotoPath) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))
//                binding.imageView11.setImageBitmap(mImageBitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (requestCode == GALLERY_REQ && resultCode == RESULT_OK) {
//            photoURI = data?.data!!







            if (data?.clipData != null) {

                val count: Int = data?.clipData!!.itemCount
                var currentItem = 0
                while (currentItem < count) {
                    val imageUri: Uri = data.clipData!!.getItemAt(currentItem).uri

                    currentPhotoPath = uriPathHelper.getPath(this, imageUri)
                    imgList.add(ImageData(currentPhotoPath))
                    imgPaths.add(currentPhotoPath!!)
                    currentItem = currentItem + 1
                    photoURI = imageUri;
                }
            } else if (data!!.data != null) {

                photoURI = data?.data!!
                currentPhotoPath = uriPathHelper.getPath(this, data?.data!!)
                imgPaths.add(currentPhotoPath!!)
                imgList.add(ImageData(currentPhotoPath))
                //do something with the image (save it to some directory or whatever you need to do with it here)
            }


            val adapter = PreviewEventImageAdapter(imgList)
            binding.vpimange.adapter = adapter
            TabLayoutMediator(binding.tabLayout, binding.vpimange) { tab, position ->

            }.attach()


            if(imgList.size < 2) {
                binding.tabLayout.visibility = View.INVISIBLE
            }

            binding.vpimange.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
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




//            Glide.with(binding.imageView11)
//                .load(photoURI)
//                .into(binding.imageView11)
//
//            imgPaths.add(currentPhotoPath!!)
//
//            try {
//                mImageBitmap =
//                    BitmapFactory.decodeFile(currentPhotoPath) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))
//                //binding.imageView11.setImageBitmap(mImageBitmap)
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }

        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val pos = p2 - 1

       var spid =  p0!!.id

        if(spid == binding.spinnerLang.id){


            try{
                if (pos == -1) language = ""

                language = lanngData[pos].id
                languagenname = lanngData[pos].name
//                binding.et104.setText(" ")
            }catch (e:Exception){

            }
        }
        if (spid == binding.spinnertimezone.id){
            try{
                if (pos == -1) language = ""
                var time=timeData[pos]
                timeZone=time.id
//                binding.et104.setText(" ")
            }catch (e:Exception){

            }
        }
//        if (pos > 0) {
//
//        //    binding.textView112.text=languagenname
//         //   binding.textTimeZone.text = timeZone
//        }
        /*else {
                "Please select valid language.".toast(this)
            }*/
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    fun openDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.eve_cat_selection)
        val adapter:SpinerCatAdapter ;
        dialog.rvcats.also { rv ->
            rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            rv.setHasFixedSize(true)
            catData.let {
                adapter =  SpinerCatAdapter(it, this, this)
                rv.adapter = adapter
            }
        }
        val send = dialog.findViewById(R.id.button53) as Button
        val cancel = dialog.findViewById(R.id.button52) as Button
        val ic_back = dialog.findViewById(R.id.textView131) as TextView
        val edt_search_category = dialog.findViewById<EditText>(R.id.edt_search_category)
        send.setOnClickListener {
            dialog.dismiss()
        }
        edt_search_category.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var value = s.toString()
                Log.e("valueee", value.toString())

//                getEventCategories(value)

                filter(adapter,value)

//                dialog.rvcats.also { rv ->
//
//                    rv.layoutManager =
//                        LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
//                    rv.setHasFixedSize(true)
//                    catData.let {
//                        rv.adapter =
//                            SpinerCatAdapter(it, this@AddEventActivity, this@AddEventActivity)
//                    }
//                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                var s = s.toString()
                Log.e("datatat", s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        cancel.setOnClickListener {
            dialog.dismiss()
            getEventCategories(" ")
            dialog.rvcats.also { rv ->
                rv.layoutManager =
                    LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
                rv.setHasFixedSize(true)
                catData.let {
                    rv.adapter = SpinerCatAdapter(it, this@AddEventActivity, this)
                }
            }
        }
        ic_back.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onEventItemChecked(eveCataData: EventCatData, b: Boolean) {
        if (b) {
            if (category.isEmpty()) {
                category = eveCataData.category_name
                categoryIds = eveCataData.id
            } else {
                category = category + "," + eveCataData.category_name
                categoryIds = categoryIds + "," + eveCataData.id
            }
        } else {
            categoryIds = categoryIds.replace(eveCataData.id + ",", "")
            category = category.replace(eveCataData.category_name + ",", "")
        }
        binding.tvSelectCats.text = category
    }

    override fun onPodSubCatSelected(subCategoryData: PodcastSubCategoryData, isSelected: Boolean) {
        if (isSelected) {
            subCatIds = if (subCatIds.isEmpty()) {
                subCategoryData.id + ","
            } else {
                subCatIds + subCategoryData.id + ","
            }
        } else {
            val replcedata = subCategoryData.id + ","
            subCatIds = subCatIds.replace(replcedata, "")
        }
    }

    override fun onPodSubCatSelectedList(subCategoryData: ArrayList<String>) {

        subCatIds= subCategoryData.toString()

        subCatIds = subCatIds.substring(1, subCatIds.length - 1);
        subCatIds = subCatIds.replace("\\s".toRegex(), "")
        Log.e("value",subCatIds.toString())
    }

    override fun onclick(Event: EventCatData) {
        parent_id = Event.id
        binding.tvSelectCats.text = Event.category_name
        category = Event.category_name
        categoryIds = Event.id
        Log.e("list", Event.id)
        getSubcatgery()

        binding.recyclerView9.visibility = View.VISIBLE
        binding.textView284.visibility = View.VISIBLE
        binding.relAdd.visibility = View.VISIBLE
        binding.textView284.text="Choose up to 3 sub-categories of "+Event.category_name
    }




}