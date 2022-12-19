package com.wiesoftware.spine.ui.home.menus.spine.comment.reply

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.net.reponses.CommentReplyData
import com.wiesoftware.spine.data.net.reponses.SpineCommentData
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityRepliesBinding
import com.wiesoftware.spine.ui.home.menus.spine.comment.postcomment.PostCommentActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class RepliesActivity : AppCompatActivity(),KodeinAware, RepliesEventListener,
    ReplyCommentAdapter.ReplyEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val homeRepositry: HomeRepository by instance()
    lateinit var binding: ActivityRepliesBinding
    var userId=""
    var postId=""
    var commentId=""
    var data: SpineCommentData? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_replies)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_replies)
        val viewmodel=ViewModelProvider(this).get(RepliesViewModel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.repliesEventListener=this
        homeRepositry.getUser().observe(this, Observer { user->
            userId=user.users_id!!
        })
        getCommentDetails()

    }

    private fun getReplies(id: String) {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.getSpinePostReplys(id)
                if (res.status){
                BASE_IMAGE=res.image
                val datalist=res.data
                    binding.rvCommentReply.also {
                        it.layoutManager=LinearLayoutManager(this@RepliesActivity,RecyclerView.VERTICAL,false)
                        it.setHasFixedSize(true)
                        it.adapter=ReplyCommentAdapter(datalist,this@RepliesActivity)
                    }
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    private fun getCommentDetails() {
        data=intent.getSerializableExtra(PostCommentActivity.SPINE_COMMENT_DATA) as SpineCommentData
        val COMMENT_PROFILE_IMAGE=intent.getStringExtra(PostCommentActivity.COMMENT_PROFILE_IMAGE)
        postId=data!!.spine_post_id
        commentId=data!!.id
        getReplies(data!!.id)
        Glide.with(binding.circleImageView5).load(COMMENT_PROFILE_IMAGE+data!!.profilePic).placeholder(R.drawable.ic_profile).into(binding.circleImageView5)
        binding.textView52.text = data!!.displayName ?: data!!.post_user_name
        binding.textView53.text = data!!.comment
        binding.editTextTextPersonName23.setText("@${data!!.displayName ?: data!!.post_user_name}  ")
        binding.editTextTextPersonName23.setSelection(binding.editTextTextPersonName23.text.length)


    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onReply(reply: String) {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.postComment(postId,userId,commentId,reply)
                if (res.status){
                    binding.editTextTextPersonName23.setText("@${data!!.displayName ?: data!!.post_user_name}  ")
                    binding.editTextTextPersonName23.setSelection(binding.editTextTextPersonName23.text.length)
                    "Comment added successfully".toast(this@RepliesActivity)
                    getReplies(commentId)
                }else{
                    "Oops! Try again.".toast(this@RepliesActivity)
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onReply(commentReplyData: CommentReplyData) {
        commentId=commentReplyData.id
        binding.editTextTextPersonName23.setText("@${data!!.displayName ?: data!!.post_user_name}  ")
        binding.editTextTextPersonName23.setSelection(binding.editTextTextPersonName23.text.length)
    }
}