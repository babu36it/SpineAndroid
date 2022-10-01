package com.wiesoftware.spine.ui.home.menus.profile.masseges.msg

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.EveMessageAdapter
import com.wiesoftware.spine.data.net.reponses.EveMsgUserData
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.FragmentMsgBinding
import com.wiesoftware.spine.ui.home.menus.events.B_IMG_URL
import com.wiesoftware.spine.ui.home.menus.profile.chat.ChatActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.AppConfig
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import constant.StringContract
import kotlinx.coroutines.launch
import listeners.OnItemClickListener
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import screen.CometChatUserListScreen
import screen.messagelist.CometChatMessageListActivity

val EVE_MSG_DATA="eveMsgData"
class MsgFragment : Fragment(),KodeinAware, EveMessageAdapter.OnUserChatListener {

    override val kodein by kodein()
    val factory: MsgFragmentViewmodelFactory by instance()
    val homeRepositry: HomeRepositry by instance()
    lateinit var binding: FragmentMsgBinding
    lateinit var user_id: String
    lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_msg,container,false)
        val viewmodel=ViewModelProvider(this,factory).get(MsgFragmentViewmodel::class.java)
        binding.viewmodel=viewmodel

        viewmodel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user->
            user_id=user.users_id!!
            userName=user.name!!
            //setUsers()
            login(user_id,"")
        })

        loadFragment(CometChatUserListScreen())
        CometChatUserListScreen.setItemClickListener(object : OnItemClickListener<Any>() {
            override fun OnItemClick(t: Any, position: Int) {
                userIntent(t as User)
            }
        })

        return binding.root
    }
    private fun loadFragment(fragment: Fragment?) {
        if (fragment != null) {
            childFragmentManager.beginTransaction().replace(R.id.frame, fragment).commit()
        }
    }
    fun userIntent(user: User) {
        val intent = Intent(requireContext(), CometChatMessageListActivity::class.java)
        intent.putExtra(StringContract.IntentStrings.UID, user.uid)
        intent.putExtra(StringContract.IntentStrings.AVATAR, user.avatar)
        intent.putExtra(StringContract.IntentStrings.STATUS, user.status)
        intent.putExtra(StringContract.IntentStrings.NAME, user.name)
        intent.putExtra(StringContract.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_USER)
        startActivity(intent)
    }
    private fun setUsers() {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.getEventMsgUsers(1,100,user_id)
                if (res.status){
                    STORY_IMAGE=res.image
                    val data=res.data
                    binding.rvEveMsg.also {
                        it.layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
                        it.setHasFixedSize(true)
                        it.adapter=EveMessageAdapter(data,this@MsgFragment)
                    }
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }

    }

    override fun onUserChat(eveMsgUserData: EveMsgUserData) {
        val intent=Intent(requireContext(),ChatActivity::class.java)
        intent.putExtra(EVE_MSG_DATA,eveMsgUserData)
        intent.putExtra(B_IMG_URL, STORY_IMAGE)
        startActivity(intent)
    }

    private fun login(uid: String,img: String) {
        CometChat.login(uid, AppConfig.AppDetails.AUTH_KEY, object : CometChat.CallbackListener<User?>() {
            override fun onSuccess(user: User?) {
                val nam=user?.name
                nam?.toast(requireContext())
            }
            override fun onError(e: CometChatException) {
                e.printStackTrace()
                createChatUserWithComet(user_id, userName,"")
            }
        })
    }

    private fun createChatUserWithComet(user_id: String, userName: String, avatar: String) {
        val user = User()
        user.uid = user_id
        user.name = userName
        user.avatar=avatar
        CometChat.createUser(user, AppConfig.AppDetails.AUTH_KEY, object : CometChat.CallbackListener<User>() {
            override fun onSuccess(user: User) {
                login(user.uid,"")
            }
            override fun onError(e: CometChatException) {
                e.printStackTrace()
                Log.e("uError",""+e.printStackTrace())
            }
        })
    }

}