package com.wiesoftware.spine.ui.home.menus.spine.foryou

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RadioButton
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.kodein.di.android.BuildConfig
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.*
import com.wiesoftware.spine.data.net.reponses.*
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.FragmentSpineForYouBinding
import com.wiesoftware.spine.ui.home.menus.events.B_IMG_URL
import com.wiesoftware.spine.ui.home.menus.events.event_details.EventDetailActivity
import com.wiesoftware.spine.ui.home.menus.profile.myprofile.MyProfileActivity
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SomeOneProfileActivity
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SomeOneProfileActivity.Companion.IS_FOLLOW_ACTION
import com.wiesoftware.spine.ui.home.menus.profile.tabs.posts.PostsFragment
import com.wiesoftware.spine.ui.home.menus.spine.categories.TrendingCatActivity
import com.wiesoftware.spine.ui.home.menus.spine.comment.impulsecomment.ImpulseCommentActivity
import com.wiesoftware.spine.ui.home.menus.spine.comment.postcomment.PostCommentActivity
import com.wiesoftware.spine.ui.home.menus.spine.homefeed.FeedAdapter
import com.wiesoftware.spine.ui.home.menus.spine.homefeed.HomeFeedModel
import com.wiesoftware.spine.ui.home.menus.spine.impulse.ImpulseActivity
import com.wiesoftware.spine.ui.home.menus.spine.postdetails.PostDetailsActivity
import com.wiesoftware.spine.ui.home.menus.spine.practicioners.PracticionersActivity
import com.wiesoftware.spine.ui.home.menus.spine.rec_followers.RecommendedFollowersActivity
import com.wiesoftware.spine.ui.home.menus.spine.selectfollowers.SelectFollowersAdapter
import com.wiesoftware.spine.ui.home.menus.spine.story.StoryActivity
import com.wiesoftware.spine.ui.home.menus.spine.story.viewstories.ViewStoryActivity
import com.wiesoftware.spine.ui.home.menus.spine.viewmedia.ViewMediaInLargeActivity
import com.wiesoftware.spine.ui.home.menus.spine.welcome.ViewWelcomeActivity
import com.wiesoftware.spine.util.*
import kotlinx.android.synthetic.main.bottomsheet_picker.view.*
import kotlinx.android.synthetic.main.fragment_spine_for_you.*
import kotlinx.android.synthetic.main.poor_quality_or_spam.view.button91
import kotlinx.android.synthetic.main.poor_quality_or_spam.view.button92
import kotlinx.android.synthetic.main.report_reason.view.*
import kotlinx.android.synthetic.main.share_bottomsheet.*
import kotlinx.android.synthetic.main.share_bottomsheet.view.*
import kotlinx.android.synthetic.main.why_r_u_reporting.view.cardView2
import kotlinx.android.synthetic.main.why_r_u_reporting.view.imageButton66
import kotlinx.android.synthetic.main.why_r_u_reporting.view.radioGroup
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import org.kodein.di.generic.kcontext

var POST_BASE_IMG_FILE: String? = null
var POST_BASE_IMG_PRO: String? = null
var BASE_IMAGE: String? = null
var STORY_IMAGE: String? = null
val POST_ID = "post_id"
val IS_WELCOME_SEEN = "isWelcomeVideoSeen"

