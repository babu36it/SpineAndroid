

package com.wiesoftware.spine.ui.home.camera.fragments

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageButton
import android.widget.TextView
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.Metadata
import androidx.camera.core.impl.VideoCaptureConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.wiesoftware.spine.R
import com.wiesoftware.spine.ui.home.camera.CustomCameraActivity
import com.wiesoftware.spine.ui.home.camera.KEY_EVENT_ACTION
import com.wiesoftware.spine.ui.home.camera.KEY_EVENT_EXTRA
import com.wiesoftware.spine.ui.home.camera.utils.ANIMATION_FAST_MILLIS
import com.wiesoftware.spine.ui.home.camera.utils.ANIMATION_SLOW_MILLIS
import com.wiesoftware.spine.ui.home.camera.utils.simulateClick
import com.wiesoftware.spine.util.toast
import kotlinx.android.synthetic.main.camera_ui_container.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.ArrayDeque
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

typealias LumaListener = (luma: Double) -> Unit


class CameraFragment : Fragment() {

    private lateinit var container: ConstraintLayout
    private lateinit var viewFinder: PreviewView
    private lateinit var outputDirectory: File
    private lateinit var broadcastManager: LocalBroadcastManager

    private var displayId: Int = -1
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private val displayManager by lazy {
        requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    private lateinit var cameraExecutor: ExecutorService

    /** Volume down button receiver used to trigger shutter */
    private val volumeDownReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getIntExtra(KEY_EVENT_EXTRA, KeyEvent.KEYCODE_UNKNOWN)) {
                // When the volume down button is pressed, simulate a shutter button click
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    val shutter = container
                            .findViewById<ImageButton>(R.id.camera_capture_button)
                    shutter.simulateClick()
                }
            }
        }
    }
    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@CameraFragment.displayId) {
                Log.d(TAG, "Rotation changed: ${view.display.rotation}")
                imageCapture?.targetRotation = view.display.rotation
                imageAnalyzer?.targetRotation = view.display.rotation
            }
        } ?: Unit
    }

    override fun onResume() {
        super.onResume()

        if (!PermissionsFragment.hasPermissions(requireContext())) {
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
                    CameraFragmentDirections.actionCameraToPermissions()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Shut down our background executor
        cameraExecutor.shutdown()

        // Unregister the broadcast receivers and listeners
        broadcastManager.unregisterReceiver(volumeDownReceiver)
        displayManager.unregisterDisplayListener(displayListener)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_camera, container, false)

    private fun setGalleryThumbnail(uri: Uri) {
        // Reference of the view that holds the gallery thumbnail
        val thumbnail = container.findViewById<ImageButton>(R.id.photo_view_button)

        // Run the operations in the view's thread
        thumbnail.post {
            thumbnail.setPadding(resources.getDimension(R.dimen.stroke_small).toInt())

            Glide.with(thumbnail)
                    .load(uri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(thumbnail)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        container = view as ConstraintLayout
        viewFinder = container.findViewById(R.id.view_finder)

        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        broadcastManager = LocalBroadcastManager.getInstance(view.context)

        // Set up the intent filter that will receive events from our main activity
        val filter = IntentFilter().apply { addAction(KEY_EVENT_ACTION) }
        broadcastManager.registerReceiver(volumeDownReceiver, filter)

        // Every time the orientation of device changes, update rotation for use cases
        displayManager.registerDisplayListener(displayListener, null)

        // Determine the output directory
        outputDirectory = CustomCameraActivity.getOutputDirectory(requireContext())

        // Wait for the views to be properly laid out
        viewFinder.post {

            // Keep track of the display in which this view is attached
            displayId = viewFinder.display.displayId

            // Build UI controls
            updateCameraUi()

            // Set up the camera and its use cases
            setUpCamera()
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Redraw the camera UI controls
        updateCameraUi()

        // Enable or disable switching between cameras
        updateCameraSwitchButton()
    }

    /** Initialize CameraX, and prepare to bind the camera use cases  */
    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {

            // CameraProvider
            cameraProvider = cameraProviderFuture.get()

            // Select lensFacing depending on the available cameras
            lensFacing = when {
                hasBackCamera() -> CameraSelector.LENS_FACING_BACK
                hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }

            // Enable or disable switching between cameras
            updateCameraSwitchButton()

            // Build and bind the camera use cases
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    /** Declare and bind preview, capture and analysis use cases */
    private fun bindCameraUseCases() {

        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
        Log.d(TAG, "Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

        val rotation = viewFinder.display.rotation

        // CameraProvider
        val cameraProvider = cameraProvider
                ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        // Preview
        preview = Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build()

        // ImageCapture
        imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                // We request aspect ratio but no resolution to match preview config, but letting
                // CameraX optimize for whatever specific resolution best fits our use cases
                .setTargetAspectRatio(screenAspectRatio)
                // Set initial target rotation, we will have to call this again if rotation changes
                // during the lifecycle of this use case
                .setTargetRotation(rotation)
                .build()

        // ImageAnalysis
        imageAnalyzer = ImageAnalysis.Builder()
                // We request aspect ratio but no resolution
                .setTargetAspectRatio(screenAspectRatio)
                // Set initial target rotation, we will have to call this again if rotation changes
                // during the lifecycle of this use case
                .setTargetRotation(rotation)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        // Values returned from our analyzer are passed to the attached listener
                        // We log image analysis results here - you should do something useful
                        // instead!
                        Log.d(TAG, "Average luminosity: $luma")
                    })
                }

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer)

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    @SuppressLint("RestrictedApi")
    private fun updateCameraUi() {

        // Remove previous UI if any
        container.findViewById<ConstraintLayout>(R.id.camera_ui_container)?.let {
            container.removeView(it)
        }

        // Inflate a new view containing all UI for controlling the camera
        val controls = View.inflate(requireContext(), R.layout.camera_ui_container, container)

        val camera_video_capture_button=controls.findViewById<ImageButton>(R.id.camera_video_capture_button)

        var isVidClicked = true
        camera_video_capture_button.setOnClickListener { v->

           /* camervie
            camera_view.startRecording(File(videoRecordingFilePath), ContextCompat.getMainExecutor(this), object: VideoCapture.OnVideoSavedCallback {
                override fun onVideoSaved(file: File) {
                    Toast.makeText(this@MainActivity, "Recording Saved", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "onVideoSaved $videoRecordingFilePath")
                }

                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                    Toast.makeText(this@MainActivity, "Recording Failed", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "onError $videoCaptureError $message")
                }
            })
            */
           /* if (isVidClicked){
                recordVideo()
                isVidClicked = false
            }else{
                videoCapture.stopRecording()
                isVidClicked = true
            }*/

        }

        controls.findViewById<ImageButton>(R.id.imageButton71).setOnClickListener {
            activity?.onBackPressed()
        }
        controls.findViewById<TextView>(R.id.textView276).setOnClickListener {
            camera_video_capture_button.visibility=View.INVISIBLE
            controls.findViewById<TextView>(R.id.textView277).setTag("off")
            (controls.findViewById<TextView>(R.id.textView276)).setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            (controls.findViewById<TextView>(R.id.textView277)).setTextColor(ContextCompat.getColor(requireContext(),R.color.text_black))
        }
        controls.findViewById<TextView>(R.id.textView277).setOnClickListener {
            //"Click button to record Video".toast(requireContext())
            camera_video_capture_button.visibility=View.VISIBLE
            controls.findViewById<TextView>(R.id.textView277).setTag("on")
            (controls.findViewById<TextView>(R.id.textView276)).setTextColor(ContextCompat.getColor(requireContext(),R.color.text_black))
            (controls.findViewById<TextView>(R.id.textView277)).setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
        }

        lifecycleScope.launch(Dispatchers.IO) {
            outputDirectory.listFiles { file ->
                EXTENSION_WHITELIST.contains(file.extension.toUpperCase(Locale.ROOT))
            }.maxOrNull()?.let {
                setGalleryThumbnail(Uri.fromFile(it))
            }
        }

        // Listener for button used to capture photo
        controls.findViewById<ImageButton>(R.id.camera_capture_button).setOnClickListener {

            val vidTag=controls.findViewById<ImageButton>(R.id.camera_capture_button).getTag()
            if (vidTag.equals("video")){
                imageCapture?.let {imageCapture ->
                    //imageCapture.
                    camera_video_capture_button.visibility=View.VISIBLE
                }
            }

           val tag= controls.findViewById<TextView>(R.id.textView277).getTag()
            if (tag.equals("on")){
                controls.findViewById<ImageButton>(R.id.camera_capture_button).setTag("video")
                //recordVideo()
            }


            // Get a stable reference of the modifiable image capture use case
            imageCapture?.let { imageCapture ->

                // Create output file to hold the image
                val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)

                // Setup image capture metadata
                val metadata = Metadata().apply {

                    // Mirror image when using the front camera
                    isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
                }

                // Create output options object which contains file + metadata
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                        .setMetadata(metadata)
                        .build()
                // Setup image capture listener which is triggered after photo has been taken
                imageCapture.takePicture(
                        outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                        Log.d(TAG, "Photo capture succeeded: $savedUri")

                        // We can only change the foreground Drawable using API level 23+ API
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            // Update the gallery thumbnail with latest picture taken
                            setGalleryThumbnail(savedUri)
                        }

                        // Implicit broadcasts will be ignored for devices running API level >= 24
                        // so if you only target API level 24+ you can remove this statement
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            requireActivity().sendBroadcast(
                                    Intent(android.hardware.Camera.ACTION_NEW_PICTURE, savedUri)
                            )
                        }

                        val mimeType = MimeTypeMap.getSingleton()
                                .getMimeTypeFromExtension(savedUri.toFile().extension)
                        MediaScannerConnection.scanFile(
                                context,
                                arrayOf(savedUri.toFile().absolutePath),
                                arrayOf(mimeType)
                        ) { _, uri ->
                            Log.d(TAG, "Image capture scanned into media store: $uri")
                        }
                    }
                })

                // We can only change the foreground Drawable using API level 23+ API
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    // Display flash animation to indicate that photo was captured
                    container.postDelayed({
                        container.foreground = ColorDrawable(Color.WHITE)
                        container.postDelayed(
                                { container.foreground = null }, ANIMATION_FAST_MILLIS)
                    }, ANIMATION_SLOW_MILLIS)
                }
            }
        }

        // Setup for button used to switch cameras
        controls.findViewById<ImageButton>(R.id.camera_switch_button).let {

            // Disable the button until the camera is set up
            it.isEnabled = false

            // Listener for button used to switch cameras. Only called if the button is enabled
            it.setOnClickListener {
                lensFacing = if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
                    CameraSelector.LENS_FACING_BACK
                } else {
                    CameraSelector.LENS_FACING_FRONT
                }
                // Re-bind use cases to update selected camera
                bindCameraUseCases()
            }
        }

        // Listener for button used to view the most recent photo
        controls.findViewById<ImageButton>(R.id.photo_view_button).setOnClickListener {
            // Only navigate when the gallery has photos
            if (true == outputDirectory.listFiles()?.isNotEmpty()) {
                Navigation.findNavController(
                        requireActivity(), R.id.fragment_container
                ).navigate(CameraFragmentDirections
                        .actionCameraToGallery(outputDirectory.absolutePath))
            }
        }
    }

    lateinit var videoCapture: VideoCapture
    @SuppressLint("RestrictedApi")
    private fun recordVideo() {
        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
        Log.d(TAG, "Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

        val rotation = viewFinder.display.rotation
        videoCapture = VideoCapture.Builder()
            .setTargetRotation(rotation)
            //.setTargetRotation(binding.previewView.display.rotation)
            .build()

        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, videoCapture, imageAnalyzer)
            preview?.setSurfaceProvider(viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
        val photoFile = createFile(outputDirectory, FILENAME, VID_EXTENSION)
        val metadata = VideoCapture.Metadata().apply {
            //isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
        }
        val outputOptions = VideoCapture.OutputFileOptions.Builder(photoFile)
            .setMetadata(metadata)
            .build()


        videoCapture.startRecording(outputOptions, cameraExecutor, object : VideoCapture.OnVideoSavedCallback {
            override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                "${outputFileResults.savedUri}".toast(requireContext())
            }

            override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {

            }
        })
    }

    /** Enabled or disabled a button to switch cameras depending on the available cameras */
    private fun updateCameraSwitchButton() {
        val switchCamerasButton = container.findViewById<ImageButton>(R.id.camera_switch_button)
        try {
            switchCamerasButton.isEnabled = hasBackCamera() && hasFrontCamera()
        } catch (exception: CameraInfoUnavailableException) {
            switchCamerasButton.isEnabled = false
        }
    }

    /** Returns true if the device has an available back camera. False otherwise */
    private fun hasBackCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    /** Returns true if the device has an available front camera. False otherwise */
    private fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }

    private class LuminosityAnalyzer(listener: LumaListener? = null) : ImageAnalysis.Analyzer {
        private val frameRateWindow = 8
        private val frameTimestamps = ArrayDeque<Long>(5)
        private val listeners = ArrayList<LumaListener>().apply { listener?.let { add(it) } }
        private var lastAnalyzedTimestamp = 0L
        var framesPerSecond: Double = -1.0
            private set

        /**
         * Used to add listeners that will be called with each luma computed
         */
        fun onFrameAnalyzed(listener: LumaListener) = listeners.add(listener)

        /**
         * Helper extension function used to extract a byte array from an image plane buffer
         */
        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }


        override fun analyze(image: ImageProxy) {
            // If there are no listeners attached, we don't need to perform analysis
            if (listeners.isEmpty()) {
                image.close()
                return
            }

            // Keep track of frames analyzed
            val currentTime = System.currentTimeMillis()
            frameTimestamps.push(currentTime)

            // Compute the FPS using a moving average
            while (frameTimestamps.size >= frameRateWindow) frameTimestamps.removeLast()
            val timestampFirst = frameTimestamps.peekFirst() ?: currentTime
            val timestampLast = frameTimestamps.peekLast() ?: currentTime
            framesPerSecond = 1.0 / ((timestampFirst - timestampLast) /
                    frameTimestamps.size.coerceAtLeast(1).toDouble()) * 1000.0


            lastAnalyzedTimestamp = frameTimestamps.first

            // Since format in ImageAnalysis is YUV, image.planes[0] contains the luminance plane
            val buffer = image.planes[0].buffer

            // Extract image data from callback object
            val data = buffer.toByteArray()

            // Convert the data into an array of pixel values ranging 0-255
            val pixels = data.map { it.toInt() and 0xFF }

            // Compute average luminance for the image
            val luma = pixels.average()

            // Call all listeners with new value
            listeners.forEach { it(luma) }

            image.close()
        }
    }

    companion object {

        private const val TAG = "CameraXSpine"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private const val VID_EXTENSION = ".mp4"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

        /** Helper function used to create a timestamped file */
        private fun createFile(baseFolder: File, format: String, extension: String) =
                File(baseFolder, SimpleDateFormat(format, Locale.US)
                        .format(System.currentTimeMillis()) + extension)
    }
}
