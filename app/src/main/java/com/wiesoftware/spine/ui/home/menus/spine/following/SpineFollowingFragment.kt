package com.wiesoftware.spine.ui.home.menus.spine.following

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
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wiesoftware.spine.BuildConfig
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.FollowingContentAdapter
import com.wiesoftware.spine.data.net.reponses.FollowersData
import com.wiesoftware.spine.data.net.reponses.FollwingData
import com.wiesoftware.spine.data.net.reponses.PostData
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.FragmentSpineFollowingBinding
import com.wiesoftware.spine.databinding.StoriesFollowingItemBinding
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SomeOneProfileActivity
import com.wiesoftware.spine.ui.home.menus.profile.tabs.posts.PostsFragment
import com.wiesoftware.spine.ui.home.menus.spine.comment.postcomment.PostCommentActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.foryou.POST_BASE_IMG_FILE
import com.wiesoftware.spine.ui.home.menus.spine.foryou.POST_BASE_IMG_PRO
import com.wiesoftware.spine.ui.home.menus.spine.foryou.POST_ID
import com.wiesoftware.spine.ui.home.menus.spine.homefeed.FeedAdapter
import com.wiesoftware.spine.ui.home.menus.spine.homefeed.HomeFeedModel
import com.wiesoftware.spine.ui.home.menus.spine.impulse.ImpulseActivity
import com.wiesoftware.spine.ui.home.menus.spine.postdetails.PostDetailsActivity
import com.wiesoftware.spine.ui.home.menus.spine.selectfollowers.SelectFollowersAdapter
import com.wiesoftware.spine.ui.home.menus.spine.story.viewstories.ViewStoryActivity
import com.wiesoftware.spine.ui.home.menus.spine.viewmedia.ViewMediaInLargeActivity
import com.wiesoftware.spine.util.*
import kotlinx.android.synthetic.main.bottomsheet_picker.view.*
import kotlinx.android.synthetic.main.poor_quality_or_spam.view.button91
import kotlinx.android.synthetic.main.poor_quality_or_spam.view.button92
import kotlinx.android.synthetic.main.report_reason.*
import kotlinx.android.synthetic.main.report_reason.view.*
import kotlinx.android.synthetic.main.share_bottomsheet.*
import kotlinx.android.synthetic.main.share_bottomsheet.textView256
import kotlinx.android.synthetic.main.share_bottomsheet.view.*
import kotlinx.android.synthetic.main.why_r_u_reporting.view.cardView2
import kotlinx.android.synthetic.main.why_r_u_reporting.view.imageButton66
import kotlinx.android.synthetic.main.why_r_u_reporting.view.radioGroup
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class SpineFollowingFragment : Fragment(), KodeinAware, SpineFollowingEventListener,
    /* FollowingStoriesAdapter.FollowingStoryEventListener,*/
    FollowingContentAdapter.FollowingContentEventListener,
    SelectFollowersAdapter.FollowersEventListener, FeedAdapter.FeedEventListener {

    override val kodein by kodein()
    val homeRepositry: HomeRepository by instance()
    val factory: SpineFollowingViewmodelFactory by instance()
    lateinit var binding: FragmentSpineFollowingBinding
    lateinit var user_id: String

    var reportTitle = "";
    var reportReason = "";
    var reportMessage = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflate(inflater, R.layout.fragment_spine_following, container, false)
        val viewmodel = ViewModelProvider(this, factory).get(SpineFollowingViewmodel::class.java)
        binding.model = viewmodel
        viewmodel.spineFollowingEventListener = this
        viewmodel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            user_id = user.users_id!!
            setFollowers()
            getFollowingStories()
            //   getFollowingContent()  Temp commented MT
            getHomeFeedTemp()
        })


        return binding.root
    }

    private fun getFollowingContent() {

        lifecycleScope.launch {
            try {
                val postRes = homeRepositry.getAllPosts(1, 100, user_id, 1, 0)
                if (postRes.status) {
                    BASE_IMAGE = postRes.image
                    val allPostData = postRes.data
                    binding.rvFollowingContent.also {
                        it.layoutManager =
                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                        it.setHasFixedSize(true)
                        it.adapter =
                            FollowingContentAdapter(allPostData, this@SpineFollowingFragment)
                    }
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }

    }

    private fun getFollowingStories() {

        lifecycleScope.launch {
            try {
                /* val res = homeRepositry.getFollowingUsersStoryList(1, 10, user_id)
                 if (res.status) {  Temp commented MT
                     STORY_IMAGE = res.image
                     val storyData = res.data
                     binding.rvFollowingStories.also {
                         it.layoutManager =
                             LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
                         it.setHasFixedSize(true)
                         it.adapter = FollowingStoriesAdapter(storyData, this@SpineFollowingFragment)
                     }
                 }*/
                var storyData: MutableList<FollwingData> = ArrayList()
                storyData.add(FollwingData("Sophia", "", ArrayList(), "", ""))
                storyData.add(FollwingData("Oliver", "", ArrayList(), "", ""))
                storyData.add(FollwingData("Sophia", "", ArrayList(), "", ""))
                storyData.add(FollwingData("Brendon", "", ArrayList(), "", ""))
                storyData.add(FollwingData("Dales", "", ArrayList(), "", ""))
                storyData.add(FollwingData("David", "", ArrayList(), "", ""))

                var mAdapter = BaseAdapter<FollwingData>(requireContext())
                mAdapter!!.listOfItems = storyData
                mAdapter!!.expressionViewHolderBinding = { data, viewBinding, context ->
                    var holder = viewBinding as StoriesFollowingItemBinding
                    holder.model = data
                    /*val img=storyData[position].stories_data[0].media_file   Temp commented MT
                    Glide.with(holder.storiesFollowingItemBinding.circleImageView2)
                        .load(STORY_IMAGE+img)
                        .placeholder(R.drawable.ef_folder_placeholder)
                        .into(holder.storiesFollowingItemBinding.circleImageView2)
            */
                    holder.textView26.text = data.name
                    holder.circleImageView2.setOnClickListener {
                        onClick(data)
                    }
                }
                mAdapter!!.expressionOnCreateViewHolder = { viewGroup ->
                    StoriesFollowingItemBinding.inflate(
                        LayoutInflater.from(viewGroup.context),
                        viewGroup,
                        false
                    )
                }
                binding.rvFollowingStories.also {
                    it.layoutManager =
                        LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
                    it.setHasFixedSize(true)
                    it.adapter = mAdapter
                }

            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }

    }

    private fun getHomeFeedTemp() {
        Coroutines.main {
            try {

                var homeFeedList = ArrayList<HomeFeedModel>()
                homeFeedList.add(HomeFeedModel(1, "PROMOTED", 0))
                homeFeedList.add(HomeFeedModel(2, "SPINE", 0))
                homeFeedList.add(HomeFeedModel(4, "PROMOTED", 0))
                homeFeedList.add(HomeFeedModel(3, "ONLINE", 0))
                homeFeedList.add(HomeFeedModel(3, "LOCAL", 0))
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

    fun onClick(storyData: FollwingData) {
        Log.e("storyData.name", storyData.name)
        startActivity(Intent(requireContext(), ViewStoryActivity::class.java))
    }


    override fun onPostLike(postData: PostData, isLikedNow: Boolean) {
        lifecycleScope.launch {
            try {
                val res = homeRepositry.likePost(postData.id, user_id)
                if (res.status) {
                    getFollowingContent()
                    "1 Like.".toast(requireContext())
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    override fun onPostComment(postData: PostData, comment: String) {
        hideKeyboard()
        lifecycleScope.launch {
            try {
                val res = homeRepositry.postComment(postData.id, user_id, "0", comment)
                if (res.status) {
                    "Your Comment is saved successfully".toast(requireContext())
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    override fun onPostShare(postData: PostData) {
        /* val intent=Intent(requireContext(), SelectFollowersActivity::class.java)
         intent.putExtra(POST_ID,postData.id)
         startActivity(intent)*/

        sharePostBottomsheet(postData)
    }

    private fun setFollowers() {
        lifecycleScope.launch {
            try {
                val followersRes = homeRepositry.getFollowers(1, 100, user_id)
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

    var followersDataList: List<FollowersData> = ArrayList<FollowersData>()
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

    var selectedData: MutableList<FollowersData> = ArrayList<FollowersData>()
    val data: MutableMap<String, String> = HashMap<String, String>()
    private fun shareInsideApp(postData: PostData) {
        val post_id = postData.id
        data.put("spine_post_id", post_id)
        data.put("user_id", user_id)
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

    override fun followerSelected(followersData: FollowersData) {
        selectedData.add(followersData)
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
        postId = postData.id
        showBottomsheet()
    }

    override fun onPostSaved(postData: PostData) {
        lifecycleScope.launch {
            try {
                val res = homeRepositry.onPostSave(user_id, postData.id)
                if (res.status) {
                    getFollowingContent()
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
        val intent = Intent(requireContext(), SomeOneProfileActivity::class.java)
        intent.putExtra(SomeOneProfileActivity.SOME_ONES_USER_ID, postData.user_id)
        startActivity(intent)
    }

    override fun onViewImgInLarge(url: String, type: String) {
        val intent = Intent(requireContext(), ViewMediaInLargeActivity::class.java)
        intent.putExtra(ViewMediaInLargeActivity.MEDIA_URL, url)
        intent.putExtra(ViewMediaInLargeActivity.MEDIA_TYPE, type)
        startActivity(intent)
    }

    override fun onViewVidInLarge(url: String, type: String, postData: PostData) {
        val intent = Intent(requireContext(), PostDetailsActivity::class.java)
        intent.putExtra(PostsFragment.POST_DATA, postData)
        intent.putExtra(PostsFragment.BASE_POST_IMG, POST_BASE_IMG_FILE)
        intent.putExtra(PostsFragment.PROFILE_IMG_BASE, POST_BASE_IMG_PRO)
        startActivity(intent)
    }

    private fun showBottomsheet() {
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
        dialog.textView256.text = reportTitle
        dialog.textView257.text = reportReason
        view.cardView2.setOnClickListener {
            dialog.dismiss()
        }
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
                val res = homeRepositry.spineReportUserPostStory(
                    user_id,
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

    override fun onAdLinkClicked(website: String) {
        openChromeTab(website)
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

    override fun onPromotedClicked(postData: HomeFeedModel) {

    }

    override fun viewAllSpineImpulse_(postData: HomeFeedModel) {
        startActivity(Intent(requireContext(), ImpulseActivity::class.java))
    }

    override fun onViewSomeonesProfile(postData: HomeFeedModel) {
        val intent = Intent(requireContext(), SomeOneProfileActivity::class.java)
        intent.putExtra(SomeOneProfileActivity.SOME_ONES_USER_ID, postData.text)
        startActivity(intent)
    }

}