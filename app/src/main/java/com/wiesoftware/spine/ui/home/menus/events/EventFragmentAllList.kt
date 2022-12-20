package com.wiesoftware.spine.ui.home.menus.events

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView.OnScrollChangeListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.EventListAdapter
import com.wiesoftware.spine.data.adapter.EventMenuAdapter
import com.wiesoftware.spine.data.net.reponses.EventsData
import com.wiesoftware.spine.data.net.reponses.EventsRecord
import com.wiesoftware.spine.data.net.reponses.EventsRes
import com.wiesoftware.spine.data.repo.EventRepositry
import com.wiesoftware.spine.databinding.FragmentEventsListBinding
import com.wiesoftware.spine.databinding.RvEventContentItemBinding
import com.wiesoftware.spine.databinding.RvEventListItemBinding
import com.wiesoftware.spine.ui.home.menus.events.event_details.EventDetailActivity
import com.wiesoftware.spine.ui.home.menus.events.filter.FilterEventActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE
import com.wiesoftware.spine.util.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class EventFragmentAllList : Fragment(), KodeinAware, EventFragmentEventListener,
    EventListAdapter.OnEventSaveListener, EventMenuAdapter.OnMenuSelectedListener {

    val PERMISSION_REQUEST_CODE = 94

    val EVE_RECORD = "eve_record"
    val B_IMG_URL = "base_url"
    val IS_FROM_EVENT_DETAILS = "isFromEventDetails"
    var PROFILE_PIC_URL = ""

//    var adapter: EventListAdapter? = null
    var mAdapter: BaseAdapter<EventsData>? = null

    var lat: Double = 0.0
    var lon: Double = 0.0

    override val kodein by kodein()
    val factory: EventFragmentViewmodelFactory by instance()
    val eventRepositry: EventRepositry by instance()
    lateinit var binding: FragmentEventsListBinding
    var user_id: String = ""
    var argString: String = ""
    var dataList: MutableList<EventsData> = mutableListOf()

    var page = 1
    var perpage = 100
    var type = "all"
    var typeID = ""
    var hasmore = true;

    var  mlayoutManager: LinearLayoutManager? =  null;

    var currnetTab = 0




    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    companion object {
        fun newInstance(fragmentArgumentsValue: String?, userID: String?): Fragment? {
            val f = EventFragmentAllList()
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
        viewmodel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            user_id = user.users_id!!
        })

        arguments?.let {
            argString = it.getString("type").toString()
            user_id = it.getString("userID").toString()
        }

        mlayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)



        when (argString) {
            "ALL" -> {
                currnetTab = 0
//                adapter = EventListAdapter(requireContext(), dataList, this@EventFragmentList, 0)
                setEventList(page, perpage, type, typeID)

            }

            "GOING" -> {
                currnetTab = 1
//                going()
                type = "going"
                setEventList(page, perpage, type, typeID)
            }

            "SAVED" -> {
//                saved()
                currnetTab = 2
                type = "saved"
                setEventList(page, perpage, type, typeID)
            }

            "FOLLOWING" -> {
//                setFollowingEvents()
                currnetTab = 3
                type = "following"
                setEventList(page, perpage, type, typeID)
            }

            "ONLINE" -> {
//                setOnLineEvents()
                currnetTab = 4
                type = "online"
                setEventList(page, perpage, type, typeID)
            }

            "NEARBY" -> {
                currnetTab = 5
              setNearbyEvents()

            }

            "PAST" -> {
                currnetTab = 6
//                past()
                type = "past"
                setEventList(page, perpage, type, typeID)
            }

            "META" -> {
                currnetTab = 7
                type = "metaverse"
                setEventList(page, perpage, type, typeID)
            }
        }


        binding.texteditFltr.setOnClickListener {
            startActivity(Intent(
                requireActivity(),
                FilterEventActivity::class.java
            ))
        }

        binding.txtCross.setOnClickListener {
            setEventList(page, perpage, type, typeID)
            binding.rlFilterData.visibility = View.GONE
        }

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false

            setEventList(page, perpage, type, typeID)
        }

