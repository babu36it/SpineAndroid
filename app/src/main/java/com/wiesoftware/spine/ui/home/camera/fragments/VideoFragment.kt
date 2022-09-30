package com.wiesoftware.spine.ui.home.camera.fragments

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.res.Configuration
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.animation.doOnCancel
import com.wiesoftware.spine.R
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import coil.fetch.VideoFrameUriFetcher
import coil.load
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.wiesoftware.spine.ui.home.camera.utils.*
import kotlinx.android.synthetic.main.fragment_video.*
import kotlinx.android.synthetic.main.fragment_video.btnFlash
import kotlinx.android.synthetic.main.fragment_video.btnGallery
import kotlinx.android.synthetic.main.fragment_video.btnRecordVideo
import kotlinx.android.synthetic.main.fragment_video.btnSwitchCamera
import kotlinx.android.synthetic.main.fragment_video.view.*
import kotlinx.android.synthetic.main.fragment_video.viewFinder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates


class VideoFragment : Fragment() {
    companion object {
        private const val TAG = "CameraXSpine"

        const val KEY_GRID = "sPrefGridVideo"

        private const val RATIO_4_3_VALUE = 4.0 / 3.0 // aspect ratio 4x3
        private const val RATIO_16_9_VALUE = 16.0 / 9.0 // aspect ratio 16x9
    }
    private val displayManager by lazy { requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager }


    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var preview: Preview? = null
    private var videoCapture: VideoCapture? = null

    private var displayId = -1
    private var lensFacing = CameraSelector.DEFAULT_BACK_CAMERA
    lateinit var binding: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=inflater.inflate(R.layout.fragment_video,container,false)


