package screen.call

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.CometChat.CallbackListener
import com.cometchat.pro.core.MessagesRequest
import com.cometchat.pro.core.MessagesRequest.MessagesRequestBuilder
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.Group
import com.cometchat.pro.models.User
import com.cometchat.pro.uikit.CometChatCallList
import com.cometchat.pro.uikit.R
import com.google.android.material.snackbar.Snackbar
import constant.StringContract
import listeners.OnItemClickListener
import screen.CometChatGroupDetailScreenActivity
import screen.CometChatUserDetailScreenActivity
import utils.Utils
import java.util.*

/**
 * AllCall.class is a Fragment which is used to display all the call being placed by or to the
 * loggedIn User. It shows the list of calls.
 *
 * Created At : 25th March 2020
 *
 * Modified On : 02nd April 2020
 */
class AllCall : Fragment() {
    private var rvCallList: CometChatCallList? = null
    private var noCallView: LinearLayout? = null
    private var messagesRequest: MessagesRequest? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_all_call, container, false)
        rvCallList = view.findViewById(R.id.callList_rv)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false
        )
        rvCallList!!.setLayoutManager(linearLayoutManager)
        noCallView = view.findViewById(R.id.no_call_vw)
        rvCallList!!.setItemClickListener(object : OnItemClickListener<Call?>() {
            override fun OnItemClick(t: Any, position: Int) {
                var call = t as Call;
                if (call.receiverType == CometChatConstants.RECEIVER_TYPE_USER) {
                    val user: User
                    user = if ((call.callInitiator as User).uid == CometChat.getLoggedInUser().uid) {
                        call.callReceiver as User
                    } else {
                        call.callInitiator as User
                    }
                    val intent = Intent(context, CometChatUserDetailScreenActivity::class.java)
                    intent.putExtra(StringContract.IntentStrings.UID, user.uid)
                    intent.putExtra(StringContract.IntentStrings.NAME, user.name)
                    intent.putExtra(StringContract.IntentStrings.AVATAR, user.avatar)
                    intent.putExtra(StringContract.IntentStrings.STATUS, user.status)
                    intent.putExtra(StringContract.IntentStrings.IS_BLOCKED_BY_ME, user.isBlockedByMe)
                    intent.putExtra(StringContract.IntentStrings.FROM_CALL_LIST, true)
                    startActivity(intent)
                } else {
                    val group: Group
                    group = call.callReceiver as Group
                    val intent = Intent(context, CometChatGroupDetailScreenActivity::class.java)
                    intent.putExtra(StringContract.IntentStrings.GUID, group.guid)
                    intent.putExtra(StringContract.IntentStrings.NAME, group.name)
                    intent.putExtra(StringContract.IntentStrings.AVATAR, group.icon)
                    intent.putExtra(StringContract.IntentStrings.MEMBER_SCOPE, group.scope)
                    intent.putExtra(StringContract.IntentStrings.GROUP_TYPE, group.groupType)
                    intent.putExtra(StringContract.IntentStrings.GROUP_OWNER, group.owner)
                    intent.putExtra(StringContract.IntentStrings.GROUP_DESC, group.description)
                    intent.putExtra(StringContract.IntentStrings.GROUP_PASSWORD, group.password)
                    intent.putExtra(StringContract.IntentStrings.MEMBER_COUNT, group.membersCount)
                    startActivity(intent)
                }
            }
        })
        rvCallList!!.setItemCallClickListener(object : OnItemClickListener<Call?>() {
//            fun OnItemClick(`var`: Call, position: Int) {
//                checkOnGoingCall(`var`)
//            }

            override fun OnItemClick(t: Any, position: Int) {
                checkOnGoingCall(t as Call)
            }
        })
        rvCallList!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!rvCallList!!.canScrollVertically(1)) {
                    callList
                }
            }
        })
        return view
    }

    private fun checkOnGoingCall(`var`: Call) {
        if (CometChat.getActiveCall() != null && CometChat.getActiveCall().callStatus == CometChatConstants.CALL_STATUS_ONGOING && CometChat.getActiveCall().sessionId != null) {
            val alert = AlertDialog.Builder(context)
            alert.setTitle(context!!.resources.getString(R.string.ongoing_call))
                    .setMessage(context!!.resources.getString(R.string.ongoing_call_message))
                    .setPositiveButton(context!!.resources.getString(R.string.join)) { dialog, which -> Utils.joinOnGoingCall(context!!) }.setNegativeButton(context!!.resources.getString(R.string.cancel)) { dialog, which -> dialog.dismiss() }.create().show()
        } else {
            initiateCall(`var`)
        }
    }

    private fun initiateCall(`var`: Call) {
        CometChat.initiateCall(`var`, object : CallbackListener<Call>() {
            override fun onSuccess(call: Call) {
                Log.e("onSuccess: ", call.toString())
                if (call.receiverType == CometChatConstants.RECEIVER_TYPE_USER) {
                    val user: User
                    user = if ((call.callInitiator as User).uid == CometChat.getLoggedInUser().uid) {
                        call.callReceiver as User
                    } else {
                        call.callInitiator as User
                    }
                    Utils.startCallIntent(context!!, user, CometChatConstants.CALL_TYPE_AUDIO, true, call.sessionId)
                } else Utils.startGroupCallIntent(context!!, call.callReceiver as Group, CometChatConstants.CALL_TYPE_AUDIO, true, call.sessionId)
            }

            override fun onError(e: CometChatException) {
                if (rvCallList != null) Snackbar.make(rvCallList!!, e.message!!, Snackbar.LENGTH_LONG).show()
            }
        })
    }

    /**
     * This method is used to get the fetch the call List.
     */
    private val callList: Unit
        private get() {
            if (messagesRequest == null) {
                messagesRequest = MessagesRequestBuilder().setCategories(Arrays.asList(CometChatConstants.CATEGORY_CALL)).setLimit(30).build()
            }
            messagesRequest!!.fetchPrevious(object : CallbackListener<List<BaseMessage?>?>() {
                override fun onSuccess(baseMessages: List<BaseMessage?>?) {
                    Collections.reverse(baseMessages)
                    rvCallList!!.setCallList(baseMessages)
                    if (rvCallList!!.size() != 0) {
                        noCallView!!.visibility = View.GONE
                    } else noCallView!!.visibility = View.VISIBLE
                }

                override fun onError(e: CometChatException) {
                    Log.e("onError: ", e.message!!)
                    if (rvCallList != null) Snackbar.make(rvCallList!!, R.string.call_list_error, Snackbar.LENGTH_LONG).show()
                }
            })
        }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            callList
        }
    }

    override fun onResume() {
        super.onResume()
    }
}