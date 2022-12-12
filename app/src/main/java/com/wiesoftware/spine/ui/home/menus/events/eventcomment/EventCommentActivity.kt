package com.wiesoftware.spine.ui.home.menus.events.eventcomment

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.EventCommentAdapter
import com.wiesoftware.spine.data.net.reponses.EventCommentData
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityEventCommentBinding
import com.wiesoftware.spine.ui.home.menus.events.event_details.EventDetailActivity
import com.wiesoftware.spine.ui.home.menus.events.eventcomment.eventreply.EventReplyActivity
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class EventCommentActivity : AppCompatActivity(),KodeinAware, EventCommentEventListener,
    EventCommentAdapter.OnEventCommentEveListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()


    val factory: EventCommentViewmodelFactory by instance()
    val homeRepositry: HomeRepository by instance()
    lateinit var binding:  ActivityEventCommentBinding
    lateinit var userId: String
    lateinit var eventId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_event_comment)
        
        binding=DataBindingUtil.setContentView(this,R.layout.activity_event_comment)
        val viewmodel=ViewModelProvider(this,factory).get(EventCommentViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.eventCommentEventListener=this
        viewmodel.getLoggedInUser().observe(this, Observer { user->
            userId=user.users_id!!
            eventId=intent.getStringExtra(EventDetailActivity.EVENT_ID)!!
            getEventComments()
        })

    }

    private fun getEventComments() {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.getSpineEventsComment(eventId)
                if (res.status){
                    val data=res.data
                    binding.rvEveComments.let {
                        it.layoutManager=
                            LinearLayoutManager(this@EventCommentActivity, RecyclerView.VERTICAL,true)
                        it.setHasFixedSize(true)

                        val reverse = data.reversed()
                        it.adapter= EventCommentAdapter(
                            reverse,
                            this@EventCommentActivity,
                            homeRepositry,
                            res.user_image
                        )
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

        binding.imageButton63.isClickable = false


        lifecycleScope.launch {
            try {
                val res=homeRepositry.spineEventsComment(eventId,userId,"0",comment)

                binding.imageButton63.isClickable = true
                if (res.status){
                    //"Comment added successfully".toast(this@EventDetailActivity)
                    binding.editTextTextPersonName24.setText("")
                    getEventComments()
                }else{
                    "OOps! something went wrong.".toast(this@EventCommentActivity)
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            } finally {
                binding.imageButton63.isClickable = true
            }
        }
    }

    override fun onReply(eventCommentData: EventCommentData, comment: String) {
        val intent=Intent(this,EventReplyActivity::class.java)
        intent.putExtra(EventDetailActivity.EVENT_ID,eventId)
        intent.putExtra(EVENT_COMMENT_DATA,eventCommentData)
        startActivity(intent)
    }

    companion object{
        val EVENT_COMMENT_DATA="eventCommentData"

    }
}