        return binding
    }


    private var flashMode by Delegates.observable(ImageCapture.FLASH_MODE_OFF) { _, _, new ->
        btnFlash.setImageResource(
            when (new) {
                ImageCapture.FLASH_MODE_ON -> R.drawable.ic_flash_on
                ImageCapture.FLASH_MODE_AUTO -> R.drawable.ic_flash_auto
                else -> R.drawable.ic_flash_off
            }
        )
    }

    private var hasGrid = false

    private var isTorchOn = false

    // Selector showing is recording currently active
    private var isRecording = false
    private val animateRecord by lazy {
        ObjectAnimator.ofFloat(btnRecordVideo, View.ALPHA, 1f, 0.5f).apply {
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
            doOnCancel { btnRecordVideo.alpha = 1f }
        }
    }



    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit

        @SuppressLint("UnsafeExperimentalUsageError", "RestrictedApi")
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@VideoFragment.displayId) {
                preview?.targetRotation = view.display.rotation
                videoCapture?.setTargetRotation(view.display.rotation)
            }
        } ?: Unit
    }
    private lateinit var cameraExecutor: ExecutorService
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        cameraExecutor = Executors.newSingleThreadExecutor()

        displayManager.registerDisplayListener(displayListener, null)

        binding.run {
            viewFinder.addOnAttachStateChangeListener(object :
                View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View) =
                    displayManager.registerDisplayListener(displayListener, null)

                override fun onViewAttachedToWindow(v: View) =
                    displayManager.unregisterDisplayListener(displayListener)
            })
            btnRecordVideo.setOnClickListener { recordVideo() }
            btnGallery.setOnClickListener { openPreview() }
            btnSwitchCamera.setOnClickListener { toggleCamera() }
            btnFlash.setOnClickListener { toggleFlash() }

            // This swipe gesture adds a fun gesture to switch between video and photo
           /* val swipeGestures = SwipeGestureDetector().apply {
                setSwipeCallback(left = {
                    Navigation.findNavController(view).navigate(R.id.action_video_to_camera)
                })
            }

            val gestureDetectorCompat = GestureDetector(requireContext(), swipeGestures)


            viewFinder.setOnTouchListener { _, motionEvent ->
                if (gestureDetectorCompat.onTouchEvent(motionEvent)) return@setOnTouchListener false
                return@setOnTouchListener true
            }*/
        }


    }

    /**
     * Create some initial states
     * */
    private fun initViews() {
//        binding.btnGrid.setImageResource(if (hasGrid) R.drawable.ic_grid_on else R.drawable.ic_grid_off)
//        binding.groupGridLines.visibility = if (hasGrid) View.VISIBLE else View.GONE

        //adjustInsets()
    }

    /**
     * This methods adds all necessary margins to some views based on window insets and screen orientation
     * */
    /*private fun adjustInsets() {
        activity?.window?.fitSystemWindows()
        btnRecordVideo.onWindowInsets { view, windowInsets ->
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                view.bottomMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            } else {
                view.endMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).right
            }
        }
        btnFlash.onWindowInsets { view, windowInsets ->
            view.topMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
        }
    }*/

    /**
     * Change the facing of camera
     *  toggleButton() function is an Extension function made to animate button rotation
     * */
    private fun toggleCamera() = btnSwitchCamera.toggleButton(
        flag = lensFacing == CameraSelector.DEFAULT_BACK_CAMERA,
        rotationAngle = 180f,
        firstIcon = R.drawable.ic_switch_camera,
        secondIcon = R.drawable.ic_switch_camera,
    ) {
        lensFacing = if (it) {
            CameraSelector.DEFAULT_BACK_CAMERA
        } else {
            CameraSelector.DEFAULT_FRONT_CAMERA
        }

        startCamera()
    }

    /**
     * Unbinds all the lifecycles from CameraX, then creates new with new parameters
     * */
    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        // This is the Texture View where the camera will be rendered
        val viewFinder = binding.viewFinder

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            // The display information
            val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
            // The ratio for the output image and preview
            val aspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
            // The display rotation
            val rotation = viewFinder.display.rotation

            val localCameraProvider = cameraProvider
                ?: throw IllegalStateException("Camera initialization failed.")

            // The Configuration of camera preview
            preview = Preview.Builder()
                .setTargetAspectRatio(aspectRatio) // set the camera aspect ratio
                .setTargetRotation(rotation) // set the camera rotation
                .build()

            val videoCaptureConfig = VideoCapture.DEFAULT_CONFIG.config // default config for video capture
            // The Configuration of video capture
            videoCapture = VideoCapture.Builder
                .fromConfig(videoCaptureConfig)
                .build()

            localCameraProvider.unbindAll() // unbind the use-cases before rebinding them

            try {
                // Bind all use cases to the camera with lifecycle
                camera = localCameraProvider.bindToLifecycle(
                    viewLifecycleOwner, // current lifecycle owner
                    lensFacing, // either front or back facing
                    preview, // camera preview use case
                    videoCapture, // video capture use case
                )

                // Attach the viewfinder's surface provider to preview use case
                preview?.setSurfaceProvider(viewFinder.surfaceProvider)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to bind use cases", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    /**
     *  Detecting the most suitable aspect ratio for current dimensions
     *
     *  @param width - preview width
     *  @param height - preview height
     *  @return suitable aspect ratio
     */
    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    /**
     * Navigate to PreviewFragment
     * */
    private fun openPreview() {
       // view?.let { Navigation.findNavController(it).navigate(R.id.action_video_to_preview) }
    }

    @SuppressLint("RestrictedApi")
    private fun recordVideo() {
        val localVideoCapture = videoCapture ?: throw IllegalStateException("Camera initialization failed.")

        // Options fot the output video file
        val outputOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis())
                put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                put(MediaStore.MediaColumns.RELATIVE_PATH, outputDirectory)
            }

            requireContext().contentResolver.run {
                val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

                VideoCapture.OutputFileOptions.Builder(this, contentUri, contentValues)
            }
        } else {
            File(outputDirectory).mkdirs()
            val file = File("$outputDirectory/${System.currentTimeMillis()}.mp4")

            VideoCapture.OutputFileOptions.Builder(file)
        }.build()

        if (!isRecording) {
            animateRecord.start()
            localVideoCapture.startRecording(
                outputOptions, // the options needed for the final video
                cameraExecutor, // the executor, on which the task will run
                object : VideoCapture.OnVideoSavedCallback { // the callback after recording a video
                    override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                        // Create small preview
                        outputFileResults.savedUri
                            ?.let { uri ->
                                setGalleryThumbnail(uri)
                                Log.d(TAG, "Video saved in $uri")
                            }
                            ?: setLastPictureThumbnail()
                    }

                    override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                        // This function is called if there is an error during recording process
                        animateRecord.cancel()
                        val msg = "Video capture failed: $message"
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        Log.e(TAG, msg)
                        cause?.printStackTrace()
                    }
                })
        } else {
            animateRecord.cancel()
            localVideoCapture.stopRecording()
        }
        isRecording = !isRecording
    }

    /**
     * Turns on or off the grid on the screen
     * */
    /*private fun toggleGrid() = binding.btnGrid.toggleButton(
        flag = hasGrid,
        rotationAngle = 180f,
        firstIcon = R.drawable.ic_grid_off,
        secondIcon = R.drawable.ic_grid_on
    ) { flag ->
        hasGrid = flag
        prefs.putBoolean(KEY_GRID, flag)
        binding.groupGridLines.visibility = if (flag) View.VISIBLE else View.GONE
    }*/

    /**
     * Turns on or off the flashlight
     * */
    private fun toggleFlash() = binding.btnFlash.toggleButton(
        flag = flashMode == ImageCapture.FLASH_MODE_ON,
        rotationAngle = 360f,
        firstIcon = R.drawable.ic_flash_off,
        secondIcon = R.drawable.ic_flash_on
    ) { flag ->
        isTorchOn = flag
        flashMode = if (flag) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
        camera?.cameraControl?.enableTorch(flag)
    }

    fun onPermissionGranted() {
        // Each time apps is coming to foreground the need permission check is being processed
        binding.viewFinder.let { vf ->
            vf.post {
                // Setting current display ID
                displayId = vf.display.displayId
                startCamera()
                lifecycleScope.launch(Dispatchers.IO) {
                    // Do on IO Dispatcher
                    setLastPictureThumbnail()
                }
                camera?.cameraControl?.enableTorch(isTorchOn)
            }
        }
    }


    protected fun getMedia(): List<Media> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        getMediaQPlus()
    } else {
        getMediaQMinus()
    }.reversed()

    private fun getMediaQPlus(): List<Media> {
        val items = mutableListOf<Media>()
        val contentResolver = requireContext().applicationContext.contentResolver

        contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.RELATIVE_PATH,
                MediaStore.Video.Media.DATE_TAKEN,
            ),
            null,
            null,
            "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RELATIVE_PATH)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(pathColumn)
                val date = cursor.getLong(dateColumn)

                val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)

                if (path == outputDirectory) {
                    items.add(Media(contentUri, true, date))
                }
            }
        }

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.RELATIVE_PATH,
                MediaStore.Images.Media.DATE_TAKEN,
            ),
            null,
            null,
            "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(pathColumn)
                val date = cursor.getLong(dateColumn)

                val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                if (path == outputDirectory) {
                    items.add(Media(contentUri, false, date))
                }
            }
        }
        return items
    }

    private fun getMediaQMinus(): List<Media> {
        val items = mutableListOf<Media>()

        File(outputDirectory).listFiles()?.forEach {
            val authority = requireContext().applicationContext.packageName + ".provider"
            val mediaUri = FileProvider.getUriForFile(requireContext(), authority, it)
            items.add(Media(mediaUri, it.extension == "mp4", it.lastModified()))
        }

        return items
    }


    private fun setLastPictureThumbnail() = binding.btnGallery.post {
        getMedia().firstOrNull() // check if there are any photos or videos in the app directory
            ?.let { setGalleryThumbnail(it.uri) } // preview the last one
            ?: binding.btnGallery.setImageResource(R.drawable.ic_photo) // or the default placeholder
    }

    private fun setGalleryThumbnail(savedUri: Uri?) = binding.btnGallery.let { btnGallery ->
        // Do the work on view's thread, this is needed, because the function is called in a Coroutine Scope's IO Dispatcher
        btnGallery.post {
            btnGallery.load(savedUri) {
                placeholder(R.drawable.ic_photo)
                transformations(CircleCropTransformation())
                listener(object : ImageRequest.Listener {
                    override fun onError(request: ImageRequest, throwable: Throwable) {
                        super.onError(request, throwable)
                        binding.btnGallery.load(savedUri) {
                            placeholder(R.drawable.ic_photo)
                            transformations(CircleCropTransformation())
                            fetcher(VideoFrameUriFetcher(requireContext()))
                        }
                    }
                })
            }
        }
    }

    fun onBackPressed() = requireActivity().finish()

    override fun onStop() {
        super.onStop()
        camera?.cameraControl?.enableTorch(false)
    }
    protected val outputDirectory: String by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "${Environment.DIRECTORY_DCIM}/Spine/"
        } else {
            "${requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM)?.path}/Spine/"
        }
    }
}

data class Media(
    val uri: Uri,
    val isVideo: Boolean,
    val date: Long,
)