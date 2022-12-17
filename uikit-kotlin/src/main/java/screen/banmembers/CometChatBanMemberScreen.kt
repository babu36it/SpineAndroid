package screen.banmembers

import adapter.GroupMemberAdapter
import android.os.Bundle
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.BannedGroupMembersRequest
import com.cometchat.pro.core.BannedGroupMembersRequest.BannedGroupMembersRequestBuilder
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.CometChat.CallbackListener
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.GroupMember
import com.cometchat.pro.uikit.R
import com.google.android.material.snackbar.Snackbar
import constant.StringContract
import listeners.ClickListener
import listeners.RecyclerTouchListener

class CometChatBanMemberScreen : Fragment() {
    private var bannedGroupMembersRequest: BannedGroupMembersRequest? = null
    private var guid: String? = null
    private var gName: String? = null
    private var noMemberTv: TextView? = null
    private var loggedInUserScope: String? = null
    private var groupMemberAdapter: GroupMemberAdapter? = null
    private var bannedMemberRv: RecyclerView? = null
    private val LIMIT = 30
    private var groupMember: GroupMember? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_comet_chat_ban_member_screen, container, false)
        bannedMemberRv = view.findViewById(R.id.bannedMembers_rv)
        noMemberTv = view.findViewById(R.id.no_member_tv)
        bannedMemberRv!!.layoutManager = LinearLayoutManager(context)
        groupMemberAdapter = GroupMemberAdapter(context!!)
        bannedMemberRv!!.adapter = groupMemberAdapter
        handleArguments()
        bannedMembers
        bannedMemberRv!!.addOnItemTouchListener(RecyclerTouchListener(context, bannedMemberRv!!, object : ClickListener() {
            override fun onClick(var1: View, var2: Int) {
                val user = var1.getTag(R.string.user) as GroupMember
                groupMember = user
                if (loggedInUserScope != null && (loggedInUserScope == CometChatConstants.SCOPE_ADMIN || loggedInUserScope == CometChatConstants.SCOPE_MODERATOR)) {
                    registerForContextMenu(bannedMemberRv!!)
                    activity!!.openContextMenu(var1)
                }
            }
        }))
        return view
    }

    fun handleArguments() {
        if (arguments != null) {
            guid = arguments!!.getString(StringContract.IntentStrings.GUID)
            gName = arguments!!.getString(StringContract.IntentStrings.GROUP_NAME)
            loggedInUserScope = arguments!!.getString(StringContract.IntentStrings.MEMBER_SCOPE)
        }
    }

    val bannedMembers: Unit
        get() {
            if (bannedGroupMembersRequest == null) bannedGroupMembersRequest = BannedGroupMembersRequestBuilder(guid).setLimit(LIMIT).build()
            bannedGroupMembersRequest!!.fetchNext(object : CallbackListener<List<GroupMember?>?>() {
                override fun onSuccess(groupMembers: List<GroupMember?>?) {
                    groupMemberAdapter!!.addAll(groupMembers)
                    checkIfNoMember()
                }

                override fun onError(e: CometChatException) {
                    if (bannedMemberRv != null) Snackbar.make(bannedMemberRv!!, resources.getString(R.string.ban_list_fetch_error), Snackbar.LENGTH_LONG).show()
                }
            })
        }

    private fun checkIfNoMember() {
        if (groupMemberAdapter != null && groupMemberAdapter!!.itemCount > 0) {
            noMemberTv!!.visibility = View.GONE
        } else {
            noMemberTv!!.visibility = View.VISIBLE
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (activity != null) {
            val menuInflater = activity!!.menuInflater
            menuInflater.inflate(R.menu.group_action_menu, menu)
            menu.findItem(R.id.item_ban).title = "Unban"
            menu.findItem(R.id.item_remove).isVisible = false
            menu.findItem(R.id.item_make_admin).isVisible = false
            menu.setHeaderTitle("Member Action")
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_ban) {
            unBanMember()
        }
        return super.onContextItemSelected(item)
    }

    private fun unBanMember() {
        CometChat.unbanGroupMember(groupMember!!.uid, guid!!, object : CallbackListener<String?>() {
            override fun onSuccess(s: String?) {
                if (bannedMemberRv != null) Snackbar.make(bannedMemberRv!!, String.format(resources.getString(R.string.member_unbanned_success), groupMember!!.name, gName), Snackbar.LENGTH_LONG).show()
                groupMemberAdapter!!.removeGroupMember(groupMember)
            }

            override fun onError(e: CometChatException) {
                if (bannedMemberRv != null) Snackbar.make(bannedMemberRv!!, String.format(resources.getString(R.string.unban_error), groupMember!!.name), Snackbar.LENGTH_LONG).show()
            }
        })
    }
}