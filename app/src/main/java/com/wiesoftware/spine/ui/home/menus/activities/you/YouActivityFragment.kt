package com.wiesoftware.spine.ui.home.menus.activities.you

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
import com.wiesoftware.spine.data.adapter.ActivityForYouAdapter
import com.wiesoftware.spine.data.net.reponses.ActivitiesData
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.FragmentYouActivityBinding
import com.wiesoftware.spine.ui.home.menus.activities.following.FollowingActivityAdapter
import com.wiesoftware.spine.ui.home.menus.activities.following.FollowingActivityFragment
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SomeOneProfileActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.viewmedia.ViewMediaInLargeActivity
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class YouActivityFragment : Fragment(),KodeinAware,
    FollowingActivityAdapter.FollowingActivityClickListener {

    override val kodein by kodein()
    lateinit var binding: FragmentYouActivityBinding
    private val factory: YouActivityViewModelFactory by instance()
    private val homeRepositry: HomeRepository by instance()

    lateinit var user_id: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_you_activity,container,false)
        val viewmodel=ViewModelProvider(this,factory).get(YouActivityViewModel::class.java)
        binding.viewmodal=viewmodel

        viewmodel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user->
            user_id=user.users_id!!
            setActivities()
        })

        return binding.root
    }

    private fun setActivities() {

        lifecycleScope.launch {
            try {
                val res=homeRepositry.getActivities(1,100,user_id,0)
                if (res.status){
                 val dataList=res.data
                    BASE_IMAGE=res.postImage
                    STORY_IMAGE=res.profilImage
                    binding.youActivity.also{
                        it.layoutManager= LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
                        it.setHasFixedSize(true)
                        it.adapter= ActivityForYouAdapter(dataList,this@YouActivityFragment,0)
                    }
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }


    }

    override fun onActivityClick(activitiesData: ActivitiesData) {
        val intent= Intent(requireContext(), SomeOneProfileActivity::class.java)
        intent.putExtra(SomeOneProfileActivity.SOME_ONES_USER_ID,activitiesData.userId)
        startActivity(intent)
    }

    override fun onShowVidImage(activitiesData: ActivitiesData) {
        if (!(activitiesData.files).isNullOrEmpty()){
            val profileImg= BASE_IMAGE +activitiesData.files.split(",")[0]
            val type= if (FollowingActivityFragment.isVideo(profileImg)){"1"}else{"0"}
            val intent=Intent(requireContext(), ViewMediaInLargeActivity::class.java)
            intent.putExtra(ViewMediaInLargeActivity.MEDIA_URL,profileImg)
            intent.putExtra(ViewMediaInLargeActivity.MEDIA_TYPE,type)
            startActivity(intent)
        }
    }

}