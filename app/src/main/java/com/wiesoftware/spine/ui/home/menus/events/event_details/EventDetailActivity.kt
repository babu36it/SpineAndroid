package com.wiesoftware.spine.ui.home.menus.events.event_details

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.TextMessage
import com.cometchat.pro.models.User
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.wiesoftware.spine.BuildConfig
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.EventCommentAdapter
import com.wiesoftware.spine.data.adapter.GoingUserAdapter
import com.wiesoftware.spine.data.net.reponses.*
import com.wiesoftware.spine.data.repo.EventRepository
import com.wiesoftware.spine.databinding.ActivityEventDetailBinding
import com.wiesoftware.spine.ui.home.menus.events.B_IMG_URL
import com.wiesoftware.spine.ui.home.menus.events.EVE_RECORD
import com.wiesoftware.spine.ui.home.menus.events.IS_FROM_EVENT_DETAILS
import com.wiesoftware.spine.ui.home.menus.events.calendarevents.CalendarEventActivity
import com.wiesoftware.spine.ui.home.menus.events.eventcomment.EventCommentActivity
import com.wiesoftware.spine.ui.home.menus.events.maps.MapviewEventsActivity
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SomeOneProfileActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.foryou.SpineForYouFragment
import com.wiesoftware.spine.ui.home.menus.spine.selectfollowers.SelectFollowersAdapter
import com.wiesoftware.spine.util.*
import constant.StringContract
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.bottomsheet_picker.view.*
import kotlinx.android.synthetic.main.eve_msg_dialog.*
import kotlinx.android.synthetic.main.join_layout.*
import kotlinx.android.synthetic.main.poor_quality_or_spam.view.button91
import kotlinx.android.synthetic.main.poor_quality_or_spam.view.button92
import kotlinx.android.synthetic.main.report_reason.*
import kotlinx.android.synthetic.main.report_reason.view.*
import kotlinx.android.synthetic.main.share_bottomsheet.*
import kotlinx.android.synthetic.main.share_bottomsheet.view.*
import kotlinx.android.synthetic.main.why_r_u_reporting.view.cardView2
import kotlinx.android.synthetic.main.why_r_u_reporting.view.imageButton66
import kotlinx.android.synthetic.main.why_r_u_reporting.view.radioGroup
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import screen.messagelist.CometChatMessageListActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

val EVENT_ID = "event_id"

