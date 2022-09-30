package com.wiesoftware.spine.ui.home.menus.spine.comment.postcomment

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.SpineCommentDataAdapter
import com.wiesoftware.spine.data.net.reponses.SpineCommentData
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityPostCommentBinding
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SOMEONE_U_ID
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SomeOneProfileActivity
import com.wiesoftware.spine.ui.home.menus.spine.comment.reply.RepliesActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.foryou.POST_ID
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class PostCommentActivity : AppCompatActivity(),KodeinAware, PostCommentEventListener,
    SpineCommentDataAdapter.SpineCommentEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    lateinit var binding: ActivityPostCommentBinding
    val factory: PostCommentViewmodelFactory by instance()
    val homeRepositry: HomeRepositry by instance()
    lateinit var user_id:  String
    lateinit var post_id: String
    var comment_id:String="0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_post_comment)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_post_comment)
        val viewmodel=ViewModelProvider(this,factory).get(PostCommentViewmodel::class.java)
        binding.viewModel=viewmodel
        viewmodel.postCommentEventListener=this
        viewmodel.getLoggedInUser().observe(this, Observer { user->
            user_id=user.users_id!!
            post_id= intent.getStringExtra(POST_ID)!!
            Log.e("postId:",post_id)
            getPostComments(post_id)
        })

    }

    private fun getPostComments(post_id: String?) {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.getSpineComments(post_id!!)
                if (res.status){
                    BASE_IMAGE=res.image
                    val commentDataList=res.data
                    binding.rvPostComments.also {
                        it.layoutManager=LinearLayoutManager(this@PostCommentActivity,RecyclerView.VERTICAL,true)
                        it.setHasFixedSize(true)
                        it.adapter=SpineCommentDataAdapter(commentDataList,this@PostCommentActivity)
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

    override fun onPost(comment: String) {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.postComment(post_id,user_id,"0",comment)
                if (res.status){
                    binding.editTextTextPersonName5.setText("")
                    getPostComments(post_id)
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onCommentReply(spineCommentData: SpineCommentData) {
        Log.e("commentId:",spineCommentData.id)
        Log.e("commentIdParent:",spineCommentData.parent_comment_id)
        val intent=Intent(this,RepliesActivity::class.java)
        intent.putExtra(SPINE_COMMENT_DATA,spineCommentData)
        intent.putExtra(COMMENT_PROFILE_IMAGE, BASE_IMAGE)
        startActivity(intent)
        /*val commentId=spineCommentData.id
        val parentCommentId=spineCommentData.parent_comment_id
        val commentUser=spineCommentData.displayName
        binding.editTextTextPersonName5.setText("@"+commentUser+" ")
        binding.editTextTextPersonName5.setSelection(binding.editTextTextPersonName5.text.length)*/

    }

    override fun onViewProfile(spineCommentData: SpineCommentData) {
        val userId=spineCommentData.user_id
        val intent=Intent(this, SomeOneProfileActivity::class.java)
        intent.putExtra(SomeOneProfileActivity.SOME_ONES_USER_ID,userId)
        startActivity(intent)
    }

    override fun onDeleteComment(spineCommentData: SpineCommentData) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.spine_alert))
        builder.setMessage(getString(R.string.r_u_sure_to_delete_this_comment))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            deleteComment(spineCommentData)
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.no) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun deleteComment(spineCommentData: SpineCommentData) {
        lifecycleScope.launch {
            try {
                val res= homeRepositry.spinePostCommentRemove(spineCommentData.id)
                if (res.status){
                    getPostComments(post_id)
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }

    }

    companion object{
        val SPINE_COMMENT_DATA="SpineCommentData"
        val COMMENT_PROFILE_IMAGE="CommentProfileImage"
    }
}