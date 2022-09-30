package com.wiesoftware.spine.ui.home.menus.activities

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.ActivityFragmentTabAdapter
import com.wiesoftware.spine.databinding.FragmentActivitiesBinding
import com.wiesoftware.spine.ui.home.menus.activities.following.FollowingActivityFragment
import com.wiesoftware.spine.ui.home.menus.activities.you.YouActivityFragment

class ActivitiesFragment : Fragment() {


    lateinit var binding: FragmentActivitiesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_activities,container,false)
        val viewmodel=ViewModelProvider(this).get(ActivitiesFragmentViewModel::class.java)
        binding.viewModel=viewmodel
        setUpViewpager()
        addTabs()

        return binding.root
    }
    private fun setUpViewpager(){
        val adapter= ActivityFragmentTabAdapter(this)
        adapter.addFragment(YouActivityFragment())
        adapter.addFragment(FollowingActivityFragment())
        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.adapter=adapter
    }
    private fun addTabs(){
        TabLayoutMediator(binding.tabLayout,binding.viewPager){tab, position ->  }
            .attach()
        binding.tabLayout.getTabAt(0)?.text=getString(R.string.you)
        binding.tabLayout.getTabAt(1)?.text=getString(R.string.following)
    }

}