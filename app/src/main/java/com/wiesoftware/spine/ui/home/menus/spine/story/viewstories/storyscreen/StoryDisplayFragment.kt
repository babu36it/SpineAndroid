package com.wiesoftware.spine.ui.home.menus.spine.story.viewstories.storyscreen

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wiesoftware.spine.BuildConfig
import com.wiesoftware.spine.R
import com.wiesoftware.spine.SpineApplication
import com.wiesoftware.spine.data.adapter.SelectStoryTimeAdapter
import com.wiesoftware.spine.data.net.reponses.*
import com.wiesoftware.spine.data.repo.EventRepository
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.FragmentStoryDisplayBinding
import com.wiesoftware.spine.ui.home.menus.profile.myprofile.MyProfileActivity
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SomeOneProfileActivity
import com.wiesoftware.spine.ui.home.menus.spine.comment.storycomment.StoryCommentActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.selectfollowers.SelectFollowersAdapter
import com.wiesoftware.spine.ui.home.menus.spine.story.viewstories.ViewStoryActivity
import com.wiesoftware.spine.ui.home.menus.spine.story.viewstories.customstoryview.StoriesProgressView
import com.wiesoftware.spine.util.*
import kotlinx.android.synthetic.main.bottomsheet_picker.view.*

import kotlinx.android.synthetic.main.fragment_story_display.*
import kotlinx.android.synthetic.main.poor_quality_or_spam.view.button91
import kotlinx.android.synthetic.main.poor_quality_or_spam.view.button92
import kotlinx.android.synthetic.main.report_reason.*
import kotlinx.android.synthetic.main.report_reason.view.*
import kotlinx.android.synthetic.main.select_time_bootom_sheet.view.*
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StoryDisplayFragment : Fragment(),
    StoriesProgressView.StoriesListener,KodeinAware, StoryDisplayEventListener,
    SelectFollowersAdapter.FollowersEventListener, SelectStoryTimeAdapter.OnTimeSelectedListener {

    override val kodein by kodein()
    val homeRepositry: HomeRepository by instance()
    val eventRepository: EventRepository by instance()
    lateinit var binding: FragmentStoryDisplayBinding
    var storyUserName=""
    private val position: Int by
    lazy { arguments?.getInt(EXTRA_POSITION) ?: 0 }

    private val storyUser: FollwingData by
    lazy {
        (arguments?.getParcelable<FollwingData>(
            EXTRA_STORY_USER
        ) as FollwingData)
    }

    private val stories: ArrayList<FollowingStoriesData> by
    lazy { storyUser.stories_data }

    private var simpleExoPlayer: SimpleExoPlayer? = null
    private lateinit var mediaDataSourceFactory: DataSource.Factory
    private var pageViewOperator: PageViewOperator? = null
    private var counter = 0
    private var pressTime = 0L
    private var limit = 500L
    private var onResumeCalled = false
    private var onVideoPrepared = false

    var userId="";var storyUserId = ""
    var reportTitle="";var reportReason="";var reportMessage=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val view=inflater.inflate(R.layout.fragment_story_display, container, false)
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_story_display, container, false)
        val viewmodel=ViewModelProvider(this).get(StoryDisplayViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.storyDisplayEventListener=this
        homeRepositry.getUser().observe(viewLifecycleOwner, androidx.lifecycle.Observer { user->
            userId=user.users_id!!
            setFollowers()
            getStoriesMonthYear()
        })

        return binding.root
    }

    var storyMonthDataList: List<StoryMothData> = ArrayList<StoryMothData>()
    private fun getStoriesMonthYear() {
        lifecycleScope.launch {
            try {
               val res=homeRepositry.getStoriesMonthYear(userId)
                if (res.status){
                 storyMonthDataList=res.data
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storyDisplayVideo.useController = false
        updateStory()
        setUpUi()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.pageViewOperator = context as PageViewOperator
    }

    override fun onStart() {
        super.onStart()
        counter = restorePosition()
    }

    override fun onResume() {
        super.onResume()
        onResumeCalled = true
        if (counter < stories.size && stories[counter].isVideo() && !onVideoPrepared) {
            simpleExoPlayer?.playWhenReady = false
            return
        }

        simpleExoPlayer?.seekTo(5)
        simpleExoPlayer?.playWhenReady = true
        if (counter == 0) {
            storiesProgressView?.startStories()
        } else {
            // restart animation
            counter = ViewStoryActivity.progressState.get(arguments?.getInt(EXTRA_POSITION) ?: 0)
            storiesProgressView?.startStories(counter)
        }
    }

    override fun onPause() {
        super.onPause()
        simpleExoPlayer?.playWhenReady = false
        storiesProgressView?.abandon()
    }

    override fun onComplete() {
        simpleExoPlayer?.release()
        pageViewOperator?.nextPageView()
    }

    override fun onPrev() {
        if (counter - 1 < 0) return
        --counter
        savePosition(counter)
        updateStory()
    }

    override fun onNext() {
        if (stories.size <= counter + 1) {
            return
        }
        ++counter
        savePosition(counter)
        updateStory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        simpleExoPlayer?.release()
    }

    fun convertDateToLong(date: String): Long {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault())//2021-02-25 12:40:55
        return df.parse(date)!!.time
    }
    private fun updateStory() {
        simpleExoPlayer?.stop()
        if (stories[counter].isVideo()) {
            storyDisplayVideo.show()
            storyDisplayImage.hide()
            storyDisplayVideoProgress.show()
            initializePlayer()
        } else {
            storyDisplayVideo.hide()
            storyDisplayVideoProgress.hide()
            storyDisplayImage.show()
          //  Glide.with(this).load(STORY_IMAGE+stories[counter].media_file).into(storyDisplayImage)
            Glide.with(this).load(stories[counter].media_file).into(storyDisplayImage)
        }

       /* val cal: Calendar = Calendar.getInstance(Locale.ENGLISH).apply {
            timeInMillis = stories[counter].storyDate
        }*/
        storyUserId=stories[counter].user_id
        storyDisplayTime.text = getStoryPostedTime(stories[counter].created_on)//DateFormat.format("MM-dd-yyyy HH:mm:ss", cal).toString()
        tvTitleStory.text=stories[counter].title
        binding.tvCommentStory.text=""+stories[counter].totalComments
        binding.tvLikeStory.text=""+stories[counter].totalLikes
        val likeStatus=stories[counter].likeStatus
        if (likeStatus==1){
            setTextViewDrawableColor(binding.tvLikeStory)
        }else{
            setTextViewDrawableColor(binding.tvLikeStory,R.color.text_white)
        }
        val saveStatus=stories[counter].saveStatus
        if (saveStatus==1){
            binding.imageButton19.setImageResource(R.drawable.ic_saved)
            binding.imageButton19.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red));
        }else{
            binding.imageButton19.setImageResource(R.drawable.ic_bookmark)
            binding.imageButton19.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white));
        }

        //setTextViewDrawableColor(binding.tvLikeStory,R.color.text_white)
    }


    fun getStoryPostedTime(time: String): String {
        var newDate=""
        val cc: Date = Calendar.getInstance().getTime()
        val simpleDateFormat = SimpleDateFormat("yyyy-M-dd hh:mm:ss", Locale.getDefault())
        try {
            val s=time
            val date1: Date = simpleDateFormat.parse(s)
            val date: String = simpleDateFormat.format(cc)
            val date2: Date = simpleDateFormat.parse(date)
            if (date1 != null && date2 != null) {
                val d: String = printDifference(date1, date2)!!
                if (d.contains("-")){
                    newDate="Today"
                }else{
                    newDate=d
                }

            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return newDate
    }



    private fun initializePlayer() {
        if (simpleExoPlayer == null) {
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(requireContext())
        } else {
            simpleExoPlayer?.release()
            simpleExoPlayer = null
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(requireContext())
        }

        mediaDataSourceFactory = CacheDataSourceFactory(
            SpineApplication.simpleCache,
            DefaultHttpDataSourceFactory(
                Util.getUserAgent(
                    requireContext(),
                    Util.getUserAgent(requireContext(), getString(R.string.app_name))
                )
            )
        )


        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(
            Uri.parse(STORY_IMAGE+stories[counter].media_file)
        )

        simpleExoPlayer?.prepare(mediaSource, false, false)
        if (onResumeCalled) {
            simpleExoPlayer?.playWhenReady = true
        }

        storyDisplayVideo.setShutterBackgroundColor(Color.BLACK)
        storyDisplayVideo.player = simpleExoPlayer

        simpleExoPlayer?.addListener(object : Player.EventListener {
            override fun onPlayerError(error: ExoPlaybackException) {
                storyDisplayVideoProgress.hide()
                if (counter == stories.size.minus(1)) {
                    pageViewOperator?.nextPageView()
                } else {
                    storiesProgressView?.skip()
                }
            }

            override fun onLoadingChanged(isLoading: Boolean) {
                if (isLoading) {
                    storyDisplayVideoProgress.show()
                    pressTime = System.currentTimeMillis()
                    pauseCurrentStory()
                } else {
                    storyDisplayVideoProgress.hide()
                    storiesProgressView?.getProgressWithIndex(counter)
                        ?.setDuration(simpleExoPlayer?.duration ?: 8000L)
                    onVideoPrepared = true
                    resumeCurrentStory()
                }
            }
        })
    }

    private fun setUpUi() {
        val touchListener = object : OnSwipeTouchListener(requireActivity()) {
            override fun onSwipeTop() {
                selectTimeBottomsheet()
            }

            override fun onSwipeBottom() {
                Toast.makeText(activity, "onSwipeBottom", Toast.LENGTH_LONG).show()
            }

            override fun onClick(view: View) {
                when (view) {
                    next -> {
                        if (counter == stories.size - 1) {
                            pageViewOperator?.nextPageView()
                        } else {
                            storiesProgressView?.skip()
                        }
                    }
                    previous -> {
                        if (counter == 0) {
                            pageViewOperator?.backPageView()
                        } else {
                            storiesProgressView?.reverse()
                        }
                    }
                }
            }

            override fun onLongClick() {
                hideStoryOverlay()
            }

            override fun onTouchView(view: View, event: MotionEvent): Boolean {
                super.onTouchView(view, event)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        pressTime = System.currentTimeMillis()
                        pauseCurrentStory()
                        return false
                    }
                    MotionEvent.ACTION_UP -> {
                        showStoryOverlay()
                        resumeCurrentStory()
                        return limit < System.currentTimeMillis() - pressTime
                    }
                }
                return false
            }
        }
        previous.setOnTouchListener(touchListener)
        next.setOnTouchListener(touchListener)

        storiesProgressView?.setStoriesCountDebug(
            stories.size, position = arguments?.getInt(EXTRA_POSITION) ?: -1
        )
        storiesProgressView?.setAllStoryDuration(4000L)
        storiesProgressView?.setStoriesListener(this)
        val profilePic=storyUser.profilePic
        Glide.with(this).load(profilePic).placeholder(R.drawable.ic_profile).circleCrop().into(civProfilePic)
        storyUserName=storyUser.displayName ?: storyUser.name
        tvProfilename.text = storyUser.displayName ?: storyUser.name
    }

    private fun showStoryOverlay() {
        if (storyOverlay == null || storyOverlay.alpha != 0F) return

        storyOverlay.animate()
            .setDuration(100)
            .alpha(1F)
            .start()
    }

    private fun hideStoryOverlay() {
        if (storyOverlay == null || storyOverlay.alpha != 1F) return

        storyOverlay.animate()
            .setDuration(200)
            .alpha(0F)
            .start()
    }

    private fun savePosition(pos: Int) {
        ViewStoryActivity.progressState.put(position, pos)
    }

    private fun restorePosition(): Int {
        return ViewStoryActivity.progressState.get(position)
    }

    fun pauseCurrentStory() {
        simpleExoPlayer?.playWhenReady = false
        storiesProgressView?.pause()
    }

    fun resumeCurrentStory() {
        if (onResumeCalled) {
            simpleExoPlayer?.playWhenReady = true
            showStoryOverlay()
            storiesProgressView?.resume()
        }
    }

    companion object {
        val STORY_TIME_DATA="storyTimeData"
        val IS_STORY_TIME_SELECTED="isStorytimeSelected"
        val STORY_ID="storyId"
        val IS_STORY="isStory"
        private const val EXTRA_POSITION = "EXTRA_POSITION"
        private const val EXTRA_STORY_USER = "EXTRA_STORY_USER"
        fun newInstance(position: Int, story: FollwingData): StoryDisplayFragment {
            return StoryDisplayFragment().apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_POSITION, position)
                    putParcelable(EXTRA_STORY_USER, story)
                }
            }
        }
    }

    override fun onBack() {
        requireActivity().onBackPressed()
    }

    fun setTextViewDrawableColor(textView: TextView,colorID: Int=R.color.color_red) {
        for (drawable in textView.compoundDrawablesRelative) {
            if (drawable != null) {
                drawable.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(requireContext(), colorID),
                        PorterDuff.Mode.SRC_IN
                    )
            }
        }
    }

    override fun onLikeStory() {
        val storyId=stories[counter].id
        setTextViewDrawableColor(binding.tvLikeStory)
        lifecycleScope.launch {
            try {
                val res=homeRepositry.spineStoriesLike(storyId,userId)
                if (res.status){
                    "${res.message}".toast(requireContext())
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onCommentStory() {
        val storyId=stories[counter].id
        val intent =Intent(requireContext(),StoryCommentActivity::class.java)
        intent.putExtra(STORY_ID,storyId)
        startActivity(intent)
    }

    override fun onShareStory() {
        /*val storyId=stories[counter].id
        val intent =Intent(requireContext(),SelectFollowersActivity::class.java)
        intent.putExtra(STORY_ID,storyId)
        intent.putExtra(IS_STORY,true)
        startActivity(intent)*/
        sharePostBottomsheet()
    }
    private fun sharePostBottomsheet() {
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
    var selectedData: MutableList<FollowersData> = ArrayList<FollowersData>()
    val data: MutableMap<String,String> = HashMap<String,String>()
    private fun shareInsideApp() {
        val post_id=stories[counter].id
        data.put("spine_story_id", post_id)
        data.put("user_id",userId)
        var i=0
        selectedData.forEach {
            data.put("share_users_id[$i]",it.user_id)
            i++
        }
        lifecycleScope.launch {
            try {
                val res=homeRepositry.spineStoriesShare(data)
                if (res.status){
                    res.message.toast(requireContext())
                }else{
                    res.message.toast(requireContext())
                }
            }catch (e:  ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }
    override fun followerSelected(followersData: FollowersData) {
        selectedData.add(followersData)
    }
    private fun shareOutsideApp() {
        val story=stories[counter]
        val img= STORY_IMAGE +story.media_file
        val title=story.title
        val uName=storyUserName
        val appId= BuildConfig.APPLICATION_ID
        val textToShare="Hey! $uName has shared you their post from Spine.\n \n" +
                "$title \n \n $img \n \n  Check your spine app for details. If you don't have spine app click below link to download->" +
                "\n\n https://play.google.com/store/apps/details?id=$appId"
        ShareCompat.IntentBuilder
            .from(requireActivity())
            .setText(textToShare)
            .setType("text/plain")
            .setChooserTitle("Share Spine Content")
            .startChooser()
    }
    private fun shareOnWhatsApp() {
        val story=stories[counter]
        val img= STORY_IMAGE +story.media_file
        val title=story.title
        val uName=storyUserName
        val appId= BuildConfig.APPLICATION_ID
        val textToShare="Hey! $uName has shared you their story from Spine.\n \n" +
                "$title \n \n $img \n \n  Check your spine app for details. If you don't have spine app click below link to download->" +
                "\n\n https://play.google.com/store/apps/details?id=$appId"
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage("com.whatsapp")
        intent.putExtra(Intent.EXTRA_TEXT, textToShare)
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.whatsapp")))
        }
    }


    private fun setFollowers() {
        lifecycleScope.launch {
            try {
                val followersRes = eventRepository.getFollowers(1, 100, userId)
                if (followersRes.status) {
                    followersDataList = followersRes.data
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }
    var followersDataList: List<FollowersData> = ArrayList<FollowersData>()
    override fun onSaveStory() {
        binding.imageButton19.setImageResource(R.drawable.ic_saved)
        binding.imageButton19.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red));
        val storyId=stories[counter].id
        lifecycleScope.launch {
            try {
                val res=homeRepositry.saveStory(userId,storyId)
                if (res.status){
                    "Story saved successfully".toast(requireContext())
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }

    }
    var postId=""
    override fun onThreeDotStory() {
        postId=stories[counter].id
        showBottomsheet()
    }

    override fun onSelectTime() {
        selectTimeBottomsheet()
    }

    override fun onStoryProfile() {
        if(userId.equals(storyUserId)){
            startActivity(Intent(requireContext(), MyProfileActivity::class.java))
        }else {
            val intent = Intent(requireContext(), SomeOneProfileActivity::class.java)
            intent.putExtra(SomeOneProfileActivity.SOME_ONES_USER_ID, storyUserId)
            //intent.putExtra(SomeOneProfileActivity.FOLLOW_STATUS, postData.followStatus)
            startActivity(intent)
        }
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
    fun showReportBottomsheet(){
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
            val radioButton=view.findViewById<RadioButton>(checkedId)
            reportTitle = radioButton.text.toString()


            when(checkedId){
                R.id.radioButton4 ->{
                    poorQualityBottomsheet()
                    dialog.dismiss()
                }
                R.id.radioButton5 ->{
                    inAppropriateBottomsheet()
                    dialog.dismiss()
                }
                R.id.radioButton6 ->{
                    voilentBottomsheet()
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    fun poorQualityBottomsheet(){
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
            val radioButton=view.findViewById<RadioButton>(checkedId)
            reportReason = radioButton.text.toString()

            when(checkedId){
                R.id.radioButton4 ->{


                }
                R.id.radioButton5 ->{


                }
                R.id.radioButton6 ->{


                }
                R.id.radioButton7 ->{


                }
                R.id.radioButton8 ->{


                }
            }
        }

        dialog.show()
    }
    fun inAppropriateBottomsheet(){
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
            val radioButton=view.findViewById<RadioButton>(checkedId)
            reportReason = radioButton.text.toString()
            when(checkedId){
                R.id.radioButton4 ->{


                }
                R.id.radioButton5 ->{


                }
                R.id.radioButton6 ->{


                }
                R.id.radioButton7 ->{


                }
                R.id.radioButton8 ->{


                }
            }
        }

        dialog.show()
    }
    fun voilentBottomsheet(){
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
            val radioButton=view.findViewById<RadioButton>(checkedId)
            reportReason = radioButton.text.toString()
            when(checkedId){
                R.id.radioButton4 ->{


                }
                R.id.radioButton5 ->{


                }
                R.id.radioButton6 ->{


                }
                R.id.radioButton8 ->{


                }
            }
        }

        dialog.show()
    }
    fun reportReseonBottomsheet(){
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
        dialog.textView256.text=reportTitle
        dialog.textView257.text=reportReason
        view.button91.setOnClickListener {
            //report
            reportMessage=view.editTextTextPersonName26.text.toString()
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
                val res=eventRepository.spineReportUserPostStory(userId,postId,"3",reportTitle,reportReason,reportMessage)
                if (res.status){
                    "Your request is submitted successfully".toast(requireContext())
                    reportThanksBottomsheet()
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }

    }
    fun reportThanksBottomsheet(){
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

    fun selectTimeBottomsheet(){
        val view: View = layoutInflater.inflate(R.layout.select_time_bootom_sheet, null)
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
        view.rvTimes.also {
            it.layoutManager=LinearLayoutManager(requireContext())
            it.addItemDecoration(DividerItemDecoration(context,LinearLayoutManager.VERTICAL))
            it.setHasFixedSize(true)
            it.adapter=SelectStoryTimeAdapter(storyMonthDataList,this)
        }

        dialog.show()
    }

    override fun onTimeSelected(storyMonthData: StoryMothData) {
       // "${storyMonthData.month_id}, ${storyMonthData.year}".toast(requireContext())
        val intent=Intent(requireContext(),ViewStoryActivity::class.java)
        intent.putExtra(STORY_TIME_DATA,storyMonthData)
        intent.putExtra(IS_STORY_TIME_SELECTED,true)
        startActivity(intent)
    }

}
