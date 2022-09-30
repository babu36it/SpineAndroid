package com.wiesoftware.spine.ui.home.menus.profile.follow.following

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
import com.wiesoftware.spine.data.adapter.FollowingAdapter
import com.wiesoftware.spine.data.net.reponses.FollowersData
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.FragmentFollowingBinding
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SomeOneProfileActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class FollowingFragment : Fragment(), KodeinAware,
    FollowingAdapter.FollowingUnfollowListener {

    override val kodein by kodein()
    val factory: FollowingFragmentViewmodelFactory by instance()
    lateinit var binding: FragmentFollowingBinding
    val homeRepositry: HomeRepositry by instance()
    var userId: String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_following,container,false)
        val viewmodel=ViewModelProvider(this,factory).get(FollowingFragmentViewmodel::class.java)
        binding.viewmodel=viewmodel

        viewmodel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user->
            userId=user.users_id!!
            getFollowings()
        })

        return binding.root
    }

    private fun getFollowings() {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.getFollowingList(1,100,userId)
                if (res.status){
                    STORY_IMAGE=res.image
                    val dataList=res.data
                    binding.rvFollowing.also {
                        it.layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
                        it.setHasFixedSize(true)
                        it.adapter=FollowingAdapter(dataList,this@FollowingFragment)
                    }
                }

            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onFollow(followersData: FollowersData) {
        lifecycleScope.launch {
            try {
                val unfollowUserId=followersData.tbl_users_user_id
                val res=homeRepositry.unFollowUser(userId,unfollowUserId)
                if (res.status){
                    "User unfollowed".toast(requireContext())
                    getFollowings()
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onViewOthersProfile(followersData: FollowersData) {
        val intent= Intent(requireContext(), SomeOneProfileActivity::class.java)
        intent.putExtra(SomeOneProfileActivity.SOME_ONES_USER_ID,followersData.tbl_users_user_id)
        startActivity(intent)
    }


}