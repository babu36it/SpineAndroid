package com.wiesoftware.spine.ui.home.menus.profile.someonesprofile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.OwnEventAdapter
import com.wiesoftware.spine.data.adapter.OwnPostAdapter
import com.wiesoftware.spine.data.net.reponses.EventsRecord
import com.wiesoftware.spine.data.net.reponses.PostData
import com.wiesoftware.spine.data.net.reponses.ProfileData
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivitySomeOneProfileBinding
import com.wiesoftware.spine.ui.home.menus.events.B_IMG_URL
import com.wiesoftware.spine.ui.home.menus.events.EVE_RECORD
import com.wiesoftware.spine.ui.home.menus.events.event_details.EventDetailActivity
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.followers.FollowersActivity
import com.wiesoftware.spine.ui.home.menus.profile.tabs.posts.PostsFragment
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.postdetails.PostDetailsActivity
import com.wiesoftware.spine.ui.home.menus.spine.viewmedia.ViewMediaInLargeActivity
import com.wiesoftware.spine.util.*
import constant.StringContract
import kotlinx.android.synthetic.main.poor_quality_or_spam.view.button91
import kotlinx.android.synthetic.main.poor_quality_or_spam.view.button92
import kotlinx.android.synthetic.main.report_reason.*
import kotlinx.android.synthetic.main.report_reason.view.*
import kotlinx.android.synthetic.main.why_r_u_reporting.view.imageButton66
import kotlinx.android.synthetic.main.why_r_u_reporting.view.radioGroup
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import screen.messagelist.CometChatMessageListActivity

