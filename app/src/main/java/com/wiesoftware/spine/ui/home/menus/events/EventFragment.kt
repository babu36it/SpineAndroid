package com.wiesoftware.spine.ui.home.menus.events

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.Nullable
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.EventListAdapter
import com.wiesoftware.spine.data.net.reponses.EventsData
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.FragmentEventBinding
import com.wiesoftware.spine.ui.home.menus.events.addordup.AddOrDupEventActivity
import com.wiesoftware.spine.ui.home.menus.events.filter.FilterEventActivity
import com.wiesoftware.spine.ui.home.menus.events.maps.MapviewEventsActivity
import com.wiesoftware.spine.ui.home.menus.podcasts.addrss.AddRssActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postmedia.PostMediaActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.poststory.AddStoryActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postthought.PostThoughtActivity
import com.wiesoftware.spine.ui.home.menus.spine.featuredpost.FeaturedPostActivity
import com.wiesoftware.spine.util.*
import kotlinx.android.synthetic.main.add_post_bottomheet.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


//val EVE_RECORD = "eve_record"
//val B_IMG_URL = "base_url"
//val IS_FROM_EVENT_DETAILS = "isFromEventDetails"
//var PROFILE_PIC_URL = ""

class EventFragment : Fragment(), KodeinAware, EventFragmentEventListener {

    val PERMISSION_REQUEST_CODE = 94

    var adapter: EventListAdapter? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    var locationCallback: LocationCallback? = null
    var lat: Double = 0.0
    var lon: Double = 0.0

