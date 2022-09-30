package com.wiesoftware.spine.ui.home.menus.spine.addposts.poststory.previewstory

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.SpineApplication
import com.wiesoftware.spine.data.adapter.SelectStoryTimeAdapter
import com.wiesoftware.spine.data.net.reponses.StoryMothData
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityPreviewStoryBinding
import com.wiesoftware.spine.ui.home.HomeActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.poststory.StoryPreview
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE
import com.wiesoftware.spine.util.hide
import com.wiesoftware.spine.util.show
import com.wiesoftware.spine.util.toast
import kotlinx.android.synthetic.main.fragment_story_display.*
import kotlinx.android.synthetic.main.select_time_bootom_sheet.view.*
import kotlinx.android.synthetic.main.why_r_u_reporting.view.*
import kotlinx.android.synthetic.main.why_r_u_reporting.view.cardView2
import kotlinx.android.synthetic.main.why_r_u_reporting.view.imageButton66
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class PreviewStoryActivity : AppCompatActivity(),KodeinAware, PreviewStoryEventListener,
    Player.EventListener, SelectStoryTimeAdapter.OnTimeSelectedListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val homeRepositry: HomeRepositry by instance()
    lateinit var binding: ActivityPreviewStoryBinding
    var photoUri: Uri? = null
    var currentPhotoPath=""
    var thoughts="";var userid="";var allowComments="1";var story_time="1"

    var photoURIList: MutableList<Uri> = ArrayList<Uri>()
    var currentPhotoPathList: MutableList<String> = ArrayList<String>()
    var photoUriList: MutableList<String> = ArrayList<String>()

    var yearMonthDataList=ArrayList<StoryMothData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_preview_story)
        val viewmodel=ViewModelProvider(this).get(PreviewStoryViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.previewStoryEventListener=this
        addYearMonthDataList()
        getStoryPreview()
    }

    private fun addYearMonthDataList() {
        val year= Calendar.getInstance().get(Calendar.YEAR)
        yearMonthDataList.add(StoryMothData("","January","",""+year));yearMonthDataList.add(StoryMothData("","February","",""+year));yearMonthDataList.add(StoryMothData("","March","",""+year));
        yearMonthDataList.add(StoryMothData("","April","",""+year));yearMonthDataList.add(StoryMothData("","May","",""+year));yearMonthDataList.add(StoryMothData("","June","",""+year));
        yearMonthDataList.add(StoryMothData("","July","",""+year));yearMonthDataList.add(StoryMothData("","August","",""+year));yearMonthDataList.add(StoryMothData("","September","",""+year));
        yearMonthDataList.add(StoryMothData("","October","",""+year));yearMonthDataList.add(StoryMothData("","November","",""+year));yearMonthDataList.add(StoryMothData("","December","",""+year));
    }

    private fun getStoryPreview() {
        val storyPreview = intent.getSerializableExtra("STORY_PREVIEW") as StoryPreview
        Log.e("STORY_PREVIEW::",""+storyPreview)
        currentPhotoPathList=storyPreview.currentPhotoPathList
        photoUriList=storyPreview.photoURIList
        for (uri in photoUriList){
            photoURIList.add(Uri.parse(uri))
        }

        currentPhotoPath=storyPreview.currentPhotoPath
        photoUri=Uri.parse(storyPreview.photoURI)
        val mime=getMimeType(this,photoUri!!)
        thoughts=storyPreview.thoughts
        userid=storyPreview.userId
        allowComments= if (storyPreview.allowComments){ "1" }else{ "0" }
        story_time= if (storyPreview.story_time){ "1" }else{ "0" }
        binding.tvTitleStory.text=thoughts
        if (mime!!.contains("jpg",true) || mime.contains("png",true) || mime.contains("jpeg",true)){
            storyDisplayVideo.hide()
            storyDisplayVideoProgress.hide()
            storyDisplayImage.show()
            Glide.with(this).load(photoURIList[0]).into(storyDisplayImage)

        }else{
            storyDisplayVideo.show()
            storyDisplayImage.hide()
            storyDisplayVideoProgress.show()
            initializePlayer()
        }
    }

    fun getMimeType(context: Context, uri: Uri): String? {
        val extension: String?
        extension = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }
        return extension
    }


    override fun onBack() {
        onBackPressed()
    }

    override fun onStoryPost() {
        selectTimeBottomsheet()
    }

    fun onStoryPosts() {
        val imgList: MutableList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
        if (currentPhotoPathList != null && currentPhotoPathList.size > 0) {

            for (item in currentPhotoPathList.indices) {
                val file: File = File(currentPhotoPathList[item])
                val requestFile: RequestBody = RequestBody.create(
                    contentResolver.getType(photoURIList[item])?.let { it.toMediaTypeOrNull() },
                    file
                )
                val img_file: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "media_file[$item]",
                    file.name,
                    requestFile
                )
                imgList.add(img_file)
                Log.e("MediaList:", currentPhotoPathList[item])
            }
        }else{
            "Please add image or video".toast(this)
            return
        }




        var types="1"
        val mime=getMimeType(this,photoUri!!)

        if (mime!!.contains("jpg",true) || mime.contains("png",true) || mime.contains("jpeg",true)){
            types="1"
        }else{
            types="2"
        }

        val uid: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), userid)
        val thoughts: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            thoughts
        )
        val allowComments: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            allowComments
        )
        val story_time: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            story_time
        )

        val type: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), types)
        binding.imageButton18.visibility=View.INVISIBLE
        lifecycleScope.launch {
            try {
                val res=homeRepositry.postAStory( imgList, uid, thoughts, type, allowComments, story_time  )
                if(res.status){
                    binding.imageButton18.visibility=View.VISIBLE
                    "Your story is added successfuly.".toast(this@PreviewStoryActivity)
                    startActivity(Intent(this@PreviewStoryActivity, HomeActivity::class.java))
                }
            }catch (e: Exception){
                e.printStackTrace()
                binding.imageButton18.visibility=View.VISIBLE
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

    fun initializePlayer() {
        simpleExoplayer = ExoPlayerFactory.newSimpleInstance(this)
        preparePlayer()
        binding.storyDisplayVideo.player =   simpleExoplayer
        simpleExoplayer.seekTo(playbackPosition)
        simpleExoplayer.playWhenReady = false
        simpleExoplayer.addListener(this)
    }
    fun preparePlayer() {
        //val uri = Uri.parse(videoUrl)
        val mediaSource = buildMediaSource(photoUri!!)
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
            binding.storyDisplayVideoProgress.visibility = View.VISIBLE
        else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED)
            binding.storyDisplayVideoProgress.visibility = View.INVISIBLE
    }

    lateinit var dialog: BottomSheetDialog
    fun selectTimeBottomsheet(){
        val view: View = layoutInflater.inflate(R.layout.select_time_bootom_sheet, null)
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
            dialog.setCancelable(true)
        }
        view.cardView2.setOnClickListener {
            dialog.dismiss()
        }
        view.imageButton66.setOnClickListener {
            dialog.dismiss()
        }
        view.rvTimes.also {
            it.layoutManager= LinearLayoutManager(this)
            it.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
            it.setHasFixedSize(true)
            it.adapter= SelectStoryTimeAdapter(yearMonthDataList,this)
        }

        dialog.show()
    }

    override fun onTimeSelected(storyMonthData: StoryMothData) {
        "${storyMonthData.month}".toast(this)
        dialog.dismiss()
        onStoryPosts()
    }
}
