package com.wiesoftware.spine.ui.home.menus.activities.following

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
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.FragmentFollowingActivityBinding
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

class FollowingActivityFragment : Fragment(),KodeinAware,
    FollowingActivityAdapter.FollowingActivityClickListener {

    override val kodein by kodein()
    val factory:  FollowingActivityViewmodelFactory by instance()
    val homeRepositry: HomeRepositry by instance()
    lateinit var binding: FragmentFollowingActivityBinding
    lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_following_activity,container,false)
        val viewmodel=ViewModelProvider(this,factory).get(FollowingActivityViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user->
            userId=user.users_id!!
            getFollowingActivities()
        })

        return binding.root
    }

    private fun getFollowingActivities() {

        lifecycleScope.launch {
            try {
                val res=homeRepositry.getActivities(1,100,userId,1)
                if (res.status){
                    BASE_IMAGE=res.postImage
                    STORY_IMAGE =res.profilImage
                    val dataList=res.data
                    binding.rvFollowingActivity.also{
                        it.layoutManager= LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
                        it.setHasFixedSize(true)
                        it.adapter= FollowingActivityAdapter(dataList,this@FollowingActivityFragment)
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
            val profileImg=BASE_IMAGE+activitiesData.files.split(",")[0]
            val type= if (isVideo(profileImg)){"1"}else{"0"}
            val intent=Intent(requireContext(), ViewMediaInLargeActivity::class.java)
            intent.putExtra(ViewMediaInLargeActivity.MEDIA_URL,profileImg)
            intent.putExtra(ViewMediaInLargeActivity.MEDIA_TYPE,type)
            startActivity(intent)
        }
    }

    companion object{
        fun isVideo(media_file: String) =
            media_file.contains(".mp4",true) ||
                    media_file.contains(".mov",true)  ||
                    media_file.contains(".3gp",true)  ||
                    media_file.contains(".avi",true)
    }


}