    val permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )


    override val kodein by kodein()
    val factory: EventFragmentViewmodelFactory by instance()
    val homeRepositry: HomeRepositry by instance()
    lateinit var binding: FragmentEventBinding
    var user_id: String = ""
    var dataList: MutableList<EventsData> = mutableListOf()

    companion object {
        var searchView: SearchView? = null
        public var tabLayout: TabLayout? = null
        lateinit var progress: ProgressDialog
    }

    var currentFragment: Fragment? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest()

        if (hasPermissions(requireContext(), permissions)) {
            getCurrentLocation()
            getLocationUpdates()
            startLocationUpdates()
        } else {
            makeRequest()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event, container, false)
        val viewmodel = ViewModelProvider(this, factory).get(EventFragmentViewModel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.eventFragmentEventListener = this
        viewmodel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            user_id = user.users_id!!
            searchView = binding.searchSpine
            tabLayout = binding.tabLayout
            setupViewPager(binding.viewPager)
            binding.tabLayout.setupWithViewPager(binding.viewPager)
        })
        Companion.progress = ProgressDialog(requireContext())
        Companion.progress.setTitle("Loading")
        Companion.progress.setMessage("Wait while loading...")
        Companion.progress.setCancelable(false) // disable dismiss by tapping outside of the dialog


        setintImages()



        binding.searchSpine?.setOnQueryTextFocusChangeListener { v, hasFocus ->
            /*if (hasFocus){
                    startActivity(Intent(requireContext(),FilterEventActivity::class.java))
                }
                binding.searchSpine.clearFocus()*/
        }
        binding.searchSpine?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String?): Boolean {
//                adapter?.filter?.filter(newText)

//                dataListTemp.clear()
//                if (newText!!.isEmpty()) {
//                    dataListTemp = dataList
//                } else {
//                    for (row in dataList) {
//                        for (data in row.records) {
//                            if ((data.title).toLowerCase(Locale.ROOT)
//                                    .contains(newText!!.toLowerCase(Locale.ROOT))
//                                || (data.displayName ?: data.useName).toLowerCase(Locale.ROOT)
//                                    .contains(newText!!.toLowerCase(Locale.ROOT))
//                                || (data.location).toLowerCase(Locale.ROOT)
//                                    .contains(newText!!.toLowerCase(Locale.ROOT))
//                                || (data.description).toLowerCase(Locale.ROOT)
//                                    .contains(newText!!.toLowerCase(Locale.ROOT))
//                            ) {
//                                dataListTemp.add(row)
//                            }
//                        }
//                    }
//                }
//
                println("Sanjay All...." + newText + "......" + currentFragment.toString())
//                adapter?.setFilterData(dataListTemp)


                if (currentFragment != null && currentFragment is EventFragmentAllList) {
                    (currentFragment as EventFragmentAllList).setFilterDataAll(newText!!)
                } else if (currentFragment != null && currentFragment is EventFragmentGoingList) {
                    (currentFragment as EventFragmentGoingList).setFilterDataGoing(newText!!)
                } else if (currentFragment != null && currentFragment is EventFragmentSavedList) {
                    (currentFragment as EventFragmentSavedList).setFilterDataSaved(newText!!)
                } else if (currentFragment != null && currentFragment is EventFragmentFollowingList) {
                    (currentFragment as EventFragmentFollowingList).setFilterDataFollowing(newText!!)
                } else if (currentFragment != null && currentFragment is EventFragmentOnLineList) {
                    (currentFragment as EventFragmentOnLineList).setFilterDataOnLine(newText!!)
                } else if (currentFragment != null && currentFragment is EventFragmentNearByList) {
                    (currentFragment as EventFragmentNearByList).setFilterDataNearBy(newText!!)
                } else if (currentFragment != null && currentFragment is EventFragmentPastList) {
                    (currentFragment as EventFragmentPastList).setFilterDataPast(newText!!)
                }

                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private fun setupViewPager(viewpager: ViewPager) {
        var adapter: ViewPagerAdapter = ViewPagerAdapter(childFragmentManager)

        // LoginFragment is the name of Fragment and the Login
        // is a title of tab
        adapter.addFragment(EventFragmentAllList.newInstance("ALL", user_id)!!, "ALL")
        adapter.addFragment(EventFragmentGoingList.newInstance("GOING", user_id)!!, "GOING")
        adapter.addFragment(EventFragmentSavedList.newInstance("SAVED", user_id)!!, "SAVED")
        adapter.addFragment(
            EventFragmentFollowingList.newInstance("FOLLOWING", user_id)!!,
            "FOLLOWING"
        )
        adapter.addFragment(EventFragmentOnLineList.newInstance("ONLINE", user_id)!!, "ONLINE")
        adapter.addFragment(EventFragmentNearByList.newInstance("NEARBY", user_id)!!, "NEARBY")
        adapter.addFragment(EventFragmentPastList.newInstance("PAST", user_id)!!, "PAST")
        adapter.addFragment(EventFragmentMetaList.newInstance("META", user_id)!!, "META")

        // setting adapter to view pager.
        viewpager.adapter = adapter


        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {

                // do this instead, assuming your adapter reference
                // is named mAdapter:
                currentFragment = adapter.fragmentList1.get(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        currentFragment = adapter.fragmentList1.get(0)
    }


    fun setintImages() {


        binding.viewPager.visibility = View.GONE
        binding.initLayout.initLiner.visibility = View.VISIBLE

        binding.initLayout.initLiner.setOnClickListener {

            binding.viewPager.visibility = View.VISIBLE
            binding.initLayout.initLiner.visibility = View.GONE
        }



        lifecycleScope.launch {
            try {
                Companion.progress.show()
                val res = homeRepositry.getEventType()
                Companion.progress.dismiss()
                if (res.status) {

                    var data = res.data

                    val circularProgressDrawable = CircularProgressDrawable(requireContext())
                    circularProgressDrawable.strokeWidth = 5f
                    circularProgressDrawable.centerRadius = 30f
                    circularProgressDrawable.start()
                    Glide.with(requireContext())
                        .load("https://thespiritualnetwork.com/assets/upload/spine-types/" + data[0].typeimage)
                        .placeholder(circularProgressDrawable)
                        .error(
                            ColorDrawable(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.light_gry
                                )
                            )
                        )
                        .into(binding.initLayout.previewLocal);

                    Glide.with(requireContext())
                        .load("https://thespiritualnetwork.com/assets/upload/spine-types/" + data[1].typeimage)
                        .placeholder(circularProgressDrawable)
                        .error(
                            ColorDrawable(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.light_gry
                                )
                            )
                        )
                        .into(binding.initLayout.previewRetraint);

                    Glide.with(requireContext())
                        .load("https://thespiritualnetwork.com/assets/upload/spine-types/" + data[2].typeimage)
                        .placeholder(circularProgressDrawable)
                        .error(
                            ColorDrawable(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.light_gry
                                )
                            )
                        )
                        .into(binding.initLayout.previewMeta);

                }
            } catch (e: ApiException) {
                Companion.progress.dismiss()
                e.printStackTrace()
            } catch (e: NoInternetException) {
                Companion.progress.dismiss()
                e.printStackTrace()
            }
        }
    }


