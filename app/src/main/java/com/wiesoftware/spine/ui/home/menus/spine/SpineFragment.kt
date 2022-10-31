package com.wiesoftware.spine.ui.home.menus.spine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.SpineFragmentTabAdapter
import com.wiesoftware.spine.databinding.FragmentSpineBinding
import com.wiesoftware.spine.ui.home.menus.events.addordup.AddOrDupEventActivity
import com.wiesoftware.spine.ui.home.menus.podcasts.addrss.AddRssActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postmedia.PostMediaActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.poststory.AddStoryActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postthought.PostThoughtActivity
import com.wiesoftware.spine.ui.home.menus.spine.featuredpost.FeaturedPostActivity
import com.wiesoftware.spine.ui.home.menus.spine.following.SpineFollowingFragment
import com.wiesoftware.spine.ui.home.menus.spine.foryou.SpineForYouFragment
import com.wiesoftware.spine.ui.home.menus.spine.homesearch.HomeSearchFragment
import kotlinx.android.synthetic.main.add_post_bottomheet.*


class SpineFragment : Fragment(),
    SpineFragmentEventListener, SearchView.OnQueryTextListener {
lateinit var binding: FragmentSpineBinding
var searchListener: OnSearchHomeListener? =null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =DataBindingUtil.inflate(inflater, R.layout.fragment_spine, container, false)
        val viewmodel=ViewModelProvider(this).get(SpineFragmentViewModel::class.java)
        binding.viewModel=viewmodel
        viewmodel.spineFragmentEventListner=this
        setupViewPager()
        addTabs()
        binding.searchSpine.setOnQueryTextListener(this)
        searchListener = HomeSearchFragment()

        return binding.root
    }
    private fun setupViewPager(){
        val adapter= SpineFragmentTabAdapter(this)
        adapter.addFragment(SpineForYouFragment())
        adapter.addFragment(SpineFollowingFragment())
        binding.viewPager.offscreenPageLimit=2
        binding.viewPager.adapter=adapter
    }
    private fun addTabs(){
        TabLayoutMediator(binding.tabLayout, binding.viewPager){ tab, position ->
        }.attach()
        binding.tabLayout.getTabAt(0)?.text = getString(R.string.discover)
        binding.tabLayout.getTabAt(1)?.text = resources.getText(R.string.following)
    }

    override fun onAddButtonClicked() {
        //startActivity(Intent(requireContext(), AddPostActivity::class.java))
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
        dialog.button106.setOnClickListener {
            startActivity(Intent(requireContext(), PostThoughtActivity::class.java))
        }
        dialog.button107.setOnClickListener { startActivity(Intent(requireContext(), PostMediaActivity::class.java)) }
        dialog.button108.setOnClickListener { startActivity(Intent(requireContext(), AddStoryActivity::class.java)) }
        dialog.button109.setOnClickListener { startActivity(Intent(requireContext(), AddOrDupEventActivity::class.java))  }
        dialog.button110.setOnClickListener { startActivity(Intent(requireContext(), AddRssActivity::class.java)) }
        dialog.button111.setOnClickListener { startActivity(Intent(requireContext(),FeaturedPostActivity::class.java)) }
        dialog.show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if(newText.isNullOrEmpty()){
            val fragment: Fragment? = childFragmentManager.findFragmentByTag("spineHomeSearch")
            if (fragment != null) childFragmentManager.beginTransaction().remove(fragment)
                .commit()
        }else {
            childFragmentManager
                .beginTransaction()
                .replace(R.id.searchHomeAuto, HomeSearchFragment(), "spineHomeSearch")
                .commit()
            searchListener?.onSearch(requireContext(),newText)

        }
        return true
    }

    public interface OnSearchHomeListener{
        fun onSearch(context: Context,searchTeaxt: String)
    }

} 