const val SOMEONE_U_ID="uid"
const val SOMEONE_U_NAME="uname"
class SomeOneProfileActivity : AppCompatActivity(),KodeinAware, SomeoneProfileEventListener,
    OwnEventAdapter.OnEventDetailsListener, OwnPostAdapter.OwnPostSelectedListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val factory: SomeoneProfileViewmodelFactory by instance()
    val homeRepositry: HomeRepository by instance()
    lateinit var binding: ActivitySomeOneProfileBinding
    lateinit var userId: String
    lateinit var cUserId: String
    var eventuserid:String=""

    var profileImg: String=""
    var bgImage: String=""
    var profileImgBase="";var website="";var buissenessPhone = "";var about=""
    var reportTitle="";var reportReason="-";var reportMessage=""
    var cUserName="";var isOtherUser = false

    var followStatus=""
    var userBlocker=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_some_one_profile)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_some_one_profile)
        val viewmodel=ViewModelProvider(this, factory).get(SomeoneProfileViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.someoneProfileEventListener=this
        eventuserid= intent.getStringExtra("eventuserid").toString()
        viewmodel.getLoggedInUser().observe(this, Observer { user ->
            cUserId = user.users_id!!
            Log.e("user_id", cUserId)
            cUserName = user.display_name ?: user.name!!
            login(cUserId, "")
            userId = intent.getStringExtra(SOME_ONES_USER_ID).toString()
            if (!userId.isNullOrEmpty()) {
                Log.e("user_id", userId)
                getUserDetails()
                getOwnPost()
            }
        })


        followStatus=intent.getStringExtra(FOLLOW_STATUS).toString()
        if (!followStatus.isNullOrEmpty()){
            binding.buttonFollow.text = if (followStatus.equals("1")){
                getString(R.string.following)
            }else{
                getString(R.string.follows)
            }
        }

        binding.imageButton26.setOnClickListener {
            openUserBottom()
        }
    }

    private fun login(uid: String, img: String) {
        CometChat.login(
            uid,
            AppConfig.AppDetails.AUTH_KEY,
            object : CometChat.CallbackListener<User?>() {
                override fun onSuccess(user: User?) {
                }

                override fun onError(e: CometChatException) {
                    e.printStackTrace()
                    isOtherUser = false
                    createChatUserWithComet(cUserId, cUserName, "")
                }
            })
    }
    private fun createChatUserWithComet(user_id: String, userName: String, avatar: String) {
        val user = User()
        user.uid = user_id
        user.name = userName
        user.avatar=avatar
        CometChat.createUser(
            user,
            AppConfig.AppDetails.AUTH_KEY,
            object : CometChat.CallbackListener<User>() {
                override fun onSuccess(user: User) {
                    if (!isOtherUser) {
                        login(user.uid, "")
                    } else {
                        sendMessage()
                    }
                }

                override fun onError(e: CometChatException) {
                    e.printStackTrace()
                    Log.e("uError", "" + e.printStackTrace())
                }
            })
    }

    private fun openUserBottom() {
            val view: View = layoutInflater.inflate(R.layout.users_three_dot, null)
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
        val btnCan=dialog.findViewById<Button>(R.id.btnCan)
        val btnReport=dialog.findViewById<Button>(R.id.btnReport)
        val btnOnline=dialog.findViewById<Button>(R.id.btnOnline)

        if (userBlocker.equals("1")){
            btnOnline?.setText("UnBlock User")
        }else{
            btnOnline?.setText("Block User")
        }
        btnReport?.setOnClickListener {
            showUserReportBottomsheet()
            dialog.dismiss()
        }
        btnCan?.setOnClickListener {
            dialog.dismiss()
        }

        btnOnline?.setOnClickListener {
            //dialog.dismiss()
            if (userBlocker.equals("1")){

                blockUser("UnBlock User")
            }else{
                btnOnline?.setText("Block User")
                blockUser("Block User")
            }

        }
            dialog.show()
    }

    private fun getUserDetails() {
        lifecycleScope.launch {
            try {


                val res=homeRepositry.getuserDetailsMSGPermision(userId, cUserId)
                if (res.status){
                    val img=res.image
                    val profileData=res.data
                    val imgName=profileData.profile_pic
                    var allowMessage=profileData.allowMessage
                    userBlocker=profileData.userBlocked

                    if (allowMessage.equals("1")){
                        binding.buttonMsg.visibility=View.VISIBLE
                    }
                    profileImg=img+imgName
                    Glide.with(binding.imageView19)
                        .load(profileImg)
                        .placeholder(R.drawable.ic_profile)
                        .into(binding.imageView19)
                    setProfileData(profileData, img)

                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }
    var displayName=""
    private fun setProfileData(profileData: ProfileData, img: String) {
        binding.imageView19.setOnClickListener {
            val intent=Intent(this, ViewMediaInLargeActivity::class.java)
            intent.putExtra(ViewMediaInLargeActivity.MEDIA_URL, profileImg)
            intent.putExtra(ViewMediaInLargeActivity.MEDIA_TYPE, "0")
            startActivity(intent)
        }
        val followers=profileData.followers_records_count!!
        val following=profileData.following_records_count!!
        displayName=profileData.display_name ?: profileData.name!!
        var address=profileData.address ?.toString()
        val category=profileData.categoryName?.toString()
        val bgImg=profileData.bg_image?.toString()
        val post=profileData.post_records_count?.toString()
        val event=profileData.event_records_count?.toString()
        val pod=profileData.pod_records_count?.toString()
        buissenessPhone=profileData.business_phone?.toString()
        website=profileData.website?.toString()
        about=profileData.bio?.toString()
        bgImage=img+bgImg
        Glide.with(binding.imageView18)
            .load(bgImage)
            .into(binding.imageView18)

        if (profileData.account_mode.equals("1")){
            binding.ivBadge.visibility= View.VISIBLE
            binding.button56.visibility= View.VISIBLE
            binding.button99.visibility= View.VISIBLE
        }else{
            binding.ivBadge.visibility= View.INVISIBLE
            binding.button56.visibility= View.GONE
            binding.button99.visibility= View.GONE
        }

        binding.textView151.text=followers
        binding.textView153.text=following
        binding.textView155.text=displayName
        if (!address.isNullOrEmpty()){
            binding.textView156.text=address
        }

        binding.textView157.text=post
        binding.textView158.text=event
        binding.textView159.text=pod
        binding.imageView18.setOnClickListener {
            val intent=Intent(this, ViewMediaInLargeActivity::class.java)
            intent.putExtra(ViewMediaInLargeActivity.MEDIA_URL, bgImage)
            intent.putExtra(ViewMediaInLargeActivity.MEDIA_TYPE, "0")
            startActivity(intent)
        }
        Prefs.putAny(SOMEONE_U_NAME, "" + displayName)
        Prefs.putAny(SOMEONE_U_ID, userId)

    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onFollowers() {
        val followers: String=getString(R.string.followers)
        val intent=Intent(this, FollowersActivity::class.java)
        intent.putExtra(ACTI_NAME, followers)
        startActivity(intent)
    }

    override fun onFollowing() {
        val following: String=getString(R.string.following)
        val intent=Intent(this, FollowersActivity::class.java)
        intent.putExtra(ACTI_NAME, following)
        startActivity(intent)
    }

    override fun onPost() {
        binding.tvAbout.visibility=View.GONE
        setTvAndBtnColor(binding.textView157, binding.button57, R.color.text_black)
        setTvAndBtnColor(binding.textView158, binding.button58, R.color.text_black_light)
        setTvAndBtnColor(binding.textView159, binding.button59, R.color.text_black_light)
        setTvAndBtnColor(binding.button75, binding.button75, R.color.text_black_light)
        getOwnPost()
    }
    fun setTvAndBtnColor(tv: TextView, btn: Button, color: Int){
        tv.setTextColor(ContextCompat.getColor(this, color))
        btn.setTextColor(ContextCompat.getColor(this, color))
    }

    private fun blockUser(s: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(s)
        //set message for alert dialog
        builder.setMessage("Are You Sure Want to ${s}")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes"){ dialogInterface, which ->

            blockApi()


        }
        //performing cancel action
        builder.setNeutralButton("Cancel"){ dialogInterface, which ->
        }
        //performing negative action

        builder.show()


    }

    fun blockApi(){
        lifecycleScope.launch {
            try {
                val res=homeRepositry.userBlock(cUserId, userId)
                if (res.status){
                    "${res.message}".toast(this@SomeOneProfileActivity)

                }
            }catch (e: com.wiesoftware.spine.util.ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    private fun getOwnPost() {
        lifecycleScope.launch {
            try {
                val postRes=homeRepositry.getAllPosts(1, 200, userId, 0, 1)
                if (postRes.status){
                    BASE_IMAGE =postRes.image
                    profileImgBase=postRes.profilImage


                        val postList:List<PostData> = postRes.data
                        if (postList.size>0){
                            binding.rvProfileData.visibility=View.VISIBLE
                            binding.rvProfileData.also{
                                it.layoutManager= StaggeredGridLayoutManager(
                                    2,
                                    RecyclerView.VERTICAL
                                )
                                it.setHasFixedSize(true)
                                it.adapter= OwnPostAdapter(postList, this@SomeOneProfileActivity)
                            }
                        }else{
                            binding.rvProfileData.visibility=View.GONE
                        }
                }else{
                    binding.rvProfileData.visibility=View.GONE
                }
            }catch (e: com.wiesoftware.spine.util.ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }


    override fun onEvents() {
        binding.tvAbout.visibility=View.GONE
        setTvAndBtnColor(binding.textView157, binding.button57, R.color.text_black_light)
        setTvAndBtnColor(binding.textView158, binding.button58, R.color.text_black)
        setTvAndBtnColor(binding.textView159, binding.button59, R.color.text_black_light)
        setTvAndBtnColor(binding.button75, binding.button75, R.color.text_black_light)
        getOwnEvents()
    }

    private fun getOwnEvents() {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.getOwnEvents()
                if (res.status){
                    BASE_IMAGE=res.image

                        val data= res.data
                        if (data.size>0){
                            binding.rvProfileData.visibility=View.VISIBLE
                            binding.rvProfileData.also {
                                it.layoutManager=
                                    LinearLayoutManager(
                                        this@SomeOneProfileActivity,
                                        RecyclerView.VERTICAL,
                                        false
                                    )
                                it.setHasFixedSize(true)
                                it.adapter= OwnEventAdapter(data, this@SomeOneProfileActivity)
                            }

                        }else{
                            binding.rvProfileData.visibility=View.GONE
                        }



                }else{
                    binding.rvProfileData.visibility=View.GONE
                }
            }catch (e: com.wiesoftware.spine.util.ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onPods() {
        binding.tvAbout.visibility=View.GONE
        setTvAndBtnColor(binding.textView157, binding.button57, R.color.text_black_light)
        setTvAndBtnColor(binding.textView158, binding.button58, R.color.text_black_light)
        setTvAndBtnColor(binding.textView159, binding.button59, R.color.text_black)
        setTvAndBtnColor(binding.button75, binding.button75, R.color.text_black_light)
        binding.rvProfileData.visibility=View.GONE
    }

    override fun onRequestFollow() {
        Prefs.putAny(IS_FOLLOW_ACTION, true)


        if (cUserId.equals(userId)){
            "You Can't Follow YourSelf".toast(this@SomeOneProfileActivity)

        }else{
            if ((binding.buttonFollow.text).equals(getString(R.string.following))){
                binding.buttonFollow.text=getString(R.string.follows)
                unfollowUser()
                return
            }else{
                binding.buttonFollow.text=getString(R.string.following)
            }
            lifecycleScope.launch {
                try {

                    val res=homeRepositry.addUserFollow(cUserId, userId)
                    if (res.status){
                        "Following".toast(this@SomeOneProfileActivity)
                    }
                }catch (e: com.wiesoftware.spine.util.ApiException){
                    e.printStackTrace()
                }catch (e: NoInternetException){
                    e.printStackTrace()
                }
            }
        }



    }

    private fun unfollowUser() {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.unFollowUser(cUserId, userId)
                if (res.status){
                    "Unfollowed".toast(this@SomeOneProfileActivity)
                }
            }catch (e: com.wiesoftware.spine.util.ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun sendMessage() {
        CometChat.getUser(userId, object : CometChat.CallbackListener<User>() {
            override fun onSuccess(p0: User?) {
                userIntent(p0!!)
            }

            override fun onError(p0: CometChatException?) {
                isOtherUser = true
                createChatUserWithComet(userId, displayName, profileImg)
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



    override fun onCall() {

        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$buissenessPhone")
        startActivity(intent)
    }

    override fun onWebsite() {
        Log.e("click", website)

        if (!website.startsWith("http://") && !website.startsWith("https://")){
            website = "http://" + website;
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(website))
            startActivity(browserIntent)
        }else{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(website))
            startActivity(browserIntent)
        }


      //  openChromeTab(website)
    }

    override fun onAbout() {
        binding.rvProfileData.visibility=View.VISIBLE
        binding.tvAbout.visibility=View.VISIBLE
        binding.tvAbout.text=about

        setTvAndBtnColor(binding.textView157, binding.button57, R.color.text_black_light)
        setTvAndBtnColor(binding.textView158, binding.button58, R.color.text_black_light)
        setTvAndBtnColor(binding.textView159, binding.button59, R.color.text_black_light)
        setTvAndBtnColor(binding.button75, binding.button75, R.color.text_black)

    }


    companion object{
        val SOME_ONES_USER_ID="someOnesUserId"
        val ACTI_NAME="actiname"
        val FOLLOW_STATUS="followStatus"
        val IS_FOLLOW_ACTION="isFollowAction"
    }

    override fun onEventDetails(ownEventData: EventsRecord) {
        val intent=Intent(this, EventDetailActivity::class.java)
        intent.putExtra(EVE_RECORD, ownEventData)
        intent.putExtra(B_IMG_URL, BASE_IMAGE)
        intent.putExtra("event_id", ownEventData.id)
        startActivity(intent)
    }

    override fun onPostSelected(postData: PostData) {
       /* val profileImg=BASE_IMAGE+postData.files
        if (!(postData.files).isNullOrEmpty()){
            val type= if (isVideo(profileImg)){"1"}else{"0"}
            val intent=Intent(this, ViewMediaInLargeActivity::class.java)
            intent.putExtra(ViewMediaInLargeActivity.MEDIA_URL,profileImg)
            intent.putExtra(ViewMediaInLargeActivity.MEDIA_TYPE,type)
            startActivity(intent)
        }*/
        val intent=Intent(this, PostDetailsActivity::class.java)
        intent.putExtra(PostsFragment.POST_DATA, postData)
        intent.putExtra(PostsFragment.BASE_POST_IMG, BASE_IMAGE)
        intent.putExtra(PostsFragment.PROFILE_IMG_BASE, profileImgBase)
        startActivity(intent)
    }
    fun isVideo(media_file: String) =
        media_file.contains(".mp4", true) ||
                media_file.contains(".mov", true)  ||
                media_file.contains(".3gp", true)  ||
                media_file.contains(".avi", true)



    private fun openChromeTab(url: String) {
        var newUrl=url
        if (newUrl.startsWith("-")){
            newUrl=newUrl.replaceFirst("-", "http://")
        }
        val uri: Uri = Uri.parse(newUrl)
        val builder = CustomTabsIntent.Builder()

        val params = CustomTabColorSchemeParams.Builder()
            .setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
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
        try{
            customTabsIntent.launchUrl(this, uri)
        }catch (e: Exception){
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


    fun showUserReportBottomsheet(){
        val view: View = layoutInflater.inflate(R.layout.report_user_bottomsheet, null)
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
        /*view.cardView2.setOnClickListener {
            dialog.dismiss()
        }*/
        view.imageButton66.setOnClickListener {
            dialog.dismiss()
        }
        view.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton=view.findViewById<RadioButton>(checkedId)
            reportTitle = radioButton.text.toString()

            when(checkedId){
                R.id.radioButton4 -> {
                    aMessageTheyPosted()
                    dialog.dismiss()
                }
                R.id.radioButton5 -> {
                    inAppropriateBottomsheet()
                    dialog.dismiss()
                }
                else ->{
                    reportReasonBottomsheet()
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    fun inAppropriateBottomsheet(){
        val view: View = layoutInflater.inflate(R.layout.inappropriate_profile_content, null)
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
       /* view.cardView2.setOnClickListener {
            dialog.dismiss()
        }*/
        view.imageButton66.setOnClickListener {
            dialog.dismiss()
        }
        view.button91.setOnClickListener {
            //next
            reportReasonBottomsheet()
            dialog.dismiss()
        }
        view.button92.setOnClickListener {
            //back
            openUserBottom()
            dialog.dismiss()
        }
        view.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton=view.findViewById<RadioButton>(checkedId)
            reportReason = radioButton.text.toString()
            when(checkedId){
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

    fun aMessageTheyPosted(){
        val view: View = layoutInflater.inflate(R.layout.a_message_they_posted, null)
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
       /* view.cardView2.setOnClickListener {
            dialog.dismiss()
        }*/
        view.imageButton66.setOnClickListener {
            dialog.dismiss()
        }
        view.button91.setOnClickListener {
            //next
            reportReasonBottomsheet()
            dialog.dismiss()
        }
        view.button92.setOnClickListener {
            //back
            openUserBottom()
            dialog.dismiss()
        }
        view.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton=view.findViewById<RadioButton>(checkedId)
            reportReason = radioButton.text.toString()
            when(checkedId){
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
    lateinit var dialog: BottomSheetDialog
    fun reportReasonBottomsheet(){
        val view: View = layoutInflater.inflate(R.layout.report_reason, null)
        dialog = BottomSheetDialog(this)
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
        val tvTitle=dialog.findViewById<TextView>(R.id.textView256)
        tvTitle?.text=reportTitle
        dialog.textView257.text=reportReason
        /*view.cardView2.setOnClickListener {
            dialog.dismiss()
        }*/
        view.imageButton66.setOnClickListener {
            dialog.dismiss()
        }
        view.button91.setOnClickListener {
            //report
            reportMessage=view.editTextTextPersonName26.text.toString()
            reportThePost()


        }
        view.button92.setOnClickListener {
            //back
            openUserBottom()
            dialog.dismiss()
        }


        dialog.show()
    }

    private fun reportThePost() {
        //"$reportTitle, $reportReason, $reportMessage".toast(requireContext())
        lifecycleScope.launch {
            try {
                val res=homeRepositry.spineReportUserPostStory(
                    cUserId,
                    userId,
                    "1",
                    reportTitle,
                    reportReason,
                    reportMessage
                )
                if (res.status){
                    "Your request is submitted successfully".toast(this@SomeOneProfileActivity)
                    reportThanksBottomsheet()
                    dialog.dismiss()
                }
            }catch (e: com.wiesoftware.spine.util.ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }

    }

    fun reportThanksBottomsheet(){
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
            dialog.setCancelable(false)
        }
       /* view.cardView2.setOnClickListener {
            dialog.dismiss()
        }*/
        view.imageButton66.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}