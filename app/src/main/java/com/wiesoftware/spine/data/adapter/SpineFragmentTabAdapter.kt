package com.wiesoftware.spine.data.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Created by Vivek kumar on 8/12/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class SpineFragmentTabAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
   val fragmentList: ArrayList<Fragment> = ArrayList()

    public fun addFragment(fragment: Fragment){
        fragmentList.add(fragment)
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}