//    listOf("ALL", "GOING", "SAVED", "FOLLOWING", "ONLINE", "NEARBY", "PAST")

    class ViewPagerAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        // objects of arraylist. One is of Fragment type and
        // another one is of String type.*/
        public var fragmentList1: ArrayList<Fragment> = ArrayList()
        private var fragmentTitleList1: ArrayList<String> = ArrayList()


        // returns which item is selected from arraylist of fragments.
        override fun getItem(position: Int): Fragment {
            return fragmentList1.get(position)
        }

        // returns which item is selected from arraylist of titles.
        @Nullable
        override fun getPageTitle(position: Int): CharSequence {
            return fragmentTitleList1.get(position)
        }

        // returns the number of items present in arraylist.
        override fun getCount(): Int {
            return fragmentList1.size
        }

        // this function adds the fragment and title in 2 separate  arraylist.
        fun addFragment(fragment: Fragment, title: String) {
            fragmentList1.add(fragment)
            fragmentTitleList1.add(title)
        }

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE;
        }


    }

//    private fun setEventList() {
//        lifecycleScope.launch {
//            try {
//                val res = homeRepositry.getAllEvents(1, 100, user_id, "all")
//                dataList.clear()
//                if (res.status) {
//                    STORY_IMAGE = res.user_image
//                    PROFILE_PIC_URL = res.image
//                    Log.e("imagetwo", res.image)
//                    dataList = res.data
//                    adapter = EventListAdapter(requireContext(), dataList, this@EventFragmentList, 0)
//                    binding.rvEventList.also {
//                        it.layoutManager =
//                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//                        it.setHasFixedSize(true)
//                        it.isNestedScrollingEnabled = false
//                        it.adapter = adapter
//                    }
//                }
//                adapter?.notifyDataSetChanged()
//
//            } catch (e: ApiException) {
//                e.printStackTrace()
//            } catch (e: NoInternetException) {
//                e.printStackTrace()
//            }
//        }
//
//    }
//
//    fun setEventMenu(i: Int) {
//        binding.rvEventMenu.also {
//            it.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
//            //  val list: List<String> = listOf("All","Nearby","Online","Following","Going","Saved","Past")
//            val list: List<String> =
//                listOf("ALL", "GOING", "SAVED", "FOLLOWING", "ONLINE", "NEARBY", "PAST")
//            it.setHasFixedSize(true)
//            it.adapter = EventMenuAdapter(requireContext(), list, i, this)
//        }
//    }
//
//    fun showSelection(pos: Int) {
//        binding.rvEventMenu.postDelayed(Runnable {
//            binding.rvEventMenu.smoothScrollToPosition(pos)
//        }, 1000)
//    }

    override fun onAddButtonClick() {
        //startActivity(Intent(requireContext(),AddPostActivity::class.java))
        showAddBottomsheet()
    }

    private fun showAddBottomsheet() {
        val view: View = layoutInflater.inflate(R.layout.add_post_bottomheet, null)
        val dialog: BottomSheetDialog = BottomSheetDialog(requireContext())
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
        dialog.button106.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    PostThoughtActivity::class.java
                )
            )
        }
        dialog.button107.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    PostMediaActivity::class.java
                )
            )
        }
        dialog.button108.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    AddStoryActivity::class.java
                )
            )
        }
        dialog.button109.setOnClickListener {
            startActivity(Intent(requireContext(), AddOrDupEventActivity::class.java))
            dialog.dismiss()
        }
        dialog.button110.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    AddRssActivity::class.java
                )
            )
        }
        dialog.button111.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    FeaturedPostActivity::class.java
                )
            )
        }
        dialog.show()
    }

    override fun onFilterEvent() {
        startActivity(Intent(requireContext(), FilterEventActivity::class.java))
    }

    override fun onMapview() {
        startActivity(Intent(requireContext(), MapviewEventsActivity::class.java))
    }

