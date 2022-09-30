package com.wiesoftware.spine.ui.home.menus.events.maps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.EventContentAdapter
import com.wiesoftware.spine.data.net.reponses.EventsData
import com.wiesoftware.spine.data.net.reponses.EventsRecord
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityMapviewEventsBinding
import com.wiesoftware.spine.ui.home.menus.events.B_IMG_URL
import com.wiesoftware.spine.ui.home.menus.events.EVE_RECORD
import com.wiesoftware.spine.ui.home.menus.events.IS_FROM_EVENT_DETAILS
import com.wiesoftware.spine.ui.home.menus.events.event_details.EventDetailActivity
import com.wiesoftware.spine.ui.home.menus.events.filter.FilterEventActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE
import com.wiesoftware.spine.util.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*
import kotlin.collections.ArrayList


class MapviewEventsActivity : AppCompatActivity(), OnMapReadyCallback, KodeinAware,
    MapviewEventListener, EventContentAdapter.SaveEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    private val AUTOCOMPLETE_REQUEST_CODE = 1
    override val kodein by kodein()
    val factory: MapviewViewmodelFactory by instance()
    val homeRepositry: HomeRepositry by instance()
    lateinit var binding: ActivityMapviewEventsBinding
    var userId: String = ""

    var lati: Double = 0.0
    var longi: Double = 0.0
    private lateinit var mMap: GoogleMap
    val PERMISSION_REQUEST_CODE = 94
    val permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null
    private var allEvents: MutableList<EventsRecord> = ArrayList<EventsRecord>()

    var listOfMarker = ArrayList<LatLng>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mapview_events)
        val viewmodel = ViewModelProvider(this, factory).get(MapviewViewmodel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.mapviewEventListener = this

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest()

        viewmodel.getLoggedInUser().observe(this, Observer { user ->
            userId = user.users_id!!
            Log.e("userid", userId)
            getAllEvents()

            if (hasPermissions(this, permissions)) {
                val IS_FROM_EVENT_DETAILS = intent.getBooleanExtra(IS_FROM_EVENT_DETAILS, false)
                if (!IS_FROM_EVENT_DETAILS) {
                    getCurrentLocation()
                    getLocationUpdates()
                    startLocationUpdates()
                }
            } else {
                makeRequest()
            }
        })

        val apiKey = getString(R.string.google_maps_key)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
        val placesClient: PlacesClient = Places.createClient(this)
        val autocompleteFragment =
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
                val cameraPosition = CameraPosition.Builder()
                    .target(place.latLng)
                    .build()
                val cu = CameraUpdateFactory.newCameraPosition(cameraPosition)
                mMap.animateCamera(cu)
            }

            override fun onError(status: Status) {
                /* "An error occurred: $status".toast(this@MapviewEventsActivity)
                 Log.e("searchPlace", "An error occurred: $status")*/
            }
        })


        /* binding.searchSpine.setOnQueryTextFocusChangeListener { v, hasFocus ->
             if (hasFocus){
                 binding.searchSpine.visibility=View.INVISIBLE
                 val autocompleteFragment =
                     supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                             as AutocompleteSupportFragment
                 autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
                 autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
                     override fun onPlaceSelected(place: Place) {
                         Log.e("searchPlace", "Place: ${place.name}, ${place.id}")
                     }
                     override fun onError(status: Status) {
                         Log.e("searchPlace", "An error occurred: $status")
                     }
                 })
             }
             binding.searchSpine.clearFocus()
         }*/


    }


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
                        lati = location.latitude
                        longi = location.longitude
                        Log.e("loc::", "$lati , $longi")
                        addCurrentMarker(lati, longi)

                        mMap?.let {
                            getAllEvents()
                        }
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
        if (locationCallback != null) {
            locationCallback?.let {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        if (!userId.isEmpty()) {
            val isFilter = Prefs.getBoolean("isFilter", false)
            if (isFilter) {
                val lat = Prefs.getString("lat", lati.toString())
                val date = Prefs.getString("date", "")
                val datetwo = Prefs.getString("datetwo", "")
                val category = Prefs.getString("category", "")
                val lon = Prefs.getString("lon", longi.toString())
                getFilteredList(lat, lon, date, datetwo, category)
            }
        }
    }

    private fun getFilteredList(
        lat: String?,
        lon: String?,
        date: String?,
        datetwo: String?,
        category: String?,

        ) {
        lifecycleScope.launch {
            try {
                val res = homeRepositry.getFilteredEventList(
                    1,
                    100,
                    userId,
                    lat!!,
                    lon!!,
                    10,
                    date!!,
                    datetwo!!,
                    category!!
                )
                allEvents.clear()
                if (res.status) {
                    STORY_IMAGE = res.image
                    val dataList = res.data
                    setEventsOnMap(dataList)
                    Log.e("filteredRes: ", "" + dataList)

                } else {
                    "${res.message}".toast(this@MapviewEventsActivity)

                }
                Prefs.putAny("isFilter", false)
            } catch (e: ApiException) {
                e.printStackTrace()
                Prefs.putAny("isFilter", false)
            } catch (e: NoInternetException) {
                e.printStackTrace()
                Prefs.putAny("isFilter", false)
            }
        }
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
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lati = location.latitude
                    longi = location.longitude

                    addCurrentMarker(lati, longi)
                    mMap?.let {
                        getAllEvents()
                    }
                }
            }
    }

    private fun addCurrentMarker(lati: Double, longi: Double) {
        val latLng = LatLng(lati, longi)
        val marker: MarkerOptions =
            MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker()).title(
                "Current position"
            )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f))
        mMap.addMarker(marker)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        listOfMarker = ArrayList<LatLng>()
        val IS_FROM_EVENT_DETAILS = intent.getBooleanExtra(IS_FROM_EVENT_DETAILS, false)
        if (IS_FROM_EVENT_DETAILS) {
            val record = intent.getSerializableExtra(EVE_RECORD) as EventsRecord
            try {
                val latitude = record.latitude.toDouble()
                val longitude = record.longitude.toDouble()
                val latlng = LatLng(latitude, longitude)
                val title = record.title
                val fee =/*record.feeCurrency+" "+*/record.fee
                val symbol = record.symbol
                addMarker(fee, title, latlng, -1, symbol, record.isSelected)


                val cameraPosition = CameraPosition.Builder()
                    .target(latlng)
                    .build()
                val cu = CameraUpdateFactory.newCameraPosition(cameraPosition)
                mMap.animateCamera(cu)
                return
            } catch (e: Exception) {
                e.printStackTrace()
            }


            val b = LatLngBounds.Builder()
            for (m in listOfMarker) {
                b.include(m)
            }
            val bounds = b.build()
//Change the padding as per needed
//Change the padding as per needed
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, 25, 25, 5)
            mMap.animateCamera(cu)

        }

    }

    private fun getAllEvents() {
        lifecycleScope.launch {
            try {
                val res = homeRepositry.getAllEvents(
                    1,
                    100,
                    userId,
                    "localevent"
                )//homeRepositry.getNearbyEvents(1,100,userId,lati,longi,10)
                if (res.status) {
                    STORY_IMAGE = res.image
                    val dataList = res.data
                    setEventsOnMap(dataList)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    private fun setEventsOnMap(dataList: List<EventsData>) {
        mMap.clear()
        var k = 0
        listOfMarker = ArrayList<LatLng>()
        allEvents = ArrayList<EventsRecord> ()

        for (list in dataList) {

            for (data in list.records) {
                allEvents.add(data)
                val lat = data.latitude
                val lon = data.longitude
                val title = data.title
                val fee =/*data.feeCurrency+" "+*/data.fee

                var symbol = data.symbol
                var isSelected = data.isSelected
                var id = data.id
                if (symbol == null) {
                    symbol = "$"
                }
                lat?.let {
                    try {
                        val l: Double = lat.toDouble()
                        val ll: Double = lon.toDouble()
                        Log.e("latlongs:", lat + " " + lon)
                        addMarker(fee, title, LatLng(l, ll), id.toInt(), symbol, isSelected)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("latlongssss:", lat + " " + lon)
                        getCurrentLocation()
                        addMarker(fee, title, LatLng(lati, longi), id.toInt(), symbol, isSelected)
                    }
                }
                k++
            }
        }

        if (listOfMarker.isNotEmpty()) {
            // Adjusting Bounds
            val builder = LatLngBounds.Builder()
            for (m in listOfMarker) {
                builder.include(m)
            }
            val bounds = builder.build()
            val mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2)
            mMap.animateCamera(mCameraUpdate)
        }


//        val b = LatLngBounds.Builder()
//        for (m in listOfMarker) {
//            b.include(m)
//        }
//        val bounds = b.build()
////Change the padding as per needed
////Change the padding as per needed
//        val cu = CameraUpdateFactory.newLatLngBounds(bounds, 25, 25, 5)
//        mMap.animateCamera(cu)

        setEveList()
    }

    private fun setEveList() {
        binding.rvMapeve.also {
            it.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
            it.setHasFixedSize(true)
            it.adapter = EventContentAdapter(this, allEvents, this)
        }
    }

    fun addMarker(
        price: String,
        title: String,
        latLng: LatLng,
        k: Int,
        symbol: String,
        isSelected: Boolean
    ) {
        val markerView = View.inflate(this, R.layout.marker_view, null)
        val tv: TextView = markerView.findViewById(R.id.textView173) as TextView
        val card: CardView = markerView.findViewById(R.id.cardView) as CardView
        tv.text = symbol + " " + price

//        println("Sanjay k isSele    cted....." + k + "....." + qa)
        if (isSelected) {
            card.setCardBackgroundColor(
                ContextCompat.getColor(
                    this@MapviewEventsActivity,
                    R.color.dot_dark_screen1
                )
            )
            tv.setTextColor(ContextCompat.getColor(this@MapviewEventsActivity, R.color.gray_tint))
        } else {
            card.setCardBackgroundColor(
                ContextCompat.getColor(
                    this@MapviewEventsActivity,
                    R.color.gray_tint
                )
            )
            tv.setTextColor(
                ContextCompat.getColor(
                    this@MapviewEventsActivity,
                    R.color.dot_dark_screen1
                )
            )
        }

        val bitmap: Bitmap = getBitmapFromView(markerView)
        val marker: MarkerOptions = MarkerOptions().position(latLng).icon(
            BitmapDescriptorFactory.fromBitmap(
                bitmap
            )
        ).title(title)
        mMap.addMarker(marker).tag = k

        listOfMarker.add(marker.position)
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16.0f))
//        mMap.setOnInfoWindowClickListener { marker ->
//            try {
//                val pos: Int = marker.tag as Int
//                showEvents(pos)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }

        mMap.setOnMarkerClickListener(OnMarkerClickListener { marker ->
            try {
                val pos: Int = marker.tag as Int
                showEvents(pos)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            true
        })
    }

    fun showEvents(pos: Int) {
        for ((position, e) in allEvents.withIndex()) {
            allEvents[position].isSelected = false
        }

        allEvents[pos].isSelected = true

        mMap.clear()
        listOfMarker.clear()
        for ((position, data) in allEvents.withIndex()) {
            val lat = data.latitude
            val lon = data.longitude
            val title = data.title
            val fee =/*data.feeCurrency+" "+*/data.fee
            var symbol = data.symbol
            var isSelected = data.isSelected
            var id = data.id
            if (symbol == null) {
                symbol = "$"
            }
            lat?.let {
                try {
                    val l: Double = lat.toDouble()
                    val ll: Double = lon.toDouble()
                    addMarker(fee, title, LatLng(l, ll), id.toInt(), symbol, isSelected)
                } catch (e: Exception) {
                    e.printStackTrace()
                    getCurrentLocation()
                    addMarker(fee, title, LatLng(lati, longi), id.toInt(), symbol, isSelected)
                }
            }
        }

        binding.rvMapeve.postDelayed(Runnable {
            binding.rvMapeve.smoothScrollToPosition(pos)
        }, 750)
    }


    override fun onBack() {
        onBackPressed()
    }

    private fun getBitmapFromView(view: View): Bitmap {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }

    override fun onFilter() {
        startActivity(Intent(this, FilterEventActivity::class.java))
    }

    override fun onEventSaved(record: EventsRecord, value: Int) {
        lifecycleScope.launch {
            try {
                val res = homeRepositry.saveEvents(userId, record.id)
                if (res.status) {
                    val msg = res.message
                    msg.toast(this@MapviewEventsActivity)

                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }


    override fun onEventDetails(record: EventsRecord) {
        val intent = Intent(this, EventDetailActivity::class.java)
        intent.putExtra(EVE_RECORD, record)
        intent.putExtra(B_IMG_URL, STORY_IMAGE)
        intent.putExtra("event_id", record.id)
        startActivity(intent)
    }

    override fun onEventShare(record: EventsRecord) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            record.title + " \n" + record.description
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }


}