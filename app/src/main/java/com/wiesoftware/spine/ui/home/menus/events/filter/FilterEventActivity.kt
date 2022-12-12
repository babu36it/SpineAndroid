package com.wiesoftware.spine.ui.home.menus.events.filter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.datepicker.MaterialDatePicker
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.net.reponses.EventCatData
import com.wiesoftware.spine.data.repo.EventRepositry
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityFilterEventBinding
import com.wiesoftware.spine.ui.home.menus.events.addevents.MutilpeSpineCatAdapter
import com.wiesoftware.spine.util.*
import kotlinx.android.synthetic.main.eve_cat_selection.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FilterEventActivity : AppCompatActivity(),KodeinAware, FilterEventListener,
    MutilpeSpineCatAdapter.OnEveItemChecked,MutilpeSpineCatAdapter.ListValue{

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    val PERMISSION_REQUEST_CODE = 94

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    var lat: Double=0.0
    var lon: Double=0.0

    val permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    var catData: List<EventCatData> = ArrayList<EventCatData>()
    var category:String=""
    var categoryIds:String=""

    override val kodein by kodein()
    lateinit var binding: ActivityFilterEventBinding
    lateinit var viewmodel: FilterEventViewmodel
    val factory: FilterEventViewmodelFactory by instance()
    val eventRepositry: EventRepositry by instance()
    private val AUTOCOMPLETE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_filter_event)
        viewmodel=ViewModelProvider(this,factory).get(FilterEventViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.filterEventListener=this

        getEventCategories("")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest()

        if (hasPermissions(this, permissions)){
            getCurrentLocation()
            getLocationUpdates()
            startLocationUpdates()
        }else{
            makeRequest()
        }
        binding.editTextTextPersonName11.setOnClickListener {
         //   showDatePicker()
            rangeDatePicker()
        }
        binding.editTextTextPersonName11.setOnFocusChangeListener { view, b ->
            if (b){
            //    showDatePicker()
                rangeDatePicker()
            }
        }
         binding.editTextTextPersonName12.setOnFocusChangeListener { view, b ->
             if (b){
                 openDialog()
             }
         }
        val apiKey = getString(R.string.google_maps_key)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
        val placesClient: PlacesClient = Places.createClient(this)
        binding.editTextTextPersonName10.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                /*val autocompleteFragment =
                    supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                            as AutocompleteSupportFragment
                autocompleteFragment.setPlaceFields(
                    listOf(
                        Place.Field.ID,
                        Place.Field.NAME,
                        Place.Field.LAT_LNG
                    )
                )
                autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
                    override fun onPlaceSelected(place: Place) {
                        // "Place: ${place.name}, ${place.id}".toast(this@MapviewEventsActivity)
                        Log.e("searchPlace", "Place: ${place.name}, ${place.id}")
                        binding.editTextTextPersonName10.setText(place.name)
                    }

                    override fun onError(status: Status) {
                        "An error occurred: $status".toast(this@FilterEventActivity)
                        Log.e("searchPlace", "An error occurred: $status")
                    }
                })*/
                stopLocationUpdates()
                val fields = listOf(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG)
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this)
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.e("Place", "Place: ${place.name}, ${place.id}")
                        lat=place.latLng!!.latitude
                        lon=place.latLng!!.longitude
                        binding.editTextTextPersonName10.setText(place.name)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.e("Place", ""+status.statusMessage)
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun getEventCategories(value:String) {
        lifecycleScope.launch {
            try {
                val catRes=eventRepositry.getEventCatRes(value)
                if (catRes.status){
                    catData=catRes.data
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }


    fun openDialog() {
        val dialog = Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.eve_cat_selection)
        dialog.rvcats.also { rv->
            rv.layoutManager= LinearLayoutManager(this, RecyclerView.VERTICAL,false)
            rv.setHasFixedSize(true)
            catData.let {
                rv.adapter= MutilpeSpineCatAdapter(it,this,this)
            }
        }
        val send = dialog.findViewById(R.id.button53) as Button
        val cancel = dialog.findViewById(R.id.button52) as Button
        val edt_search_category=dialog.findViewById<EditText>(R.id.edt_search_category)
        val textView131=dialog.findViewById<TextView>(R.id.textView131)

        textView131.setOnClickListener {

            dialog.dismiss()
        }

        edt_search_category.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var value=s.toString()
                Log.e("valueee",value.toString())

                getEventCategories(value)

                dialog.rvcats.also { rv->

                        rv.layoutManager= LinearLayoutManager(applicationContext, RecyclerView.VERTICAL,false)
                        rv.setHasFixedSize(true)
                        catData.let {
                            rv.adapter= MutilpeSpineCatAdapter(it,this@FilterEventActivity,this@FilterEventActivity)
                        }
                    }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                var s=s.toString()
                Log.e("datatat",s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        send.setOnClickListener {
            dialog.dismiss()
        }
        cancel.setOnClickListener {

            dialog.dismiss()
            getEventCategories(" ")
            dialog.rvcats.also { rv->
                rv.layoutManager= LinearLayoutManager(applicationContext, RecyclerView.VERTICAL,false)
                rv.setHasFixedSize(true)
                catData.let {
                    rv.adapter= MutilpeSpineCatAdapter(it,this@FilterEventActivity,this)
                }
            }
        }
        dialog.show()
    }


    override fun onclose() {
        onBackPressed()
    }

    override fun onFindEvent(location: String, date: String,datetwo:String ,category: String) {
        Log.e("dataaa",date)
        Prefs.putAny("location",location)
        Prefs.putAny("date",date)
        Prefs.putAny("datetwo",datetwo)
        Prefs.putAny("category",categoryIds)
        Prefs.putAny("categoryNames",categoryIds)
        Prefs.putAny("category",categoryIds)
        Prefs.putAny("lat",lat.toString())
        Prefs.putAny("lon",lon.toString())
        Prefs.putAny("isFilter",true)
        onBackPressed()
    }

    override fun onOpenDialog() {
        openDialog()
    }


    fun showDatePicker(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, day)
        val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.time)
        //binding.editTextTextPersonName11.text=date
        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val mm: Int = monthOfYear
                c.set(Calendar.YEAR, year)
                c.set(Calendar.MONTH, mm)
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val d: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.time)
                val dates = binding.editTextTextPersonName11.text.toString()
                if (dates.isNullOrEmpty()){
                    binding.editTextTextPersonName11.setText(d+" to ")
                }else{
                    if(dates.endsWith("to ")){
                        binding.editTextTextPersonName11.setText(dates+d)
                    }else{
                        binding.editTextTextPersonName11.setText(d+" to ")
                    }
                }

            },
            year,
            month,
            day
        )
        dpd.show()
    }

    @SuppressLint("SetTextI18n")
    fun rangeDatePicker(){
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setTitleText("ADD DATES")
        val now = Calendar.getInstance()
        builder.setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))
        val picker = builder.build()
        picker.show(this?.supportFragmentManager!!, picker.toString())

        picker.addOnNegativeButtonClickListener {

        }

        picker.addOnPositiveButtonClickListener {
            var first=it.first
            var second=it.second

            val timeZoneUTC = TimeZone.getDefault()
            val offsetFromUTC = timeZoneUTC.getOffset(Date().time) * -1
            val simpleFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val dateone = Date(first!! + offsetFromUTC)
            val datetwo = Date(second!! + offsetFromUTC)
            Log.e("dateone", simpleFormat.format(dateone))
            Log.e("datetwo", simpleFormat.format(datetwo))
            viewmodel.date= simpleFormat.format(dateone)
            viewmodel.datetwo=simpleFormat.format(datetwo)
            val dates = binding.editTextTextPersonName11.text.toString()

            if (dates.isNullOrEmpty()){
                binding.editTextTextPersonName11.setText(simpleFormat.format(dateone)+" to "+simpleFormat.format(datetwo))
            }
          /*  else{
                if(dates.endsWith("to ")){
                    binding.editTextTextPersonName11.setText(dates+d)
                }else{
                    binding.editTextTextPersonName11.setText(d+" to ")
                }
            }*/

            //Toast.makeText(this,"The selected date range is ${it.first} - ${it.second}",Toast.LENGTH_SHORT).show()
           // Timber.i("The selected date range is ${it.first} - ${it.second}")
        }

    }



    fun hasPermissions(context: Context, permissions: Array<String>): Boolean{
        for(p in permissions){
            if(ActivityCompat.checkSelfPermission(context, p) != PackageManager.PERMISSION_GRANTED){
                return false
            }
        }
        return true
    }
    fun makeRequest() {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
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
            .addOnSuccessListener { location->
                if (location != null) {
                    lat=location.latitude
                    lon=location.longitude
                    Log.e("loc::", "$lat , $lon")
                    getAddress(lat,lon)
                }
            }

    }


    private fun getLocationUpdates()
    {
        locationRequest.interval = 50000
        locationRequest.fastestInterval = 50000
        locationRequest.smallestDisplacement = 170f // 170 m = 0.1 mile
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.lastLocation
                    if (location != null){
                        lat=location.latitude
                        lon=location.longitude
                        Log.e("loc::", "$lat , $lon")
                        getAddress(lat,lon)
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
                result.append(address.getLocality()).append(", ")
                result.append(address.getCountryName()).append(", ")
                result.append(address.subLocality).append(", ")
                result.append(address.subAdminArea)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("tag", e.message!!)
        }
        binding.editTextTextPersonName10.setText(result.toString())
        return result.toString()
    }

    override fun onEventItemChecked(eveCataData: EventCatData, b: Boolean) {
        if (category.isEmpty()){
            category=eveCataData.category_name
            categoryIds=eveCataData.id
        }else{
            categoryIds=categoryIds+","+eveCataData.id
            category=category+", "+eveCataData.category_name
        }
        binding.editTextTextPersonName12.setText(category)
    }

    override fun onclick(Event: EventCatData) {
        category=Event.category_name
        categoryIds=Event.id
        binding.editTextTextPersonName12.setText(Event.category_name)
    }


}