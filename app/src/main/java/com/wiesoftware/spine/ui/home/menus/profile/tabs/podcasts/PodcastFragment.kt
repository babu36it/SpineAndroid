package com.wiesoftware.spine.ui.home.menus.profile.tabs.podcasts


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
import com.wiesoftware.spine.data.adapter.UserPodcastAdapter
import com.wiesoftware.spine.data.net.reponses.PodDatas
import com.wiesoftware.spine.data.net.reponses.PodcastData
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.FragmentPodcastBinding
import com.wiesoftware.spine.ui.home.menus.podcasts.addpodcasts.AddPodcastActivity
import com.wiesoftware.spine.ui.home.menus.podcasts.addrss.AddRssActivity
import com.wiesoftware.spine.ui.home.menus.podcasts.podcastdetails.PodcastDetailActivity
import com.wiesoftware.spine.ui.home.menus.podcasts.watch.WatchPodcastsFragment
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.POD_FILE_BASE
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class PodcastFragment : Fragment(),KodeinAware, PodcastsEventListner,
    UserPodcastAdapter.OnPodEveListener {

    override val kodein by kodein()
    val homeRepositry: HomeRepositry by instance()
    lateinit var binding: FragmentPodcastBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_podcast, container, false)
        val viewmodel=ViewModelProvider(this).get(PodcastsViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.podcastsEventListner=this
        homeRepositry.getUser().observe(viewLifecycleOwner, Observer { user->
            val userId=user.users_id!!
            getOwnPodcasts(userId)
        })



        return binding.root
    }

    private fun getOwnPodcasts(userId: String) {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.getAllPodcasts()
                if (res.status){
                    STORY_IMAGE =res.profile_img
                    POD_FILE_BASE =res.image
                    BASE_IMAGE = POD_FILE_BASE
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
        if(dataList.size > 0){
           binding.button36.visibility=View.GONE
        }
        binding.rvOwnPods.also {
            it.layoutManager= LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
            it.setHasFixedSize(true)
            it.adapter= UserPodcastAdapter(dataList,this)
        }
    }

    override fun onAddPodCast() {
        startActivity(Intent(requireContext(),AddRssActivity::class.java))
    }

    override fun onPodDetails(podcastData: PodDatas) {
        val intent=Intent(requireContext(), PodcastDetailActivity::class.java)
        intent.putExtra(WatchPodcastsFragment.POD_ID,podcastData.id)
        startActivity(intent)
    }
}