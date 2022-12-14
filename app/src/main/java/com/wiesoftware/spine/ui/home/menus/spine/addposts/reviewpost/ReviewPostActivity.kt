package com.wiesoftware.spine.ui.home.menus.spine.addposts.reviewpost

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityReviewPostBinding
import com.wiesoftware.spine.ui.home.HomeActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.hashtags.HASHTAG
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postthought.COLOR_ID
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postthought.PostThoughtActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postthought.THOUGHT
import com.wiesoftware.spine.ui.home.menus.spine.addposts.previewpost.PreviewPostActivity
import com.wiesoftware.spine.util.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.Serializable

class ReviewPostActivity : AppCompatActivity(), KodeinAware, ReviewPostEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val homeRepositry: HomeRepository by instance()
    val factory: ReviewPostViewmodelFactory by instance()
    lateinit var binding: ActivityReviewPostBinding
    lateinit var user_id: String
    lateinit var thought: String
    var featurePost: String = "0"
    var hashtag: String = "."
    lateinit var color_id: String
    lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_review_post)
        val viewmodel = ViewModelProvider(this, factory).get(ReviewPostViewmodel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.reviewPostEventListener = this
        progressDialog = ProgressDialog(this)
        viewmodel.getLoggedInUser().observe(this, Observer { user ->
            user_id = user.users_id!!
            thought = intent.getStringExtra(THOUGHT)!!
            hashtag = intent.getStringExtra(HASHTAG)!!
            color_id = intent.getStringExtra(COLOR_ID)!!
            binding.tvThought.text = thought
            binding.tvHashtag.text = hashtag
            binding.reviewLayout.setBackgroundColor(Color.parseColor(color_id))
        })

    }

    override fun onBack() {
        onBackPressed()
    }

    /* override fun onPost() {
         binding.button41.visibility=View.VISIBLE
         val uid: RequestBody = RequestBody.create(
             "multipart/form-data".toMediaTypeOrNull(),
             user_id
         )
         val thoughts: RequestBody = RequestBody.create(
             "multipart/form-data".toMediaTypeOrNull(),
             thought
         )
         val hashtag: RequestBody = RequestBody.create(
             "multipart/form-data".toMediaTypeOrNull(),
             "." + hashtag
         )
         val color: RequestBody =RequestBody.create(
             "multipart/form-data".toMediaTypeOrNull(),
             color_id
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
                     binding.button41.visibility=View.VISIBLE
                     "Post added successfully.".toast(this@ReviewPostActivity)
                     val intent = Intent(this@ReviewPostActivity, HomeActivity::class.java)
                     intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                     startActivity(intent)
                 }
             }catch (e: ApiException){
                 e.printStackTrace()
                 binding.button41.visibility=View.VISIBLE
             }catch (e: NoInternetException){
                 e.printStackTrace()
                 binding.button41.visibility=View.VISIBLE
             }
         }
     }*/

    override fun onPost() {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.getQuetionsThought(thought, "0", hashtag)
                if (res.status) {
                    dismissProgressDailog()
                    Toast.makeText(this@ReviewPostActivity, res.message, Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this@ReviewPostActivity, HomeActivity::class.java)
                    intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                } else {
                    dismissProgressDailog()
                    Toast.makeText(this@ReviewPostActivity, res.message, Toast.LENGTH_SHORT)
                        .show()

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

    override fun onThoughtEdit(thought: String) {
        startActivity(Intent(this, PostThoughtActivity::class.java))
    }

    override fun onHashtagEdit(hashtag: String) {
        onBackPressed()
    }

    override fun onCheckedChange(on: Boolean) {
        featurePost = if (on) {
            "3"
        } else {
            "0"
        }

    }


    override fun onPreview() {
        val postThoughtModel =
            PostThoughtModel(user_id, thought, hashtag, color_id, "0", "0", featurePost)

        val intent = Intent(this, PreviewPostActivity::class.java)
        intent.putExtra(POST_THOUGHT_DATA, postThoughtModel)
        startActivity(intent)
    }

    override fun onDelete() {
        val intent = Intent(this@ReviewPostActivity, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun showProgressDialog() {
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun dismissProgressDailog() {
        progressDialog.dismiss()
    }

    companion object {
        const val POST_THOUGHT_DATA = "postThoughtData"
    }
}


data class PostThoughtModel(
    val userId: String,
    val thought: String,
    val hashtag: String,
    val colorId: String,
    val type: String,
    val multiplity: String,
    val featured: String
) : Serializable