//    override fun onEventSave(record: EventsRecord, value: Int) {
//        Log.e("valuee", value.toString())
//        if (value == 0) {
//            lifecycleScope.launch {
//                try {
//                    val res = homeRepositry.removeEventSave(user_id, record.id)
//                    if (res.status) {
//
//                        "Removed".toast(requireContext())
//                        setEventList()
//                        setEventMenu(0)
//                    }
//                } catch (e: ApiException) {
//                    e.printStackTrace()
//                } catch (e: NoInternetException) {
//                    e.printStackTrace()
//                }
//            }
//        } else {
//            lifecycleScope.launch {
//                try {
//                    val res = homeRepositry.saveEvents(user_id, record.id)
//                    if (res.status) {
//                        val msg = res.message
//                        msg.toast(requireContext())
//                        setEventList()
//                    }
//                } catch (e: ApiException) {
//                    e.printStackTrace()
//                } catch (e: NoInternetException) {
//                    e.printStackTrace()
//                }
//            }
//        }
//
//    }

//    override fun onEventDetails(record: EventsRecord) {
//        val intent = Intent(requireContext(), EventDetailActivity::class.java)
//        intent.putExtra(EVE_RECORD, record)
//        intent.putExtra(B_IMG_URL, PROFILE_PIC_URL)
//        intent.putExtra("event_id", record.id)
//        startActivity(intent)
//    }

//    override fun crossEvent(value: Int) {
//
//        if (value == 1) {
//            setEventList()
//        }
//    }

    override fun onResume() {
        super.onResume()
        /* user_id?.let {
             setEventList()
             setEventMenu(0)
         }*/
        if (!user_id.isEmpty()) {
            val isFilter = Prefs.getBoolean("isFilter", false)
            if (isFilter) {
                val lat = Prefs.getString("lat", lat.toString())
                val date = Prefs.getString("date", "")
                val datetwo = Prefs.getString("datetwo", "")
                val category = Prefs.getString("category", "")
                val lon = Prefs.getString("lon", lon.toString())
                getFilteredList(lat, lon, date, datetwo, category)
            }
        }
    }

    private fun getFilteredList(
        lat: String?,
        lon: String?,
        start_date: String?,
        end_date: String?,
        category: String?
    ) {
        Log.e("start", start_date.toString())
        lifecycleScope.launch {

            try {
                Companion.progress.show()
                val res = homeRepositry.getFilteredEventList(
                    "1", "100",
                    user_id,
                    lat!!,
                    lon!!,
                    "",
                    start_date!!,
                    end_date!!,
                    category!!
                )
                dataList.clear()
                Companion.progress.dismiss()
                if (res.status) {

                    dataList = res.data
//                    STORY_IMAGE = res.image
//
//                    Log.e("filteredRes: ", "" + dataList)
//                    adapter = EventListAdapter(requireContext(), dataList, this@EventFragmentList, 1)
//                    binding.rvEventList.also {
//                        it.layoutManager =
//                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//                        it.setHasFixedSize(true)
//                        it.adapter = adapter
//                    }
                } else {
                    Companion.progress.dismiss()
                    "${res.message}".toast(requireContext())
                }
                adapter?.notifyDataSetChanged()
                Prefs.putAny("isFilter", false)
            } catch (e: ApiException) {
                Companion.progress.dismiss()
                e.printStackTrace()
                "${e.message}".toast(requireContext())
                Prefs.putAny("isFilter", false)
            } catch (e: NoInternetException) {
                Companion.progress.dismiss()
                e.printStackTrace()
                Prefs.putAny("isFilter", false)
            }
        }
    }

