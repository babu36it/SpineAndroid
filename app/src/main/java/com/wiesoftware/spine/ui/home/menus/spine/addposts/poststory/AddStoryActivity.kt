package com.wiesoftware.spine.ui.home.menus.spine.addposts.poststory

import android.app.ProgressDialog
import android.content.ClipData
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wiesoftware.spine.BuildConfig
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.StoryImageCommonAdapter
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityAddStoryBinding
import com.wiesoftware.spine.ui.home.HomeActivity
import com.wiesoftware.spine.ui.home.camera.*
import com.wiesoftware.spine.ui.home.menus.spine.addposts.poststory.previewstory.PreviewStoryActivity
import com.wiesoftware.spine.util.Prefs
import com.wiesoftware.spine.util.UriPathHelper
import com.wiesoftware.spine.util.putAny
import com.wiesoftware.spine.util.toast
import kotlinx.android.synthetic.main.bottomsheet_picker.view.*
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddStoryActivity : AppCompatActivity(), AddStoryEventListener,
    SelectedImageAdapter.ImageRemoveListener, KodeinAware {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val REQUEST_TAKE_PHOTO = 1
    val GALLERY_REQ = 2
    val PERMISSION_REQUEST_CODE = 94
    val permissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    var currentPhotoPathList: MutableList<String> = ArrayList<String>()
    var photoURIList: MutableList<Uri> = ArrayList<Uri>()
    var photoUriList: MutableList<String> = ArrayList<String>()


    private var bitmaps: ArrayList<Bitmap> = arrayListOf()
    lateinit var adapter: SelectedImageAdapter
    var currentPhotoPath: String? = null
    lateinit var photoURI: Uri
    lateinit var binding: ActivityAddStoryBinding
    lateinit var viewmodel: AddStoryViewmodel
    lateinit var userid: String
    val homeRepositry: HomeRepositry by instance()
    val factory: AddStoryViewmodelFactory by instance()
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_story)
        viewmodel = ViewModelProvider(this, factory).get(AddStoryViewmodel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.addStoryEventListener = this
        bitmaps = ArrayList()
        progressDialog = ProgressDialog(this)
        homeRepositry.getUser().observe(this, androidx.lifecycle.Observer { user ->
            userid = user.users_id!!
//            userid.toast(this)
        })

        val mNameTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                binding.textView61.setText((90 - s.length).toString())

                //This sets a textview to the current length
//                binding.tvNameCounter.setText(40-s.length);
            }

            override fun afterTextChanged(s: Editable) {
            }
        }

        binding.editTextTextPersonName6.addTextChangedListener(mNameTextWatcher)


        val isFromGallery = intent.getBooleanExtra(IS_FROM_GALLERY, false)
        if (isFromGallery) {
            val photoPath = Prefs.getString(CURR_PHOTO_PATH_FROM_CAM_X, "")
            val photoUri = Prefs.getString(CURR_PHOTO_URI_FROM_CAM_X, "")
            currentPhotoPathList.add(photoPath.toString())
            photoUriList.add(photoUri.toString())
            photoURIList.add(Uri.parse(photoUri))
            photoURI = Uri.parse(photoUri)
            try {
                val mImageBitmap =
                    BitmapFactory.decodeFile(photoPath) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))
                bitmaps.add(mImageBitmap)
                binding.rvSelectedImages.also {
                    it.layoutManager = LinearLayoutManager(
                        this,
                        RecyclerView.HORIZONTAL,
                        false
                    )
                    it.setHasFixedSize(true)
                    adapter = SelectedImageAdapter(bitmaps, this)
                    it.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            Prefs.putAny(CURR_PHOTO_URI_FROM_CAM_X, "")
            Prefs.putAny(CURR_PHOTO_PATH_FROM_CAM_X, "")
        }

        setAdapter()

    }

    override fun onBack() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun setAdapter() {
        binding.rvSelectedImages.also {
            it.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
            it.setHasFixedSize(true)
            adapter = SelectedImageAdapter(bitmaps, this)
            it.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    override fun onAdd() {
        if (adapter.itemCount >= 5) {
            Toast.makeText(this, "You can not select more than 5 pictures", Toast.LENGTH_SHORT)
                .show()
            return
        }
        showPicker()
    }

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

        view.textView64.text = "Add image(s)"

        dialog.window?.let {
            it.setGravity(Gravity.BOTTOM)
            it.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(false)
        }
        view.btnCan.setOnClickListener {
            dialog.dismiss()
        }
        view.btnOnline.text = "Choose existing photo"

        view.btnFollow.visibility = View.VISIBLE
        view.btnFollow.setOnClickListener {
            if (hasPermissions(this, permissions)) {
                //dispatchTakePictureIntent()
                //startActivity(Intent(this,CustomCameraActivity::class.java))
                /*    Prefs.putAny(IS_FROM, ADD_STORY)
                    startActivity(Intent(this, CustomCameraActivity::class.java))*/

                /*ImagePicker.with(this)

                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .galleryOnly()
                    .start(Constant.REQ_PICK_IMAGE)*/
                dispatchTakePictureIntent()
            } else {
                makeRequest()
            }
            dialog.dismiss()
        }
        view.btnOnline.setOnClickListener {
            //startActivity(Intent(this, CustomCameraActivity::class.java))
            if (hasPermissions(this, permissions)) {

                /*  ImagePicker.with(this)

                      .compress(1024)
                      .maxResultSize(1080, 1080)
                      .galleryOnly()
                      .start(Constant.REQ_PICK_IMAGE)*/
                openGallery()
            } else {
                makeRequest()
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun openGallery() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "*/*"
//        startActivityForResult(intent, GALLERY_REQ)

        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, GALLERY_REQ)
    }

    override fun onPreview() {
        if (currentPhotoPath.isNullOrEmpty()) {
            "Please select image".toast(this)
            return
        }
        val allocmnt = binding.switch3.isChecked
        val storyTime = binding.switch4.isChecked
        val moto = binding.editTextTextPersonName6.text.toString()
        if (moto.isEmpty()) {
            "Please enter about story".toast(this)
            return
        }

        val storyPreview = StoryPreview(
            moto,
            allocmnt,
            storyTime,
            userid,
            currentPhotoPath!!,
            photoURI.toString(),
            currentPhotoPathList,
            photoUriList
        )
        val intent = Intent(this, PreviewStoryActivity::class.java)
        intent.putExtra("STORY_PREVIEW", storyPreview)
        startActivity(intent)
    }

    override fun onDelete() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.spine_alert))
        builder.setMessage(getString(R.string.r_u_sure_to_delete))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            bitmaps.clear()
            adapter.notifyDataSetChanged()
            binding.editTextTextPersonName6.setText("")
            binding.switch3.isChecked = false
            binding.switch4.isChecked = false
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.no) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
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

    override fun onPostStory(allowComments: Boolean) {

        if (currentPhotoPathList.size == 0) {
            Toast.makeText(this, "Please select images", Toast.LENGTH_SHORT).show()
            return
        } else if (binding.editTextTextPersonName6.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please add story title", Toast.LENGTH_SHORT).show()
            return
        }

        val imgList: MutableList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
        if (currentPhotoPathList != null && currentPhotoPathList.size > 0) {

            for (item in currentPhotoPathList.indices) {
                val file: File = File(currentPhotoPathList[item])
                var mediaType = contentResolver.getType(photoURIList[item])
                if (mediaType == null) {
                    mediaType = "image/jpeg"
                }

                val requestFile: RequestBody = RequestBody.create(
                    mediaType.toMediaTypeOrNull(),
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
        }


        var types = "1"
        val mime = getMimeType(this, photoURI)

        if (mime!!.contains("jpg", true) || mime.contains("png", true) || mime.contains(
                "jpeg",
                true
            )
        ) {
            types = "1"
        } /*else {
            types = "2"
        }
*/
        val thoughts: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            binding.editTextTextPersonName6.text.toString().trim()
        )
        val allowComments: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            allowComments.toString()
        )


        val type: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), types)
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.postAStory(
                    imgList,
                    thoughts,
                    type,
                    allowComments,
                )
                if (res.status) {
                    dismissProgressDailog()
                    Toast.makeText(this@AddStoryActivity, res.message, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@AddStoryActivity, HomeActivity::class.java))
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
                        this,
                        "${BuildConfig.APPLICATION_ID}.fileprovider",
                        it
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
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun showProgressDialog() {
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun dismissProgressDailog() {
        progressDialog.dismiss()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                currentPhotoPathList.add(currentPhotoPath!!)
                photoURIList.add(photoURI)
                photoUriList.add(photoURI.toString())

                val mImageBitmap =
                    BitmapFactory.decodeFile(currentPhotoPath) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))
                bitmaps.add(mImageBitmap)
                binding.rvSelectedImages.also {
                    it.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
                    it.setHasFixedSize(true)
                    adapter = SelectedImageAdapter(bitmaps, this)
                    it.adapter = adapter
                    adapter.notifyDataSetChanged()
                }

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
                    photoURIList.add(photoURI)
                    photoUriList.add(photoURI.toString())

                    //"path:  $currentPhotoPath ".toast(this)
                    try {
                        val mImageBitmap =
                            BitmapFactory.decodeFile(currentPhotoPath) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))
                        bitmaps.add(mImageBitmap)
                        binding.rvSelectedImages.also {
                            it.layoutManager =
                                LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
                            it.setHasFixedSize(true)
                            adapter = SelectedImageAdapter(bitmaps, this)
                            it.adapter = adapter
                            adapter.notifyDataSetChanged()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } else {
                photoURI = data?.data!!
                val uriPathHelper = UriPathHelper()
                currentPhotoPath = uriPathHelper.getPath(this, photoURI)
                currentPhotoPathList.add(currentPhotoPath!!)
                photoURIList.add(photoURI)
                photoUriList.add(photoURI.toString())

                //"path:  $currentPhotoPath ".toast(this)
                try {
                    val mImageBitmap =
                        BitmapFactory.decodeFile(currentPhotoPath) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))
                    bitmaps.add(mImageBitmap)
                    binding.rvSelectedImages.also {
                        it.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
                        it.setHasFixedSize(true)
                        adapter = SelectedImageAdapter(bitmaps, this)
                        it.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }


        }
    }

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (p in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    p
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun makeRequest() {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
    }

    override fun onImageRemoved(position: Int) {
        photoURIList.removeAt(position)
        currentPhotoPathList.removeAt(position)
        bitmaps.removeAt(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyDataSetChanged()
    }

    override fun onItemImageRemoved(position: Int, bitmaps: ArrayList<Bitmap>) {
        for (i in 0..bitmaps.size - 1) {
            photoURIList.removeAt(i)
            photoUriList.removeAt(i)
            currentPhotoPathList.removeAt(i)
            bitmaps.removeAt(i)
            adapter.notifyItemRemoved(i)
        }
    }
}

data class StoryPreview(
    var thoughts: String,
    var allowComments: Boolean,
    var story_time: Boolean,
    var userId: String,
    var currentPhotoPath: String,
    var photoURI: String,
    var currentPhotoPathList: MutableList<String>,
    var photoURIList: MutableList<String>,
) : Serializable {}