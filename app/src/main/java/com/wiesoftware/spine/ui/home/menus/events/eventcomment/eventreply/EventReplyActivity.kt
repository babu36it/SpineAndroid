package com.wiesoftware.spine.ui.home.menus.events.eventcomment.eventreply

import android.content.Context
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
import com.wiesoftware.spine.data.adapter.EventReplyAdapter
import com.wiesoftware.spine.data.net.reponses.EventCommentData
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityEventReplyBinding
import com.wiesoftware.spine.ui.home.menus.events.event_details.EventDetailActivity
import com.wiesoftware.spine.ui.home.menus.events.eventcomment.EventCommentActivity
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class EventReplyActivity : AppCompatActivity(),KodeinAware, EventReplyEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val factory: EventReplyViewmodelFactory by instance()
    val homeRepositry: HomeRepositry by instance()
    lateinit var binding:  ActivityEventReplyBinding
    lateinit var userId: String
    var eventId=""
    var commentId=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_event_reply)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_event_reply)
        val viewmodel=ViewModelProvider(this,factory).get(EventReplyViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.eventReplyEventListener=this
        viewmodel.getLoggedInUser().observe(this, Observer { user->
            userId=user.users_id!!
            getEventData()
        })

    }

    private fun getEventData() {
        val eventCommentData=intent.getSerializableExtra(EventCommentActivity.EVENT_COMMENT_DATA) as EventCommentData
        eventId=intent.getStringExtra(EventDetailActivity.EVENT_ID)!!
        commentId=eventCommentData.id
        binding.textView253.text=eventCommentData.post_user_name
        binding.textView254.text=eventCommentData.comment
        getReplies()

    }


    override fun onBack() {
        onBackPressed()
    }

    override fun onEventReply(reply: String) {

        binding.imageButton63.isClickable = false

        lifecycleScope.launch {
            try {
                val res=homeRepositry.spineEventsComment(eventId,userId,commentId,reply)
                if (res.status){
                    binding.editTextTextPersonName24.setText("")
                    getReplies()
                }else{
                    "OOps! something went wrong.".toast(this@EventReplyActivity)
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

    fun getReplies(){
        lifecycleScope.launch {
            try {
                val res=homeRepositry.getSpineEventReplys(commentId)
                if (res.status){
                    val list=res.data
                    binding.rvEveComments.also {
                        it.layoutManager=LinearLayoutManager(this@EventReplyActivity, RecyclerView.VERTICAL,false)
                        it.setHasFixedSize(true)
                        val reverse = list.reversed();
                        it.adapter= EventReplyAdapter(reverse)
                    }
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }
}