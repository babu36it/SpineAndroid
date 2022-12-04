package com.wiesoftware.spine.ui.home.menus.voice_over

import android.graphics.BitmapFactory
import android.media.MediaPlayer.OnPreparedListener
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Size
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityVoiceOverPreviewBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.io.IOException


class VoiceOverPreviewActivity : AppCompatActivity() , KodeinAware{
    override val kodein by kodein()
    lateinit var viewmodel: VoiceOverViewModel
    val homeRepositry: HomeRepositry by instance()
    private val voiceOverViewModel: VoiceViewmodelFactory by instance()
    lateinit var binding: ActivityVoiceOverPreviewBinding
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_over_preview)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_voice_over_preview)
        viewmodel = ViewModelProvider(
            this@VoiceOverPreviewActivity, voiceOverViewModel
        ).get(VoiceOverViewModel::class.java)
        binding.viewmodel = viewmodel

        val dataFromVoiceOverPreview = intent.getSerializableExtra("VOICE_OVER_PREVIEW") as VoiceOverActivity.VoicePreview


        setData(dataFromVoiceOverPreview)

        binding.backImageButton.setOnClickListener { finish() }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setData(dataFromVoiceOverPreview: VoiceOverActivity.VoicePreview) {
        binding.textDescription.text = dataFromVoiceOverPreview.description
        val currentPhotoPath = dataFromVoiceOverPreview.currentPathPhoto
        try {

            if (currentPhotoPath!!.endsWith(".mp4")) {

                val bitmap = ThumbnailUtils.createVideoThumbnail(
                    File(currentPhotoPath), Size(120, 120), null
                )

                binding.videoViews.visibility = View.VISIBLE
                binding.imageView.visibility = View.GONE
                playVideo(currentPhotoPath)

            } else {
                val mImageBitmap =
                    BitmapFactory.decodeFile(currentPhotoPath) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))

                binding.videoViews.visibility = View.GONE
                binding.imageView.visibility = View.VISIBLE
                binding.imageView.setImageBitmap(mImageBitmap)
            }


            //  val bitmap=ThumbnailUtils.createVideoThumbnail(currentPhotoPath,MediaStore.Video.Thumbnails.KIND)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun playVideo(currentPhotoPath: String) {
        val video: Uri =
            Uri.parse(currentPhotoPath)//"http://www.servername.com/projects/projectname/videos/1361439400.mp4"
        binding.videoViews.setVideoURI(video)
        binding.videoViews.setOnPreparedListener(OnPreparedListener { mp ->
            mp.isLooping = false
            mp.setVolume(0f,0f)
            mp.start()
        })
    }
}