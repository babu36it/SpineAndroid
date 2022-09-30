package com.wiesoftware.spine.ui.home.menus.profile.tabs

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Created by Vivek kumar on 10/13/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class ProfileTabAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    val fragmentList: ArrayList<Fragment> = ArrayList()

    fun addFragment(fragment: Fragment){
        fragmentList.add(fragment)
    }

    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int) = fragmentList[position]
}