class SpineForYouFragment : Fragment(), KodeinAware, SpineForYouEventListener,
    SpineImpulseAdapter.SpineImpulseEventListener, WelcomeDataAdapter.WelcomeEventListener,
    ForYouContentAdapter.ForYouContentEventListener, StoriesAdapter.StoryEventListener,
    RecommendedFollowersAdapter.RecommendedFollowersEventListener,
    SelectFollowersAdapter.FollowersEventListener, FeedAdapter.FeedEventListener {

    companion object {
        val EVENT_POST_ID = "eventPostId"
        val IS_EVE_FROM_POST = "isEveFromPost"
    }

    override val kodeinContext = kcontext<Fragment>(this)
    override val kodein by kodein()
    private val factory: SpineForYouViewModelFactory by instance()
    private val homeRepositry: HomeRepositry by instance()
    val data: MutableMap<String, String> = HashMap<String, String>()
    var reportTitle = "";
    var reportReason = "";
    var reportMessage = ""
    lateinit var binding: FragmentSpineForYouBinding
    private lateinit var userId: String
    var followersDataList: List<FollowersData> = ArrayList<FollowersData>()
    var selectedData: MutableList<FollowersData> = ArrayList<FollowersData>()

    fun scrollToView(recyclerView: RecyclerView) {
        binding.scrollview.post(object : Runnable {
            override fun run() {
                try {
                    binding.scrollview.smoothScrollTo(0, constraintLayout.bottom)
                } catch (e: Exception) {

                }

            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_spine_for_you, container, false)
        val viewModel = ViewModelProvider(this, factory).get(SpineForYouViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.spineForYouEventListener = this
        viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->

            userId = user.users_id!!
            Log.d("useris", user.users_id!!)
            setFollowers()
            getForYouContent()
            getStories()
            getRecommnededFollowers()
            getSpineImpulse()
            getUserDetails()
            getHomeFeedTemp()
        })

        val isWelcomeSeen = Prefs.getBoolean(IS_WELCOME_SEEN, false)
        if (isWelcomeSeen) {
            binding.textView16.visibility = View.GONE
            binding.recyclerView.visibility = View.GONE
        }
        getWelcomeList()

        getTrendingCat()

       /* binding.rvForYouContent.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // Show your view again.. If you want to show them..
                    binding.constraintLayout. animate()
                        .alpha(1.0f)
                        .setDuration(300);
                    binding.constraintLayout.visibility=View.VISIBLE
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    // Hide your view..
                    binding.constraintLayout. animate()
                        .alpha(0.0f)
                        .setDuration(200);
                    binding.constraintLayout.visibility=View.GONE
                }
                super.onScrollStateChanged(recyclerView, newState)

            }
           *//* override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {


                if (recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {

                }
                super.onScrolled(recyclerView, dx, dy)
            }*//*
        })*/


        return binding.root
    }

    private fun getUserDetails() {
        lifecycleScope.launch {
            try {
                val res = homeRepositry.getUserDetails(userId)
                if (res.status) {
                    val data = res.data
                    binding.tvUser.text = data.display_name ?: data.name
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }


    private fun getForYouContent() {
        lifecycleScope.launch {
            try {
                val postRes = homeRepositry.getAllPosts(1, 200, userId, 0, 0)
                if (postRes.status) {
                    POST_BASE_IMG_FILE = postRes.image
                    POST_BASE_IMG_PRO = postRes.profilImage
                    val postList: List<PostData> = postRes.data
                    for ((position, data) in postList.withIndex()) {
                        if(data.files != null) {
                            if(data.files.contains(",")) {
                                var filesData = data.files.split(",")
                                postList[position].files = filesData[0]
                            }
                        }
                    }

                    binding.rvForYouContent.also {
                        it.layoutManager = LinearLayoutManager(
                            requireContext(),
                            RecyclerView.VERTICAL,
                            false
                        )
                        it.setHasFixedSize(true)
                        it.adapter = ForYouContentAdapter(postList, this@SpineForYouFragment)
                    }

                }

            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }

    }

    private fun getRecommnededFollowers() {

        lifecycleScope.launch {
            try {
                Log.e("user_id::", userId)
//                Harsh: Tempeory Desible because Api not working:21-10-22
//                val allUsersRes = homeRepositry.recommendedFollowersListByCategories(1, 10, userId)
//                if (allUsersRes.status) {
//                    BASE_IMAGE = allUsersRes.image
//                    val allUsersData = allUsersRes.data
//                    setPicMember(allUsersRes.data, allUsersRes.image)
//                    binding.rvRecommendingFollowers.also {
//                        it.layoutManager = LinearLayoutManager(
//                            requireContext(),
//                            RecyclerView.HORIZONTAL,
//                            false
//                        )
//                        it.setHasFixedSize(true)
//                        it.adapter = RecommendedFollowersAdapter(
//                            allUsersData,
//                            this@SpineForYouFragment
//                        )
//                    }
//                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }


    }


    private fun getTrendingCat() {
        lifecycleScope.launch {
            try {
                val hashtagRes = homeRepositry.getHashtagList()
                if (hashtagRes.status) {
                    val hashtagDataList = hashtagRes.data
                    try {
                        //binding.tvYoga.setText(hashtagDataList[0].hash_title)
                       // binding.tvMeditation.setText(hashtagDataList[1].hash_title)
                    } catch (e: Exception) {

                    }

                    binding.rvTrendingCat.also {
                        it.layoutManager = LinearLayoutManager(
                            requireContext(),
                            RecyclerView.HORIZONTAL,
                            false
                        )
                        it.setHasFixedSize(true)
                        it.adapter = TrendingCatAdapter(hashtagDataList)
                    }
                }

            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }

    }

    private fun getStories() {
        lifecycleScope.launch {
            try {
                val storyRes = homeRepositry.getFollowingUsersStoryList(
                    1,
                    100,
                    userId
                )//homeRepositry.getYourStories(userId)
                if (storyRes.status) {
                    STORY_IMAGE = storyRes.user_image
                    val storyList: List<FollwingData> = storyRes.data
                    if (storyRes.data.size > 2) {
                        var user_pic_one = storyRes.data[0].profilePic
                        var user_pic_two = storyRes.data[1].profilePic
                        var user_pic_three = storyRes.data[2].profilePic
                        setPic(user_pic_one, user_pic_two, user_pic_three)
                    } else if (storyRes.data.size > 0) {
                        var user_pic_one = storyRes.data[0].profilePic
                        setPic(user_pic_one, "", "")
                    }


                    binding.rvStories.also {
                        it.layoutManager = LinearLayoutManager(
                            requireContext(),
                            RecyclerView.HORIZONTAL,
                            false
                        )
                        it.setHasFixedSize(true)
                        it.adapter = StoriesAdapter(storyList, this@SpineForYouFragment, 0, userId)
                    }
                }

            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }

    }
    /*MT all feed with dummy data*/
    private fun getHomeFeedTemp(){
        Coroutines.main {
            try {

                var homeFeedList = ArrayList<HomeFeedModel> ()
                homeFeedList.add(HomeFeedModel(1,"PROMOTED",0))
                homeFeedList.add(HomeFeedModel(2,"SPINE",0))
                homeFeedList.add(HomeFeedModel(4,"PROMOTED",0))
                homeFeedList.add(HomeFeedModel(3,"ONLINE",0))
                homeFeedList.add(HomeFeedModel(3,"LOCAL",0))

                binding.rvHomeFeed.also {
                    it.layoutManager = LinearLayoutManager(
                        requireContext(),
                        RecyclerView.VERTICAL,
                        false
                    )
                    it.setHasFixedSize(true)
                    it.adapter = FeedAdapter(homeFeedList, this)
                }

            } catch (e: ApiException) {
                e.printStackTrace()
                context?.let { "${e.message}".toast(it) }
            } catch (e: NoInternetException) {
                e.printStackTrace()
                context?.let { "${e.message}".toast(it) }
            }
        }
    }
    private fun getSpineImpulse() {
        Coroutines.main {
            try {
                val impulseResponse = homeRepositry.getSpineImpulse(1, 10, userId)
                if (impulseResponse.status) {
                    BASE_IMAGE = impulseResponse.image
                    val impulseList: List<SpineImpulseData> = impulseResponse.data!!
                    binding.rvSImpulse.also {
                        it.layoutManager = LinearLayoutManager(
                            requireContext(),
                            RecyclerView.HORIZONTAL,
                            false
                        )
                        it.setHasFixedSize(true)
                        it.adapter = SpineImpulseAdapter(impulseList, this)
                    }
                }

            } catch (e: ApiException) {
                e.printStackTrace()
                context?.let { "${e.message}".toast(it) }
            } catch (e: NoInternetException) {
                e.printStackTrace()
                context?.let { "${e.message}".toast(it) }
            }
        }
    }

    private fun getWelcomeList() {
        Coroutines.main {
            try {
                val welcomeResponse = homeRepositry.getWelcomeData()
                if (welcomeResponse.status!!) {
                    BASE_IMAGE = welcomeResponse.image
                    val welcomeList: List<WelcomeData> = welcomeResponse.data!!
                    binding.recyclerView.also {
                        it.layoutManager = LinearLayoutManager(
                            requireContext(), RecyclerView.HORIZONTAL, false
                        )
                        it.setHasFixedSize(true)
                        it.adapter = WelcomeDataAdapter(welcomeList, this)
                        Prefs.putAny(IS_WELCOME_SEEN, true)
                    }
                } else {
                    // context?.let { welcomeResponse.message!!.toast(it) }
                }

            } catch (e: ApiException) {
                e.printStackTrace()
                context?.let { "${e.message}".toast(it) }
            } catch (e: NoInternetException) {
                e.printStackTrace()
                context?.let { "${e.message}".toast(it) }
            }
        }

    }

    override fun viewAllSpineImpulse() {
        startActivity(Intent(requireContext(), ImpulseActivity::class.java))
    }

    override fun viewAllStories() {
        startActivity(Intent(requireContext(), StoryActivity::class.java))
    }

    override fun trendingCategories() {
        startActivity(Intent(requireContext(), PracticionersActivity::class.java))
        //requireContext().toast("Inprogress")
    }

    override fun recommendedFollowers() {
        startActivity(Intent(requireContext(), RecommendedFollowersActivity::class.java))
    }

    override fun onCommentClicked(spineImpulseData: SpineImpulseData) {
        val intent = Intent(requireContext(), ImpulseCommentActivity::class.java)
        intent.putExtra("impulse_id", spineImpulseData.id)
        startActivity(intent)
    }

    override fun onLikeComment(spineImpulseData: SpineImpulseData) {
        lifecycleScope.launch {
            try {
                if (spineImpulseData.user_like_status.equals("1")) {
                    val res = homeRepositry.unlikeImpulse(userId, spineImpulseData.id)
                    if (res.status) {
                        getSpineImpulse()
                    }
                } else {
                    val res = homeRepositry.likeSpineImpulse(userId, spineImpulseData.id)
                    val mRes = res.string()
                    val jRes = JSONObject(mRes)
                    Log.e("like:", mRes)
                    if (jRes.getBoolean("status")) {
                        getSpineImpulse()
                    }
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    override fun onViewWelcome() {
        startActivity(Intent(requireContext(), ViewWelcomeActivity::class.java))
    }

    override fun onPostLike(postData: PostData, isLikedNow: Boolean) {
        lifecycleScope.launch {
            try {
                val res = homeRepositry.likePost(postData.id, userId)
                if (res.status) {
                    //getForYouContent()
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val isFollowActionPerformed = Prefs.getBoolean(IS_FOLLOW_ACTION, false)
        if (isFollowActionPerformed) {
            getForYouContent()
            Prefs.putAny(IS_FOLLOW_ACTION, false)
        }

    }

    override fun onPostComment(postData: PostData, comment: String) {
        hideKeyboard()
        lifecycleScope.launch {
            try {
                val res = homeRepositry.postComment(postData.id, userId, "0", comment)
                if (res.status) {
                    "Your Comment is saved successfully".toast(requireContext())
                    val intent = Intent(requireContext(), PostCommentActivity::class.java)
                    intent.putExtra(POST_ID, postData.id)
                    startActivity(intent)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    override fun onPostShare(postData: PostData) {
        /*  val intent=Intent(requireContext(),SelectFollowersActivity::class.java)
          intent.putExtra(POST_ID,postData.id)
          startActivity(intent)*/

        sharePostBottomsheet(postData)

    }

    private fun setFollowers() {
        lifecycleScope.launch {
            try {
                val followersRes = homeRepositry.getFollowers(1, 100, userId)
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


    private fun sharePostBottomsheet(postData: PostData) {
        val view: View = layoutInflater.inflate(R.layout.share_bottomsheet, null)
        val dialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        dialog.setContentView(view)
        dialog.rvShareFollowers.also {
            it.layoutManager = /*GridLayoutManager(requireContext(),2,RecyclerView.VERTICAL,false)*/
                LinearLayoutManager(
                    requireContext(),
                    RecyclerView.VERTICAL,
                    false
                )
            it.setHasFixedSize(true)
            it.adapter = SelectFollowersAdapter(followersDataList, this@SpineForYouFragment)
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
            shareOnWhatsApp(postData)
        }
        view.imageView33.setOnClickListener {
            shareOutsideApp(postData)
        }
        view.button93.setOnClickListener {
            shareInsideApp(postData)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun shareInsideApp(postData: PostData) {
        val post_id = postData.id
        data.put("spine_post_id", post_id)
        data.put("user_id", userId)
        var i = 0
        selectedData.forEach {
            data.put("share_users_id[$i]", it.user_id)
            i++
        }
        lifecycleScope.launch {
            try {
                val res = homeRepositry.sharePost(data)
                if (res.status) {
                    res.message.toast(requireContext())
                } else {
                    "${res.message}".toast(requireContext())
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }


    }

    private fun shareOutsideApp(postData: PostData) {
        val img = BASE_IMAGE + postData.files
        val title = postData.title
        val hastag = postData.hashtag_ids
        val uName = postData.post_user_name
        val appId = BuildConfig.APPLICATION_ID
        val textToShare = "Hey! $uName has shared you their post from Spine.\n \n" +
                "$title \n  \n $hastag \n \n $img \n \n  Check your spine app for details. If you don't have spine app click below link to download->" +
                "\n\n https://play.google.com/store/apps/details?id=$appId"
        ShareCompat.IntentBuilder
            .from(requireActivity())
            .setText(textToShare)
            .setType("text/plain")
            .setChooserTitle("Share Spine Content")
            .startChooser()
    }

    private fun shareOnWhatsApp(postData: PostData) {
        val img = BASE_IMAGE + postData.files
        val title = postData.title
        val hastag = postData.hashtag_ids
        val uName = postData.post_user_name
        val appId = BuildConfig.APPLICATION_ID
        val textToShare = "Hey! $uName has shared you their post from Spine.\n \n" +
                "$title \n  \n $hastag \n \n $img \n \n  Check your spine app for details. If you don't have spine app click below link to download->" +
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

    var postId = ""
    override fun onPostInAppShare(postData: PostData) {
        /*val intent=Intent(requireContext(),SelectFollowersActivity::class.java)
        intent.putExtra(POST_ID,postData.id)
        startActivity(intent)*/

        postId = postData.id
        showBottomsheet(postData)
    }

    private fun showBottomsheet(postData: PostData) {


        val view: View = layoutInflater.inflate(R.layout.home_feed_three_dots, null)
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
            dialog.setCancelable(false)
        }

        if (userId.equals(postData.user_id)) {
            view.btnFollow.visibility = View.GONE
        }
        if (postData.followStatus.equals("1")) {
            view.btnFollow.text = getString(R.string.unfollow)
        } else {
            view.btnFollow.text = getString(R.string.follow)
        }

        view.btnCan.setOnClickListener {
            dialog.dismiss()
        }
        view.btnFollow.setOnClickListener {
            "follow".toast(requireContext())
            dialog.dismiss()
        }
        view.btnOnline.setOnClickListener {
            showReportBottomsheet()
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onPostSaved(postData: PostData) {
        lifecycleScope.launch {
            try {
                val res = homeRepositry.onPostSave(userId, postData.id)
                if (res.status) {
                    getForYouContent()
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }

    }

    override fun onPostCommentDoubleClick(postData: PostData) {
        val intent = Intent(requireContext(), PostCommentActivity::class.java)
        intent.putExtra(POST_ID, postData.id)
        startActivity(intent)
    }

    override fun onSomeonesProfileView(postData: PostData) {
        if (userId.equals(postData.user_id)) {
            startActivity(Intent(requireContext(), MyProfileActivity::class.java))
        } else {
            val intent = Intent(requireContext(), SomeOneProfileActivity::class.java)
            intent.putExtra(SomeOneProfileActivity.SOME_ONES_USER_ID, postData.user_id)
            intent.putExtra(SomeOneProfileActivity.FOLLOW_STATUS, postData.followStatus)
            startActivity(intent)
        }
    }

    override fun onViewImgInLarge(url: String, type: String) {
        val intent = Intent(requireContext(), ViewMediaInLargeActivity::class.java)
        intent.putExtra(ViewMediaInLargeActivity.MEDIA_URL, url)
        intent.putExtra(ViewMediaInLargeActivity.MEDIA_TYPE, type)
        startActivity(intent)
    }

    override fun onViewVidInLarge(url: String, type: String, postData: PostData) {
        /* val intent=Intent(requireContext(),ViewMediaInLargeActivity::class.java)
         intent.putExtra(ViewMediaInLargeActivity.MEDIA_URL,url)
         intent.putExtra(ViewMediaInLargeActivity.MEDIA_TYPE,type)
         startActivity(intent)*/

        if ((!postData.featureAdminApprove.isNullOrEmpty()) && postData.featureAdminApprove.equals("1")) {
            return
        }


        if ((!postData.eventPost.isNullOrEmpty()) && postData.eventPost.equals("1")) {
            Log.e("imaggeee", BASE_IMAGE.toString())
            val intent = Intent(requireContext(), EventDetailActivity::class.java)
            intent.putExtra(IS_EVE_FROM_POST, true)
            intent.putExtra(EVENT_POST_ID, postData.id)
            intent.putExtra(B_IMG_URL, BASE_IMAGE)
            intent.putExtra("event_id", postData.eventId)

            startActivity(intent)
        } else {
            val intent = Intent(requireContext(), PostDetailsActivity::class.java)
            intent.putExtra(PostsFragment.POST_DATA, postData)
            intent.putExtra(PostsFragment.BASE_POST_IMG, POST_BASE_IMG_FILE)
            intent.putExtra(PostsFragment.PROFILE_IMG_BASE, POST_BASE_IMG_PRO)
            startActivity(intent)
        }
    }

    override fun onAdLinkClicked(url: String) {
        openChromeTab(url)
    }

    override fun onDestroyView() {
        if (view != null) {
            val parent = requireView().parent as ViewGroup
            parent.removeAllViews()
        }
        super.onDestroyView()
    }

    override fun onStoryClick() {
        startActivity(Intent(requireContext(), ViewStoryActivity::class.java))
    }

    override fun onViewsomeonesProfile(allUsersData: AllUsersData) {
        val intent = Intent(requireContext(), SomeOneProfileActivity::class.java)
        intent.putExtra(SomeOneProfileActivity.SOME_ONES_USER_ID, allUsersData.usersId)
        startActivity(intent)
    }

    fun showReportBottomsheet() {
        val view: View = layoutInflater.inflate(R.layout.why_r_u_reporting, null)
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
            dialog.setCancelable(false)
        }
        view.cardView2.setOnClickListener {
            dialog.dismiss()
        }
        view.imageButton66.setOnClickListener {
            dialog.dismiss()
        }
        view.button91.setOnClickListener {
            //report
            val report = view.editTextTextPersonName26.text.toString()
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
                val res = homeRepositry.spineReportUserPostStory(
                    userId,
                    postId,
                    "2",
                    reportTitle,
                    reportReason,
                    reportMessage
                )
                if (res.status) {
                    "Your request is submitted successfully".toast(requireContext())
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
        view.cardView2.setOnClickListener {
            dialog.dismiss()
        }
        view.imageButton66.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun followerSelected(followersData: FollowersData) {
        selectedData.add(followersData)
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
            .setNavigationBarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            .setSecondaryToolbarColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimaryDark
                )
            )
            .build()
        builder.setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, params)
        builder.setStartAnimations(
            requireContext(),
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        builder.setExitAnimations(
            requireContext(),
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        builder.setShowTitle(true)
        if (isChromeInstalled()) {
            builder.build().intent.setPackage("com.android.chrome")
        }
        val customTabsIntent = builder.build()
        try {
            customTabsIntent.launchUrl(requireContext(), uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isChromeInstalled(): Boolean {
        return try {
            requireActivity().getPackageManager().getPackageInfo("com.android.chrome", 0)
            true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            false
        }
    }

    fun setPic(One: String, Two: String, Three: String) {
        Glide.with(this)
            .load(One)
            .placeholder(R.drawable.ic_profile)
            .into(binding.ivRec)

        Glide.with(this)
            .load(Two)
            .placeholder(R.drawable.ic_profile)
            .into(binding.ivImage2)

        Glide.with(this)
            .load(Three)
            .placeholder(R.drawable.ic_profile)
            .into(binding.ivImage3)
    }

    fun setPicMember(data: List<AllUsersData>, url: String) {

        try {
            var one_Pic = data[0].profilePic
            Log.e("logpic", one_Pic.toString())
            var two_Pic = data[1].profilePic
            var three_Pic = data[2].profilePic

            Glide.with(this)
                .load(url + one_Pic)
                .placeholder(R.drawable.ic_profile)
                .into(binding.ivMem)

            Glide.with(this)
                .load(url + two_Pic)
                .placeholder(R.drawable.ic_profile)
                .into(binding.ivMem1)

            Glide.with(this)
                .load(url + three_Pic)
                .placeholder(R.drawable.ic_profile)
                .into(binding.ivMem2)
        } catch (e: Exception) {
            Log.e("datavalue0", e.toString())
        }

    }
    /*MT temp click event*/
    override fun onPromotedClicked(postData: HomeFeedModel) {
       Log.d("---->Click",postData.text)
    }

    override fun viewAllSpineImpulse_(postData: HomeFeedModel) {
        startActivity(Intent(requireContext(), ImpulseActivity::class.java))
    }

    fun RecyclerView.addOnScrollHiddenView(
        hiddenView: View,
        translationX: Float = 0F,
        translationY: Float = 0F,
        duration: Long = 200L
    ) {
        var isViewShown = true
        this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                when{
                    dy> 0 && isViewShown -> {
                        isViewShown = false
                        hiddenView.animate()
                            .translationX(translationX)
                            .translationY(translationY)
                            .duration = duration
                    }
                    dy < 0 && !isViewShown ->{
                        isViewShown = true
                        hiddenView.animate()
                            .translationX(0f)
                            .translationY(0f)
                            .duration = duration
                    }
                }
            }
        })
    }
}