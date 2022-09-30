package com.wiesoftware.spine.ui.home.menus.profile.follow.followers

import android.content.Intent
import android.os.Bundle
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
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.FollowersAdapter
import com.wiesoftware.spine.data.net.reponses.FollowersData
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.FragmentFollowersBinding
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SomeOneProfileActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class FollowersFragment : Fragment(),KodeinAware, FollowersAdapter.FollowersFollowListener {

    override val kodein by kodein()
    val factory: FollowersFragmentViewmodelFactory by instance()
    val homeRepositry: HomeRepositry by instance()
    lateinit var binding: FragmentFollowersBinding
    var userId: String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_followers,container,false)
        val viewmodel=ViewModelProvider(this,factory).get(FollowersFragmentViewmodel::class.java)
        binding.viewmodel=viewmodel

        viewmodel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user->
            userId=user.users_id!!
            getFollowersList()
        })

        return binding.root
    }

    private fun getFollowersList() {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.getFollowers(1,100,userId)
                if (res.status){
                    STORY_IMAGE=res.image
                    val dataList=res.data
                    binding.rvFollowers.also {
                        it.layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
                        it.setHasFixedSize(true)
                        it.adapter=FollowersAdapter(dataList,this@FollowersFragment)
                    }

                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onFollow(followersData: FollowersData,followStatus: String) {
        if (followStatus.equals("0")){
            addUserFollow(followersData)
        }else{
            unfollowUser(followersData)
        }
    }

    override fun onViewOthersProfile(followersData: FollowersData) {
        val intent= Intent(requireContext(), SomeOneProfileActivity::class.java)
        intent.putExtra(SomeOneProfileActivity.SOME_ONES_USER_ID,followersData.tbl_users_user_id)
        startActivity(intent)
    }

    private fun unfollowUser(followersData: FollowersData) {
        val folloUserId=followersData.tbl_users_user_id
        lifecycleScope.launch {
            try {
                val res=homeRepositry.unFollowUser(userId,folloUserId)
                if (res.status){
                    getFollowersList()
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    fun addUserFollow(followersData: FollowersData){
        val folloUserId=followersData.tbl_users_user_id
        lifecycleScope.launch {
            try {
                val res=homeRepositry.addUserFollow(userId,folloUserId)
                if (res.status){
                    getFollowersList()
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

}