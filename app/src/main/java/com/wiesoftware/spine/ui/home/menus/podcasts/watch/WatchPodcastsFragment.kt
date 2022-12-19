package com.wiesoftware.spine.ui.home.menus.podcasts.watch

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
import com.wiesoftware.spine.data.adapter.ListenPodcastAdapter
import com.wiesoftware.spine.data.adapter.WatchPodcastAdapter
import com.wiesoftware.spine.data.net.reponses.PodDatas
import com.wiesoftware.spine.data.net.reponses.RssItem
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.data.repo.PodcastRepository
import com.wiesoftware.spine.databinding.FragmentWatchPodcastsBinding
import com.wiesoftware.spine.ui.home.menus.podcasts.podcastdetails.PodcastDetailActivity
import com.wiesoftware.spine.ui.home.menus.podcasts.userpodcast.UserPodcastActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.POD_FILE_BASE
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class WatchPodcastsFragment : Fragment(),KodeinAware, ListenPodcastAdapter.PodCastEventsListener {

    override val kodein by kodein()
    val homeRepositry: HomeRepository by instance()
    val podcastRepository: PodcastRepository by instance()
    val factory: WtachPodcastViewModelFactory by instance()
    lateinit var binding:  FragmentWatchPodcastsBinding
    lateinit var userId: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_watch_podcasts,container,false)
        val viewmodel= ViewModelProvider(this,factory).get(WatchPodcastViewModel::class.java)
        binding.viewmodel=viewmodel

        viewmodel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user->
            userId=user.users_id!!
            getAllPodcasts()
        })
        return  binding.root//inflater.inflate(R.layout.fragment_watch_podcasts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeRefresh.setOnRefreshListener {
            "Refreshed".toast(requireContext())
            binding.swipeRefresh.isRefreshing=false
            getAllPodcasts()
        }
    }

    private fun getAllPodcasts() {
        lifecycleScope.launch {
            try {
                val res=podcastRepository.getAllPodcasts()
                if (res.status){
                    STORY_IMAGE=res.profile_img
                    POD_FILE_BASE =res.image
                    BASE_IMAGE= POD_FILE_BASE
                    val dataList=res.data
                    setPodData(dataList)
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    private fun setPodData(dataList: List<PodDatas>) {
        /*val podList: MutableList<PodDatas> = ArrayList<PodDatas>()
        for(podcast in dataList){
            val type=podcast.type
            if (type.equals("1")){
                podList.add(podcast)
            }
        }*/
        binding.rvWatchPod.also {
            it.layoutManager= LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
            it.setHasFixedSize(true)
            it.adapter= WatchPodcastAdapter(dataList)
        }
    }

    override fun onPodcastLike(podcastData: RssItem) {
        val podIld=podcastData.guid
        lifecycleScope.launch {
            try {
                val res=podcastRepository.managePodcastLikes(userId,podIld)
                if (res.status){
                    getAllPodcasts()
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onPodcastBookmark(podcastData: RssItem) {
        val podIld=podcastData.guid
        lifecycleScope.launch {
            try {
                val res=podcastRepository.managePodcastBookmarks(userId,podIld)
                if (res.status){
                    getAllPodcasts()
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onPodcastUser(podcastData: RssItem) {
        val intent=Intent(requireContext(),UserPodcastActivity::class.java)
        intent.putExtra(POD_USER_ID,podcastData.guid)
        startActivity(intent)
    }

    override fun onPodcastDetails(podcastData: RssItem) {
        val intent=Intent(requireContext(), PodcastDetailActivity::class.java)
        intent.putExtra(POD_ID,podcastData.guid)
        startActivity(intent)
    }

    companion object{
        val POD_USER_ID="podUserId"
        val POD_ID="podId"
    }
}