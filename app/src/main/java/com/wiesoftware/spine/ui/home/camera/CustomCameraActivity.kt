package com.wiesoftware.spine.ui.home.camera

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.FrameLayout
import com.wiesoftware.spine.R


import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.ui.home.camera.utils.FLAGS_FULLSCREEN
import java.io.File

const val IS_FROM = "isFrom"
const val POST_MEDIA = "PostMediaActivity"
const val ADD_STORY = "AddStoryActivity"
const val KEY_EVENT_ACTION = "key_event_action"
const val KEY_EVENT_EXTRA = "key_event_extra"
const val IS_FROM_GALLERY="isFromGallery"
const val CURR_PHOTO_PATH_FROM_CAM_X="photoPath"
const val CURR_PHOTO_URI_FROM_CAM_X="photoUri"
private const val IMMERSIVE_FLAG_TIMEOUT = 500L
class CustomCameraActivity : AppCompatActivity() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    private lateinit var container: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_camera)
        container = findViewById(R.id.fragment_container)
    }

    override fun onResume() {
        super.onResume()
        container.postDelayed({
            container.systemUiVisibility = FLAGS_FULLSCREEN
        }, IMMERSIVE_FLAG_TIMEOUT)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                val intent = Intent(KEY_EVENT_ACTION).apply { putExtra(KEY_EVENT_EXTRA, keyCode) }
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    companion object {
        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() } }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }
    }

}