//        binding.idNestedSV.setOnScrollChangeListener(OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            // on scroll change we are checking when users scroll as bottom.
//            if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
//                // in this method we are incrementing page number,
//                // making progress bar visible and calling get data method.
//                page++
//                setEventList(page,perpage,type,typeID)
//            }
//        })
        binding.rvEventList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                try {
                    val firstPos: Int = mlayoutManager!!.findFirstCompletelyVisibleItemPosition()
                    if (firstPos > 0) {
                        binding.swipeRefresh.setEnabled(false)
                    } else {
                        binding.swipeRefresh.setEnabled(true)
                        if (binding.rvEventList.getScrollState() === 1) if (binding.swipeRefresh.isRefreshing()) binding.rvEventList.stopScroll()
                    }
                } catch (e: Exception) {
                    Log.e("Harsh", "Scroll Error : " + e.localizedMessage)
                }
            }
        })



        return binding.root
    }


    private fun setEventList(page: Int, perpage: Int, type: String, typeID: String) {

        if (!hasmore) {
            // checking if the page number is greater than limit.
            // displaying toast message in this case when page>limit.
            EventFragment.progress.dismiss()
            return;
        }

        lifecycleScope.launch {
            try {
                EventFragment.progress.show()
                val res = eventRepositry.getAllEvents(page, perpage, type, typeID)
                dataList.clear()
                EventFragment.progress.dismiss()
                if (res.status) {
                    dataList = res.data
                    STORY_IMAGE = res.user_image
                    PROFILE_PIC_URL = res.image

                    hasmore = true

                    setData(0,res)


//                    if (dataList.size > 0)
//                        EventFragment.tabLayout?.getTabAt(0)?.setText("ALL ("+dataList.size+")");
//                    else
//                        EventFragment.tabLayout?.getTabAt(0)?.setText("ALL");

                    var count = 0
                    if (dataList.size > 0) {
                        for (list in dataList) {
                            if(list.records != null) count += list.records.size
                        }
                    }
                    if(count > 0)
                        EventFragment.tabLayout?.getTabAt(currnetTab)?.setText("$argString ("+count+")");
                    else
                        EventFragment.tabLayout?.getTabAt(currnetTab)?.setText(argString);
                }else{
                    hasmore = false;
                }
//                adapter?.notifyDataSetChanged()


            } catch (e: ApiException) {
                EventFragment.progress.dismiss()
                e.printStackTrace()
            } catch (e: NoInternetException) {
                EventFragment.progress.dismiss()
                e.printStackTrace()
            }
        }

    }

    private fun setData(value: Int, res: EventsRes) {

         mAdapter = BaseAdapter<EventsData>(requireContext())

        mAdapter!!.listOfItems = res.data.toList()

        mAdapter!!.expressionViewHolderBinding = {eachItem,viewBinding,context->

            var view = viewBinding as RvEventListItemBinding

            val eve_date = eachItem.startDate

            val simpleDateFormat = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())

            try {
                val date1: Date = simpleDateFormat.parse(eve_date)
                val dd = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
                val ss: String = dd.format(date1).toUpperCase()
                Log.e("fmtDate: ", ss)
                if (value == 1) {

                    view.textView33.text = ss
                    view.texteditFltr.visibility = View.VISIBLE
                    view.txtCross.visibility = View.VISIBLE
                    view.texteditFltr.setOnClickListener {
                        startActivity(
                            Intent(
                                context,
                                FilterEventActivity::class.java
                            )
                        )
                    }
                    view.txtCross.setOnClickListener {
                        crossEvent(1)
                    }
                } else {
                    view.textView33.text = ss
                    view.texteditFltr.visibility = View.GONE
                    view.txtCross.visibility = View.GONE
                }

            } catch (e: ParseException) {
                e.printStackTrace()
                Log.e("fmtDate: ", e.message.toString())
            }



            // Content data
            view.model= eachItem

            var ContentAdapter = BaseAdapter<EventsRecord>(requireContext())

            ContentAdapter.listOfItems = eachItem.records

            ContentAdapter.expressionViewHolderBinding = {recordsList,viewBinding,context->
                //eachItem will provide the each item in the list, in this case its a string type
                //cast the viewBinding with yout layout binding class
                var holder = viewBinding as RvEventContentItemBinding

                holder.model=recordsList
                val eveType=recordsList.type

                //        Harsh: its tempeory line code because olg logic count event type from 0
                if (eveType.equals("0")) {
                    holder.textView36.text= getString(R.string.local_event)
                }

                if (eveType.equals("1")) {
                    holder.textView36.text=getString(R.string.local_event)
                } else if (eveType.equals("2")) {
                    holder.textView36.text=getString(R.string.online_event)
                } else if(eveType.equals("3")) {
                    holder.textView36.text=getString(R.string.retreat_event)
                } else if(eveType.equals("4")) {
                    holder.textView36.text=getString(R.string.metaverse_event)
                }
                if(recordsList.hostedProfilePic != null && recordsList.hostedProfilePic.isNotEmpty()){

                }

                Glide.with(context)
                    .load("https://thespiritualnetwork.com/assets/upload/profile/"+recordsList.hostedProfilePic)
                    .circleCrop()
                    .placeholder( ColorDrawable(ContextCompat.getColor(context, R.color.light_gry)))
                    .error( ColorDrawable(ContextCompat.getColor(context, R.color.light_gry)))
                    .dontAnimate()
                    .into(holder.circleImageView3);

                if(recordsList.title.toString().isNotEmpty()){
                    holder.textView37.text= recordsList.title.capitalize()
                }


                holder.circleImageView3.setOnClickListener {
                    onEventDetails(recordsList)
                }
                holder.textView36.setOnClickListener {
                    onEventDetails(recordsList)
                }
                holder.root.setOnClickListener {
                    onEventDetails(recordsList)
                }
                if (recordsList.userSaveStatus?.equals("1")) {
                    holder.imageButton7.setImageResource(R.drawable.ic_saved)
                }else{
                    holder.imageButton7.setImageResource(R.drawable.ic_bookmark)
                }
                var check:Boolean=false

                if (recordsList.userSaveStatus.equals("1")){
                    holder.imageButton7.setOnClickListener {
                        onEventSave(recordsList,0)
                    }
                }else{
                    holder.imageButton7.setOnClickListener {
                        onEventSave(recordsList,1)
                        holder.imageButton7.setImageResource(R.drawable.ic_saved)
                    }
                }

                holder.imageButton8.setOnClickListener {
                    onEventShare(recordsList)
                }


                val stDate=recordsList.startDate
                val edDate=recordsList.endDate
                val cc: Date = Calendar.getInstance().getTime()
                val simpleDateFormat = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())
                try {
                    val s=recordsList.createdOn


                    val date1: Date = simpleDateFormat.parse(stDate)
                    val date: String = simpleDateFormat.format(cc)
                    val date2: Date = simpleDateFormat.parse(edDate)
                    if (date1 != null && date2 != null) {
                        val d: String = printDifference(date1, date2)!!

                        if (d.contains("-")){
                            holder.textView39.text=d.replace("-","")
                        }else{
                            holder.textView39.text=d
                            if (d.contains("0")){
                                holder.textView39.visibility = View.GONE
                                holder.tvTimeTxt.visibility = View.VISIBLE
                            } else {
                                holder.textView39.visibility = View.VISIBLE
                                holder.tvTimeTxt.visibility = View.GONE
                            }
                        }
                        Log.e("Diff::","$d")
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                    Log.e("Diff::","$stDate, $edDate")
                }

                var formatter =  SimpleDateFormat("HH:mm:ss");
                val dTime = formatter.parse(recordsList.startTime)
                val dTimeone = formatter.parse(recordsList.endTime)
                Log.e("hou",dTime.hours.toString())

                holder.tvTimeTxt.text= dTime.hours.toString() +":"+ dTime.hours +"-"+dTimeone.hours+":"+dTimeone.minutes



            }

            ContentAdapter.expressionOnCreateViewHolder = {viewGroup->

                RvEventContentItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
            }

            view.rvEventContent.also {
                it.layoutManager =  LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                it.setHasFixedSize(true)
                it.adapter = ContentAdapter
            }
        }

        mAdapter!!.expressionOnCreateViewHolder = {viewGroup->
            RvEventListItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        }

        binding.rvEventList.apply {
            layoutManager = mlayoutManager
            adapter = mAdapter
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
                        setEventList(page, perpage, type, typeID)
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
                        setEventList(page, perpage, type, typeID)
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
            setEventList(page, perpage, type, typeID)
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
                        dd.format(simpleDateFormat.parse(Prefs.getString("date", ""))) + " - "  +dd.format(simpleDateFormat.parse(Prefs.getString("datetwo", "")))
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


    public fun setFilterDataAll(newText: String) {
        val dataListTemp: MutableList<EventsData> = mutableListOf()

        for (row in dataList) {
            for (data in row.records) {
                if ((data.title).toLowerCase(Locale.ROOT)
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

        mAdapter?.setFilterData(dataListTemp.toList())
    }

    private fun getFilteredList(
        lat: String?,
        lon: String?,
        start_date: String?,
        end_date: String?,
        category: String?
    ) {
        Log.e("start", start_date.toString())
        EventFragment.progress.dismiss()
        lifecycleScope.launch {
            try {
                val res = eventRepositry.getFilteredEventList(
                    "1",
                    "100",
                    user_id,
                    lat!!,
                    lon!!,
                    "10",
                    start_date!!,
                    end_date!!,
                    category!!
                )
                dataList.clear()
                if (res.status) {

                    STORY_IMAGE = res.image
                    dataList = res.data
                    Log.e("filteredRes: ", "" + dataList)

                    setData(0,res)

//                    adapter =
//                        EventListAdapter(requireContext(), dataList, this@EventFragmentAllList, 0)
//                    binding.rvEventList.also {
//                        it.layoutManager =
//                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//                        it.setHasFixedSize(true)
//                        it.adapter = adapter
//                    }

                    var count = 0
                    if (dataList.size > 0) {
                        for (list in dataList) {
                            if(list.records != null) count += list.records.size
                        }
                    }
                    if(count > 0)
                        EventFragment.tabLayout?.getTabAt(0)?.setText("ALL ("+count+")");
                    else
                        EventFragment.tabLayout?.getTabAt(0)?.setText("ALL");

                } else {
                    "${res.message}".toast(requireContext())
                }
//                adapter?.notifyDataSetChanged()
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
//        lifecycleScope.launch {
//            try {
//                val res = eventRepositry.getAllSavedEvents(1, 100, user_id)
//                dataList.clear()
//                if (res.status) {
//                    BASE_IMAGE = res.image
//                    dataList = res.data
//                    adapter =
//                        EventListAdapter(requireContext(), dataList, this@EventFragmentAllList, 0)
//                    binding.rvEventList.also {
//                        it.layoutManager =
//                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//                        it.setHasFixedSize(true)
//                        it.adapter = adapter
//                    }
//                }
//
//                if (dataList.size > 0)
//                    EventFragment.tabLayout?.getTabAt(2)?.setText("SAVED ("+dataList.size+")");
//                else
//                    EventFragment.tabLayout?.getTabAt(2)?.setText("SAVED");
//
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
//                val res = eventRepositry.getGoingPastEventsList(1, 100, user_id, goingPast)
//                dataList.clear()
//                if (res.status) {
//                    BASE_IMAGE = res.image
//                    dataList = res.data
//                    adapter =
//                        EventListAdapter(requireContext(), dataList, this@EventFragmentAllList, 0)
//                    binding.rvEventList.also {
//                        it.layoutManager =
//                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//                        it.setHasFixedSize(true)
//                        it.adapter = adapter
//                    }
//                }
//
//                if(goingPast == 0) {
//                    if (dataList.size > 0)
//                        EventFragment.tabLayout?.getTabAt(1)?.setText("GOING ("+dataList.size+")");
//                    else
//                        EventFragment.tabLayout?.getTabAt(1)?.setText("GOING");
//                } else {
//                    if (dataList.size > 0)
//                        EventFragment.tabLayout?.getTabAt(6)?.setText("PAST ("+dataList.size+")");
//                    else
//                        EventFragment.tabLayout?.getTabAt(6)?.setText("PAST");
//                }
//
//
//                adapter?.notifyDataSetChanged()
//            } catch (e: ApiException) {
//                e.printStackTrace()
//            } catch (e: NoInternetException) {
//                e.printStackTrace()
//            }
//        }
    }

    private fun setNearbyEvents() {
        Log.e("latlong: ", "$lat, $lon")
        lifecycleScope.launch {
            try {
                EventFragment.progress.show()
                val res = eventRepositry.getNearbyEvents(1, 100, user_id, lat, lon, 10)
                dataList.clear()
                EventFragment.progress.dismiss()
                if (res.status) {
                    STORY_IMAGE = res.image
                    dataList = res.data
                    Log.e("latlongRes: ", "" + dataList)

                    setData(0,res)
//                    adapter =
//                        EventListAdapter(requireContext(), dataList, this@EventFragmentNearByList, 0)
//                    binding.rvEventList.also {
//                        it.layoutManager =
//                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//                        it.setHasFixedSize(true)
//                        it.adapter = adapter
//                    }
                }

                var count = 0
                if (dataList.size > 0) {
                    for (list in dataList) {
                        if(list.records != null) count = count + list.records.size
                    }
                }
                if(count > 0)
                    EventFragment.tabLayout?.getTabAt(5)?.setText("NEARBY ("+count+")");
                else
                    EventFragment.tabLayout?.getTabAt(5)?.setText("NEARBY");

//                adapter?.setFilterData(dataList)
            } catch (e: ApiException) {
                EventFragment.progress.dismiss()
                e.printStackTrace()
            } catch (e: NoInternetException) {
                EventFragment.progress.dismiss()
                e.printStackTrace()
            }
        }
    }

    private fun setFollowingEvents() {
//        lifecycleScope.launch {
//            try {
//                val res = eventRepositry.getFollowingUsersEventsList(1, 100, user_id)
//                dataList.clear()
//                if (res.status) {
//                    STORY_IMAGE = res.image
//                    dataList = res.data
//                    adapter =
//                        EventListAdapter(requireContext(), dataList, this@EventFragmentAllList, 0)
//                    binding.rvEventList.also {
//                        it.layoutManager =
//                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//                        it.setHasFixedSize(true)
//                        it.adapter = adapter
//                    }
//                }
//
//                if (dataList.size > 0)
//                    EventFragment.tabLayout?.getTabAt(3)?.setText("FOLLOWING ("+dataList.size+")");
//                else
//                    EventFragment.tabLayout?.getTabAt(3)?.setText("FOLLOWING");
//
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
//                val res = eventRepositry.getOnLineEventsList(1, 100, user_id)
//                dataList.clear()
//                if (res.status) {
//                    STORY_IMAGE = res.image
//                    dataList = res.data
//                    adapter =
//                        EventListAdapter(requireContext(), dataList, this@EventFragmentAllList, 0)
//                    binding.rvEventList.also {
//                        it.layoutManager =
//                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//                        it.setHasFixedSize(true)
//                        it.adapter = adapter
//                    }
//                }
//
//                if (dataList.size > 0)
//                    EventFragment.tabLayout?.getTabAt(4)?.setText("ONLINE ("+dataList.size+")");
//                else
//                    EventFragment.tabLayout?.getTabAt(4)?.setText("ONLINE");
//
//                adapter?.notifyDataSetChanged()
//            } catch (e: ApiException) {
//                e.printStackTrace()
//            } catch (e: NoInternetException) {
//                e.printStackTrace()
//            }
//        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }

}
