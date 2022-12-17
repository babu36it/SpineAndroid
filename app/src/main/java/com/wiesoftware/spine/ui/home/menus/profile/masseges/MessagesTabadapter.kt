package com.wiesoftware.spine.ui.home.menus.profile.masseges

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Created by Vivek kumar on 1/18/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class MessagesTabadapter(activity: AppCompatActivity): FragmentStateAdapter(activity) {
    val fragmentList: ArrayList<Fragment> = ArrayList()

    fun addFragment(fragment: Fragment){
        fragmentList.add(fragment)
    }

    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int) = fragmentList[position]
}