package com.wiesoftware.spine.ui.home.menus.profile.chat

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.EveChatAdapter
import com.wiesoftware.spine.data.net.reponses.EveMsgUserData
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityChatBinding
import com.wiesoftware.spine.ui.home.menus.events.B_IMG_URL
import com.wiesoftware.spine.ui.home.menus.profile.masseges.msg.EVE_MSG_DATA
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.hideKeyboard
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ChatActivity : AppCompatActivity(),KodeinAware, ChatActivityEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val homeRepositry: HomeRepository by instance()
    val factory: ChatActivityViewmodelFactory by instance()
    lateinit var userId: String
    lateinit var eveUserId: String
    lateinit var binding: ActivityChatBinding
    lateinit var eveUserImg: String
    lateinit var eventId: String

    lateinit var mainHandler: Handler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_chat)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_chat)
        val  viewmodel=ViewModelProvider(this,factory).get(ChatActivityViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.chatActivityEventListener=this
        getEveMsgData()
        viewmodel.getLoggedInUser().observe(this, Observer { user->
            userId=user.users_id!!
        })



    }

    private fun getEveMsgData() {
        val eveData=intent.getSerializableExtra(EVE_MSG_DATA) as  EveMsgUserData
        val eveUserName=eveData.event_user_name
        eveUserId=eveData.event_user_id
        userId=eveData.second_user_id
        eventId=eveData.event_id
        val img= eveData.event_user_image
        val bImgUrl=intent.getStringExtra(B_IMG_URL)
        eveUserImg=bImgUrl+img

        binding.textView143.text=eveUserName

        mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(updateChat)

        getChatMsgs()
    }

    private fun getChatMsgs() {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.getEventChatMsg(1,100,eveUserId,userId)
                if (res.status){
                    val data=res.data
                    binding.rvChat.also {
                        it.layoutManager=LinearLayoutManager(this@ChatActivity,RecyclerView.VERTICAL,true)
                        it.setHasFixedSize(true)
                        val adapterr=EveChatAdapter(data,eveUserImg,userId)
                        it.adapter=adapterr
                        adapterr.notifyDataSetChanged()

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

    override fun onMsgSend(msg: String?) {
        var type: String="2"
        if (eveUserId.equals(userId)){
            type="1"
        }else{
            type="2"
        }
        binding.editTextTextPersonName18.setText("")
        lifecycleScope.launch {
            try {
                val  res=homeRepositry.sendEventMessage(eventId,eveUserId,userId,msg!!,type)
                if (res.status){
                    getChatMsgs()
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateChat)
    }

    private val updateChat = object : Runnable {
        override fun run() {
            getChatMsgs()
            mainHandler.postDelayed(this, 5000)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        hideKeyboard()
        return super.dispatchTouchEvent(ev)
    }

}