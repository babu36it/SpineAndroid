package com.wiesoftware.spine.data.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Created by Vivek kumar on 9/10/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class PodcastsFragmentTabAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    val fragmentList: ArrayList<Fragment> = ArrayList()

    fun addFragment(fragment : Fragment){
        fragmentList.add(fragment)
    }

    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int) = fragmentList[position]
}