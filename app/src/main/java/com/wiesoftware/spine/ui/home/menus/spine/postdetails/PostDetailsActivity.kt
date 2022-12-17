package com.wiesoftware.spine.ui.home.menus.spine.postdetails

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
import com.wiesoftware.spine.data.adapter.PostDetailsImageSlider
import com.wiesoftware.spine.data.adapter.SpineCommentDataAdapter
import com.wiesoftware.spine.data.net.reponses.FollowersData
import com.wiesoftware.spine.data.net.reponses.PostData
import com.wiesoftware.spine.data.net.reponses.SpineCommentData
import com.wiesoftware.spine.data.repo.EventRepository
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityPostDetailsBinding
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SomeOneProfileActivity
import com.wiesoftware.spine.ui.home.menus.profile.tabs.posts.PostsFragment
import com.wiesoftware.spine.ui.home.menus.profile.tabs.posts.PostsFragment.Companion.PROFILE_IMG_BASE
import com.wiesoftware.spine.ui.home.menus.spine.comment.postcomment.PostCommentActivity
import com.wiesoftware.spine.ui.home.menus.spine.comment.reply.RepliesActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.foryou.POST_ID
import com.wiesoftware.spine.ui.home.menus.spine.selectfollowers.SelectFollowersAdapter
import com.wiesoftware.spine.ui.home.menus.spine.viewmedia.ViewMediaInLargeActivity
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.hideKeyboard
import com.wiesoftware.spine.util.toast
import kotlinx.android.synthetic.main.bottomsheet_picker.view.*
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


