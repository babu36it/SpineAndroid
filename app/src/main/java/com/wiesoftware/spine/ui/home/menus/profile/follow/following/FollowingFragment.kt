package com.wiesoftware.spine.ui.home.menus.profile.follow.following

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.FollowingAdapter
import com.wiesoftware.spine.data.net.reponses.FollowersData
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.FragmentFollowingBinding
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SomeOneProfileActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*
import kotlin.collections.ArrayList

class FollowingFragment : Fragment(), KodeinAware,
    FollowingAdapter.FollowingUnfollowListener {

    override val kodein by kodein()
    val factory: FollowingFragmentViewmodelFactory by instance()
    lateinit var binding: FragmentFollowingBinding
    val homeRepositry: HomeRepository by instance()
    var userId: String = ""
    lateinit var adapter: FollowingAdapter
    private var dataList: ArrayList<FollowersData> = arrayListOf()
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_following, container, false)
        val viewmodel = ViewModelProvider(this, factory).get(FollowingFragmentViewmodel::class.java)
        binding.viewmodel = viewmodel
        progressDialog = ProgressDialog(context)
        viewmodel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            userId = user.users_id!!
            mNetworkCallFollowingAPI()
        })



        binding.editTextTextPersonName19.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

                filter(s.toString())
            }
        })

        return binding.root
    }

    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredlist: ArrayList<FollowersData> = ArrayList()

        // running a for loop to compare elements.
        for (item in dataList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.displayName.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            ) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(requireContext(), "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapter.filterList(filteredlist)
        }
    }

    private fun mNetworkCallFollowingAPI() {
        lifecycleScope.launch {
            showProgressDialog()
            dataList.clear()
            try {
                val res = homeRepositry.getFollowingList(1, 100, userId)
                if (res.status) {
                    dismissProgressDailog()
                    binding.tvNoData.visibility = View.GONE
                    binding.rvFollowing.visibility = View.VISIBLE
                    STORY_IMAGE = res.image
                    dataList = res.data
                    adapter = FollowingAdapter(dataList, this@FollowingFragment)
                    binding.rvFollowing.also {
                        it.layoutManager =
                            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                        it.setHasFixedSize(true)
                        it.adapter = adapter
                    }
                } else {
                    dismissProgressDailog()
                    binding.tvNoData.visibility = View.VISIBLE
                    binding.rvFollowing.visibility = View.GONE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dismissProgressDailog()
            }
        }
    }


    override fun onFollow(followersData: FollowersData) {
        lifecycleScope.launch {
            try {
                val unfollowUserId = followersData.tbl_users_user_id
                val res = homeRepositry.unFollowUser(userId, unfollowUserId)
                if (res.status) {
                    "User unfollowed".toast(requireContext())
                    mNetworkCallFollowingAPI()
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    override fun onViewOthersProfile(followersData: FollowersData) {
        val intent = Intent(requireContext(), SomeOneProfileActivity::class.java)
        intent.putExtra(SomeOneProfileActivity.SOME_ONES_USER_ID, followersData.tbl_users_user_id)
        startActivity(intent)
    }

    fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun showProgressDialog() {
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun dismissProgressDailog() {
        progressDialog.dismiss()
    }

}