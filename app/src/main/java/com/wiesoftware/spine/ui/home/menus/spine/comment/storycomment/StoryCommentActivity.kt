package com.wiesoftware.spine.ui.home.menus.spine.comment.storycomment

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.net.reponses.StoriesCommentData
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityStoryCommentBinding
import com.wiesoftware.spine.ui.home.menus.spine.comment.postcomment.PostCommentActivity
import com.wiesoftware.spine.ui.home.menus.spine.comment.storycomment.reply.StoryCommenReplytActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.story.viewstories.storyscreen.StoryDisplayFragment
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class StoryCommentActivity : AppCompatActivity(),KodeinAware, StoryCommentEventListener,
    StoryCommentAdapter.StoryCommentListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val homeRepositry: HomeRepository by instance()
    val factory: StoryCommentViewmodelFactory by instance()
    lateinit var binding: ActivityStoryCommentBinding
    lateinit var userId: String
    var storyId=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_story_comment)
        val viewmodel=ViewModelProvider(this,factory).get(StoryCommentViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.storyCommentEventListener=this

        viewmodel.getLoggedInUser().observe(this, Observer { user->
            userId=user.users_id!!
            storyId=intent.getStringExtra(StoryDisplayFragment.STORY_ID).toString()
            getStoriesComments()
        })

    }

    private fun getStoriesComments() {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.storyCommentsList(storyId)
                if (res.status){
                    val dataList=res.data
                    binding.rvStoryComment.also {
                     it.layoutManager=LinearLayoutManager(this@StoryCommentActivity,RecyclerView.VERTICAL,false)
                     it.setHasFixedSize(true)
                     it.adapter=StoryCommentAdapter(dataList,this@StoryCommentActivity)
                 }
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onSendComment(comment: String) {
        //"$comment , $userId , $storyId".toast(this)
        lifecycleScope.launch {
            try {
            val res=homeRepositry.spineStoriesComment(storyId,userId,"0",comment)
                if (res.status){
                    binding.editTextTextPersonName22.setText("")
                    res.message.toast(this@StoryCommentActivity)
                    getStoriesComments()
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onCommentReply(storyCommentData: StoriesCommentData) {
        val intent= Intent(this, StoryCommenReplytActivity::class.java)
        intent.putExtra(PostCommentActivity.SPINE_COMMENT_DATA,storyCommentData)
        intent.putExtra(PostCommentActivity.COMMENT_PROFILE_IMAGE, BASE_IMAGE)
        startActivity(intent)
    }

    override fun onDeleteComment(storyCommentData: StoriesCommentData) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.spine_alert))
        builder.setMessage(getString(R.string.r_u_sure_to_delete_this_comment))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            val id=storyCommentData.id
            lifecycleScope.launch {
                try {
                    val res=homeRepositry.removeStoryCommentReply(id)
                    if (res.status){
                        getStoriesComments()
                    }
                }catch (e: ApiException){
                    e.printStackTrace()
                }catch (e: NoInternetException){
                    e.printStackTrace()
                }
            }
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.no) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }
}