package com.wiesoftware.spine.ui.home.menus.spine.comment.storycomment.reply

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
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.net.reponses.StoriesCommentData
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityStoryCommenReplytBinding
import com.wiesoftware.spine.ui.home.menus.spine.comment.postcomment.PostCommentActivity
import com.wiesoftware.spine.ui.home.menus.spine.comment.storycomment.StoryCommentAdapter
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class StoryCommenReplytActivity : AppCompatActivity(), KodeinAware, StoryCommentReplyEventListener,
    StoryCommentAdapter.StoryCommentListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    lateinit var binding: ActivityStoryCommenReplytBinding
    val homeRepositry: HomeRepository by instance()
    lateinit var userId: String
    lateinit var data: StoriesCommentData
    var storyId="";var commentId=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_story_commen_replyt)
        val viewmodel=ViewModelProvider(this).get(StoryCommentReplyViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.storyCommentReplyEventListener=this
        homeRepositry.getUser().observe(this, Observer { user->
            userId=user.users_id!!
        })
        getCommentDetails()
    }

    private fun getCommentDetails() {
        data=intent.getSerializableExtra(PostCommentActivity.SPINE_COMMENT_DATA) as StoriesCommentData
        val COMMENT_PROFILE_IMAGE=intent.getStringExtra(PostCommentActivity.COMMENT_PROFILE_IMAGE)
        storyId=data.spineStoryId
        commentId=data.id
        getReplies(commentId)
        Glide.with(binding.circleImageView5).load(COMMENT_PROFILE_IMAGE+data.profilePic).placeholder(R.drawable.ic_profile).into(binding.circleImageView5)
        binding.textView52.text = data.name
        binding.textView53.text = data.comment


    }

    private fun getReplies(commentId: String) {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.storyCommentReplyList(commentId)
                if (res.status){
                    val dataList=res.data
                    binding.rvCommentReply.also {
                        it.layoutManager= LinearLayoutManager(this@StoryCommenReplytActivity,
                            RecyclerView.VERTICAL,false)
                        it.setHasFixedSize(true)
                        it.adapter= StoryCommentAdapter(dataList,this@StoryCommenReplytActivity)
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

    override fun onSend(reply: String) {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.spineStoriesComment(storyId,userId,commentId,reply)
                if (res.status){
                    binding.editTextTextPersonName23.setText("")
                    res.message.toast(this@StoryCommenReplytActivity)
                    getReplies(commentId)
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
                        getReplies(commentId)
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