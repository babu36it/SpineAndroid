package com.wiesoftware.spine.ui.home.menus.spine.addposts.postmedia

import android.app.Dialog
import android.app.ProgressDialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Size
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wiesoftware.spine.BuildConfig
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.HashtagAutocompleteAdapter
import com.wiesoftware.spine.data.net.reponses.HashtagData
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityPostMediaBinding
import com.wiesoftware.spine.ui.home.HomeActivity
import com.wiesoftware.spine.ui.home.camera.CURR_PHOTO_PATH_FROM_CAM_X
import com.wiesoftware.spine.ui.home.camera.CURR_PHOTO_URI_FROM_CAM_X
import com.wiesoftware.spine.ui.home.camera.IS_FROM_GALLERY
import com.wiesoftware.spine.ui.home.menus.spine.addposts.hashtags.autosearchfrag.AutoSearchFragment
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postpreview.PostPreviewActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.poststory.SelectedImageAdapter
import com.wiesoftware.spine.util.*
import kotlinx.android.synthetic.main.activity_post_media.*
import kotlinx.android.synthetic.main.bottomsheet_picker.view.*
import kotlinx.android.synthetic.main.for_you_content_item.*
import kotlinx.coroutines.launch
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


class PostMediaActivity : AppCompatActivity(), KodeinAware, PostMediaEventListener,
    SelectedImageAdapter.ImageRemoveListener, HashtagAutocompleteAdapter.OnHashtagSelectedListener,
    AutoSearchFragment.OnHashSelectedListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    val REQUEST_TAKE_PHOTO = 1
    val GALLERY_REQ = 2
    val PERMISSION_REQUEST_CODE = 94
    val permissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )
    var bitmaps: ArrayList<Bitmap> = ArrayList<Bitmap>()
    private var adapter: SelectedImageAdapter? = null
    var currentPhotoPath: String? = null
    lateinit var photoURI: Uri
    var featuredPost = "0"

    var currentPhotoPathList: MutableList<String> = ArrayList<String>()
    var photoURIList: MutableList<Uri> = ArrayList<Uri>()
    var photoUriList: MutableList<String> = ArrayList<String>()

    lateinit var hashAdapter: HashtagAutocompleteAdapter

    override val kodein by kodein()
    val factory: PostMediaViewmodelFactory by instance()
    val homeRepositry: HomeRepositry by instance()
    lateinit var binding: ActivityPostMediaBinding
    lateinit var userId: String
    var hashtagDataList: MutableList<HashtagData> = mutableListOf()
    lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_post_media)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_media)
        val viewmodel = ViewModelProvider(this, factory).get(PostMediaViewmodel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.postMediaEventListener = this

        bitmaps = ArrayList()
        progressDialog = ProgressDialog(this)
        homeRepositry.getUser().observe(this, androidx.lifecycle.Observer { user ->
            userId = user.users_id!!
        })
        //   getHashtags()
/*
        binding.editTextTextPersonName66.setOnClickListener {
            binding.editTextTextPersonName66.isEnabled = false
            binding.button31.visibility = View.GONE
            binding.progressBar6.visibility = View.GONE
            supportFragmentManager
                .beginTransaction()
                .add(R.id.searchAuto, AutoSearchFragment(), "spine search")
                .commit()
            //openHashtagDialog()
        }
*/
/*
        binding.editTextTextPersonName66.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.editTextTextPersonName66.isEnabled = false
                binding.button31.visibility = View.GONE
                binding.progressBar6.visibility = View.GONE
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.searchAuto, AutoSearchFragment(), "spine search")
                    .commit()

                //openHashtagDialog()
            }
        }
*/

        val mNameTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.tvCounter.setText((140 - s.length).toString())
                //This sets a textview to the current length
//                binding.tvNameCounter.setText(40-s.length);
            }

            override fun afterTextChanged(s: Editable) {
            }
        }

        binding.editTextTextPersonName6.addTextChangedListener(mNameTextWatcher)


        val mHashTagTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length <= 5) {
                    val str = "(" + ((5 - s.length).toString()) + "/5 characters)"
                    binding.textView99.setText(str)
                }


                //This sets a textview to the current length
