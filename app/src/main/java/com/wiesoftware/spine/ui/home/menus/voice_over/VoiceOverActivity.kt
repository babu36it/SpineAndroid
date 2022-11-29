package com.wiesoftware.spine.ui.home.menus.voice_over

import android.Manifest
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaRecorder
import android.os.*
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityVoiceOverBinding
import constant.StringContract
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import utils.Utils
import java.io.File
import java.io.IOException
import java.util.*


class VoiceOverActivity : AppCompatActivity(), KodeinAware, VoiceOverListner {
    override val kodein by kodein()
    lateinit var viewmodel: VoiceOverViewModel
    lateinit var binding: ActivityVoiceOverBinding
    val homeRepositry: HomeRepositry by instance()
    private val voiceOverViewModel: VoiceViewmodelFactory by instance()

    // creating a variable for media recorder object class.
    private var mediaRecorder: MediaRecorder? = null

    // creating a variable for mediaplayer class
    var mediaPlayer: MediaPlayer? = null

    // string variable is created for storing a file name
    private var mFileName: String? = null
    private var audioFileNameWithPath: String? = null
    private var timer: Timer? = Timer()
    private var timerRunnable: Runnable? = null
    val seekHandler = Handler(Looper.getMainLooper())
    var isRecording = false
    var isPlaying = false
    var voiceMessage = false
    private var permissionToRecordAccepted = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ///setContentView(R.layout.activity_voice_over)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_voice_over)
        viewmodel = ViewModelProvider(
            this@VoiceOverActivity,
            voiceOverViewModel
        ).get(VoiceOverViewModel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.voiceOverListener = this
        mediaPlayer = MediaPlayer()
        var audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

    }

    override fun onBack() {
        finish()
    }

    override fun addImageOrVideo() {

    }

    override fun onImageCLose() {

    }

    override fun onPlay() {
        Log.e("audioFileNameWithPath", audioFileNameWithPath.toString() + " -->onPreviewClick")

        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(audioFileNameWithPath)
                setOnCompletionListener(OnCompletionListener { stopRecording(true) })
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(VoiceOverActivity::class.java.getSimpleName() + ":playRecording()", "prepare() failed")
            }
        }


    }

    private fun startPlayingAudio(audioFileNameWithPath: String) {

    }

    private fun stopPlayingAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
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
            mediaRecorder?.stop();

            // below method will release
            // the media recorder class.
            mediaRecorder?.release();
            binding.imageViewPlayPause.visibility= View.VISIBLE
            binding.imageViewPause.visibility= View.GONE

        }
        binding.buttonPost.setText("Recording Stopped");
    }

    override fun onDeleteVoice() {

        // stopRecording(true)
    }

    override fun onPreviewClick() {



    }

    override fun onStartRecordings() {
        if (Utils.hasPermissions(
                this,
                *arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        ) {
            binding.imageViewPlayPause.visibility= View.GONE
            binding.imageViewPause.visibility= View.VISIBLE
            binding.imageViewStartRecord.visibility= View.GONE

            startRecording()

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    StringContract.RequestCode.RECORD
                )
            }
        }
    }

    private fun stopRecording(isCancel: Boolean) {

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

}