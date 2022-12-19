package com.wiesoftware.spine.ui.home.menus.podcasts.addpodcasts

import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wiesoftware.spine.BuildConfig
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.CategoryAdapter
import com.wiesoftware.spine.data.adapter.LanguageAdapter
import com.wiesoftware.spine.data.adapter.PodcastSubcategoryAdapter
import com.wiesoftware.spine.data.net.reponses.*
import com.wiesoftware.spine.data.repo.EventRepository
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.data.repo.PodcastRepository
import com.wiesoftware.spine.data.repo.SettingsRepository
import com.wiesoftware.spine.databinding.ActivityAddPodcastBinding
import com.wiesoftware.spine.ui.home.menus.events.addevents.SpinerCatAdapter
import com.wiesoftware.spine.ui.home.menus.podcasts.addrss.AddRssActivity
import com.wiesoftware.spine.ui.home.menus.podcasts.reviewpodcast.ReviewPodcastActivity
import com.wiesoftware.spine.util.*
import kotlinx.android.synthetic.main.activity_add_podcast.*
import kotlinx.android.synthetic.main.bottomsheet_picker.view.*
import kotlinx.android.synthetic.main.eve_cat_selection.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddPodcastActivity : AppCompatActivity(), KodeinAware, AddPodcastEventListener,
    AdapterView.OnItemSelectedListener, SpinerCatAdapter.OnEveItemChecked,
    PodcastSubcategoryAdapter.OnPodSubCatSelectedListener, SpinerCatAdapter.ListValue {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    private val REQUEST_TAKE_PHOTO = 1
    private val GALLERY_REQ = 2
    val PERMISSION_REQUEST_CODE = 94
    val permissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )
    var currentPhotoPath: String? = null
    var currentThumbnailPath: String? = null
    lateinit var photoURI: Uri
    lateinit var thumbnailUri: Uri
    var parent_id = "0"

    override val kodein by kodein()
    val factory: AddPodcastViewModelFactory by instance()
    val homeRepositry: HomeRepository by instance()
    val eventRepository: EventRepository by instance()
    private val podcastRepository: PodcastRepository by instance()
    val settingsRepository: SettingsRepository by instance()
    lateinit var binding: ActivityAddPodcastBinding
    lateinit var userId: String
    var catData: List<EventCatData> = ArrayList<EventCatData>()
    var lanngData: List<LangData> = ArrayList()
    var languages: String = "0"
    var categorys: String = "0"
    var allowComment: String = "0"
    var mediafille: String = "";
    var thumbnail: String = "";

    var category: String = ""
    var categoryIds: String = ""
    var subCatIds: String = ""
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_add_podcast)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_podcast)
        val viewmodel = ViewModelProvider(this, factory).get(AddPodcastViewModel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.addPodcastEventListener = this
        progressDialog = ProgressDialog(this)

        viewmodel.getLoggedInUser().observe(this, Observer { user ->
            userId = user.users_id!!

        })

        getLanguages()
        getEventCategories("")

        getRssFeed()

    }

    private fun getSubcatgery() {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = eventRepository.getPodcastSubcategory(parent_id)
                if (res.status) {
                    dismissProgressDailog()
                    val dataList = res.data
                    binding.recyclerView9.also {
                        it.layoutManager = GridLayoutManager(this@AddPodcastActivity, 2)
                        it.setHasFixedSize(true)
                        it.adapter = PodcastSubcategoryAdapter(
                            this@AddPodcastActivity,
                            dataList,
                            this@AddPodcastActivity
                        )
                    }
                } else {
                    dismissProgressDailog()
                    Toast.makeText(this@AddPodcastActivity, res.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getRssFeed() {
        val rssLink = Prefs.getString(AddRssActivity.RSS_LINK, "")
        if (rssLink.isNullOrEmpty()) {
            startActivity(Intent(this, AddRssActivity::class.java))
        } else {
            lifecycleScope.launch {
                try {
                    showProgressDialog()
                    val rssRes = podcastRepository.getRssFeedData(rssLink)
                    if (rssRes.status) {
                        dismissProgressDailog()
                        setFeedData(rssRes.data.feed)
                    }
                } catch (e: Exception) {
                    dismissProgressDailog()
                    e.printStackTrace()
                }
            }
        }
    }

    private fun setFeedData(feed: RssFeed) {
        Glide.with(binding.imageView39)
            .load(feed.image)
            .placeholder(R.drawable.ic_photo)
            .into(binding.imageView39)
        binding.editTextTextPersonName21.setText(feed.title)
        binding.editTextTextPersonName21.isEnabled = false
        binding.editTextTextMultiLine.setText(feed.description)
        binding.editTextTextMultiLine.isEnabled = false

        mediafille = feed.link
        thumbnail = feed.image
    }


    private fun getEventCategories(value: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val catRes = eventRepository.getEventCatRes(value)
                if (catRes.status) {
                    dismissProgressDailog()
                    catData = catRes.data
                    setEventCategories(catData)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
                dismissProgressDailog()
            } catch (e: NoInternetException) {
                e.printStackTrace()
                dismissProgressDailog()
            }
        }
    }

    private fun setEventCategories(catData: List<EventCatData>) {
        val list: ArrayList<String> = ArrayList()
        list.add(getString(R.string.select))
        for (data in catData) {
            list.add(data.category_name)
        }

        val catgeoryAdapter = CategoryAdapter(this, list)
        spinnerPodCat.adapter = catgeoryAdapter
        spinnerPodCat.setSelection(0)
//        spinnerContry.prompt = getString(R.string.select)
        spinnerPodCat.gravity = Gravity.CENTER
//        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
//        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        /*with(spinnerPodCat) {
            adapter = aa
            setSelection(0, false)
            onItemSelectedListener = this@AddPodcastActivity
            prompt = getString(com.wiesoftware.spine.R.string.add_categories)
            gravity = android.view.Gravity.CENTER
        }*/
        spinnerPodCat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!list.get(position).equals(getString(R.string.select))) {
                    parent_id = catData.get(position - 1).id
                    Log.e("Parent_id", "=" + parent_id);
                    getSubcatgery()
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

    }


    private fun getLanguages() {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = settingsRepository.getLanguages()
                if (res.status) {
                    dismissProgressDailog()
                    lanngData = res.data
                    setLanguages(lanngData)
                } else {
                    dismissProgressDailog()
                    Toast.makeText(this@AddPodcastActivity, res.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                e.printStackTrace()
                dismissProgressDailog()

            } catch (e: NoInternetException) {
                e.printStackTrace()
                dismissProgressDailog()

            }
        }
    }

    override fun onBack() {
        onBackPressed()
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

    override fun onPost(titles: String, descriptions: String) {
        showProgressDialog()
        Log.e("category", category)

        /*   if (photoURI == null) {
               "Please add audio or video".toast(this); return
           }*/
        /*  if (thumbnailUri == null) {
              "Please add image".toast(this); return
          }*/

   /*     val file: File = File(currentPhotoPath!!)
        val fileThumb: File = File(currentThumbnailPath!!)*/

        if (title.isEmpty()) {
            "Please Enter Title".toast(this);return
        } else if (title.isEmpty()) {
            "Please Enter Description".toast(this);return
        } else if (languages.equals("0")) {
            "Please Select Language".toast(this);return
        } else if (parent_id.equals("")) {
            "Please Select category".toast(this);return
        }

        /* var types = "0"
         val mime = getMimeType(this, photoURI)
         if (mime!!.contains("aac", true) || mime!!.contains("m4a", true) || mime!!.contains(
                 "opus",
                 true
             ) || mime!!.contains("mp3", true) || mime.contains("acc", true) || mime.contains(
                 "wav",
                 true
             ) || mime.contains("ogg", true)
         ) {
             types = "0"
         } else {
             types = "1"
         }*/
    /*    val requestFileThumb: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), fileThumb)
        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)*/
        /*     val pod_file: MultipartBody.Part =
                 MultipartBody.Part.createFormData("media_file", file.name, requestFile)
             val thumb_file: MultipartBody.Part =
                 MultipartBody.Part.createFormData("thumbnail", fileThumb.name, requestFileThumb)*/

        val title: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), titles)
        val description: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), descriptions)
        val language: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), languages)
        val category: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), parent_id)
        val subCategory: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), subCatIds.toString())
        val mediafile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), mediafille)
        val thumbnail: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), thumbnail)
        val rssFeed: RequestBody =
            RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                Prefs.getString(AddRssActivity.RSS_LINK, "")!!
            )
        val allowcomments: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), allowComment)
        binding.button72.visibility = View.INVISIBLE
        lifecycleScope.launch {
            try {

                Log.e(
                    "podsssssss::",
                    "$titles,$descriptions,$languages,$categoryIds,$allowComment"
                )
                val res = podcastRepository.addPodcasts(
                    title,
                    description,
                    language,
                    category,
                    subCategory,
                    rssFeed,
                    allowcomments,
                    mediafile,
                    thumbnail
                )
                Log.e("pod::", "" + res)

                if (res.status) {
                    dismissProgressDailog()
                    "Podcast added successfully.".toast(this@AddPodcastActivity)
                    val intent = Intent(this@AddPodcastActivity, ReviewPodcastActivity::class.java)
                    val reviewPodData =
                        ReviewPodData(userId, languages, parent_id, subCatIds, allowComment)
                    intent.putExtra(REVIEW_POD_DATA, reviewPodData)
                    startActivity(intent)
                } else {
                    dismissProgressDailog()
                    "Oops! Server not responding.".toast(this@AddPodcastActivity)
                }

                binding.button72.visibility = View.VISIBLE
            } catch (e: ApiException) {
                e.printStackTrace()
                dismissProgressDailog()
                Log.e("pod::", "" + e.message)
                binding.button72.visibility = View.VISIBLE
            } catch (e: NoInternetException) {
                e.printStackTrace()
                dismissProgressDailog()
                Log.e("pod::", "" + e.message)
                binding.button72.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
                dismissProgressDailog()
                Log.e("pod::", "" + e.message)
                binding.button72.visibility = View.VISIBLE
            }
        }


    }

    var isPodFile = false
    override fun onAddPodcast() {
        isPodFile = true
        showPicker(1)
    }

    override fun onAddImage() {
        isPodFile = false
        showPicker(2)
    }

    override fun isAllowComment(isChecked: Boolean) {
        if (isChecked) {
            allowComment = "1"
        } else {
            allowComment = "0"
        }
    }

    override fun onSelectCategories() {
        openDialog()
    }

    var isAdditionalBtnClick = true
    override fun onAddAditionalCategory() {
        if (isAdditionalBtnClick) {
            binding.editTextTextPersonName31.visibility = View.VISIBLE
            binding.rrAddNewCategory.visibility = View.VISIBLE
            binding.button101.visibility = View.VISIBLE
            isAdditionalBtnClick = false
            binding.imageButton73.setImageResource(R.drawable.ic_minus)
        } else {
            binding.editTextTextPersonName31.visibility = View.GONE
            binding.rrAddNewCategory.visibility = View.GONE
            binding.button101.visibility = View.GONE
            isAdditionalBtnClick = true
            binding.imageButton73.setImageResource(R.drawable.ic_add_new)
        }
    }

    override fun onAddNewCategory(category: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = eventRepository.addPodcastSubcategory(parent_id, category)
                if (res.status) {
                    dismissProgressDailog()
                    binding.editTextTextPersonName31.setText("")
                    getSubcatgery()
                    Toast.makeText(this@AddPodcastActivity, res.message, Toast.LENGTH_SHORT).show()
                } else {
                    dismissProgressDailog()
                    Toast.makeText(this@AddPodcastActivity, res.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onReview(title: String, description: String) {
        //"$languages , $categoryIds , $subCatIds , $allowComment".toast(this)

        if (languages.isEmpty()) {
            "Please select language".toast(this); return
        }
        // else if (category.isEmpty()){ "Please select category".toast(this); return }
        else if (subCatIds.isEmpty()) {
            "Please select sub category".toast(this); return
        }

        binding.button72.setBackgroundResource(R.drawable.round_button_bg)
        binding.button72.setTextColor(Color.WHITE)
        val intent = Intent(this, ReviewPodcastActivity::class.java)
        val reviewPodData = ReviewPodData(userId, languages, category, subCatIds, allowComment)
        intent.putExtra(REVIEW_POD_DATA, reviewPodData)
        startActivity(intent)
    }

    fun openDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.eve_cat_selection)
        dialog.rvcats.also { rv ->
            rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            rv.setHasFixedSize(true)
            catData.let {
                rv.adapter = SpinerCatAdapter(it, this, this)
            }
        }
        val send = dialog.findViewById(R.id.button53) as Button
        val cancel = dialog.findViewById(R.id.button52) as Button
        val ic_back = dialog.findViewById(R.id.textView131) as TextView
        val edt_search_category = dialog.findViewById<EditText>(R.id.edt_search_category)
        send.setOnClickListener {
            dialog.dismiss()
        }
        edt_search_category.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var value = s.toString()
                Log.e("valueee", value.toString())

                getEventCategories(value)

                dialog.rvcats.also { rv ->

                    rv.layoutManager =
                        LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
                    rv.setHasFixedSize(true)
                    catData.let {
                        rv.adapter =
                            SpinerCatAdapter(it, this@AddPodcastActivity, this@AddPodcastActivity)
                    }
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                var s = s.toString()
                Log.e("datatat", s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        cancel.setOnClickListener {
            dialog.dismiss()
            getEventCategories(" ")
            dialog.rvcats.also { rv ->
                rv.layoutManager =
                    LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
                rv.setHasFixedSize(true)
                catData.let {
                    rv.adapter = SpinerCatAdapter(it, this@AddPodcastActivity, this)
                }
            }
        }
        ic_back.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }


    private fun setLanguages(langData: List<LangData>) {
        val list: java.util.ArrayList<String> = ArrayList()
        list.add(getString(R.string.select))
        for (data in langData) {
            list.add(data.name)
        }
        val languageAdapter = LanguageAdapter(this, list)
        spinnerContry.adapter = languageAdapter
        spinnerContry.setSelection(0)
//        spinnerContry.prompt = getString(R.string.select)
        spinnerContry.gravity = Gravity.CENTER
        spinnerContry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!list.get(position).equals(getString(R.string.select))) {
                    languages = langData.get(position).id
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

//        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
//        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        /*  with(binding.spinnerContry) {
              adapter = aa
              setSelection(0, false)
              onItemSelectedListener = this@AddPodcastActivity
              prompt = getString(R.string.select)
              gravity = Gravity.CENTER
          }*/

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //"cat: $categorys".toast(this)
        when (parent?.id) {
            R.id.spinnerPodCat -> {
                if (position > 0) {
                    categorys = catData[position].id
                }
            }
            R.id.spinnerContry -> {
                if (position > 0) {
                    languages = lanngData[position].id
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }


    private fun showPicker(i: Int) {
        val view: View
        if (i == 1) {
            view = layoutInflater.inflate(R.layout.choose_podcast_item, null)
        } else {
            view = layoutInflater.inflate(R.layout.bottomsheet_picker, null)
        }

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
            if (hasPermissions(this, permissions)) {
                if (i == 1) {
                    openGallery(1)
                } else {
                    dispatchTakePictureIntent()
                }
            } else {
                makeRequest()
            }
            dialog.dismiss()
        }
        view.btnOnline.setOnClickListener {
            //startActivity(Intent(this, CustomCameraActivity::class.java))
            if (hasPermissions(this, permissions)) {
                openGallery(2)
            } else {
                makeRequest()
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun openGallery(i: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        if (i == 1) {
            startActivityForResult(intent, 103)
        } else {
            startActivityForResult(intent, GALLERY_REQ)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                val mImageBitmap =
                    BitmapFactory.decodeFile(currentPhotoPath) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))
                currentThumbnailPath = currentPhotoPath

                binding.button76.visibility = View.INVISIBLE
                binding.ivCover.setImageBitmap(mImageBitmap)
                //"{$currentPhotoPath}".toast(this)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (requestCode == GALLERY_REQ && resultCode == RESULT_OK) {


            val uriPathHelper = UriPathHelper()
            if (isPodFile) {
                currentPhotoPath = uriPathHelper.getPath(this, photoURI)
                isPodFile = false
                photoURI = data?.data!!
            }

            //"path:  $currentPhotoPath ".toast(this)
            thumbnailUri = data?.data!!
            currentThumbnailPath = uriPathHelper.getPath(this, thumbnailUri)
            try {
                val mImageBitmap =
                    BitmapFactory.decodeFile(currentThumbnailPath) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))
                binding.button76.visibility = View.INVISIBLE
                binding.ivCover.setImageBitmap(mImageBitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        if (requestCode == 103 && resultCode == RESULT_OK) {
            photoURI = data?.data!!
            val uriPathHelper = UriPathHelper()
            currentPhotoPath = uriPathHelper.getPath(this, photoURI)
            //"path:  $currentPhotoPath ".toast(this)
            binding.textView193.text = currentPhotoPath

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

    override fun onEventItemChecked(eveCataData: EventCatData, b: Boolean) {
        if (b) {
            if (category.isEmpty()) {
                category = eveCataData.category_name
                categoryIds = eveCataData.id
            } else {
                category = category + "," + eveCataData.category_name
                categoryIds = categoryIds + "," + eveCataData.id
            }
        } else {
            categoryIds = categoryIds.replace(eveCataData.id + ",", "")
            category = category.replace(eveCataData.category_name + ",", "")
        }

        binding.tvPodCat.text = category

    }

    override fun onPodSubCatSelected(subCategoryData: PodcastSubCategoryData, isSelected: Boolean) {
        subCatIds = if (isSelected) {
            if (subCatIds.isEmpty()) {
                subCategoryData.id + ","
            } else {
                subCatIds + subCategoryData.id + ","
            }
        } else {
            val replcedata = subCategoryData.id + ","
            subCatIds.replace(replcedata, "")
        }
    }

    override fun onPodSubCatSelectedList(subCategoryData: ArrayList<String>) {

        if (subCategoryData.size > 0) {
            binding.button72.setBackgroundResource(R.drawable.round_button_bg)
            binding.button72.setTextColor(Color.WHITE)
            binding.button72.setPadding(15, 15, 15, 15)
        } else {
            binding.button72.setBackgroundResource(R.drawable.round_border)
            binding.button72.setPadding(15, 15, 15, 15)
            binding.button72.setTextColor(Color.parseColor("#B89A8A"));

        }
        subCatIds = subCategoryData.toString()
        subCatIds = subCatIds.substring(1, subCatIds.length - 1);
        subCatIds = subCatIds.replace("\\s".toRegex(), "")
        Log.e("value", subCatIds.toString())
    }

    companion object {
        public const val REVIEW_POD_DATA = "reviewPodData"
    }

    override fun onclick(Event: EventCatData) {
        parent_id = Event.id
        Log.e("datacategory", Event.category_name)
        binding.tvPodCat.text = Event.category_name
        binding.textView284.text = "Choose up to 3 sub-categories of " + Event.category_name
        category = Event.category_name
        Log.e("list", Event.id)
        getSubcatgery()
    }

    private fun showProgressDialog() {
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun dismissProgressDailog() {
        progressDialog.dismiss()
    }
}
