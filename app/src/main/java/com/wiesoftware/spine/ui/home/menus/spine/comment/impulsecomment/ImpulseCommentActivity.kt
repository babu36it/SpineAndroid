package com.wiesoftware.spine.ui.home.menus.spine.comment.impulsecomment

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
import com.wiesoftware.spine.data.adapter.CommentDataAdapter
import com.wiesoftware.spine.data.db.entities.User
import com.wiesoftware.spine.data.net.reponses.CommentData
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityImpulseCommentBinding
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SomeOneProfileActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.Coroutines
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ImpulseCommentActivity : AppCompatActivity(),KodeinAware, ImpulseCommentEventListener,
    CommentDataAdapter.CommentEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    lateinit var binding: ActivityImpulseCommentBinding
    lateinit var viewmodel: ImpulseCommentViewModel
    val factory: ImpulseCommentViewModelFactory by instance()
    val homeRepositry: HomeRepository by instance()
    lateinit var c_user: User
    lateinit var impulse_id:String
    var comment_user: String=""
    var comment_id="0"
    var user_id=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_impulse_comment)
        viewmodel=ViewModelProvider(this,factory).get(ImpulseCommentViewModel::class.java)
        binding.viewModel=viewmodel
        viewmodel.impulseCommentEventListener=this

        viewmodel.getLoggedInUser().observe(this, Observer {user->
            if (user != null){
                c_user= user
                user_id=c_user.users_id!!
                impulse_id= intent.getStringExtra("impulse_id")!!
                Log.e("impulseId: ",impulse_id)
                getCommentList()
            }
        })

    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onPost(comment: String) {
        if (impulse_id.isEmpty()){
            return
        }
        postAComment(comment,user_id,comment_id)
    }

    private fun postAComment(comment: String, usersId: String?, s: String) {
        Coroutines.main {
            try {
                val responseBody=homeRepositry.saveImpulseComment(s,impulse_id,usersId!!,comment)
                val res=responseBody.string()
                Log.e("comment",res)
                val resp: JSONObject= JSONObject(res)
                val status=resp.getBoolean("status")
                if (status){
                binding.editTextTextPersonName5.setText("")
                //viewmodel.comment=""
                getCommentList()
                }
            }catch (e: ApiException){
                e.message!!.toast(this)
            }catch (e: NoInternetException){
                e.message!!.toast(this)
            }

        }
    }

    private fun getCommentList() {
        Coroutines.main {
            try {
                val response=homeRepositry.getComments(1,100,impulse_id)
                if(response.status){
                    BASE_IMAGE=response.image
                    val dataList: ArrayList<CommentData>?=response.data
                    binding.rvComments.also {
                        it.layoutManager=LinearLayoutManager(this,RecyclerView.VERTICAL,true)
                        it.setHasFixedSize(true)
                        val adapter=CommentDataAdapter(dataList!!,this)
                        it.adapter=adapter
                        adapter.notifyDataSetChanged()
                    }
                }

            }catch (e: ApiException){
                e.message!!.toast(this)
            }catch (e: NoInternetException){
                e.message!!.toast(this)
            }
        }
    }

    override fun onReplyComment(commentData: CommentData) {
        comment_user=commentData.name
        user_id=commentData.user_id
        comment_id=commentData.id
        binding.editTextTextPersonName5.setText("@"+comment_user+" ")
        binding.editTextTextPersonName5.setSelection(binding.editTextTextPersonName5.text.length)
    }

    override fun onViewProfile(commentData: CommentData) {
        val uId=commentData.user_id
        val intent=Intent(this,SomeOneProfileActivity::class.java)
        intent.putExtra(SomeOneProfileActivity.SOME_ONES_USER_ID,uId)
        startActivity(intent)
    }

    override fun onDeleteComment(commentData: CommentData) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.spine_alert))
        builder.setMessage(getString(R.string.r_u_sure_to_delete_this_comment))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            deleteComment(commentData)
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.no) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun deleteComment(commentData: CommentData) {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.removeCommentOnImpluse(commentData.id)
                if (res.status){
                    getCommentList()
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }
}