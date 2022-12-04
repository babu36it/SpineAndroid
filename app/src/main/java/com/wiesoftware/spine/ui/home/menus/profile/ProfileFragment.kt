package com.wiesoftware.spine.ui.home.menus.profile

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.FragmentProfileBinding
import com.wiesoftware.spine.ui.home.menus.events.addevents.AddEventActivity
import com.wiesoftware.spine.ui.home.menus.events.addordup.AddOrDupEventActivity
import com.wiesoftware.spine.ui.home.menus.podcasts.addrss.AddRssActivity
import com.wiesoftware.spine.ui.home.menus.profile.follow.FollowActivity
import com.wiesoftware.spine.ui.home.menus.profile.masseges.MessagesActivity
import com.wiesoftware.spine.ui.home.menus.profile.myprofile.MyProfileActivity
import com.wiesoftware.spine.ui.home.menus.profile.setting.SettingsActivity
import com.wiesoftware.spine.ui.home.menus.profile.tabs.*
import com.wiesoftware.spine.ui.home.menus.profile.tabs.bookmark.BookmarkFragment
import com.wiesoftware.spine.ui.home.menus.profile.tabs.events.EventsFragment
import com.wiesoftware.spine.ui.home.menus.profile.tabs.podcasts.PodcastFragment
import com.wiesoftware.spine.ui.home.menus.profile.tabs.posts.PostsFragment
import com.wiesoftware.spine.ui.home.menus.spine.addposts.AddPostActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postmedia.PostMediaActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.poststory.AddStoryActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postthought.PostThoughtActivity
import com.wiesoftware.spine.ui.home.menus.spine.featuredpost.FeaturedPostActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.ui.home.menus.voice_over.VoiceOverActivity
import com.wiesoftware.spine.util.*
import kotlinx.android.synthetic.main.add_post_bottomheet.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import org.kodein.di.generic.kcontext


class ProfileFragment : Fragment(), KodeinAware, ProfileFragmentEventListener {
    override val kodein by kodein()

    override val kodeinContext = kcontext<Fragment>(this)
    lateinit var binding: FragmentProfileBinding
    lateinit var viewModel: ProfileFragmentViewModel
    val factory: ProfileFragmentViewModelFactory by instance()
    val homeRepositry: HomeRepositry by instance()
    lateinit var user_id: String
    var followers: String = "30k"
    var following: String = "30k"
    lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        viewModel = ViewModelProvider(this, factory).get(ProfileFragmentViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel.profileFragmentEventListener = this
        progressDialog = ProgressDialog(context)
        setupViewPager()
        addTabs()

        viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            user_id = user.users_id!!
            if (user.facebook_id != null) {
                val fbImg = getFbImage(user.facebook_id!!)
                Log.e("fbImg:", fbImg)
                Glide.with(requireContext()).asBitmap().load(fbImg)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            binding.circleImageView4.setImageBitmap(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            binding.circleImageView4.setImageDrawable(placeholder)
                        }

                    })
            }
            getUsersDetails()
        })

        return binding.root
    }

    private fun getUsersDetails() {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val profileRes = homeRepositry.getUserDetails()
                if (profileRes.status) {
                    dismissProgressDailog()
                    val profileData = profileRes.data
                    BASE_IMAGE = profileRes.image
                    if (!profileData.user_image.isNullOrEmpty()) {
                        Glide.with(requireContext())
                            .load("https://thespiritualnetwork.com/assets/upload/profile/" + profileData.user_image)
                            .placeholder(R.drawable.userprofile)
                            .into(binding.circleImageView4)
                    }
                    followers = profileData.followers_records_count
                    following = profileData.following_records_count
                    binding.textView48.text = followers
                    binding.textView49.text = following
                    if (profileData.account_mode.equals("1")) {
                        binding.ivBadge.visibility = View.VISIBLE
                    } else {
                        binding.ivBadge.visibility = View.INVISIBLE
                    }

                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupViewPager() {
        val adapter = ProfileTabAdapter(this)
        adapter.addFragment(PostsFragment())
        adapter.addFragment(EventsFragment())
        adapter.addFragment(PodcastFragment())
        adapter.addFragment(BookmarkFragment())
        binding.viewpager2.offscreenPageLimit = 4
        binding.viewpager2.adapter = adapter
    }

    private fun addTabs() {
        TabLayoutMediator(binding.tabLayout2, binding.viewpager2) { tab, position ->
        }.attach()
        binding.tabLayout2.getTabAt(0)?.text = resources.getText(R.string.post_value)
        binding.tabLayout2.getTabAt(1)?.text = resources.getText(R.string.events)
        binding.tabLayout2.getTabAt(2)?.text = resources.getText(R.string.podcasts)
        binding.tabLayout2.getTabAt(3)?.setIcon(R.drawable.ic_bookmark)
    }

    override fun onSettingBtnClick() {
        startActivity(Intent(requireContext(), SettingsActivity::class.java))
    }

    override fun onMessageBtnClick() {
        startActivity(Intent(requireContext(), MessagesActivity::class.java))
    }

    override fun onViewProfileClick() {
        startActivity(Intent(requireContext(), MyProfileActivity::class.java))
    }

    override fun onFollowers() {
        val intent = Intent(requireContext(), FollowActivity::class.java)
        intent.putExtra("followers", followers)
        intent.putExtra("following", following)
        startActivity(intent)
//        startActivity(Intent(requireContext(),FollowActivity::class.java))
    }

    override fun onFollowing() {
        val intent = Intent(requireContext(), FollowActivity::class.java)
        intent.putExtra("followers", followers)
        intent.putExtra("following", following)
        startActivity(intent)
//        startActivity(Intent(requireContext(),FollowActivity::class.java))
    }

    override fun onAddPost() {
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
        dialog.buttonVoiceOver.setOnClickListener {
            startActivity(Intent(requireContext(), VoiceOverActivity::class.java))
            dialog.window?.let {
                it.setGravity(Gravity.BOTTOM)
                it.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(true)
            }
        }
            dialog.button106.setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        PostThoughtActivity::class.java
                    )
                )
            }
            dialog.button107.setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        PostMediaActivity::class.java
                    )
                )
            }
            dialog.button108.setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        AddStoryActivity::class.java
                    )
                )
            }
            dialog.button109.setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        AddOrDupEventActivity::class.java
                    )
                )
            }
            dialog.button110.setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        AddRssActivity::class.java
                    )
                )
            }
            dialog.button111.setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        FeaturedPostActivity::class.java
                    )
                )
            }
            dialog.show()
        }

        override fun onResume() {
            super.onResume()
            getUsersDetails()
        }

        private fun showProgressDialog() {
            progressDialog.setMessage("Please wait...")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        private fun dismissProgressDailog() {
            progressDialog.dismiss()
        }
    }
