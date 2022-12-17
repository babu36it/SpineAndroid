package com.wiesoftware.spine.ui.home.menus.profile.myprofile

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wiesoftware.spine.BuildConfig
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.ListPodcastAdapter
import com.wiesoftware.spine.data.adapter.OwnEventAdapter
import com.wiesoftware.spine.data.adapter.OwnPostAdapter
import com.wiesoftware.spine.data.net.reponses.*
import com.wiesoftware.spine.data.repo.*
import com.wiesoftware.spine.databinding.ActivityMyProfileBinding
import com.wiesoftware.spine.ui.home.menus.events.B_IMG_URL
import com.wiesoftware.spine.ui.home.menus.events.EVE_RECORD
import com.wiesoftware.spine.ui.home.menus.events.event_details.EventDetailActivity
import com.wiesoftware.spine.ui.home.menus.profile.editprofile.EditProfileActivity
import com.wiesoftware.spine.ui.home.menus.profile.follow.FollowActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.viewmedia.ViewMediaInLargeActivity
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.UriPathHelper
import com.wiesoftware.spine.util.getFbImage
import com.wiesoftware.spine.util.toast
import kotlinx.android.synthetic.main.bottomsheet_picker.view.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyProfileActivity : AppCompatActivity(), KodeinAware, MyProfileEventListener,
    OwnEventAdapter.OnEventDetailsListener, OwnPostAdapter.OwnPostSelectedListener,
    ListPodcastAdapter.OnPodEveListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    var profileImg: String = ""
    var bgImage: String = ""

    val REQUEST_TAKE_PHOTO = 1
    val GALLERY_REQ = 2
    var currentPhotoPath: String? = null
    lateinit var photoURI: Uri
    val PERMISSION_REQUEST_CODE = 94
    val permissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override val kodein by kodein()
    val factory: MyProfileViewmodelFactory by instance()
    val homeRepositry: HomeRepository by instance()
    val eventRepository: EventRepository by instance()
    val authRepository: AuthRepository by instance()
    val profileRepository: ProfileRepository by instance()
    val podcastRepository: PodcastRepository by instance()
    var userId: String = ""
    lateinit var binding: ActivityMyProfileBinding
    var followers: String = "0"
    var following: String = "0"
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_my_profile)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile)
        val viewmodel = ViewModelProvider(this, factory).get(MyProfileViewmodel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.myProfileEventListener = this
        progressDialog = ProgressDialog(this)

        viewmodel.getLoggedInUser().observe(this, Observer { user ->
            userId = user.users_id!!
            if (user.facebook_id != null) {
                val fbImg = getFbImage(user.facebook_id!!)
                Log.e("fbImg:", fbImg)
                Glide.with(this).asBitmap().load(fbImg).into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        binding.imageView19.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        binding.imageView19.setImageDrawable(placeholder)
                    }

                })
            }
            getUserDetails()
            getOwnPost()
        })


    }

    private fun getUserDetails() {
        lifecycleScope.launch {
            try {
                val res = authRepository.getUserDetails()
                if (res.status) {
                    val img = res.image
                    val profileData = res.data
                    val imgName = profileData.user_image
                    profileImg = img + imgName

                    setProfileData(profileData, img)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    private fun setProfileData(profileData: ProfileData, img: String) {
        binding.imageView19.setOnClickListener {
            val intent = Intent(this, ViewMediaInLargeActivity::class.java)
            intent.putExtra(ViewMediaInLargeActivity.MEDIA_URL, profileImg)
            intent.putExtra(ViewMediaInLargeActivity.MEDIA_TYPE, "0")
            startActivity(intent)
        }
        followers = profileData.followers_records_count
        following = profileData.following_records_count
        val displayName = profileData.display_name ?: profileData.name
        val category = profileData.categoryName
        val bgImg = profileData.bg_image
        val post = profileData.post_records_count
        val event = profileData.event_records_count
        val pod = profileData.pod_records_count
        bgImage = img + bgImg
        Glide.with(binding.imageView18)
            .load(bgImage)
            .into(binding.imageView18)

        Glide.with(this)
            .load("https://thespiritualnetwork.com/assets/upload/profile/" + profileData.user_image)
            .placeholder(R.drawable.userprofile)
            .error(R.drawable.userprofile)
            .into(binding.imageView19)

        if (profileData.account_mode.equals("1")) {
            binding.ivBadge.visibility = View.VISIBLE
        } else {
            binding.ivBadge.visibility = View.INVISIBLE
        }

        binding.textView151.text = followers
        binding.textView153.text = following
        binding.textView155.text = displayName
        binding.textView156.text = category
        binding.textView157.text = post
        binding.textView158.text = event
        binding.textView159.text = pod

        binding.imageView18.setOnClickListener {
            val intent = Intent(this, ViewMediaInLargeActivity::class.java)
            intent.putExtra(ViewMediaInLargeActivity.MEDIA_URL, bgImage)
            intent.putExtra(ViewMediaInLargeActivity.MEDIA_TYPE, "0")
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onMore() {
        showPopupMenu()
    }

    fun showPopupMenu() {
        val popupMenu: PopupMenu = PopupMenu(this, binding.imageButton26)
        popupMenu.menuInflater.inflate(R.menu.profile_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit ->
                    showPicker()
            }
            true
        })
        popupMenu.show()
    }


    override fun onEditProfile() {
        startActivity(Intent(this, EditProfileActivity::class.java))
    }

    override fun onPost() {
        setTvAndBtnColor(binding.textView157, binding.textViewPost, R.color.text_black)
        setTvAndBtnColor(binding.textView158, binding.textViewEvents, R.color.text_black_light)
        setTvAndBtnColor(binding.textView159, binding.textViewPods, R.color.text_black_light)
        getOwnPost()
        binding.rvProfileData.visibility = View.VISIBLE
    }

    var postList: MutableList<PostData> = ArrayList<PostData>()
    var adapter = OwnPostAdapter(postList, this@MyProfileActivity)
    private fun getOwnPost() {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val postRes = homeRepositry.getAllPosts(1, 200, userId, 0, 1)
                if (postRes.status) {
                    dismissProgressDailog()
//                    BASE_IMAGE =postRes.image
//                    postList = postRes.data
                    postList = arrayListOf<PostData>()
                    binding.rvProfileData.also {
                        it.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
                        it.setHasFixedSize(true)
                        adapter = OwnPostAdapter(postList, this@MyProfileActivity)
                        it.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    dismissProgressDailog()
                    Toast.makeText(this@MyProfileActivity, postRes.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: com.wiesoftware.spine.util.ApiException) {
                e.printStackTrace()
                dismissProgressDailog()
            } catch (e: NoInternetException) {
                e.printStackTrace()
                dismissProgressDailog()
            }
        }
    }


    override fun onEvent() {
        postList.clear()
        adapter.notifyDataSetChanged()
        setTvAndBtnColor(binding.textView157, binding.textViewPost, R.color.text_black_light)
        setTvAndBtnColor(binding.textView158, binding.textViewEvents, R.color.text_black)
        setTvAndBtnColor(binding.textView159, binding.textViewPods, R.color.text_black_light)
        getOwnEvents()
        binding.rvProfileData.visibility = View.VISIBLE
    }

    var evedata: MutableList<EventsRecord> = mutableListOf()
    var eveAdapter = OwnEventAdapter(evedata, this@MyProfileActivity)
    private fun getOwnEvents() {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = eventRepository.getOwnEvents()
                if (res.status) {
                    dismissProgressDailog()
//                    BASE_IMAGE=res.image
                    val evedata = res.data
                    binding.rvProfileData.also {
                        it.layoutManager =
                            LinearLayoutManager(
                                this@MyProfileActivity,
                                RecyclerView.VERTICAL,
                                false
                            )
                        it.setHasFixedSize(true)
                        eveAdapter = OwnEventAdapter(evedata, this@MyProfileActivity)
                        it.adapter = eveAdapter
                        eveAdapter.notifyDataSetChanged()
                    }
                } else {
                    dismissProgressDailog()
                    Toast.makeText(this@MyProfileActivity, res.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: com.wiesoftware.spine.util.ApiException) {
                e.printStackTrace()
                dismissProgressDailog()
            } catch (e: NoInternetException) {
                e.printStackTrace()
                dismissProgressDailog()
            }
        }
    }

    override fun onPod() {
        evedata.clear()
        eveAdapter.notifyDataSetChanged()
        postList.clear()
        binding.rvProfileData.visibility = View.VISIBLE
        adapter.notifyDataSetChanged()
        setTvAndBtnColor(binding.textView157, binding.textViewPost, R.color.text_black_light)
        setTvAndBtnColor(binding.textView158, binding.textViewEvents, R.color.text_black_light)
        setTvAndBtnColor(binding.textView159, binding.textViewPods, R.color.text_black)
        getPods()
    }

    var podData: List<PodDatas> = arrayListOf()
    var podAdapter = ListPodcastAdapter(podData, this)
    private fun getPods() {

        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = podcastRepository.getAllPodcasts()
                if (res.status) {
                    dismissProgressDailog()
                    podData = res.data
                    setPodCastAdapter(podData)
                } else {
                    dismissProgressDailog()
                    Toast.makeText(this@MyProfileActivity, res.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: com.wiesoftware.spine.util.ApiException) {
                e.printStackTrace()
                dismissProgressDailog()
            } catch (e: NoInternetException) {
                e.printStackTrace()
                dismissProgressDailog()
            }
        }
    }

    private fun setPodCastAdapter(podData: List<PodDatas>) {
        binding.rvProfileData.also {
            it.layoutManager =
                LinearLayoutManager(
                    this@MyProfileActivity,
                    RecyclerView.VERTICAL,
                    false
                )
            it.setHasFixedSize(true)

            it.adapter = ListPodcastAdapter(podData, this)
            podAdapter.notifyDataSetChanged()
        }
    }

    override fun onFollowers() {
        val intent = Intent(Intent(this, FollowActivity::class.java))
        intent.putExtra("followers", followers)
        intent.putExtra("following", following)
        startActivity(intent)
//        startActivity(Intent(this,FollowActivity::class.java))
    }

    override fun onFollowing() {
        startActivity(Intent(this, FollowActivity::class.java))
    }

    fun setTvAndBtnColor(tv: TextView, btn: TextView, color: Int) {
        tv.setTextColor(ContextCompat.getColor(this, color))
        btn.setTextColor(ContextCompat.getColor(this, color))
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
                dispatchTakePictureIntent()
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

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
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
                binding.imageView18.setImageBitmap(mImageBitmap)
                updateProfilePic()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (requestCode == GALLERY_REQ && resultCode == RESULT_OK) {
            photoURI = data?.data!!
            val uriPathHelper = UriPathHelper()
            currentPhotoPath = uriPathHelper.getPath(this, photoURI)
            try {
                val mImageBitmap =
                    BitmapFactory.decodeFile(currentPhotoPath) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))
                binding.imageView18.setImageBitmap(mImageBitmap)
                updateProfilePic()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    private fun updateProfilePic() {
        val file: File = File(currentPhotoPath!!)
        val requestFile: RequestBody = RequestBody.create(
            contentResolver.getType(photoURI)?.let { it.toMediaTypeOrNull() },
            file
        )
        val img_file: MultipartBody.Part = MultipartBody.Part.createFormData(
            "image",
            file.name,
            requestFile
        )
        val uid: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), userId)

        lifecycleScope.launch {
            try {
                val res = profileRepository.updateUserBgProfilePic(img_file)
                if (res.status) {
                    "Profile background updated successfully.".toast(this@MyProfileActivity)
                } else {
                    "Oops! Something went wrong".toast(this@MyProfileActivity)
                }
            } catch (e: com.wiesoftware.spine.util.ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    override fun onEventDetails(ownEventData: EventsRecord) {
        val intent = Intent(this, EventDetailActivity::class.java)
        intent.putExtra(EVE_RECORD, ownEventData)
        intent.putExtra(B_IMG_URL, BASE_IMAGE)
        intent.putExtra("event_id", ownEventData.id)
        startActivity(intent)
    }

    override fun onPostSelected(postData: PostData) {
        val profileImg = BASE_IMAGE + postData.files
        if (!(postData.files).isNullOrEmpty()) {
            val type = if (isVideo(profileImg)) {
                "1"
            } else {
                "0"
            }
            val intent = Intent(this, ViewMediaInLargeActivity::class.java)
            intent.putExtra(ViewMediaInLargeActivity.MEDIA_URL, profileImg)
            intent.putExtra(ViewMediaInLargeActivity.MEDIA_TYPE, type)
            startActivity(intent)
        }
    }

    fun isVideo(media_file: String) =
        media_file.contains(".mp4", true) ||
                media_file.contains(".mov", true) ||
                media_file.contains(".3gp", true) ||
                media_file.contains(".avi", true)

    override fun onPodDetails(podcastData: PodDatas) {

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