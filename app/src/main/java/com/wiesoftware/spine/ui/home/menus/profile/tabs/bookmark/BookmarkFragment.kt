package com.wiesoftware.spine.ui.home.menus.profile.tabs.bookmark

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.EventSavedAdapter
import com.wiesoftware.spine.data.adapter.PostSavedAdapter
import com.wiesoftware.spine.data.adapter.SaveStoryAdapter
import com.wiesoftware.spine.data.adapter.SavedPodcastAdapter
import com.wiesoftware.spine.data.net.reponses.*
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.FragmentBookmarkBinding
import com.wiesoftware.spine.ui.home.menus.activities.following.FollowingActivityFragment.Companion.isVideo
import com.wiesoftware.spine.ui.home.menus.events.B_IMG_URL
import com.wiesoftware.spine.ui.home.menus.events.EVE_RECORD
import com.wiesoftware.spine.ui.home.menus.events.event_details.EventDetailActivity
import com.wiesoftware.spine.ui.home.menus.profile.tabs.posts.PostsFragment
import com.wiesoftware.spine.ui.home.menus.profile.tabs.posts.profileImgBase
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.postdetails.PostDetailsActivity
import com.wiesoftware.spine.ui.home.menus.spine.viewmedia.ViewMediaInLargeActivity
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.POD_FILE_BASE
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.lang.Exception

class BookmarkFragment : Fragment(),KodeinAware, BookmarkEventListener,
    PostSavedAdapter.OnPostClickLisstener, EventSavedAdapter.EventItemClickListener,
    SaveStoryAdapter.SavedStoryClickListener {
    override val kodein by kodein()
    val homeRepositry: HomeRepositry by instance()
    val factory: BookmarkViewmodelFactory by instance()
    lateinit var binding: FragmentBookmarkBinding
    var userId: String=""
    var allEvents: MutableList<EventsRecord> = ArrayList<EventsRecord>()



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_bookmark,container,false)
        val viewmodel=ViewModelProvider(this,factory).get(BookmarkViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.bookmarkEventListener=this
        viewmodel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user->
            userId=user.users_id!!
            getAllSavedPost()
            getAllSavedEvents()
            getAllSavedStory()
            getAllSavedPod()
        })
        binding.recyclerView3.also {
            it.layoutManager=GridLayoutManager(requireContext(),2,RecyclerView.VERTICAL,false)
            it.setHasFixedSize(true)
            it.adapter = EventSavedAdapter(allEvents,this)
        }
        return binding.root
    }

    private fun getAllSavedPod() {
        val datalist: MutableList<PodDatas> = ArrayList<PodDatas>()

        lifecycleScope.launch {
            try {
                val res=homeRepositry.getAllPodcasts()
                if (res.status){
                    STORY_IMAGE =res.profile_img
                    POD_FILE_BASE =res.image
                    BASE_IMAGE = POD_FILE_BASE
                    val dataList=res.data
                    for (data in dataList){
                        val d=data.bookmarks
                        if (d.equals("1")){
                            datalist.add(data)
                        }
                    }
                    binding.recyclerView5.also {
                        it.layoutManager=GridLayoutManager(requireContext(),2,RecyclerView.VERTICAL,false)
                        it.setHasFixedSize(true)
                        it.adapter=SavedPodcastAdapter(datalist)
                    }

                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }catch (e: Exception){
                e.printStackTrace()
            }
        }





    }

    private fun getAllSavedStory() {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.getAllSavedStory(userId)
                if (res.status){
                    BASE_IMAGE=res.image
                    val dataList=res.data
                    binding.recyclerView6.also {
                        it.layoutManager=GridLayoutManager(requireContext(),2,RecyclerView.VERTICAL,false)
                        it.setHasFixedSize(true)
                        it.adapter=SaveStoryAdapter(dataList,this@BookmarkFragment)
                    }
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    private fun getAllSavedEvents() {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.getAllSavedEvents(1,100,userId)
                if (res.status){
                    BASE_IMAGE=res.image
                    val dataList=res.data
                    setAllSavedEvents(dataList)
                    }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e:NoInternetException){
                e.printStackTrace()
            }
        }
    }
    private fun setAllSavedEvents(dataList: List<EventsData>) {
        for (i in dataList.indices){
            val record=dataList[i].records
            allEvents.addAll(record)
        }
        binding.recyclerView3.also {
            it.layoutManager=GridLayoutManager(requireContext(),2,RecyclerView.VERTICAL,false)
            it.setHasFixedSize(true)
            it.adapter = EventSavedAdapter(allEvents,this)
        }
    }

    private fun getAllSavedPost() {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.getAllSavedPost(userId)
                if (res.status){
                    BASE_IMAGE =res.image
                    Log.e("baseimageone", BASE_IMAGE.toString())
                    //profileImgBase=res.profilImage
                    val dataList=res.data
                    binding.recyclerView4.also {
                        it.layoutManager=GridLayoutManager(requireContext(),2,RecyclerView.VERTICAL,false)
                        it.setHasFixedSize(true)
                        it.adapter=PostSavedAdapter(dataList,this@BookmarkFragment)
                    }
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }



    override fun allPost() {

    }

    override fun allEvents() {

    }

    override fun allStories() {

    }

    override fun allPodcasts() {

    }

    override fun onPostClick(postData: PostData) {
        val intent= Intent(requireContext(), PostDetailsActivity::class.java)
        intent.putExtra(PostsFragment.POST_DATA,postData)
        intent.putExtra(PostsFragment.BASE_POST_IMG, BASE_IMAGE)
        intent.putExtra(PostsFragment.PROFILE_IMG_BASE,profileImgBase)
        startActivity(intent)
    }

    override fun onEventItemClick(eventsRecord: EventsRecord) {
        val intent=Intent(requireContext(), EventDetailActivity::class.java)
        intent.putExtra(EVE_RECORD,eventsRecord)
        intent.putExtra(B_IMG_URL, BASE_IMAGE)
        intent.putExtra("event_id",eventsRecord.id)
        startActivity(intent)
    }

    override fun onSavedStoryClick(storyData: StoryData) {
        val url= BASE_IMAGE+storyData.media_file

        val type= if (isVideo(url)){"1"}else{"0"}
        val intent= Intent(requireContext(), ViewMediaInLargeActivity::class.java)
        intent.putExtra(ViewMediaInLargeActivity.MEDIA_URL,url)
        intent.putExtra(ViewMediaInLargeActivity.MEDIA_TYPE,type)
        startActivity(intent)
    }

}