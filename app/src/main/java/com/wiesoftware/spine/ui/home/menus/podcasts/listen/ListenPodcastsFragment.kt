package com.wiesoftware.spine.ui.home.menus.podcasts.listen

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.ListenPodcastAdapter
import com.wiesoftware.spine.data.net.reponses.PodDatas
import com.wiesoftware.spine.data.net.reponses.RssItem
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.data.repo.RssRepository
import com.wiesoftware.spine.databinding.FragmentListenPodcastsBinding
import com.wiesoftware.spine.ui.home.menus.podcasts.podcastdetails.PodcastDetailActivity
import com.wiesoftware.spine.ui.home.menus.podcasts.userpodcast.UserPodcastActivity
import com.wiesoftware.spine.ui.home.menus.podcasts.watch.WatchPodcastsFragment
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
import java.lang.Exception

class ListenPodcastsFragment : Fragment(),KodeinAware, ListenPodcastAdapter.PodCastEventsListener {

    override val kodein by kodein()

    val rssRepository: RssRepository by instance()
    val homeRepositry: HomeRepositry by instance()
    val factory: ListenPodcastViewmodelFactory by instance()

    lateinit var binding: FragmentListenPodcastsBinding
    lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_listen_podcasts,container,false)
        val viewmodel=ViewModelProvider(this,factory).get(ListenPodcastViewmodel::class.java)
        binding.viewmodel=viewmodel

        viewmodel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user->
            userId=user.users_id!!
            getAllPodcasts()
        })



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            getAllPodcasts()
            "Refreshed".toast(requireContext())
        }
    }

    private fun getAllPodcasts() {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.getAllPodcasts(userId)
                if (res.status){
                    STORY_IMAGE =res.profile_img
                    POD_FILE_BASE=res.image
                    BASE_IMAGE = POD_FILE_BASE
                val dataList=res.data
                    //setPodData(dataList)
                    if(dataList.size > 0){
                        val d= dataList[0]
                        Log.e("feed",d.rss_feed)
                        val rssLink = d.rss_feed
                        getRssData(rssLink)
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

    private fun getRssData(rssLink: String) {
        lifecycleScope.launch {
            try {
                val rssRes=rssRepository.getPodsFromRss(rssLink)
            //    Log.e("responxce", rssRes.toString())
                if (rssRes.status.equals("ok")){
                    val rssEpisodes = rssRes.items
                //    Log.e("responxcelistt", rssEpisodes.toString())
                    setRssData(rssEpisodes)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    private fun setRssData(rssEpisodes: List<RssItem>) {
        binding.rvListenPod.also {
            it.layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            it.setHasFixedSize(true)
            it.adapter=ListenPodcastAdapter(rssEpisodes,this,requireContext())
        }
    }

    private fun setPodData(dataList: List<PodDatas>) {
        /*val podList: MutableList<PodDatas> = ArrayList<PodDatas>()
        for(podcast in dataList){
            val type=podcast.type
            if (type.equals("0")){
                podList.add(podcast)
            }
        }*/
        binding.rvListenPod.also {
            it.layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            it.setHasFixedSize(true)
            //it.adapter=ListenPodcastAdapter(dataList,this)
        }
    }

    override fun onPodcastLike(podcastData: RssItem) {
        val podIld=podcastData.guid
        lifecycleScope.launch {
            try {
                val res=homeRepositry.managePodcastLikes(userId,podIld)
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
                val res=homeRepositry.managePodcastBookmarks(userId,podIld)
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
        intent.putExtra(WatchPodcastsFragment.POD_USER_ID,podcastData.guid)
        startActivity(intent)
    }

    override fun onPodcastDetails(podcastData: RssItem) {
        Log.e("dtatawhole0", podcastData.toString())
        val intent=Intent(requireContext(), PodcastDetailActivity::class.java)
        intent.putExtra(WatchPodcastsFragment.POD_ID,podcastData.guid)
        startActivity(intent)
    }



}