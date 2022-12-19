package com.wiesoftware.spine.ui.home.menus.spine.addposts.previewpost

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityPreviewPostBinding
import com.wiesoftware.spine.ui.home.HomeActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.reviewpost.PostThoughtModel
import com.wiesoftware.spine.ui.home.menus.spine.addposts.reviewpost.ReviewPostActivity
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class PreviewPostActivity : AppCompatActivity(),KodeinAware, PreviewPostEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val homeRepositry: HomeRepository by instance()
    lateinit var binding: ActivityPreviewPostBinding

    lateinit var userId:String
    lateinit var thought: String
    var featurePost: String = "0"
    var hashtag: String="."
    lateinit var colorId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_preview_post)
        val viewmodel=ViewModelProvider(this).get(PreviewPostViewModel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.previewPostEventListener=this
        getPostData()
    }

    private fun getPostData() {
        val postThoughtData=intent.getSerializableExtra(ReviewPostActivity.POST_THOUGHT_DATA) as PostThoughtModel
        userId=postThoughtData.userId
        thought=postThoughtData.thought
        featurePost=postThoughtData.featured
        hashtag=postThoughtData.hashtag
        colorId=postThoughtData.colorId

        binding.tvThought.text=thought
        binding.tvHashtag.text=hashtag
        binding.reviewLayout.setBackgroundColor(Color.parseColor(colorId))
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onPost() {

        binding.button41.visibility= View.INVISIBLE
        val uid: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), userId)
        val thoughts: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            thought
        )
        val hashtag: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            "." + hashtag
        )
        val color: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            colorId
        )
        val type: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "0")
        val multiplity: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            "0"
        )
        val featured: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            featurePost
        )
        lifecycleScope.launch {
            try {
                val res=homeRepositry.addUserPost(type,uid,thoughts,hashtag,color,multiplity,featured)
                if (res.status){
                    binding.button41.visibility= View.VISIBLE
                    "Post added successfully.".toast(this@PreviewPostActivity)
                    val intent = Intent(this@PreviewPostActivity, HomeActivity::class.java)
                    intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }catch (e: ApiException){
                e.printStackTrace()
                binding.button41.visibility= View.VISIBLE
            }catch (e: NoInternetException){
                e.printStackTrace()
                binding.button41.visibility= View.VISIBLE
            }
        }
    }
}