//                binding.tvNameCounter.setText(40-s.length);
            }

            override fun afterTextChanged(s: Editable) {
            }
        }

        binding.editTextTextPersonName66.addTextChangedListener(mHashTagTextWatcher)


        val isFromGallery = intent.getBooleanExtra(IS_FROM_GALLERY, false)
        if (isFromGallery) {
            val photoPath = Prefs.getString(CURR_PHOTO_PATH_FROM_CAM_X, "")
            val photoUri = Prefs.getString(CURR_PHOTO_URI_FROM_CAM_X, "")
            currentPhotoPathList.add(photoPath.toString())
            photoUriList.add(photoUri.toString())
            photoURIList.add(Uri.parse(photoUri))
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
                    adapter!!.notifyDataSetChanged()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            Prefs.putAny(CURR_PHOTO_URI_FROM_CAM_X, "")
            Prefs.putAny(CURR_PHOTO_PATH_FROM_CAM_X, "")
        }
        setAdapter()
    }

    private fun setAdapter() {
        binding.rvSelectedImages.also {
            it.layoutManager = LinearLayoutManager(
                this,
                RecyclerView.HORIZONTAL,
                false
            )
            it.setHasFixedSize(true)
            adapter = SelectedImageAdapter(bitmaps, this)
            it.adapter = adapter
            adapter!!.notifyDataSetChanged()
        }

    }

    private fun getHashtags() {
        lifecycleScope.launch {
            val hastagRes = homeRepositry.getHashtagList()
            if (hastagRes.status) {
                hashtagDataList = hastagRes.data
            }
        }
    }


    override fun onBack() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onPost() {

        if (currentPhotoPathList.size == 0) {
            Toast.makeText(this, "Please select image or video", Toast.LENGTH_SHORT).show()
            return
        } else if (binding.editTextTextPersonName6.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "please enter add capture", Toast.LENGTH_SHORT).show()
            return
        } else if (binding.editTextTextPersonName66.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter hashtags", Toast.LENGTH_SHORT).show()
            return
        } else if (binding.edtMarkFriends.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter mark friends", Toast.LENGTH_SHORT).show()
            return
        } else if (binding.edtLinkPlace.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter place link", Toast.LENGTH_SHORT).show()
            return
        }


        val imgList: MutableList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
        for (item in currentPhotoPathList.indices) {
            val file: File = File(currentPhotoPathList[item])
            var mediaType = contentResolver.getType(photoURIList[item])
            Log.e("mediaType:", "" + mediaType)
            if (mediaType == null) {
                mediaType = "image/jpeg"
            }
            val requestFile: RequestBody = RequestBody.create(
                mediaType.toMediaTypeOrNull(),
                file
            )
            val img_file: MultipartBody.Part = MultipartBody.Part.createFormData(
                "files[$item]",
                file.name,
                requestFile
            )
            imgList.add(img_file)
            Log.e("MediaList:", currentPhotoPathList[item])
        }


        val title: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            binding.editTextTextPersonName6.text.toString().trim()
        )

        val type: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            "2"
        )

        val hashtag: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            binding.editTextTextPersonName66.text.toString().trim()
        )
        val markfriends: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            binding.edtMarkFriends.text.toString().trim()
        )
        val placelink: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            edtLinkPlace.text.toString().trim()
        )


        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.userImageVideoPost(
                    title,type,hashtag,
                    markfriends,placelink,
                    imgList
                )
                if (res.status) {
                    dismissProgressDailog()
                    Toast.makeText(this@PostMediaActivity,res.message,Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@PostMediaActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            } catch (e: ApiException) {
                dismissProgressDailog()
                e.printStackTrace()
            } catch (e: NoInternetException) {
                dismissProgressDailog()
                e.printStackTrace()
            }
        }


    }

    override fun onAdd() {
        if (adapter!!.itemCount >= 5) {
            Toast.makeText(
                this,
                "You can not select more than 5 picture or videos",
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }
        showPicker()
    }

    override fun onPreview() {
        val thought = binding.editTextTextPersonName6.text.toString()
        val postPreview = PostPreview(
            selectedHashtags,
            userId,
            currentPhotoPathList,
            photoUriList,
            featuredPost,
            thought
        )
        val intent = Intent(this, PostPreviewActivity::class.java)
        intent.putExtra("POST_PREVIEW", postPreview)
        startActivity(intent)

    }

    override fun onDelete() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.spine_alert))
        builder.setMessage(getString(R.string.r_u_sure_to_delete))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            if (adapter != null) {
                photoUriList.clear()
                photoURIList.clear()
                currentPhotoPathList.clear()
                bitmaps.clear()
                adapter!!.notifyDataSetChanged()
                binding.editTextTextPersonName6.setText("")
                binding.editTextTextPersonName66.setText("")
                binding.edtMarkFriends.setText("")
                binding.edtLinkPlace.setText("")
                dialog.dismiss()
            } else {
                dialog.dismiss()
            }

        }
        builder.setNegativeButton(R.string.no) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onCheckedChange(isFeatured: Boolean) {
        featuredPost = if (isFeatured) {
            "3"
        } else {
            "0"
        }
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

        dialog.window?.let {
            it.setGravity(Gravity.BOTTOM)
            it.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(false)
        }
        view.btnCan.setOnClickListener {
            dialog.dismiss()
        }
        view.btnFollow.visibility = View.GONE
        view.btnFollow.setOnClickListener {
            if (hasPermissions(this, permissions)) {
                openCustomPicker()
                //dispatchTakePictureIntent()
            } else {
                makeRequest()
            }
            dialog.dismiss()
        }
        view.btnOnline.setOnClickListener {
            //startActivity(Intent(this, CustomCameraActivity::class.java))
            if (hasPermissions(this, permissions)) {
                openGallery()
            } else {
                makeRequest()
            }
            dialog.dismiss()
        }
        dialog.show()
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
            .limit(10) // max images can be selected (99 by default)
            .showCamera(true) // show camera or not (true by default)
            .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
            //.origin(images) // original selected images, used in multi mode
            //.exclude(images) // exclude anything that in image.getPath()
            //.excludeFiles(files) // same as exclude but using ArrayList<File>
            //.theme(R.style.CustomImagePickerTheme) // must inherit ef_BaseTheme. please refer to sample
            .enableLog(false) // disabling log
            .start(); // start image picker activity with request code
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

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                currentPhotoPathList.add(currentPhotoPath!!)
                photoURIList.add(photoURI)
                photoUriList.add(photoURI.toString())

                if (currentPhotoPath!!.endsWith(".mp4")) {
                    val bitmap = ThumbnailUtils.createVideoThumbnail(
                        File(currentPhotoPath),
                        Size(120, 120),
                        null
                    )
                    bitmaps.add(bitmap)
                } else {
                    val mImageBitmap =
                        BitmapFactory.decodeFile(currentPhotoPath) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))
                    bitmaps.add(mImageBitmap)
                }


                binding.rvSelectedImages.also {
                    it.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
                    it.setHasFixedSize(true)
                    adapter = SelectedImageAdapter(bitmaps, this)
                    it.adapter = adapter
                    adapter!!.notifyDataSetChanged()
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

                        if (currentPhotoPath!!.endsWith(".mp4")) {
                            val bitmap = ThumbnailUtils.createVideoThumbnail(
                                File(currentPhotoPath),
                                Size(120, 120),
                                null
                            )
                            bitmaps.add(bitmap)
                        } else {
                            val mImageBitmap =
                                BitmapFactory.decodeFile(currentPhotoPath) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))
                            bitmaps.add(mImageBitmap)
                        }


                        binding.rvSelectedImages.also {
                            it.layoutManager = LinearLayoutManager(
                                this,
                                RecyclerView.HORIZONTAL,
                                false
                            )
                            it.setHasFixedSize(true)
                            adapter = SelectedImageAdapter(bitmaps, this)
                            it.adapter = adapter
                            adapter!!.notifyDataSetChanged()
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


                try {

                    if (currentPhotoPath!!.endsWith(".mp4")) {

                        val bitmap = ThumbnailUtils.createVideoThumbnail(
                            File(currentPhotoPath),
                            Size(120, 120),
                            null
                        )
                        bitmaps.add(bitmap)
                    } else {
                        val mImageBitmap =
                            BitmapFactory.decodeFile(currentPhotoPath) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))
                        bitmaps.add(mImageBitmap)
                    }


                    //  val bitmap=ThumbnailUtils.createVideoThumbnail(currentPhotoPath,MediaStore.Video.Thumbnails.KIND)

                    binding.rvSelectedImages.also {
                        it.layoutManager = LinearLayoutManager(
                            this,
                            RecyclerView.HORIZONTAL,
                            false
                        )
                        it.setHasFixedSize(true)
                        adapter = SelectedImageAdapter(bitmaps, this)
                        it.adapter = adapter
                        adapter!!.notifyDataSetChanged()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }


        }
    }

    fun getRealPathFromUri(content: Uri?): String? {
        // get intent from activity and added it here
        var uri: Uri? = null
        val stringUri = uri.toString()
        return stringUri
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
        photoUriList.removeAt(position)
        currentPhotoPathList.removeAt(position)
        bitmaps.removeAt(position)
        adapter!!.notifyItemRemoved(position)
        adapter!!.notifyDataSetChanged()
    }

    override fun onItemImageRemoved(position: Int, bitmaps: ArrayList<Bitmap>) {
        TODO("Not yet implemented")
    }

    lateinit var hashtagName: TextView
    fun openHashtagDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.open_hashtag_dialog)
        hashtagName = dialog.findViewById(R.id.textView267) as TextView
        val add = dialog.findViewById(R.id.Button71) as Button
        val cancel = dialog.findViewById(R.id.imageButton70) as ImageButton
        val etSearch = dialog.findViewById(R.id.editTextTextPersonName27) as EditText
        val rvHashtagList = dialog.findViewById(R.id.rvHashtagList) as RecyclerView
        hashAdapter = HashtagAutocompleteAdapter(hashtagDataList, this)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                hashAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        rvHashtagList.also {
            it.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            it.setHasFixedSize(true)
            it.adapter = hashAdapter

        }
        add.setOnClickListener {

            dialog.dismiss()
        }
        cancel.setOnClickListener { dialog.dismiss() }


        dialog.show()
    }

    var selectedHashtags = ""
    override fun onHashtagSelected(hashtagData: HashtagData) {
        val hashtag = if (hashtagData.hash_title.contains("#")) {
            hashtagData.hash_title
        } else {
            "#" + hashtagData.hash_title
        }
        if (selectedHashtags.isEmpty()) {
            selectedHashtags = hashtag
        } else {
            selectedHashtags = selectedHashtags + "," + hashtag
        }
        hashtagName.text = selectedHashtags
        binding.editTextTextPersonName66.setText(selectedHashtags)
    }

    override fun onHashSelected(hashtagData: HashtagData) {
        binding.editTextTextPersonName66.isEnabled = true
        binding.button31.visibility = View.VISIBLE
        binding.progressBar6.visibility = View.VISIBLE
        val hashtag = if (hashtagData.hash_title.contains("#")) {
            hashtagData.hash_title
        } else {
            "#" + hashtagData.hash_title
        }
        if (selectedHashtags.isEmpty()) {
            selectedHashtags = hashtag
        } else {
            selectedHashtags = selectedHashtags + "," + hashtag
        }
        //hashtagName.text=selectedHashtags
        binding.editTextTextPersonName66.setText(selectedHashtags)

    }

    private fun showProgressDialog() {
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun dismissProgressDailog() {
        progressDialog.dismiss()
    }


    override fun onCloseFragment() {
        binding.editTextTextPersonName66.isEnabled = true
        binding.button31.visibility = View.VISIBLE
        binding.progressBar6.visibility = View.VISIBLE
    }
}

data class PostPreview(
    var selectedHashtags: String,
    var userId: String,
    var currentPhotoPathList: MutableList<String>,
    var photoURIList: MutableList<String>,
    var featuredPost: String,
    var description: String
) : Serializable {}