//    override fun onMenuSelected(selected: Int) {
//        setEventMenu(selected)
//        showSelection(selected)
//        when (selected) {
//            0 -> setEventList()
//            1 -> going()
//            2 -> saved()
//            3 -> setFollowingEvents()
//            4 -> setOnLineEvents()
//            5 -> setNearbyEvents()
//            6 -> past()
//            else -> {
//                setEventList()
//            }
//        }
//    }

    private fun past() {
        getGoingPastEventList(1)
    }

    private fun saved() {
//        lifecycleScope.launch {
//            try {
//                val res = homeRepositry.getAllSavedEvents(1, 100, user_id)
//                dataList.clear()
//                if (res.status) {
//                    BASE_IMAGE = res.image
//                    dataList = res.data
//                    adapter = EventListAdapter(requireContext(), dataList, this@EventFragmentList, 0)
//                    binding.rvEventList.also {
//                        it.layoutManager =
//                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//                        it.setHasFixedSize(true)
//                        it.adapter = adapter
//                    }
//                }
//                adapter?.notifyDataSetChanged()
//            } catch (e: ApiException) {
//                e.printStackTrace()
//            } catch (e: NoInternetException) {
//                e.printStackTrace()
//            }
//        }
    }

    private fun going() {
        getGoingPastEventList(0)
    }

    private fun getGoingPastEventList(goingPast: Int) {
//        lifecycleScope.launch {
//            try {
//                val res = homeRepositry.getGoingPastEventsList(1, 100, user_id, goingPast)
//                dataList.clear()
//                if (res.status) {
//                    BASE_IMAGE = res.image
//                    dataList = res.data
//                    adapter = EventListAdapter(requireContext(), dataList, this@EventFragmentList, 0)
//                    binding.rvEventList.also {
//                        it.layoutManager =
//                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//                        it.setHasFixedSize(true)
//                        it.adapter = adapter
//                    }
//                }
//                adapter?.notifyDataSetChanged()
//            } catch (e: ApiException) {
//                e.printStackTrace()
//            } catch (e: NoInternetException) {
//                e.printStackTrace()
//            }
//        }
    }

    private fun setNearbyEvents() {
//        Log.e("latlong: ", "$lat, $lon")
//        lifecycleScope.launch {
//            try {
//                val res = homeRepositry.getNearbyEvents(1, 100, user_id, lat, lon, 10)
//                dataList.clear()
//                if (res.status) {
//                    STORY_IMAGE = res.image
//                    dataList = res.data
//                    Log.e("latlongRes: ", "" + dataList)
//                    adapter = EventListAdapter(requireContext(), dataList, this@EventFragmentList, 0)
//                    binding.rvEventList.also {
//                        it.layoutManager =
//                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//                        it.setHasFixedSize(true)
//                        it.adapter = adapter
//                    }
//                }
//                adapter?.notifyDataSetChanged()
//            } catch (e: ApiException) {
//                e.printStackTrace()
//            } catch (e: NoInternetException) {
//                e.printStackTrace()
//            }
//        }
    }

    private fun setFollowingEvents() {
//        lifecycleScope.launch {
//            try {
//                val res = homeRepositry.getFollowingUsersEventsList(1, 100, user_id)
//                dataList.clear()
//                if (res.status) {
//                    STORY_IMAGE = res.image
//                    dataList = res.data
//                    adapter = EventListAdapter(requireContext(), dataList, this@EventFragmentList, 0)
//                    binding.rvEventList.also {
//                        it.layoutManager =
//                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//                        it.setHasFixedSize(true)
//                        it.adapter = adapter
//                    }
//                }
//                adapter?.notifyDataSetChanged()
//            } catch (e: ApiException) {
//                e.printStackTrace()
//            } catch (e: NoInternetException) {
//                e.printStackTrace()
//            }
//        }
    }

    private fun setOnLineEvents() {
//        lifecycleScope.launch {
//            try {
//                val res = homeRepositry.getOnLineEventsList(1, 100, user_id)
//                dataList.clear()
//                if (res.status) {
//                    STORY_IMAGE = res.image
//                    dataList = res.data
//                    adapter = EventListAdapter(requireContext(), dataList, this@EventFragmentList, 0)
//                    binding.rvEventList.also {
//                        it.layoutManager =
//                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//                        it.setHasFixedSize(true)
//                        it.adapter = adapter
//                    }
//                }
//                adapter?.notifyDataSetChanged()
//            } catch (e: ApiException) {
//                e.printStackTrace()
//            } catch (e: NoInternetException) {
//                e.printStackTrace()
//            }
//        }
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
                        lat = location.latitude
                        lon = location.longitude
                        Log.e("loc::", "$lat , $lon")
                    }
                }


            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
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
        if (locationCallback != null) {
            stopLocationUpdates()
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
        ActivityCompat.requestPermissions(requireActivity(), permissions, PERMISSION_REQUEST_CODE)
    }


    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
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
                }
            }

    }

}