class PostDetailsActivity : AppCompatActivity(),KodeinAware, PostDetailEventListener,
    Player.EventListener, SpineCommentDataAdapter.SpineCommentEventListener,
    SelectFollowersAdapter.FollowersEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val homeRepositry: HomeRepository by instance()
    val eventRepository: EventRepository by instance()
    lateinit var binding: ActivityPostDetailsBinding
    var cUserId="";var fUserId=""
    var postId=""
    var reportTitle="";var reportReason="";var reportMessage=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_post_details)
        val viewModel=ViewModelProvider(this).get(PostDetailViewModel::class.java)
        binding.viewModel=viewModel
        viewModel.postDetailEventListener=this
        homeRepositry.getUser().observe(this, Observer { user ->
            cUserId = user.users_id!!
            getPostData()
            setFollowers()
        })
    }

    lateinit var postData:PostData
    private fun getPostData() {
        postData=intent.getSerializableExtra(PostsFragment.POST_DATA) as PostData
        val baseImg=intent.getStringExtra(PostsFragment.BASE_POST_IMG)
        val profileImgBase=intent.getStringExtra(PROFILE_IMG_BASE)
        val postImg=baseImg+postData.files
        val profileImg=profileImgBase+postData.profilePic
        val colorId=postData.post_backround_color_id

        val imageList: MutableList<String> = mutableListOf()
        if ((!postData.multiplity.isNullOrEmpty()) && postData.multiplity.equals("1")) {
            val fList = postData.files.split(",")
            if (fList.size > 0) {
                fList.forEach { postImageItem ->
                    imageList.add(baseImg + postImageItem)
                }
                binding.imageSliders.visibility=View.VISIBLE
                val adapter=PostDetailsImageSlider(imageList)
                binding.imageSliders.setSliderAdapter(adapter)
            }


        }else{
            binding.imageSliders.visibility=View.INVISIBLE
        }


        fUserId=postData.user_id

        if (cUserId == fUserId){
            binding.button90.visibility = View.INVISIBLE
        }else{
            binding.button90.visibility = View.VISIBLE
            if(!postData.followStatus.isNullOrEmpty()) {
                if (postData.followStatus.equals("1")) {
                    binding.button90.text = getString(R.string.following)
                } else {
                    binding.button90.text = getString(R.string.follows)
                }
            }
        }


        postId=postData.id
        val accountMode=postData.accountMode
        getPostComments(postId)

        if (!accountMode.isNullOrEmpty()){
            if (accountMode.equals("1")) binding.ivBadge.visibility = View.VISIBLE else binding.ivBadge.visibility = View.GONE
        }

        Glide.with(binding.imageView4).load(profileImg).placeholder(R.drawable.ic_profile).into(
            binding.imageView4
        )
        binding.textView255.text=(postData.displayName ?:  postData.post_user_name) + " - post"
        binding.textView29.text=postData.displayName ?:  postData.post_user_name
        binding.textView31.text=postData.total_like
        binding.textView32.text=postData.total_comment
        binding.tvDescPost.text=postData.title + "\n" + postData.hashtag_ids

        if (!postData.user_like_status.isNullOrEmpty() && postData.user_like_status.equals("1")){
            setTextViewDrawableColor(binding.textView31)
        }
        if (!postData.user_save_status.isNullOrEmpty() && postData.user_save_status.equals("1")){
            binding.imageButton4.setImageResource(R.drawable.ic_saved)
        }

        binding.imageButton4.setOnClickListener {
            onPostSaved(postData)
        }

        binding.imageButton5.setOnClickListener {
            showBottomsheet()
            //onShowPopupMenu(postData)
        }
        binding.imageButton3.setOnClickListener {
           /* val intent=Intent(this, SelectFollowersActivity::class.java)
            intent.putExtra(POST_ID,postData.id)
            startActivity(intent)*/
            sharePostBottomsheet()
        }
        binding.imageButton65.setOnClickListener {
            val comment=binding.editTextTextPersonName25.text.toString()
            if (comment.isEmpty()){
                return@setOnClickListener
            }
            onPostComment(postData, comment)
        }
        binding.textView31.setOnClickListener {
            onPostLike(postData)
        }
        binding.button90.setOnClickListener {
            if (postData.followStatus.equals("1")){
                binding.button90.text=getString(R.string.follows)
                unfollowUser()
            }else{
                onRequestFollow()
                binding.button90.text=getString(R.string.following)
            }

        }
        binding.imageView5.setOnClickListener {
            viewPostInLarge(postImg)
        }
        binding.exoplayerView.setOnClickListener {
            viewPostInLarge(postImg)
        }
        if ((postData.files).isNullOrEmpty()){
            binding.tvPostColor.text=postData.title + "\n\n\n" + postData.hashtag_ids
            if((!colorId.isNullOrEmpty()) && colorId.contains("#")){
                try {
                    binding.tvPostColor.setBackgroundColor(
                        Color.parseColor(
                            colorId
                        )
                    )
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
            binding.tvPostColor.visibility=View.VISIBLE
            binding.imageView5.visibility=View.INVISIBLE
            binding.exoplayerView.visibility=View.INVISIBLE
        }else{
            if (isVideo(postImg)){
                binding.tvPostColor.visibility=View.INVISIBLE
                binding.imageView5.visibility=View.INVISIBLE
                binding.exoplayerView.visibility=View.VISIBLE
                initializePlayer(postImg)
            }else{
                binding.tvPostColor.visibility=View.INVISIBLE
                binding.imageView5.visibility=View.VISIBLE
                binding.exoplayerView.visibility=View.INVISIBLE
                Glide.with(binding.imageView5).load(postImg).placeholder(R.drawable.ic_photo).into(
                    binding.imageView5
                )
            }
        }

    }

    private fun unfollowUser() {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.unFollowUser(cUserId, fUserId)
                if (res.status){
                    "User unfollowed".toast(this@PostDetailsActivity)
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    fun onShowPopupMenu(postData: PostData) {
        val wrapper: Context = ContextThemeWrapper(this, R.style.PopupStyle)
        val popupMenu: PopupMenu = PopupMenu(wrapper, binding.imageButton5)
        popupMenu.menuInflater.inflate(R.menu.story_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_share ->
                    onPostShare(postData)
            }
            true
        })
        popupMenu.show()
    }

    private fun onPostShare(postData: PostData) {
        val img= BASE_IMAGE+postData.files
        val title=postData.title
        val hastag=postData.hashtag_ids
        val uName=postData.post_user_name
        val appId= BuildConfig.APPLICATION_ID
        val textToShare="Hey! $uName has shared you their post from Spine.\n \n" +
                "$title \n  \n $hastag \n \n $img \n \n  Check your spine app for details. If you don't have spine app click below link to download->" +
                "\n\n https://play.google.com/store/apps/details?id=$appId"
        ShareCompat.IntentBuilder
            .from(this)
            .setText(textToShare)
            .setType("text/plain")
            .setChooserTitle("Share Spine Content")
            .startChooser()
    }

    fun viewPostInLarge(url: String){
        val type= if (isVideo(url)){"1"}else{"0"}
        val intent= Intent(this, ViewMediaInLargeActivity::class.java)
        intent.putExtra(ViewMediaInLargeActivity.MEDIA_URL, url)
        intent.putExtra(ViewMediaInLargeActivity.MEDIA_TYPE, type)
        startActivity(intent)
    }

    fun isVideo(media_file: String) =
        media_file.contains(".mp4", true) ||
                media_file.contains(".mov", true)  ||
                media_file.contains(".3gp", true)  ||
                media_file.contains(".avi", true)


    override fun onBack() {
        onBackPressed()
    }

    fun onPostSaved(postData: PostData) {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.onPostSave(cUserId, postData.id)
                if (res.status){
                    binding.imageButton4.setImageResource(R.drawable.ic_saved)
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }

    }
    fun setTextViewDrawableColor(textView: TextView) {
        for (drawable in textView.compoundDrawablesRelative) {
            if (drawable != null) {
                drawable.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(this, R.color.color_red),
                        PorterDuff.Mode.SRC_IN
                    )
            }
        }
    }
    companion object{
        public lateinit var simpleExoplayer: SimpleExoPlayer
    }
    private var playbackPosition: Long = 0
    private val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(this, "spine-player")
    }

    fun initializePlayer(url: String) {
        simpleExoplayer = ExoPlayerFactory.newSimpleInstance(this)
        preparePlayer(url)
        binding.exoplayerView.player =   simpleExoplayer
        simpleExoplayer.seekTo(playbackPosition)
        simpleExoplayer.playWhenReady = false
        simpleExoplayer.addListener(this)
    }
    fun preparePlayer(videoUrl: String) {
        val uri = Uri.parse(videoUrl)
        val mediaSource = buildMediaSource(uri)
        simpleExoplayer.prepare(mediaSource)
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


    fun onRequestFollow() {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.addUserFollow(cUserId, fUserId)
                if (res.status){

                }
            }catch (e: com.wiesoftware.spine.util.ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    fun onPostLike(postData: PostData) {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.likePost(postData.id, cUserId)
                if (res.status){
                    "1 Like.".toast(this@PostDetailsActivity)
                    setTextViewDrawableColor(binding.textView31)
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

  fun onPostComment(postData: PostData, comment: String) {
        hideKeyboard()
        lifecycleScope.launch {
            try{
                val res=homeRepositry.postComment(postData.id, cUserId, "0", comment)
                if (res.status){
                    binding.editTextTextPersonName25.setText("")
                    "Your Comment is saved successfully".toast(this@PostDetailsActivity)
                    val intent=Intent(this@PostDetailsActivity, PostCommentActivity::class.java)
                    intent.putExtra(POST_ID, postData.id)
                    startActivity(intent)
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }


    private fun getPostComments(post_id: String?) {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.getSpineComments(post_id!!)
                if (res.status){
                    BASE_IMAGE =res.image
                    val commentDataList=res.data
                    binding.rvCommentsPost.also {
                        it.layoutManager=
                            LinearLayoutManager(
                                this@PostDetailsActivity,
                                RecyclerView.VERTICAL,
                                true
                            )
                        it.setHasFixedSize(true)
                        it.adapter= SpineCommentDataAdapter(
                            commentDataList,
                            this@PostDetailsActivity
                        )
                    }
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onCommentReply(spineCommentData: SpineCommentData) {
        val intent=Intent(this, RepliesActivity::class.java)
        intent.putExtra(PostCommentActivity.SPINE_COMMENT_DATA, spineCommentData)
        intent.putExtra(PostCommentActivity.COMMENT_PROFILE_IMAGE, BASE_IMAGE)
        startActivity(intent)
    }

    override fun onViewProfile(spineCommentData: SpineCommentData) {
        val userId=spineCommentData.user_id
        val intent=Intent(this, SomeOneProfileActivity::class.java)
        intent.putExtra(SomeOneProfileActivity.SOME_ONES_USER_ID, userId)
        startActivity(intent)
    }

    override fun onDeleteComment(spineCommentData: SpineCommentData) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.spine_alert))
        builder.setMessage(getString(R.string.r_u_sure_to_delete_this_comment))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            deleteComment(spineCommentData)
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.no) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun deleteComment(spineCommentData: SpineCommentData) {
        lifecycleScope.launch {
            try {
                val res= homeRepositry.spinePostCommentRemove(spineCommentData.id)
                if (res.status){
                    getPostComments(postId)
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    var followersDataList: List<FollowersData> = ArrayList<FollowersData>()
    private fun setFollowers() {
        lifecycleScope.launch {
            try {
                val followersRes = eventRepository.getFollowers(1, 100, cUserId)
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
    val data: MutableMap<String, String> = HashMap<String, String>()
    private fun shareInsideApp() {
        val post_id=postData.id
        data.put("spine_post_id", post_id)
        data.put("user_id", cUserId)
        var i=0
        selectedData.forEach {
            data.put("share_users_id[$i]", it.user_id)
            i++
        }
        lifecycleScope.launch {
            try {
                val res=homeRepositry.sharePost(data)
                if (res.status){
                    res.message.toast(this@PostDetailsActivity)
                }else{
                    "${res.message}".toast(this@PostDetailsActivity)
                }
            }catch (e: ApiException){
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
        val img= BASE_IMAGE+postData.files
        val title=postData.title
        val hastag=postData.hashtag_ids
        val uName=postData.post_user_name
        val appId= BuildConfig.APPLICATION_ID
        val textToShare="Hey! $uName has shared you their post from Spine.\n \n" +
                "$title \n  \n $hastag \n \n $img \n \n  Check your spine app for details. If you don't have spine app click below link to download->" +
                "\n\n https://play.google.com/store/apps/details?id=$appId"
        ShareCompat.IntentBuilder
            .from(this)
            .setText(textToShare)
            .setType("text/plain")
            .setChooserTitle("Share Spine Content")
            .startChooser()
    }
    private fun shareOnWhatsApp() {
        val img= BASE_IMAGE+postData.files
        val title=postData.title
        val hastag=postData.hashtag_ids
        val uName=postData.post_user_name
        val appId= BuildConfig.APPLICATION_ID
        val textToShare="Hey! $uName has shared you their post from Spine.\n \n" +
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
        view.cardView2.setOnClickListener {
            dialog.dismiss()
        }
        view.imageButton66.setOnClickListener {
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
                val res=eventRepository.spineReportUserPostStory(
                    cUserId,
                    postId,
                    "2",
                    reportTitle,
                    reportReason,
                    reportMessage
                )
                if (res.status){
                    "Your request is submitted successfully".toast(this@PostDetailsActivity)
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
        view.cardView2.setOnClickListener {
            dialog.dismiss()
        }
        view.imageButton66.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}