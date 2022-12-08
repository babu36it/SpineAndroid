package com.wiesoftware.spine.ui.home.menus.profile.follow.followers

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
import com.wiesoftware.spine.data.adapter.FollowersAdapter
import com.wiesoftware.spine.data.adapter.FollowingAdapter
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
import java.util.*
import kotlin.collections.ArrayList

class FollowersFragment : Fragment(),KodeinAware, FollowersAdapter.FollowersFollowListener {

    override val kodein by kodein()
    val factory: FollowersFragmentViewmodelFactory by instance()
    val homeRepositry: HomeRepositry by instance()
    lateinit var binding: FragmentFollowersBinding
    var userId: String=""
    lateinit var adapter: FollowersAdapter
    lateinit var dataList:ArrayList<FollowersData>

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

        binding.editTextTextPersonName19.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

                filter(s.toString())
            }
        })

        return binding.root
    }

    private fun getFollowersList() {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.getFollowers(1,100,userId)
                if (!res.status){
//                    STORY_IMAGE=res.image
//                    val dataList=res.data
                     dataList= arrayListOf<FollowersData>()

                    dataList.add(FollowersData(
                        "created_on",
                        "image",
                        "hashtag_ids" ,
                        "1" ,
                        "multiplity" ,
                        "post_backround_color_id" ,
                        "post_user_name" ,
                        "status" ,
                        "title" ,
                        "type" ,
                        "updated_on" ,
                        "user_id" ,
                        "96arsh123" ,
                        "Craig Warner",
                        "" ,
                        "profile_pic" ,
                        "96arsh123" ,
                        "0" ,

                        ))

                    dataList.add(FollowersData(
                        "created_on",
                        "image",
                        "hashtag_ids" ,
                        "1" ,
                        "multiplity" ,
                        "post_backround_color_id" ,
                        "post_user_name" ,
                        "status" ,
                        "title" ,
                        "type" ,
                        "updated_on" ,
                        "user_id" ,
                        "Jo123" ,
                        "Jo Warner",
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTNLMjiyMC85Z_zNLvUTXc18evEX6bsYhPeZw&usqp=CAU" ,
                        "asdasd" ,
                        "i love coading" ,
                        "1" ,

                        ))

                    dataList.add(FollowersData(
                        "created_on",
                        "image",
                        "hashtag_ids" ,
                        "1" ,
                        "multiplity" ,
                        "post_backround_color_id" ,
                        "post_user_name" ,
                        "status" ,
                        "title" ,
                        "type" ,
                        "updated_on" ,
                        "user_id" ,
                        "96arsh123" ,
                        "Craig Warner",
                        "" ,
                        "profile_pic" ,
                        "96arsh123" ,
                        "0" ,

                        ))


                    dataList.add(FollowersData(
                        "created_on",
                        "image",
                        "hashtag_ids" ,
                        "4" ,
                        "multiplity" ,
                        "post_backround_color_id" ,
                        "post_user_name" ,
                        "status" ,
                        "title" ,
                        "type" ,
                        "updated_on" ,
                        "user_id" ,
                        "2345as" ,
                        "Nilesh Smith",
                        "" ,
                        "asdasd" ,
                        getRandomString(10) ,
                        "1" ,

                        ))
                    dataList.add(FollowersData(
                        "created_on",
                        "image",
                        "hashtag_ids" ,
                        "4" ,
                        "multiplity" ,
                        "post_backround_color_id" ,
                        "post_user_name" ,
                        "status" ,
                        "title" ,
                        "type" ,
                        "updated_on" ,
                        "user_id" ,
                        "2345as" ,
                        "Mahi Patel",
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRVtCMt6WAKwJLit-pkDeK-qPowGhMMcLlsGSl-YFChzQ&s" ,
                        "asdasd" ,
                        getRandomString(10) ,
                        "0" ,

                        ))
                    dataList.add(FollowersData(
                        "created_on",
                        "image",
                        "hashtag_ids" ,
                        "4" ,
                        "multiplity" ,
                        "post_backround_color_id" ,
                        "post_user_name" ,
                        "status" ,
                        "title" ,
                        "type" ,
                        "updated_on" ,
                        "user_id" ,
                        "2345as" ,
                        "Roni Smith",
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSK_vjpKVAjkub5O0sFL7ij3mIzG-shVt-6KKLNdxq4&sU" ,
                        "asdasd" ,
                        getRandomString(10) ,
                        "0" ,

                        ))

                    dataList.add(FollowersData(
                        "created_on",
                        "image",
                        "hashtag_ids" ,
                        "4" ,
                        "multiplity" ,
                        "post_backround_color_id" ,
                        "post_user_name" ,
                        "status" ,
                        "title" ,
                        "type" ,
                        "updated_on" ,
                        "user_id" ,
                        "2345as" ,
                        "Dhruv Clark",
                        "https://res.cloudinary.com/demo/image/facebook/65646572251.jpg" ,
                        "asdasd" ,
                        getRandomString(10) ,
                        "1" ,

                        ))

                    dataList.add(FollowersData(
                        "created_on",
                        "image",
                        "hashtag_ids" ,
                        "4" ,
                        "multiplity" ,
                        "post_backround_color_id" ,
                        "post_user_name" ,
                        "status" ,
                        "title" ,
                        "type" ,
                        "updated_on" ,
                        "user_id" ,
                        "2345as" ,
                        "Anjali Sonejee",
                        "" ,
                        "asdasd" ,
                        getRandomString(10) ,
                        "0" ,

                        ))

                    adapter = FollowersAdapter(dataList,this@FollowersFragment)
                    binding.rvFollowers.also {
                        it.layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
                        it.setHasFixedSize(true)
                        it.adapter=adapter
                    }

                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredlist: ArrayList<FollowersData> = ArrayList()

        // running a for loop to compare elements.
        for (item in dataList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.displayName.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
//            Toast.makeText(requireContext(), "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapter.filterList(filteredlist)
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

    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

}