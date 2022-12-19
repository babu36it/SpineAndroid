package com.wiesoftware.spine.ui.home.menus.spine.addposts.postpreview

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.PreviewImageSliderAdapter
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityPostPreviewBinding
import com.wiesoftware.spine.ui.home.HomeActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postmedia.PostPreview
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File

class PostPreviewActivity : AppCompatActivity(),KodeinAware, PostPreviewEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    lateinit var binding: ActivityPostPreviewBinding
    var userId:String=""
    val homeRepositry: HomeRepository by instance()

    var selectedHashtags: String=""
    var currentPhotoPathList: MutableList<String> = mutableListOf()
    var photoURIList: MutableList<String> = mutableListOf()
    var photoURILists: MutableList<Uri> = ArrayList<Uri>()


    var featuredPost: String = ""
    var description: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_post_preview)
        val viewmodel=ViewModelProvider(this).get(PostPreviewViewModel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.postPreviewEventListener=this

        getPostData()
    }

    private fun getPostData() {
        val postData= intent.getSerializableExtra("POST_PREVIEW") as PostPreview
        userId=postData.userId
        selectedHashtags=postData.selectedHashtags
        currentPhotoPathList=postData.currentPhotoPathList
        photoURIList=postData.photoURIList
        featuredPost=postData.featuredPost
        description=postData.description
        for (uri in photoURIList){
            photoURILists.add(Uri.parse(uri))
        }

        val adapter=PreviewImageSliderAdapter(photoURILists)
        binding.imageSlider.setSliderAdapter(adapter)
        //Glide.with(this).load(photoURILists[0]).into(binding.imageView35)

        binding.textView273.text=description
        binding.textView274.text=selectedHashtags

        if(selectedHashtags.isEmpty()){
            selectedHashtags="-"
        }

        Log.e("postPreview::",""+postData)
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onPost() {

        val imgList:MutableList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
        for (item in currentPhotoPathList.indices) {
            val file: File = File(currentPhotoPathList[item])
            val requestFile: RequestBody = RequestBody.create(
                contentResolver.getType(photoURILists[item])?.let { it.toMediaTypeOrNull() },
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
        val multi: Int =  if (currentPhotoPathList.size > 1) {
            1
        } else {
            0
        }


        val uid: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), userId)
        val thoughts: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            description
        )
        val hashtag: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            selectedHashtags
        )
        val color: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "-")
        val type: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "1")
        val multiplity: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            "" + multi
        )
        val featured: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            featuredPost
        )

        binding.button95.visibility= View.INVISIBLE

        lifecycleScope.launch {
            try {
                val res=homeRepositry.addUserImgVideoPost(
                    featured,
                    type,
                    uid,
                    thoughts,
                    hashtag,
                    color,
                    multiplity,
                    imgList
                )
                if (res.status){
                    binding.button95.visibility= View.VISIBLE
                    "Post added successfully.".toast(this@PostPreviewActivity)
                    val intent = Intent(this@PostPreviewActivity, HomeActivity::class.java)
                    intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }catch (e: ApiException){
                binding.button95.visibility= View.VISIBLE
                e.printStackTrace()
            }catch (e: Exception){
                binding.button95.visibility= View.VISIBLE
                e.printStackTrace()
            }
        }
    }
}
