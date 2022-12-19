package com.wiesoftware.spine.ui.home.menus.spine.impulse

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.ImpulseAdapter
import com.wiesoftware.spine.data.net.reponses.SpineImpulseData
import com.wiesoftware.spine.data.repo.AuthRepository
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityImpulseBinding
import com.wiesoftware.spine.ui.home.menus.spine.comment.impulsecomment.ImpulseCommentActivity
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.Coroutines
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ImpulseActivity : AppCompatActivity(), ImpulseEventListener,KodeinAware,
    ImpulseAdapter.ImpulseEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    private val factory: ImpulseViewModelFactory by instance()
    private val homeRepositry: HomeRepository by instance()
    private val authRepository: AuthRepository by instance()
    lateinit var binding: ActivityImpulseBinding
    var page_no=1
    var page_per_count=20
    var userId = ""
    var status="0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=DataBindingUtil.setContentView(this,R.layout.activity_impulse)
        val viewmodel= ViewModelProvider(this,factory).get(ImpulseViewModel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.impulseEventListener=this
        homeRepositry.getUser().observe(this, Observer {user->
            userId=user.users_id!!
            getUserDetails()
            getImpulseData(page_no,page_per_count)
        })

        binding.rvImpulse.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    //bottom last
                    getImpulseData(page_no,page_per_count)
                }
            }
        })




    }

    private fun getUserDetails() {
        lifecycleScope.launch {
            try {
                val res=authRepository.getUserDetails()
                if (res.status){
                 val data=res.data
                    status=data.impulseFollow
                    if (status.equals("1")){
                        binding.sbStory.text=getString(R.string.following)
                    }else{
                        binding.sbStory.text=getString(R.string.follows)
                    }
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    private fun getImpulseData(pageNo: Int, pagePerCount: Int) {
        Coroutines.main{
            try{

                val impulseList: MutableList<SpineImpulseData> = ArrayList()
                impulseList.add(SpineImpulseData("",getString(R.string.spine_dumy_description),"","","Spine","1","10","","","","","","1"))
                impulseList.add(SpineImpulseData("",getString(R.string.spine_dumy_description),"","","Spine","0","2","","","","","","0"))
                impulseList.add(SpineImpulseData("",getString(R.string.spine_dumy_description),"","","Spine","1","4","","","","","","1"))
                impulseList.add(SpineImpulseData("",getString(R.string.spine_dumy_description),"","","Spine","1","12","","","","","","0"))
                binding.rvImpulse.also {
                    it.layoutManager=LinearLayoutManager(this,RecyclerView.VERTICAL,false)
                    it.setHasFixedSize(true)
                    val adapter= ImpulseAdapter(impulseList,this)
                    it.adapter=adapter
                    adapter.notifyDataSetChanged()
                }
                /*  Temparary commented MT
                val impulseResponse=homeRepositry.getSpineImpulse(pageNo,pagePerCount,userId)
                if (impulseResponse.status){
                    page_no++
                    BASE_IMAGE =impulseResponse.image
                    val impulseList: List<SpineImpulseData> = impulseResponse.data!!
                    binding.rvImpulse.also {
                        it.layoutManager=LinearLayoutManager(this,RecyclerView.VERTICAL,false)
                        it.setHasFixedSize(true)
                        val adapter= ImpulseAdapter(impulseList,this)
                        it.adapter=adapter
                        adapter.notifyDataSetChanged()
                    }
                }else{
                    page_no=1
                }*/

            }catch(e: ApiException){
                e.printStackTrace()
                "${e.message}".toast(this)
            }catch(e: NoInternetException){
                e.printStackTrace()
                "${e.message}".toast(this)
            }
        }
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onFollow() {
        if (status.equals("0")){
            status="1"
            binding.sbStory.text=getString(R.string.following)
        }else if(status.equals("1")){
            status="0"
            binding.sbStory.text=getString(R.string.follows)
        }
        lifecycleScope.launch {
            try {
                val res=homeRepositry.followUnfollowImpluse(userId,status.toInt())
                getUserDetails()
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onComment(spineImpulseData: SpineImpulseData) {
        val intent= Intent(this, ImpulseCommentActivity::class.java)
        intent.putExtra("impulse_id",spineImpulseData.id)
        startActivity(intent)
    }

    override fun onLike(spineImpulseData: SpineImpulseData) {
        Coroutines.main {
            try {
                val res=homeRepositry.likeSpineImpulse(userId,spineImpulseData.id)
                val mRes= res.string()
                val jRes= JSONObject(mRes)
                Log.e("like:",mRes)
                if (jRes.getBoolean("status")){
                    getImpulseData(page_no,page_per_count)
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun omShare(spineImpulseData: SpineImpulseData) {
        "In Progress...".toast(this)
    }
}