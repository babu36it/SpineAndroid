package com.wiesoftware.spine.ui.home.menus.podcasts.podcastdetails

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wiesoftware.spine.BuildConfig
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.net.reponses.FollowersData
import com.wiesoftware.spine.data.net.reponses.PodcastDetailsData
import com.wiesoftware.spine.data.repo.EventRepository
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.data.repo.PodcastRepository
import com.wiesoftware.spine.databinding.ActivityPodcastDetailBinding
import com.wiesoftware.spine.ui.home.menus.podcasts.watch.WatchPodcastsFragment.Companion.POD_ID
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.selectfollowers.SelectFollowersAdapter
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.POD_FILE_BASE
import com.wiesoftware.spine.util.toast
import kotlinx.android.synthetic.main.bottomsheet_picker.view.*
import kotlinx.android.synthetic.main.poor_quality_or_spam.view.button91
import kotlinx.android.synthetic.main.poor_quality_or_spam.view.button92
import kotlinx.android.synthetic.main.report_reason.*
import kotlinx.android.synthetic.main.report_reason.view.*
import kotlinx.android.synthetic.main.share_bottomsheet.*
import kotlinx.android.synthetic.main.share_bottomsheet.cardView2
import kotlinx.android.synthetic.main.share_bottomsheet.imageButton66
import kotlinx.android.synthetic.main.share_bottomsheet.view.*
import kotlinx.android.synthetic.main.why_r_u_reporting.view.radioGroup
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.HashMap