class EventDetailActivity : AppCompatActivity(), KodeinAware, EventDetailEventListener,
    EventCommentAdapter.OnEventCommentEveListener, GoingUserAdapter.GoingUsersEventListener,
    SelectFollowersAdapter.FollowersEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    lateinit var binding: ActivityEventDetailBinding
    val factory: EventDetailViewModelFactory by instance()
    val eventRepositry: EventRepository by instance()
    var img: String? = null;
    var img_base: String = "";
    var name: String = "";
    var title = ""
    var event_id: String = "";
    var eve_date = "";
    var start_time = "";
    var bookingId: String = "0"
    var saved_status = ""
    var user_id: String = ""
    var eve_user_id = ""
    var eventType = ""
    var fee = "";
    var link = ""
    var bookingStatus = ""
    var userName = ""
    var eveStartDate = "";
    var eveEndDate = "";
    var reportTitle = "";
    var reportReason = "";
    var reportMessage = ""
    var event_link = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_event_detail)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_detail)
        val viewmodel = ViewModelProvider(this, factory).get(EventDetailViewModel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.eventDetailEventListener = this


        viewmodel.getLoggedInUser().observe(this, androidx.lifecycle.Observer { user ->
            user_id = user.users_id!!
            userName = user.name!!
            login(user_id, "")
            setFollowers()

            event_id = intent.getStringExtra("event_id").toString()

            if (!event_id.equals("")) {
                getEventDetails()
            }


            val isFromPost: Boolean =
                intent.getBooleanExtra(SpineForYouFragment.IS_EVE_FROM_POST, false)

            if (isFromPost) {
                event_id = intent.getStringExtra(SpineForYouFragment.EVENT_POST_ID)!!
                Log.e("eventid", event_id)
                getEventDetails()
            } else {
                getRecords(0)
            }

        })

    }

    private fun getGoingUsers() {
        lifecycleScope.launch {
            try {
                val res = eventRepositry.getGoingUsers(1, 100, event_id)
                if (res.status) {
                    STORY_IMAGE = res.profileImage
                    val userData = res.data.userList
                    val c = userData.size
                    if (c > 0) {
                        if (c == 1) {
                            binding.tvGoing.text = "$c person is going"
                        } else {
                            binding.tvGoing.text = "$c person are going"
                        }

                        binding.rvGoingUsers.also {
                            it.layoutManager = LinearLayoutManager(
                                this@EventDetailActivity,
                                RecyclerView.HORIZONTAL,
                                false
                            )
                            it.setHasFixedSize(true)
                            it.adapter = GoingUserAdapter(userData, this@EventDetailActivity)
                        }
                    }
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        hideKeyboard()
        return super.dispatchTouchEvent(ev)
    }

    var file: String = ""
    lateinit var record: EventsRecord


    private fun getRecords(ident: Int) {
        if (ident == 0) {
            record = intent.getSerializableExtra(EVE_RECORD) as EventsRecord
        } else {

        }
        event_id = record.id
        if (record.bookingId == null || record.equals("")) {
            bookingId = "0"
        } else {
            bookingId = record.bookingId
        }


        eveStartDate = record.startDate
        eveEndDate = record.endDate

        getGoingUsers()
        // getEventDetails()
        getSpineEventComments()
        BASE_IMAGE = intent.getStringExtra(B_IMG_URL)
        file = record.file
        val imgList: MutableList<ImageData> = ArrayList<ImageData>()

        if (file != null) {
            if (file.contains(",")) {
                var files = file.split(",")
                for (path in files) {
                    imgList.add(ImageData( "https://thespiritualnetwork.com/assets/upload/spine-post/"+path))
                }
            } else {
                imgList.add(ImageData("https://thespiritualnetwork.com/assets/upload/spine-post/"+file))
            }

            val adapter = EventsDetailsImageAdapter(imgList)
            binding.vpSlider.adapter = adapter
            TabLayoutMediator(binding.tabLayout, binding.vpSlider) { tab, position ->

            }.attach()

            if(imgList.size < 2) {
                binding.tabLayout.visibility = View.INVISIBLE
            }

            binding.vpSlider.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
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

        setEventDetails(record)
    }

    private fun getSpineEventComments() {
        lifecycleScope.launch {
            try {
                val res = eventRepositry.getSpineEventsComment(event_id)
                if (res.status) {
                    val data = res.data
                    binding.recyclerView2.let {
                        it.layoutManager = LinearLayoutManager(
                            this@EventDetailActivity,
                            RecyclerView.VERTICAL,
                            true
                        )
                        it.setHasFixedSize(true)
                        it.adapter = EventCommentAdapter(
                            data,
                            this@EventDetailActivity,
                            eventRepositry,
                            res.user_image
                        )
                    }
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    var desc = "";
    var location = "";
    var end_time = ""
    private fun setEventDetails(record: EventsRecord) {
        eve_user_id = record.userId

        if (user_id.equals(eve_user_id)) {
            binding.button47.visibility = View.INVISIBLE
            binding.button46.visibility = View.INVISIBLE
        } else {
            binding.button47.visibility = View.VISIBLE
            binding.button46.visibility = View.VISIBLE
        }


        getUserDetails("https://thespiritualnetwork.com/assets/upload/profile/"+record.hostedProfilePic ?: "",record.displayName ?:record.useName)

        ///  getEventDetails()

        eve_date = record.startDate
        start_time = (record.startTime).removeRange(5 until (record.startTime).length)
        end_time =
            (record.endTime).removeRange(5 until (record.endTime).length) + " " + record.timezoneName
        location = record.location

        title = record.title
        link = record.join_event_link
        event_link = record.linkOfEvent
        desc = record.description
        val total_cmnt = record.totalComment
        fee = record.fee.toString()
        val currency = if ((record.symbol).isNullOrEmpty()) {
            "$"
        } else {
            record.symbol
        }

        if (record.bookingStatus != "") {
            bookingStatus = record.bookingStatus
        }

//       bookingStatus="3"
        if (bookingStatus == null) {
            bookingStatus = "0"
        }
        if (fee.equals("0")) {
            binding.textView127.text = getString(R.string.free)
        } else {
            binding.textView127.text = currency + " " + fee
        }

        saved_status = "" + record.userSaveStatus
        saved_status?.let {
            if (saved_status.equals("1")) {
                binding.imageButton19.setImageResource(R.drawable.ic_saved)
            }
        }

        if (total_cmnt != null) {
            binding.textView126.text = getString(R.string.comments) + "($total_cmnt)"
        } else {
            binding.textView126.text = getString(R.string.comments)
        }

        binding.textView125.text = desc
        binding.textView119.text = link
        binding.textView115.text = title
        binding.textView121.text = record.languageName ?: "English"
        binding.textView117.text = start_time + " - " + end_time
        eventType = record.type

        if (bookingStatus.equals("you_are_going")) {
            if (eventType.equals("1")) {
                binding.textView119.text = event_link
            } else {

            }
        } else {
            if (eventType.equals("1")) {
                binding.textView119.text = event_link
            } else {

            }
        }



        //        Harsh: its tempeory line code because olg logic count event type from 0
        if (eventType.equals("0")) {
            binding.textView108.text= getString(R.string.local_event)
            binding.textView118.text = getString(R.string.local)
            binding.button47.text = getString(R.string.reserve_spot)
            binding.button47.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }

        if (eventType.equals("1")) {
            binding.textView108.text=getString(R.string.local_event)
            binding.textView118.text = getString(R.string.local)
            binding.button47.text = getString(R.string.reserve_spot)
            binding.button47.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        } else if (eventType.equals("2")) {
            binding.textView108.text=getString(R.string.online_event)
            binding.textView118.text = getString(R.string.online)
        } else if(eventType.equals("3")) {
            binding.textView108.text=getString(R.string.retreat_event)
        } else if(eventType.equals("4")) {
            binding.textView108.text=getString(R.string.metaverse_event)
        }


//        if (eventType.equals("1")) {
//            binding.textView108.text = getString(R.string.online_event)
//            binding.textView118.text = getString(R.string.online)
//
//        } else {
//            binding.textView108.text = getString(R.string.local_event)
//
//        }

        //  setBookingStatus()
        Bookingstataus()

        val simpleDateFormat = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())
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
    }

//    private fun setBookingStatus() {
//        if (bookingStatus != null) {
//            if (bookingStatus.equals("0")) {
//                //request sent
//                binding.button48.visibility = View.GONE
//                if (eventType.equals("1")) {
//                    binding.textView108.text = getString(R.string.local_event)
//                    binding.textView118.text = getString(R.string.local)
//                    if (fee.equals("0")) {
//                        binding.button47.text = getString(R.string.reserve_spot)
//                    } else {
//                        binding.button47.text = getString(R.string.book_event)
//                    }
//                    binding.button47.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
//                } else {
//                    if (fee.equals("0")) {
//                        binding.button47.text = getString(R.string.reserve_spot)
//                    } else {
//                        binding.button47.text = getString(R.string.book_event)
//                    }
//                    val img: Drawable? = ContextCompat.getDrawable(this, R.drawable.ic_arrow_right)
//                    img?.setTint(getColor(this, R.color.text_white))
//                    binding.button47.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null)
//                    binding.button47.compoundDrawablePadding = 16
//                }
//            } else if (bookingStatus.equals("1")) {
//                //Booking confirmed
//                binding.button48.visibility = View.GONE
//
//            } else if (bookingStatus.equals("2")) {
//                //accept by event user
//                binding.button48.visibility = View.VISIBLE
//                binding.button47.text = getString(R.string.you_re_going)
//                binding.button47.setBackgroundColor(getColor(this, R.color.transparent))
//                binding.button47.setTextColor(getColor(this, R.color.text_black))
//            } else if (bookingStatus.equals("3")) {
//                //reject by event user
//                binding.button48.visibility = View.GONE
//                binding.button47.text = getString(R.string.your_req_is_declined)
//                binding.button47.setBackgroundColor(getColor(this, R.color.transparent))
//                binding.button47.setTextColor(getColor(this, R.color.text_black))
//
//            } else if (bookingStatus.equals("4")) {
//                //reject by booking user
//                binding.button48.visibility = View.GONE
//                binding.button47.text = getString(R.string.you_re_not_going)
//                binding.button47.setBackgroundColor(getColor(this, R.color.transparent))
//                binding.button47.setTextColor(getColor(this, R.color.text_black))
//            } else if (bookingStatus.equals("5")) {
//                binding.button48.visibility = View.GONE
//                binding.button47.text = getString(R.string.booking_status_req)
//                binding.button47.setBackgroundColor(getColor(this, R.color.transparent))
//                binding.button47.setTextColor(getColor(this, R.color.text_black))
//            }
//        }
//    }

    fun Bookingstataus() {
        if (bookingStatus != null) {

            if (eventType.equals("1")) {

                if (bookingStatus.equals("BOOK EVENT")) {
                    binding.button47.text = getString(R.string.book_event)
                    var url = record.booking_url

                    binding.button47.setOnClickListener {
                        if (!url.startsWith("http")) {
                            val uri =
                                Uri.parse("https://" + url) // missing 'http://' will cause crashed
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                        } else {
                            val uri = Uri.parse(url) // missing 'http://' will cause crashed
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                        }
                    }


                    Log.d("TAG", "Bookingstataus: ${record.booking_url}")


                } else if (bookingStatus.equals(getString(R.string.reserve_spot))) {
                    binding.button47.text = getString(R.string.reserve_spot)

                    binding.button47.setOnClickListener {
                        sendRequestDialog()
                    }


                } else if (bookingStatus.equals("YOU SEND A REQUEST TO JOIN")) {
                    binding.button48.visibility = View.GONE
                    binding.button47.text = getString(R.string.booking_status_req)
                    binding.button47.setBackgroundColor(getColor(this, R.color.transparent))
                    binding.button47.setTextColor(getColor(this, R.color.text_black))
                } else if (bookingStatus.equals("YOUR GOING")) {
                    binding.button48.visibility = View.VISIBLE
                    binding.button47.text = getString(R.string.you_re_going)
                    binding.button47.setBackgroundColor(getColor(this, R.color.transparent))
                    binding.button47.setTextColor(getColor(this, R.color.text_black))
                }

            } else {
                        Log.d("Harsh",bookingStatus)
                if (bookingStatus.equals("REQUEST TO ATTEND ONLINE")) {
                    binding.button47.visibility = View.VISIBLE
                    binding.button47.setOnClickListener {
                        sendOnlineRequestDialog()

                    }
                } else if (bookingStatus.equals("YOU SEND A REQUEST TO JOIN")) {
                    binding.button48.visibility = View.GONE
                    binding.button47.text = getString(R.string.booking_status_req)
                    binding.button47.setBackgroundColor(getColor(this, R.color.transparent))
                    binding.button47.setTextColor(getColor(this, R.color.text_black))
                } else if (bookingStatus.equals("YOUR GOING")) {
                    binding.button48.visibility = View.VISIBLE
                    binding.button47.text = getString(R.string.you_re_going)
                    binding.button47.setBackgroundColor(getColor(this, R.color.transparent))
                    binding.button47.setTextColor(getColor(this, R.color.text_black))
                } else if (bookingStatus.equals("REJECTED BY YOU")) {
                    binding.button48.visibility = View.GONE
                    binding.button47.text = getString(R.string.you_re_not_going)
                    binding.button47.setBackgroundColor(getColor(this, R.color.transparent))
                    binding.button47.setTextColor(getColor(this, R.color.text_black))
                } else if (bookingStatus.equals("REJECTED BY HOST")) {
                    binding.button48.visibility = View.GONE
                    binding.button47.text = getString(R.string.your_req_is_declined)
                    binding.button47.setBackgroundColor(getColor(this, R.color.transparent))
                    binding.button47.setTextColor(getColor(this, R.color.text_black))
                } else if (bookingStatus.equals("ATTEND ONLINE")) {
                    binding.button47.text = "ATTEND ONLINE"
                    var url = record.linkOfEvent

                    binding.button47.setOnClickListener {
                        if (!url.startsWith("http")) {
                            val uri =
                                Uri.parse("https://" + url) // missing 'http://' will cause crashed
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                        } else {
                            val uri = Uri.parse(url) // missing 'http://' will cause crashed
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                        }
                    }
                }


            }
        }
    }

    private fun getUserDetails(image: String,name:String) {


        binding.textView122.text = name
        img_base = image
        Glide.with(this)
            .load(image)
            .centerCrop()
            .placeholder(R.drawable.ic_profile)
            .error( ColorDrawable(getColor(this, R.color.light_gry)))
            .dontAnimate()
            .into(binding.circleImageView7);

//        Harsh: Not necceary for new api
//        lifecycleScope.launch {
//            try {
//                Log.e("idd", userId)
//                val userProfile = eventRepositry.getUserDetails(userId)
//                if (userProfile.status) {
//                    img_base = userProfile.image
//                    val userData = userProfile.data
//                    try {
//                        if (!userData.profile_pic.isNullOrEmpty()) {
//                            img = userData.profile_pic
//                            img.let {
//                                Glide.with(this@EventDetailActivity)
//                                    .load(img_base + img)
//                                    .placeholder(R.drawable.ic_profile)
//                                    .into(binding.circleImageView7)
//                            }
//                        }
//                        name = userData.display_name ?: userData.name!!
//                        binding.textView122.text = name
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//
//                }
//            } catch (e: ApiException) {
//                e.printStackTrace()
//            } catch (e: NoInternetException) {
//                e.printStackTrace()
//            }
//        }
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun openMessageDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.eve_msg_dialog)
        val cImg = dialog.findViewById(R.id.imageView14) as CircleImageView
        val nameTv = dialog.findViewById(R.id.textView128) as TextView
        nameTv.text =  binding.textView122.text.toString()
        Glide.with(this@EventDetailActivity)
            .load(img_base)
            .placeholder(R.drawable.ic_profile)
            .into(cImg)
        val send = dialog.findViewById(R.id.button49) as Button
        val cancel = dialog.findViewById(R.id.button48) as Button
        send.setOnClickListener {
            val msg: String = dialog.editTextTextPersonName17.text.toString()
            //sendEventMessage(msg)

            sendEveMessage(msg)
            dialog.dismiss()
        }
        cancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun sendEveMessage(msg: String) {
        val receiverID: String = eve_user_id
        val messageText: String = msg
        val receiverType: String = CometChatConstants.RECEIVER_TYPE_USER

        val textMessage = TextMessage(receiverID, messageText, receiverType)

        CometChat.sendMessage(textMessage, object : CometChat.CallbackListener<TextMessage>() {
            override fun onSuccess(p0: TextMessage?) {
                "Message sent successfully: ".toast(this@EventDetailActivity)
                Log.d("SendCometMsg:", "Message sent successfully: " + p0?.toString())
            }

            override fun onError(p0: CometChatException?) {
                "Oops! Something went wrong: ".toast(this@EventDetailActivity)
                Log.d("SendCometMsg", "Message sending failed with exception: " + p0?.message)
            }

        })
    }

    private fun sendEventMessage(msg: String) {

        lifecycleScope.launch {
            try {
                var type: String = "2"
                if (eve_user_id == user_id) {
                    type = "1"
                } else {
                    type = "2"
                }
                val res = eventRepositry.sendEventMessage(event_id, eve_user_id, user_id, msg, type)
                if (res.status) {
                    "Message sent successfully".toast(this@EventDetailActivity)
                    //createChatUserWithComet(eve_user_id,name,(img_base+img))
                    //login(user_id,(img_base+img))
                    getCometChatUser(eve_user_id)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }

    }

    private fun getCometChatUser(uid: String) {
        CometChat.getUser(uid, object : CometChat.CallbackListener<User>() {
            override fun onSuccess(p0: User?) {
                userIntent(p0!!)
            }

            override fun onError(p0: CometChatException?) {
                createChatUserWithComet(eve_user_id, name, (img_base + img))
            }
        })
    }

    private fun login(uid: String, img: String) {
        CometChat.login(
            uid,
            AppConfig.AppDetails.AUTH_KEY,
            object : CometChat.CallbackListener<User?>() {
                override fun onSuccess(user: User?) {
                    val nam = user?.name
//                    nam?.toast(this@EventDetailActivity)
                    if (user_id != user?.uid) {
                        //userIntent(user!!)
                    }
                    //startActivity(Intent(this@EventDetailActivity, ChatUserListActivity::class.java))
                }

                override fun onError(e: CometChatException) {
                    createChatUserWithComet(user_id, userName, img)
                }
            })
    }

    fun userIntent(user: User) {
        val intent = Intent(this, CometChatMessageListActivity::class.java)
        intent.putExtra(StringContract.IntentStrings.UID, user.uid)
        intent.putExtra(StringContract.IntentStrings.AVATAR, user.avatar)
        intent.putExtra(StringContract.IntentStrings.STATUS, user.status)
        intent.putExtra(StringContract.IntentStrings.NAME, user.name)
        intent.putExtra(StringContract.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_USER)
        startActivity(intent)
    }


    private fun createChatUserWithComet(user_id: String, userName: String, avatar: String) {
        val user = User()
        user.uid = user_id
        user.name = userName
        user.avatar = avatar
        CometChat.createUser(
            user,
            AppConfig.AppDetails.AUTH_KEY,
            object : CometChat.CallbackListener<User>() {
                override fun onSuccess(user: User) {
                    if (user_id != user?.uid) {
                        userIntent(user)
                    }
                }

                override fun onError(e: CometChatException) {
                    e.printStackTrace()
                    Log.e("uError", "" + e.printStackTrace())
                }
            })

    }

    private fun login(user: User) {
        CometChat.login(
            user.uid,
            AppConfig.AppDetails.AUTH_KEY,
            object : CometChat.CallbackListener<User?>() {
                override fun onSuccess(user: User?) {
                    userIntent(user!!)
                    //startActivity(Intent(this@EventDetailActivity, ChatUserListActivity::class.java))
                }

                override fun onError(e: CometChatException) {
                    e.printStackTrace()
                }
            })
    }

    override fun onShareEvent() {
        /* val intent=Intent(this,SelectUsersActivity::class.java)
         intent.putExtra(EVENT_ID,event_id)
         startActivity(intent)*/
        sharePostBottomsheet()

        /* val img= BASE_IMAGE+file
         val appId= BuildConfig.APPLICATION_ID
         val textToShare="Hey! $name has shared you their event from Spine.\n \n" +
                 "$title  \n" +
                 " \n $link \n \n $img \n \n  Check your spine app for details. If you don't have spine app click below link to download->" +
                 "\n\n https://play.google.com/store/apps/details?id=$appId"
         ShareCompat.IntentBuilder
             .from(this)
             .setText(textToShare)
             .setType("text/plain")
             .setChooserTitle("Share Spine Content")
             .startChooser()*/

    }

    override fun onEventSaved() {
        if (saved_status.equals("1")) {
            saved_status = "0"
            binding.imageButton19.setImageResource(R.drawable.ic_bookmark)
            removeEventsave()
            return
        }
        saved_status = "1"
        binding.imageButton19.setImageResource(R.drawable.ic_saved)
        lifecycleScope.launch {
            try {
                val res = eventRepositry.saveEvents(user_id, event_id)
                if (res.status) {
                    val msg = res.message
                    msg.toast(this@EventDetailActivity)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    private fun removeEventsave() {
        lifecycleScope.launch {
            try {
                val res = eventRepositry.removeEventSave(user_id, event_id)
                if (res.status) {

                    "Removed".toast(this@EventDetailActivity)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestToAttend() {
        if (bookingStatus != null) {
            if (bookingStatus.equals("0")) {
                if (eventType.equals("2")) {
                   sendOnlineRequestDialog()

                } else {
                    sendRequestDialog()
                }
            }
        }
    }

    private fun openChromeTab(url: String) {
        val newUrl = if (url.startsWith("http://", true) || url.startsWith("https://", true)) {
            url
        } else {
            "http://$url"
        }


        val uri: Uri = Uri.parse(newUrl)
        val builder = CustomTabsIntent.Builder()

        val params = CustomTabColorSchemeParams.Builder()
            .setNavigationBarColor(getColor(this, R.color.colorPrimary))
            .setToolbarColor(getColor(this, R.color.colorPrimary))
            .setSecondaryToolbarColor(getColor(this, R.color.colorPrimaryDark))
            .build()
        builder.setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, params)
        builder.setStartAnimations(
            this,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        builder.setExitAnimations(
            this,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        builder.setShowTitle(true)
        if (isChromeInstalled()) {
            builder.build().intent.setPackage("com.android.chrome")
        }
        val customTabsIntent = builder.build()
        try {
            customTabsIntent.launchUrl(this, uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isChromeInstalled(): Boolean {
        return try {
            getPackageManager().getPackageInfo("com.android.chrome", 0)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun postEventComment() {
        //eve_comment.toast(this)

        if (binding.etCmnt.text.toString().isEmpty()){
            return
        }


        lifecycleScope.launch {
            try {
                val res = eventRepositry.spineEventsComment(event_id, user_id, "0", binding.etCmnt.text.toString())
                if (res.status) {
                    //"Comment added successfully".toast(this@EventDetailActivity)
                    binding.etCmnt.setText("")
                    getSpineEventComments()
                } else {
                    "${res.message}".toast(this@EventDetailActivity)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    override fun onShowPopupMenu() {
        /*val popupMenu: PopupMenu = PopupMenu(this,binding.imageButton18)
        popupMenu.menuInflater.inflate(R.menu.popup_menu,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.action_edit ->
                    item.title.toast(this)
                R.id.action_delete ->
                    if (user_id.equals(eve_user_id)){
                        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
                        alertDialog.setTitle("Spine event")
                        alertDialog.setMessage("Are you sure to delete this event")
                        alertDialog.setPositiveButton(
                            "yes"
                        ) { d, _ ->
                            deleteEvent()
                            d.dismiss()
                        }
                        alertDialog.setNegativeButton(
                            "No"
                        ) { d, _ ->
                            d.dismiss()
                        }
                        val alert: AlertDialog = alertDialog.create()
                        alert.setCanceledOnTouchOutside(false)
                        alert.show()
                    }else{
                     "Sorry, you don't have access to delete this event.".toast(this)
                    }
            }
            true
        })
        popupMenu.show()*/
        showBottomsheet()
    }

    override fun editRegistration() {
        updateDialog()
    }

    override fun onProfileView() {
        val intent = Intent(this, SomeOneProfileActivity::class.java)
        intent.putExtra(SomeOneProfileActivity.SOME_ONES_USER_ID, eve_user_id)
        intent.putExtra("eventuserid", eve_user_id)
        startActivity(intent)
    }

    override fun onTargetFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            val intent = Intent(this, EventCommentActivity::class.java)
            intent.putExtra(EVENT_ID, event_id)
            startActivity(intent)
            binding.etCmnt.clearFocus()
        }
    }

    override fun onHostedIn() {
        val intent = Intent(this, MapviewEventsActivity::class.java)
        intent.putExtra(EVE_RECORD, record)
        intent.putExtra(IS_FROM_EVENT_DETAILS, true)
        startActivity(intent)
    }

    override fun onLinkClicked() {
        openChromeTab(link)
    }

    override fun onCalenderEvent() {

        val intent = Intent(this, CalendarEventActivity::class.java)
        intent.putExtra(EVE_RECORD, record)
        startActivity(intent)
        return

        //2021-03-15, 2021-04-16, 08:00:00, 12:00:00
        //Log.e("dddddddddd::","$eveStartDate, $eveEndDate, $start_time, $end_time")

    }

    override fun eventClick() {
        openChromeTab(event_link)
    }


    override fun onReply(eventCommentData: EventCommentData, comment: String) {
        lifecycleScope.launch {
            try {
                val c_id = eventCommentData.id
                val res = eventRepositry.spineEventsComment(event_id, user_id, c_id, comment)
                if (res.status) {
                    //"Comment added successfully".toast(this@EventDetailActivity)
                    binding.etCmnt.setText("")
                    //getSpineEventComments()
                } else {
                    "OOps! something went wrong.".toast(this@EventDetailActivity)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    fun getEveDateInFormat(eveDate: String, format: String): String {
        var returnDate = ""
        val simpleDateFormat = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())
        try {
            val date1: Date = simpleDateFormat.parse(eveDate)
            val dd = SimpleDateFormat(format, Locale.getDefault())
            returnDate = dd.format(date1)
            Log.e("fmtDate: ", returnDate)
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("fmtDate: ", e.message.toString())
        }
        return returnDate
    }

    fun sendRequestDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.join_layout)
        val tvEventTitle = dialog.findViewById<TextView>(R.id.tvEventTitle)
        val tvEventsDate = dialog.findViewById<TextView>(R.id.tvEventDates)
        tvEventsDate.text = "${getEveDateInFormat(eveStartDate, "dd MMM")} - ${
            getEveDateInFormat(
                eveEndDate,
                "dd MMM yyyy"
            )
        }"
        tvEventTitle.text = title
        val etHost = dialog.findViewById<EditText>(R.id.editTextTextPersonName20)
        etHost.setHint("Dear $name , I'd like to join your your event.")
        val send = dialog.findViewById(R.id.button68) as Button
        val cancel = dialog.findViewById(R.id.imageButton32) as ImageButton
        send.setOnClickListener {
            val msg: String = dialog.editTextTextPersonName20.text.toString()
            bookThisEvent(msg, "5")
            dialog.dismiss()
        }
        cancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun bookThisEvent(msg: String, s: String) {

        lifecycleScope.launch {
            try {
                val res = eventRepositry.bookEvents(user_id, eventType, event_id, msg, fee)
                if (res.status) {
                    res.message.toast(this@EventDetailActivity)
                    changeBookingstatus(bookingId, s)
                } else {
                    res.message.toast(this@EventDetailActivity)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    private fun changeBookingstatus(s: String, s1: String) {
        lifecycleScope.launch {
            try {

                val res = eventRepositry.changeBookingStatus("" + s, "" + s1)
                if (res.status) {
                    res.message.toast(this@EventDetailActivity)
                    getEventDetails()
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    private fun getEventDetails() {
        lifecycleScope.launch {
            try {

                val res = eventRepositry.getEventDetails(event_id, user_id)
                if (res.status) {
                    record = res.data
                    println("SanjayC record......" + Gson().toJson(record))
                    BASE_IMAGE = res.image
                    Log.e("datanidhi", res.image)
                    getRecords(1)
                   setEventDetails(record)
                    if (record.bookingId.equals("")) {
                        bookingId = "0"
                    } else {
                        bookingId = record.bookingId
                    }

                    var total_cmnt = record.totalComment
                    if (total_cmnt != null) {
                        binding.textView126.text = getString(R.string.comments) + "($total_cmnt)"
                    } else {
                        binding.textView126.text = getString(R.string.comments)
                    }

                    if (!record.bookingStatus.equals("")) {
                        bookingStatus = record.bookingStatus
                        Log.e("bookingagainn", record.bookingStatus)
                    }

                    //  setBookingStatus()
                    Bookingstataus()
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    fun sendOnlineRequestDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.join_online_event)
        val tvEventTitle = dialog.findViewById(R.id.tvEventTitle) as TextView
        val tvEventTime = dialog.findViewById(R.id.textView176) as TextView
        tvEventTitle.text = title

        val simpleDateFormat = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())
        try {
            val date1: Date = simpleDateFormat.parse(eve_date)
            val dd = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
            val ss: String = dd.format(date1)
            tvEventTime.text = ss +", "+ start_time
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("fmtDate: ", e.message.toString())
        }


        //name
        val send = dialog.findViewById(R.id.button68) as Button
        val cancel = dialog.findViewById(R.id.imageButton32) as ImageButton
        send.setOnClickListener {
            bookThisEvent("", "5")
            dialog.dismiss()
        }
        cancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun youAreGoingDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.online_you_are_going)
        val cancel = dialog.findViewById(R.id.imageButton32) as ImageButton
        val tvEventName = dialog.findViewById(R.id.tvEventTitle) as TextView
        val tvEventDate = dialog.findViewById(R.id.textView176) as TextView
        val forwardToAFriend = dialog.findViewById(R.id.button68) as Button
        val simpleDateFormat = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())
        tvEventName.text = title
        try {
            val date1: Date = simpleDateFormat.parse(eve_date)
            val dd = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
            val ss: String = dd.format(date1)
            Log.e("fmtDate: ", ss)
            tvEventDate.text = ss + ", "+ start_time

        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("fmtDate: ", e.message.toString())
        }
        forwardToAFriend.setOnClickListener {
            forwardToAFriend()
            dialog.dismiss()
        }
        cancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun forwardToAFriend() {

    }

    fun updateDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.change_reservation_layout)
        val no = dialog.findViewById(R.id.button68) as Button
        val yes = dialog.findViewById(R.id.button69) as Button
        val send = dialog.findViewById(R.id.button70) as Button
        val cancel = dialog.findViewById(R.id.imageButton32) as ImageButton
        var status: String = "2"
        no.setOnClickListener {
            no.background = ContextCompat.getDrawable(this, R.drawable.round_white)
            yes.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
            status = "4"
        }
        yes.setOnClickListener {
            yes.background = ContextCompat.getDrawable(this, R.drawable.round_white)
            no.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
            status = "2"
        }
        send.setOnClickListener {
            //chnage edit status
            changeBookingstatus("" + bookingId, "" + status)
            dialog.dismiss()
        }
        cancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onGoingUserClick(goingUser: GoingUser) {
        val intent = Intent(this, SomeOneProfileActivity::class.java)
        intent.putExtra(SomeOneProfileActivity.SOME_ONES_USER_ID, goingUser.usersId)
        intent.putExtra("eventuserid", eve_user_id)
        startActivity(intent)
    }

    companion object {
        val EVENT_ID = "eventId"
    }

    var followersDataList: List<FollowersData> = ArrayList<FollowersData>()

    private fun setFollowers() {
        lifecycleScope.launch {
            try {
                val followersRes = eventRepositry.getFollowers(1, 100, user_id)
                if (followersRes.status) {
                    followersDataList = followersRes.data
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    private fun sharePostBottomsheet() {
        val view: View = layoutInflater.inflate(R.layout.share_bottomsheet, null)
        val dialog: BottomSheetDialog = BottomSheetDialog(this)
        dialog.setContentView(view)
        dialog.rvShareFollowers.also {
            it.layoutManager = /*GridLayoutManager(requireContext(),2,RecyclerView.VERTICAL,false)*/
                LinearLayoutManager(
                    this,
                    RecyclerView.VERTICAL,
                    false
                )
            it.setHasFixedSize(true)
            it.adapter = SelectFollowersAdapter(followersDataList, this)
        }
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
        view.cardView2.setOnClickListener {
            dialog.dismiss()
        }
        view.imageButton66.setOnClickListener {
            dialog.dismiss()
        }
        view.imageView32.setOnClickListener {
            shareOnWhatsApp()
        }
        view.imageView33.setOnClickListener {
            shareOutsideApp()
        }
        view.button93.setOnClickListener {
            shareInsideApp()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun shareOutsideApp() {
        val img = BASE_IMAGE + file
        val appId = BuildConfig.APPLICATION_ID
        val textToShare = "Hey! $name has shared you their event from Spine.\n \n" +
                "$title  \n" +
                " \n $link \n \n $img \n \n  Check your spine app for details. If you don't have spine app click below link to download->" +
                "\n\n https://play.google.com/store/apps/details?id=$appId"
        ShareCompat.IntentBuilder
            .from(this)
            .setText(textToShare)
            .setType("text/plain")
            .setChooserTitle("Share Spine Content")
            .startChooser()
    }

    private fun shareOnWhatsApp() {
        val img = BASE_IMAGE + file
        val appId = BuildConfig.APPLICATION_ID
        val textToShare = "Hey! $name has shared you their event from Spine.\n \n" +
                "$title  \n" +
                " \n $link \n \n $img \n \n  Check your spine app for details. If you don't have spine app click below link to download->" +
                "\n\n https://play.google.com/store/apps/details?id=$appId"
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage("com.whatsapp")
        intent.putExtra(Intent.EXTRA_TEXT, textToShare)
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=com.whatsapp")
                )
            )
        }
    }

    val data: MutableMap<String, String> = HashMap<String, String>()

    private fun shareInsideApp() {
        val post_id = event_id
        data.put("spine_event_id", post_id)
        data.put("user_id", user_id)
        var i = 0
        selectedData.forEach {
            data.put("share_users_id[$i]", it.user_id)
            i++
        }
        lifecycleScope.launch {
            try {
                val res = eventRepositry.spineEventShare(data)
                if (res.status) {
                    res.message.toast(this@EventDetailActivity)
                } else {
                    "${res.message}".toast(this@EventDetailActivity)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    var selectedData: MutableList<FollowersData> = ArrayList<FollowersData>()
    override fun followerSelected(followersData: FollowersData) {
        selectedData.add(followersData)
    }


    private fun showBottomsheet() {
        val view: View = layoutInflater.inflate(R.layout.home_feed_three_dots, null)
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
            "follow".toast(this)
            dialog.dismiss()
        }
        view.btnOnline.setOnClickListener {
            showReportBottomsheet()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showReportBottomsheet() {
        val view: View = layoutInflater.inflate(R.layout.why_r_u_reporting, null)
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
        view.cardView2.setOnClickListener {
            dialog.dismiss()
        }
        view.imageButton66.setOnClickListener {
            dialog.dismiss()
        }
        view.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = view.findViewById<RadioButton>(checkedId)
            reportTitle = radioButton.text.toString()

            when (checkedId) {
                R.id.radioButton4 -> {
                    poorQualityBottomsheet()
                    dialog.dismiss()
                }
                R.id.radioButton5 -> {
                    inAppropriateBottomsheet()
                    dialog.dismiss()
                }
                R.id.radioButton6 -> {
                    voilentBottomsheet()
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    fun poorQualityBottomsheet() {
        val view: View = layoutInflater.inflate(R.layout.poor_quality_or_spam, null)
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
        view.cardView2.setOnClickListener {
            dialog.dismiss()
        }
        view.imageButton66.setOnClickListener {
            dialog.dismiss()
        }
        view.button91.setOnClickListener {
            //next
            reportReseonBottomsheet()
            dialog.dismiss()
        }
        view.button92.setOnClickListener {
            //back
            showReportBottomsheet()
            dialog.dismiss()
        }
        view.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = view.findViewById<RadioButton>(checkedId)
            reportReason = radioButton.text.toString()
            when (checkedId) {
                R.id.radioButton4 -> {


                }
                R.id.radioButton5 -> {


                }
                R.id.radioButton6 -> {


                }
                R.id.radioButton7 -> {


                }
                R.id.radioButton8 -> {


                }
            }
        }

        dialog.show()
    }

    fun inAppropriateBottomsheet() {
        val view: View = layoutInflater.inflate(R.layout.inappropriate_bottom, null)
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
        view.cardView2.setOnClickListener {
            dialog.dismiss()
        }
        view.imageButton66.setOnClickListener {
            dialog.dismiss()
        }
        view.button91.setOnClickListener {
            //next
            reportReseonBottomsheet()
            dialog.dismiss()
        }
        view.button92.setOnClickListener {
            //back
            showReportBottomsheet()
            dialog.dismiss()
        }
        view.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = view.findViewById<RadioButton>(checkedId)
            reportReason = radioButton.text.toString()
            when (checkedId) {
                R.id.radioButton4 -> {


                }
                R.id.radioButton5 -> {


                }
                R.id.radioButton6 -> {


                }
                R.id.radioButton7 -> {


                }
                R.id.radioButton8 -> {


                }
            }
        }

        dialog.show()
    }

    fun voilentBottomsheet() {
        val view: View = layoutInflater.inflate(R.layout.voilent_report, null)
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
        view.cardView2.setOnClickListener {
            dialog.dismiss()
        }
        view.imageButton66.setOnClickListener {
            dialog.dismiss()
        }
        view.button91.setOnClickListener {
            //next
            reportReseonBottomsheet()
            dialog.dismiss()
        }
        view.button92.setOnClickListener {
            //back
            showReportBottomsheet()
            dialog.dismiss()
        }
        view.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = view.findViewById<RadioButton>(checkedId)
            reportReason = radioButton.text.toString()
            when (checkedId) {
                R.id.radioButton4 -> {


                }
                R.id.radioButton5 -> {


                }
                R.id.radioButton6 -> {


                }
                R.id.radioButton8 -> {


                }
            }
        }

        dialog.show()
    }

    fun reportReseonBottomsheet() {
        val view: View = layoutInflater.inflate(R.layout.report_reason, null)
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
        val tvTitle = dialog.findViewById<TextView>(R.id.textView256)
        tvTitle?.text = reportTitle
        dialog.textView257.text = reportReason
//        view.cardView2.setOnClickListener {
//            dialog.dismiss()
//        }
        view.imageButton66.setOnClickListener {
            dialog.dismiss()
        }
        view.button91.setOnClickListener {
            //report
            reportMessage = view.editTextTextPersonName26.text.toString()
            reportThePost()

            dialog.dismiss()
        }
        view.button92.setOnClickListener {
            //back
            showReportBottomsheet()
            dialog.dismiss()
        }


        dialog.show()
    }

    private fun reportThePost() {
        //"$reportTitle, $reportReason, $reportMessage".toast(requireContext())
        lifecycleScope.launch {
            try {
                val res = eventRepositry.spineReportUserPostStory(
                    user_id,
                    event_id,
                    "4",
                    reportTitle,
                    reportReason,
                    reportMessage
                )
                if (res.status) {
                    "Your request is submitted successfully".toast(this@EventDetailActivity)
                    reportThanksBottomsheet()
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }

    }

    fun reportThanksBottomsheet() {
        val view: View = layoutInflater.inflate(R.layout.thanks_report, null)
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
//        view.cardView2.setOnClickListener {
//            dialog.dismiss()
//        }
        view.imageButton66.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onRestart() {
        super.onRestart()
        Log.e("restart", "restarr")
        getEventDetails()
        getSpineEventComments()
    }
}
