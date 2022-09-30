package com.wiesoftware.spine.ui.home.menus.spine.story.viewstories.customstoryview

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.wiesoftware.spine.data.net.reponses.FollwingData
import com.wiesoftware.spine.ui.home.menus.spine.story.viewstories.storyscreen.StoryDisplayFragment

class StoryPagerAdapter constructor(fragmentManager: FragmentManager, private val storyList: ArrayList<FollwingData>)
    : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment = StoryDisplayFragment.newInstance(position, storyList[position])

    override fun getCount(): Int {
        return storyList.size
    }

    fun findFragmentByPosition(viewPager: ViewPager, position: Int): Fragment? {
        try {
            val f = instantiateItem(viewPager, position)
            return f as? Fragment
        } finally {
            finishUpdate(viewPager)
        }
    }
}