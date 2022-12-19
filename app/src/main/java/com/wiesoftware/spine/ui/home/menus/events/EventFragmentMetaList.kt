package com.wiesoftware.spine.ui.home.menus.events

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.EventListAdapter
import com.wiesoftware.spine.data.adapter.EventMenuAdapter
import com.wiesoftware.spine.data.net.reponses.EventsData
import com.wiesoftware.spine.data.net.reponses.EventsRecord
import com.wiesoftware.spine.data.repo.EventRepository
import com.wiesoftware.spine.databinding.FragmentEventsListBinding
import com.wiesoftware.spine.ui.home.menus.events.event_details.EventDetailActivity
import com.wiesoftware.spine.ui.home.menus.events.filter.FilterEventActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE
import com.wiesoftware.spine.util.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.text.SimpleDateFormat
import java.util.*


class EventFragmentMetaList : Fragment(), KodeinAware, EventFragmentEventListener,
    EventListAdapter.OnEventSaveListener, EventMenuAdapter.OnMenuSelectedListener {

    val PERMISSION_REQUEST_CODE = 94

    var adapter: EventListAdapter? = null

    var lat: Double = 0.0
    var lon: Double = 0.0

    override val kodein by kodein()
    val factory: EventFragmentViewModelFactory by instance()
    val eventRepositry: EventRepository by instance()
    lateinit var binding: FragmentEventsListBinding
    var user_id: String = ""
    var argString: String = ""
    var dataList: MutableList<EventsData> = mutableListOf()
    var dataListTemp: MutableList<EventsData> = mutableListOf()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    companion object {
        fun newInstance(fragmentArgumentsValue: String?, userID: String?): Fragment? {
            val f = EventFragmentMetaList()
            val bun = Bundle()
            bun.putString("type", fragmentArgumentsValue)
            bun.putString("userID", userID)
            f.arguments = bun
            return f
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_events_list, container, false)
        val viewmodel = ViewModelProvider(this, factory).get(EventFragmentViewModel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.eventFragmentEventListener = this
//        viewmodel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
//            user_id = user.users_id!!
//        })

        viewmodel.getLoggedInUser().observe(viewLifecycleOwner, androidx.lifecycle.Observer { user -> user_id = user.users_id!!  })

        arguments?.let {
            argString = it.getString("type").toString()
            user_id = it.getString("userID").toString()
        }

//        EventFragment.searchView?.setOnQueryTextFocusChangeListener { v, hasFocus ->
//            /*if (hasFocus){
//                    startActivity(Intent(requireContext(),FilterEventActivity::class.java))
//                }
//                binding.searchSpine.clearFocus()*/
//        }
//        EventFragment.searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//
//            override fun onQueryTextChange(newText: String?): Boolean {
////                adapter?.filter?.filter(newText)
//
//                println("Sanjay....."+newText)
//
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
//                adapter?.setFilterData(dataListTemp)
//
//                return true
//            }
//
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return true
//            }
//        })

        when (argString) {
            "ALL" -> {
//                adapter = EventListAdapter(requireContext(), dataList, this@EventFragmentList, 0)
                setEventList()
            }

            "GOING" -> {
                going()
            }

            "SAVED" -> {
                saved()
            }

            "FOLLOWING" -> {
                setFollowingEvents()
            }

            "ONLINE" -> {
                setOnLineEvents()
            }

            "NEARBY" -> {
                setNearbyEvents()
            }

            "PAST" -> {
                past()
            }

            "META" -> {
                meta()
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false



            meta();
        }

        binding.texteditFltr.setOnClickListener {
            startActivity(
                Intent(
                requireActivity(),
                FilterEventActivity::class.java
            )
            )
        }

        binding.txtCross.setOnClickListener {
            setOnLineEvents()
            binding.rlFilterData.visibility = View.GONE
        }

        return binding.root
    }


    fun setFilterDataOnLine(newText: String) {
        dataListTemp.clear()
        if (newText!!.isEmpty()) {
            dataListTemp = dataList
        } else {
            for (row in dataList) {
                for (data in row.records) {
                    if ((data.title).toLowerCase(Locale.ROOT)
                            .contains(newText!!.toLowerCase(Locale.ROOT))
                        || (data.displayName ?: data.useName).toLowerCase(Locale.ROOT)
                            .contains(newText!!.toLowerCase(Locale.ROOT))
                        || (data.location).toLowerCase(Locale.ROOT)
                            .contains(newText!!.toLowerCase(Locale.ROOT))
                        || (data.description).toLowerCase(Locale.ROOT)
                            .contains(newText!!.toLowerCase(Locale.ROOT))
                    ) {
                        dataListTemp.add(row)
                    }
                }
            }
        }
        println("Sanjay OnLine...."+newText+"......"+dataListTemp.size)
        adapter?.setFilterData(dataListTemp)
    }


    private fun setEventList() {
        lifecycleScope.launch {
            try {
                val res = eventRepositry.getAllEvents(1, 100, user_id, "all")
                dataList.clear()
                if (res.status) {
                    STORY_IMAGE = res.user_image
                    PROFILE_PIC_URL = res.image
                    Log.e("imagetwo", res.image)
                    dataList = res.data
                    adapter =
                        EventListAdapter(requireContext(), dataList, this@EventFragmentMetaList, 0)
                    binding.rvEventList.also {
                        it.layoutManager =
                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                        it.setHasFixedSize(true)
                        it.isNestedScrollingEnabled = false
                        it.adapter = adapter
                    }

                    if (dataList.size > 0)
                        EventFragment.tabLayout?.getTabAt(0)?.setText("ALL ("+dataList.size+")");
                    else
                        EventFragment.tabLayout?.getTabAt(0)?.setText("ALL");
                }
                adapter?.notifyDataSetChanged()

            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }

    }


    override fun onAddButtonClick() {

    }

    override fun onFilterEvent() {

    }

    override fun onMapview() {

    }


    override fun onEventSave(record: EventsRecord, value: Int) {
        if (value == 0) {
            lifecycleScope.launch {
                try {
                    val res = eventRepositry.removeEventSave(user_id, record.id)
                    if (res.status) {

                        "Removed".toast(requireContext())
                        setEventList()
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                } catch (e: NoInternetException) {
                    e.printStackTrace()
                }
            }
        } else {
            lifecycleScope.launch {
                try {
                    val res = eventRepositry.saveEvents(user_id, record.id)
                    if (res.status) {
                        val msg = res.message
                        msg.toast(requireContext())
                        setEventList()
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                } catch (e: NoInternetException) {
                    e.printStackTrace()
                }
            }
        }

    }

    override fun onEventDetails(record: EventsRecord) {
        val intent = Intent(requireContext(), EventDetailActivity::class.java)
        intent.putExtra(EVE_RECORD, record)
        intent.putExtra(B_IMG_URL, PROFILE_PIC_URL)
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

    override fun crossEvent(value: Int) {
        if (value == 1) {
            setEventList()
        }
    }

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

                binding.rlFilterData.visibility = View.VISIBLE


                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dd = SimpleDateFormat("dd MMM", Locale.getDefault())

                var combineDate: String = ""
                if(Prefs.getString("date", "") != "") {
                    var firstDateMonth: List<String> = Prefs.getString("date", "")?.split("-")!!
                    var secondDateMonth: List<String>  = Prefs.getString("datetwo", "")?.split("-")!!

                    combineDate = if(firstDateMonth[1] == secondDateMonth[1]) {
                        firstDateMonth[2] + " - "  +dd.format(simpleDateFormat.parse(Prefs.getString("datetwo", "")))
                    } else {
                        dd.format(simpleDateFormat.parse(Prefs.getString("date", ""))) + " - "  +dd.format(simpleDateFormat.parse(
                            Prefs.getString("datetwo", "")))
                    }
                }

                var totalCategories: Int = 0
                if(Prefs.getString("category", "") != "") {
                    var categories: List<String> = Prefs.getString("category", "")?.split(",")!!
                    totalCategories = categories.size
                }

                var filterText = ""

                if(Prefs.getString("location", "") != "") {
                    if(Prefs.getString("location", "")!!.contains(",")) {
                        filterText = Prefs.getString("location", "")?.split(",")!![0] + ", "
                    } else {
                        filterText = Prefs.getString("location", "") + ", "
                    }
                }

                if(combineDate != "") {
                    filterText = filterText + combineDate + ", "
                }

                if(totalCategories > 0) {
                    if(totalCategories > 1)
                        binding.textView33.text = filterText + totalCategories.toString() + "categories"
                    else
                        binding.textView33.text = filterText + totalCategories.toString() + "category"
                } else {
                    binding.textView33.text = method(filterText)
                }
            }
        }

    }

    fun method(str: String?): String? {
        var str = str
        if (str != null && str.length > 0 && str[str.length - 2] == ',') {
            str = str.substring(0, str.length - 2)
        }
        return str
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
                val res = eventRepositry.getFilteredEventList(
                    "1",
                    "100",
                    user_id,
                    lat!!,
                    lon!!,
                    "0",
                    start_date!!,
                    end_date!!,
                    category!!
                )
                dataList.clear()
                if (res.status) {
                    STORY_IMAGE = res.image
                    dataList = res.data
                    Log.e("filteredRes: ", "" + dataList)
                    adapter =
                        EventListAdapter(requireContext(), dataList, this@EventFragmentMetaList, 0)
                    binding.rvEventList.also {
                        it.layoutManager =
                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                        it.setHasFixedSize(true)
                        it.adapter = adapter
                    }

                    var count = 0
                    if (dataList.size > 0) {
                        for (list in dataList) {
                            if (list.records != null) count += list.records.size
                        }
                    }

                    if (count > 0)
                        EventFragment.tabLayout?.getTabAt(4)?.setText("ONLINE (" + count + ")");
                    else
                        EventFragment.tabLayout?.getTabAt(4)?.setText("ONLINE");

                } else {
                    "${res.message}".toast(requireContext())
                }
                adapter?.notifyDataSetChanged()
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

    override fun onMenuSelected(selected: Int) {
    }

    private fun past() {
        getGoingPastEventList(1)
    }

    private fun saved() {
        lifecycleScope.launch {
            try {
                val res = eventRepositry.getAllSavedEvents(1, 100, user_id)
                dataList.clear()
                if (res.status) {
                    BASE_IMAGE = res.image
                    dataList = res.data
                    adapter =
                        EventListAdapter(requireContext(), dataList, this@EventFragmentMetaList, 0)
                    binding.rvEventList.also {
                        it.layoutManager =
                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                        it.setHasFixedSize(true)
                        it.adapter = adapter
                    }
                }

                if (dataList.size > 0)
                    EventFragment.tabLayout?.getTabAt(2)?.setText("SAVED ("+dataList.size+")");
                else
                    EventFragment.tabLayout?.getTabAt(2)?.setText("SAVED");

                adapter?.notifyDataSetChanged()
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    private fun meta() {
        lifecycleScope.launch {
            try {
                EventFragment.progress.show()
                val res = eventRepositry.getAllEvents(1, 100, "metaverse", "")
                dataList.clear()
                EventFragment.progress.dismiss()
                if (res.status) {
                    BASE_IMAGE = res.image
                    dataList = res.data
                    adapter =
                        EventListAdapter(requireContext(), dataList, this@EventFragmentMetaList, 0)
                    binding.rvEventList.also {
                        it.layoutManager =
                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                        it.setHasFixedSize(true)
                        it.adapter = adapter
                    }
                }

                if (dataList.size > 0)
                    EventFragment.tabLayout?.getTabAt(7)?.setText("META ("+dataList.size+")");
                else
                    EventFragment.tabLayout?.getTabAt(7)?.setText("META");

                adapter?.notifyDataSetChanged()
            } catch (e: ApiException) {
                EventFragment.progress.dismiss()
                e.printStackTrace()
            } catch (e: NoInternetException) {
                EventFragment.progress.dismiss()
                e.printStackTrace()
            }
        }
    }

    private fun going() {
        getGoingPastEventList(0)
    }

    private fun getGoingPastEventList(goingPast: Int) {
        lifecycleScope.launch {
            try {
                val res = eventRepositry.getGoingPastEventsList(1, 100, user_id, goingPast)
                dataList.clear()
                if (res.status) {
                    BASE_IMAGE = res.image
                    dataList = res.data
                    adapter =
                        EventListAdapter(requireContext(), dataList, this@EventFragmentMetaList, 0)
                    binding.rvEventList.also {
                        it.layoutManager =
                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                        it.setHasFixedSize(true)
                        it.adapter = adapter
                    }
                }

                if(goingPast == 0) {
                    if (dataList.size > 0)
                        EventFragment.tabLayout?.getTabAt(1)?.setText("GOING ("+dataList.size+")");
                    else
                        EventFragment.tabLayout?.getTabAt(1)?.setText("GOING");
                } else {
                    if (dataList.size > 0)
                        EventFragment.tabLayout?.getTabAt(6)?.setText("PAST ("+dataList.size+")");
                    else
                        EventFragment.tabLayout?.getTabAt(6)?.setText("PAST");
                }


                adapter?.notifyDataSetChanged()
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    private fun setNearbyEvents() {
        Log.e("latlong: ", "$lat, $lon")
        lifecycleScope.launch {
            try {
                val res = eventRepositry.getNearbyEvents(1, 100, user_id, lat, lon, 10)
                dataList.clear()
                if (res.status) {
                    STORY_IMAGE = res.image
                    dataList = res.data
                    Log.e("latlongRes: ", "" + dataList)
                    adapter =
                        EventListAdapter(requireContext(), dataList, this@EventFragmentMetaList, 0)
                    binding.rvEventList.also {
                        it.layoutManager =
                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                        it.setHasFixedSize(true)
                        it.adapter = adapter
                    }
                }

                if (dataList.size > 0)
                    EventFragment.tabLayout?.getTabAt(5)?.setText("NEARBY ("+dataList.size+")");
                else
                    EventFragment.tabLayout?.getTabAt(5)?.setText("NEARBY");

                adapter?.notifyDataSetChanged()
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    private fun setFollowingEvents() {
        lifecycleScope.launch {
            try {
                val res = eventRepositry.getFollowingUsersEventsList(1, 100, user_id)
                dataList.clear()
                if (res.status) {
                    STORY_IMAGE = res.image
                    dataList = res.data
                    adapter =
                        EventListAdapter(requireContext(), dataList, this@EventFragmentMetaList, 0)
                    binding.rvEventList.also {
                        it.layoutManager =
                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                        it.setHasFixedSize(true)
                        it.adapter = adapter
                    }
                }

                if (dataList.size > 0)
                    EventFragment.tabLayout?.getTabAt(3)?.setText("FOLLOWING ("+dataList.size+")");
                else
                    EventFragment.tabLayout?.getTabAt(3)?.setText("FOLLOWING");

                adapter?.notifyDataSetChanged()
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    private fun setOnLineEvents() {
        lifecycleScope.launch {
            try {
                val res = eventRepositry.getAllEvents(1, 100, "online", "")
                dataList.clear()
                if (res.status) {
                    STORY_IMAGE = res.image
                    dataList = res.data
                    adapter =
                        EventListAdapter(requireContext(), dataList, this@EventFragmentMetaList, 0)
                    binding.rvEventList.also {
                        it.layoutManager =
                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                        it.setHasFixedSize(true)
                        it.adapter = adapter
                    }
                }

//                if (dataList.size > 0)
//                    EventFragment.tabLayout?.getTabAt(4)?.setText("ONLINE ("+dataList.size+")");
//                else
//                    EventFragment.tabLayout?.getTabAt(4)?.setText("ONLINE");

                var count = 0
                if (dataList.size > 0) {
                    for (list in dataList) {
                        if (list.records != null) count += list.records.size
                    }
                }

                if (count > 0)
                    EventFragment.tabLayout?.getTabAt(4)?.setText("ONLINE (" + count + ")");
                else
                    EventFragment.tabLayout?.getTabAt(4)?.setText("ONLINE");

                adapter?.notifyDataSetChanged()
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }

}