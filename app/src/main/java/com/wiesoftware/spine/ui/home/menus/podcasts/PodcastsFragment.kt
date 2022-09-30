package com.wiesoftware.spine.ui.home.menus.podcasts

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.PodcastsFragmentTabAdapter
import com.wiesoftware.spine.databinding.FragmentPodcastsBinding
import com.wiesoftware.spine.ui.home.menus.events.addevents.AddEventActivity
import com.wiesoftware.spine.ui.home.menus.events.addordup.AddOrDupEventActivity
import com.wiesoftware.spine.ui.home.menus.podcasts.addpodcasts.AddPodcastActivity
import com.wiesoftware.spine.ui.home.menus.podcasts.addrss.AddRssActivity
import com.wiesoftware.spine.ui.home.menus.podcasts.listen.ListenPodcastsFragment
import com.wiesoftware.spine.ui.home.menus.podcasts.watch.WatchPodcastsFragment
import com.wiesoftware.spine.ui.home.menus.spine.addposts.AddPostActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postmedia.PostMediaActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.poststory.AddStoryActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postthought.PostThoughtActivity
import com.wiesoftware.spine.ui.home.menus.spine.featuredpost.FeaturedPostActivity
import kotlinx.android.synthetic.main.add_post_bottomheet.*

class PodcastsFragment : Fragment(), PodcastFragmentEventListener {

    lateinit var binding : FragmentPodcastsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_podcasts,container,false)
        val viewModel=ViewModelProvider(this).get(PodcastsFragmentViewModel::class.java)
        binding.viewmodel=viewModel
        viewModel.podcastFragmentEventListener=this
        setUpViewPager()
        addTabs()

        return binding.root
    }

    private fun setUpViewPager() {
        val adapter = PodcastsFragmentTabAdapter(this)
        adapter.addFragment(ListenPodcastsFragment())
        adapter.addFragment(WatchPodcastsFragment())
        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.adapter=adapter
    }
    private fun addTabs(){
        TabLayoutMediator(binding.tabLayout,binding.viewPager){tab, position ->  }
            .attach()
        binding.tabLayout.getTabAt(0)?.text=getString(R.string.episodes)
        binding.tabLayout.getTabAt(1)?.text=getString(R.string.podcasts)
    }

    override fun onAddPodcast() {
        //startActivity(Intent(requireContext(),AddPostActivity::class.java))
        showAddBottomsheet()
    }
    private fun showAddBottomsheet() {
        val view: View = layoutInflater.inflate(R.layout.add_post_bottomheet, null)
        val dialog: BottomSheetDialog = BottomSheetDialog(requireContext())
        dialog.setContentView(view)
        dialog.setOnShowListener {
            val dialogTmp: BottomSheetDialog = it as BottomSheetDialog
            val bottomSheet: FrameLayout =
                dialogTmp.findViewById(R.id.design_bottom_sheet) as FrameLayout?
                    ?: return@setOnShowListener
            bottomSheet.background = null
        }
        dialog.window?.let {
            it.setGravity(Gravity.BOTTOM)
            it.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(true)
        }
        dialog.button106.setOnClickListener { startActivity(Intent(requireContext(), PostThoughtActivity::class.java)) }
        dialog.button107.setOnClickListener { startActivity(Intent(requireContext(), PostMediaActivity::class.java)) }
        dialog.button108.setOnClickListener { startActivity(Intent(requireContext(), AddStoryActivity::class.java)) }
        dialog.button109.setOnClickListener { startActivity(Intent(requireContext(), AddOrDupEventActivity::class.java))  }
        dialog.button110.setOnClickListener { startActivity(Intent(requireContext(), AddRssActivity::class.java)) }
        dialog.button111.setOnClickListener { startActivity(Intent(requireContext(),
            FeaturedPostActivity::class.java)) }
        dialog.show()
    }

}