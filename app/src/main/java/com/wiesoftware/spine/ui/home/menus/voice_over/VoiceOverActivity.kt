package com.wiesoftware.spine.ui.home.menus.voice_over

import android.Manifest
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaRecorder
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wiesoftware.spine.BuildConfig
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.ui.home.camera.CURR_PHOTO_PATH_FROM_CAM_X
import com.wiesoftware.spine.ui.home.camera.CURR_PHOTO_URI_FROM_CAM_X
import com.wiesoftware.spine.ui.home.camera.IS_FROM_GALLERY
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postmedia.PostPreview
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postpreview.PostPreviewActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.poststory.SelectedImageAdapter
import com.wiesoftware.spine.util.Prefs
import com.wiesoftware.spine.util.UriPathHelper
import com.wiesoftware.spine.util.putAny
import constant.StringContract
import kotlinx.android.synthetic.main.bottomsheet_picker.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import utils.Utils
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class VoiceOverActivity : AppCompatActivity(), KodeinAware, VoiceOverListner {
    override val kodein by kodein()
    lateinit var viewmodel: VoiceOverViewModel
    lateinit var binding: com.wiesoftware.spine.databinding.ActivityVoiceOverBinding
    val homeRepositry: HomeRepositry by instance()
    private val voiceOverViewModel: VoiceViewmodelFactory by instance()

    // creating a variable for media recorder object class.
    private var mediaRecorder: MediaRecorder? = null

    // creating a variable for mediaplayer class
    var mediaPlayer: MediaPlayer? = null

    var isRecording = false

    // string variable is created for storing a file name
    private var audioFileNameWithPath: String? = null
    val REQUEST_TAKE_PHOTO = 1
    val GALLERY_REQ = 2
    val PERMISSION_REQUEST_CODE = 110
    var bitmaps: Bitmap? = null
    private var adapter: SelectedImageAdapter? = null
    var currentPhotoPath: String? = null
    lateinit var photoURI: Uri
    var currentPhotoPathList: MutableList<String> = ArrayList<String>()
    var photoURIList: Uri? = null
    var photoUriList: String? = null
    val REPEAT_INTERVAL = 40

    private val mHandler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ///setContentView(R.layout.activity_voice_over)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_voice_over)
        viewmodel = ViewModelProvider(
            this@VoiceOverActivity, voiceOverViewModel
        ).get(VoiceOverViewModel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.voiceOverListener = this
        mediaPlayer = MediaPlayer()
        var audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        homeRepositry.getUser().observe(this, androidx.lifecycle.Observer { user ->
            viewmodel.userId = user.users_id!!
        })
    }

    override fun onBack() {
        finish()
    }

    override fun addImageOrVideo() {
        showPicker()
    }

    override fun onImageCLose() {
        photoUriList = null
        bitmaps = null
        binding.constarintImageVideo.visibility = View.GONE
    }

    override fun onPlay() {
        Log.e("audioFileNameWithPath", audioFileNameWithPath.toString() + " -->onPreviewClick")
        binding.imageViewPlayPause.visibility = View.GONE
        binding.imageViewPause.visibility = View.VISIBLE
        binding.imageViewStartRecord.visibility = View.GONE
        binding.imageViewDelete.visibility = View.VISIBLE
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(audioFileNameWithPath)
                setOnCompletionListener(OnCompletionListener { stopPlayingAudio() })
                prepare()
                start()


            } catch (e: IOException) {
                Log.e(
                    VoiceOverActivity::class.java.getSimpleName() + ":playRecording()",
                    "prepare() failed"
                )
            }
        }


    }


    private fun stopPlayingAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
        binding.imageViewPlayPause.visibility = View.VISIBLE
        binding.imageViewPause.visibility = View.GONE
        binding.imageViewStartRecord.visibility = View.GONE
        binding.imageViewDelete.visibility = View.VISIBLE

    }


    private fun startRecording() {
        val uuid = UUID.randomUUID().toString()
        audioFileNameWithPath = externalCacheDir!!.absolutePath + "/" + uuid + ".3gp"
        Log.i(VoiceOverActivity::class.java.getSimpleName(), audioFileNameWithPath!!)

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(audioFileNameWithPath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            try {
                prepare()
                isRecording = true

            } catch (e: IOException) {
                Log.e(
                    VoiceOverActivity::class.java.getSimpleName() + ":startRecording()",
                    "prepare() failed"
                )
            }
        }
        mediaRecorder?.start()

    }

    override fun onPaused() {

        Log.e("audioFileNameWithPath", audioFileNameWithPath.toString() + " -->onPaused")
        // binding. voiceMessageLayout!!.visibility = View.GONE
        if (mediaRecorder != null) {
            // mediaRecorder?.stop();

            // below method will release
            // the media recorder class.
            mediaRecorder?.release();
            mediaRecorder = null
            binding.imageViewPlayPause.visibility = View.VISIBLE
            binding.imageViewPause.visibility = View.GONE
            binding.imageViewStartRecord.visibility = View.GONE
            binding.imageViewDelete.visibility = View.VISIBLE

        }
        binding.buttonPost.setText("Recording onPaused");
    }

    override fun onDeleteVoice() {
        deleteRecording(true)
        binding.imageViewPlayPause.visibility = View.GONE
        binding.imageViewPause.visibility = View.GONE
        binding.imageViewStartRecord.visibility = View.VISIBLE
        binding.imageViewDelete.visibility = View.GONE

    }

    override fun onPreviewClick() {
        val postPreview = VoicePreview(
            "selectedHashtags",
            viewmodel.userId,
            currentPhotoPathList,
            photoUriList.toString(),
            currentPhotoPath!!,
            binding.editTextCapture.text.toString()
        )
        val intent = Intent(this, VoiceOverPreviewActivity::class.java)
        intent.putExtra("VOICE_OVER_PREVIEW", postPreview)
        startActivity(intent)


    }

    override fun onStartRecordings() {
        if (Utils.hasPermissions(
                this, *arrayOf(
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        ) {
            binding.imageViewPlayPause.visibility = View.GONE
            binding.imageViewPause.visibility = View.VISIBLE
            binding.imageViewStartRecord.visibility = View.GONE
            binding.imageViewDelete.visibility = View.VISIBLE

            startRecording()

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), StringContract.RequestCode.RECORD
                )
            }
        }
    }

    private fun deleteRecording(isCancel: Boolean) {

        try {
            if (mediaRecorder != null) {
                mediaRecorder!!.stop()
                mediaRecorder!!.release()
                mediaRecorder = null
                if (isCancel) {
                    File(audioFileNameWithPath).delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer?.release()
    }

    /*var updateVisualizer: Runnable = object : Runnable {
        override fun run() {
            if (isRecording) // if we are already recording
            {
                // get the current amplitude
                val x: Int? = mediaRecorder?.getMaxAmplitude()
                binding.seekBar. // update the VisualizeView
                visualizerView.invalidate() // refresh the VisualizerView

                // update in 40 milliseconds
                handler.postDelayed(this, REPEAT_INTERVAL)
            }
        }
    }*/

    private fun showPicker() {
        val view: View = layoutInflater.inflate(R.layout.bottomsheet_picker, null)
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
        view.btnFollow.visibility = View.VISIBLE
        view.btnFollow.setOnClickListener {
            if (Utils.hasPermissions(
                    this, *arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            ) {
                //openCustomPicker()
                dispatchTakePictureIntent()
            } else {
                makeRequest()
            }
            dialog.dismiss()
        }
        view.btnOnline.setOnClickListener {
            //startActivity(Intent(this, CustomCameraActivity::class.java))
            if (Utils.hasPermissions(
                    this, *arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            ) {
                openGallery()
            } else {
                makeRequest()
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun makeRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    photoURI = FileProvider.getUriForFile(
                        this, "${BuildConfig.APPLICATION_ID}.fileprovider", it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", ".jpg", storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/* , video/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
//        val intent = Intent(
//            Intent.ACTION_GET_CONTENT,
//            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        )
//        intent.type = "image/* , video/*"
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, GALLERY_REQ)
    }

    private fun openCustomPicker() {
//    Prefs.putAny(IS_FROM, POST_MEDIA)
//    startActivity(Intent(this,CustomCameraActivity::class.java))
        ImagePicker.create(this)
            .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
            .folderMode(true) // folder mode (false by default)
            .toolbarFolderTitle("Folder") // folder selection title
            .toolbarImageTitle("Tap to select") // image selection title
            .toolbarArrowColor(Color.BLACK) // Toolbar 'up' arrow color
            .includeVideo(true) // Show video on image picker
            //.onlyVideo(onlyVideo) // include video (false by default)
            .single() // single mode
            //.multi() // multi mode (default mode)
            .limit(1) // max images can be selected (99 by default)
            .showCamera(true) // show camera or not (true by default)
            .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
            //.origin(images) // original selected images, used in multi mode
            //.exclude(images) // exclude anything that in image.getPath()
            //.excludeFiles(files) // same as exclude but using ArrayList<File>
            //.theme(R.style.CustomImagePickerTheme) // must inherit ef_BaseTheme. please refer to sample
            .enableLog(true) // disabling log
            .start() // start image picker activity with request code
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                currentPhotoPathList.add(currentPhotoPath!!)
                photoURIList = photoURI


                if (currentPhotoPath!!.endsWith(".mp4")) {
                    val bitmap = ThumbnailUtils.createVideoThumbnail(
                        File(currentPhotoPath), Size(120, 120), null
                    )
                    bitmaps = bitmap
                } else {
                    val mImageBitmap =
                        BitmapFactory.decodeFile(currentPhotoPath) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))
                    bitmaps = mImageBitmap
                }

                binding.constarintImageVideo.visibility = View.VISIBLE
                binding.imageViewPhotoOrVideo.setImageBitmap(bitmaps)
                //"{$currentPhotoPath}".toast(this)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (requestCode == GALLERY_REQ && resultCode == RESULT_OK) {

            if (data?.clipData != null) {
                val clipData: ClipData = data.clipData!!
                val count = (clipData.itemCount - 1)
                for (i in 0..count) {
                    val imgUri = clipData.getItemAt(i).uri
                    photoURI = imgUri/*data?.data!!*/
                    val uriPathHelper = UriPathHelper()
                    currentPhotoPath = uriPathHelper.getPath(this, photoURI)
                    currentPhotoPathList.add(currentPhotoPath!!)


                    photoUriList = photoURI.toString()

                    //"path:  $currentPhotoPath ".toast(this)
                    try {

                        if (currentPhotoPath!!.endsWith(".mp4")) {
                            val bitmap = ThumbnailUtils.createVideoThumbnail(
                                File(currentPhotoPath), Size(120, 120), null
                            )
                            bitmaps = bitmap
                        } else {
                            val mImageBitmap =
                                BitmapFactory.decodeFile(currentPhotoPath) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))
                            bitmaps = mImageBitmap
                        }
                        binding.constarintImageVideo.visibility = View.VISIBLE
                        binding.imageViewPhotoOrVideo.setImageBitmap(bitmaps)

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } else {
                photoURI = data?.data!!
                val uriPathHelper = UriPathHelper()
                currentPhotoPath = uriPathHelper.getPath(this, photoURI)
                currentPhotoPathList.add(currentPhotoPath!!)
                photoURIList = photoURI



                try {

                    if (currentPhotoPath!!.endsWith(".mp4")) {

                        val bitmap = ThumbnailUtils.createVideoThumbnail(
                            File(currentPhotoPath), Size(120, 120), null
                        )
                        bitmaps = bitmap
                    } else {
                        val mImageBitmap =
                            BitmapFactory.decodeFile(currentPhotoPath) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))
                        bitmaps = mImageBitmap
                    }
                    binding.constarintImageVideo.visibility = View.VISIBLE
                    binding.imageViewPhotoOrVideo.setImageBitmap(bitmaps)

                    //  val bitmap=ThumbnailUtils.createVideoThumbnail(currentPhotoPath,MediaStore.Video.Thumbnails.KIND)

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }


        }
    }

    data class VoicePreview(
        var selectedHashtags: String,
        var userId: String,
        var currentPhotoPathList: MutableList<String>,
        var photoURIList: String,
        var currentPathPhoto: String,
        var description: String
    ) : Serializable {}
}