class PodcastDetailActivity : AppCompatActivity(),KodeinAware, PodcastDetailsEventListener,
    SelectFollowersAdapter.FollowersEventListener, Player.EventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    private val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(this, "spine-player")
    }

    override val kodein by kodein()
    val factory: PodcastDetailsViewmodelFactory by instance()
    val homeRepositry: HomeRepository by instance()
    val podcastRepository: PodcastRepository by instance()
    val eventRepository: EventRepository by instance()
    lateinit var binding: ActivityPodcastDetailBinding
    lateinit var userId: String
    lateinit var podId: String

    var url: String=""
    var userDisplyname=""
    var reportTitle="";var reportReason="";var reportMessage=""




    lateinit var player: SimpleExoPlayer
    private var playbackPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_podcast_detail)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_podcast_detail)
        val viewmodel=ViewModelProvider(this, factory).get(PodcastDetailsViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.podcastDetailsEventListener=this
        viewmodel.getLoggedInUser().observe(this, Observer { user ->
            userId = user.users_id!!
            userDisplyname = user.display_name ?: user.name!!
            podId = intent.getStringExtra(POD_ID)!!
            Log.e("podid",podId)
            getPodcastDetails(podId)
            setFollowers()
        })



    }


    private fun initializeExoPlayer(urllll: String) {
        player = ExoPlayerFactory.newSimpleInstance(this)
        preparePlayer(urllll)
        binding.player.player = player
        player.seekTo(playbackPosition)
        player.playWhenReady = true
        player.addListener(this)
    }
    fun preparePlayer(videoUrl: String) {
        val uri = Uri.parse(videoUrl)
        val mediaSource = buildMediaSource(uri)
        player.prepare(mediaSource)
    }

    fun buildMediaSource(uri: Uri): MediaSource {
        return  ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(uri)
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        error.printStackTrace()
    }
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_BUFFERING)
            binding.progressBar.visibility = View.VISIBLE
        else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED)
            binding.progressBar.visibility = View.INVISIBLE
    }


    private fun getPodcastDetails(podId: String) {
        lifecycleScope.launch {
            try {
                val res=podcastRepository.getPodcastDetails(podId)
                if (res.status){
                    STORY_IMAGE =res.profile_img
                    POD_FILE_BASE =res.image
                    BASE_IMAGE = POD_FILE_BASE
                    val podData=res.data
                    setPodcastDetails(podData)
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }


    private fun setPodcastDetails(podData: PodcastDetailsData) {
        binding.textView222.text=podData.title
        binding.textView223.text=podData.description
        binding.textView224.text=podData.username
        url= BASE_IMAGE+podData.media_file
        Log.e("podUrl:",url)
        initializeExoPlayer(url)

        Glide.with(binding.imageView29).load(STORY_IMAGE + podData.profile_pic).error(R.drawable.ic_profile).into(
            binding.imageView29
        )
        if(podData.user_like.equals("1")){
            binding.imageButton55.setColorFilter(ContextCompat.getColor(this, R.color.red))
        }else{
            binding.imageButton55.setColorFilter(ContextCompat.getColor(this, R.color.gray_tint))
        }
        //startAudioPlayer()
    }


    override fun onBack() {
        onBackPressed()
    }

    override fun onMore() {
        showBottomsheet()
    }

    override fun onLike() {
        onPodcastLike()
    }

    override fun onShare() {
        sharePostBottomsheet()
    }

    fun onPodcastLike() {
        lifecycleScope.launch {
            try {
                val res=podcastRepository.managePodcastLikes(userId,podId)
                if (res.status){
                    getPodcastDetails(podId)
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }


    override fun onStart() {
        super.onStart()
    }



    var followersDataList: List<FollowersData> = ArrayList<FollowersData>()
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
        dialog.cardView2.setOnClickListener {
            dialog.dismiss()
        }
        dialog.imageButton66.setOnClickListener {
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
        val img= url
        val appId= BuildConfig.APPLICATION_ID
        val textToShare="Hey! $userDisplyname has shared you their podcast from Spine.\n \n" +
                "$title  \n" +
                " \n \n $img \n \n  Check your spine app for details. If you don't have spine app click below link to download->" +
                "\n\n https://play.google.com/store/apps/details?id=$appId"
        ShareCompat.IntentBuilder
            .from(this)
            .setText(textToShare)
            .setType("text/plain")
            .setChooserTitle("Share Spine Content")
            .startChooser()
    }
    private fun shareOnWhatsApp() {
        val img= url
        val appId= BuildConfig.APPLICATION_ID
        val textToShare="Hey! $userDisplyname has shared you their podcast from Spine.\n \n" +
                "$title  \n" +
                " \n \n $img \n \n  Check your spine app for details. If you don't have spine app click below link to download->" +
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
    val data: MutableMap<String,String> = HashMap<String,String>()
    private fun shareInsideApp() {
        val post_id=podId
        data.put("user_id",userId)
        data.put("spine_podcast_id", post_id)

        var i=0
        selectedData.forEach {
            data.put("receiver_users[$i]",it.user_id)
            i++
        }
        lifecycleScope.launch {
            try {
                val res=podcastRepository.sharePodcasts(data)
                if (res.status){
                    res.message.toast(this@PodcastDetailActivity)
                }else{
                    "${res.message}".toast(this@PodcastDetailActivity)
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
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
    fun showReportBottomsheet(){
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
        dialog.cardView2.setOnClickListener {
            dialog.dismiss()
        }
        dialog.imageButton66.setOnClickListener {
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
        dialog.cardView2.setOnClickListener {
            dialog.dismiss()
        }
        dialog.imageButton66.setOnClickListener {
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
        dialog.cardView2.setOnClickListener {
            dialog.dismiss()
        }
        dialog.imageButton66.setOnClickListener {
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
        dialog.cardView2.setOnClickListener {
            dialog.dismiss()
        }
        dialog.imageButton66.setOnClickListener {
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
        val tvTitle=dialog.findViewById<TextView>(R.id.textView256)
        tvTitle?.text=reportTitle
        dialog.textView257.text=reportReason
        dialog.cardView2.setOnClickListener {
            dialog.dismiss()
        }
        dialog.imageButton66.setOnClickListener {
            dialog.dismiss()
        }
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
                val res=eventRepository.spineReportUserPostStory(userId,podId,"5",reportTitle,reportReason,reportMessage)
                if (res.status){
                    "Your request is submitted successfully".toast(this@PodcastDetailActivity)
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
        dialog.cardView2.setOnClickListener {
            dialog.dismiss()
        }
        dialog.imageButton66.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}