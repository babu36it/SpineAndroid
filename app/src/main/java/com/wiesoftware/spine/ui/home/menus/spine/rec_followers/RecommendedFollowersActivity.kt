package com.wiesoftware.spine.ui.home.menus.spine.rec_followers

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.DiscoverMemberStaggeredAdapter
import com.wiesoftware.spine.data.net.reponses.AllUsersData
import com.wiesoftware.spine.data.net.reponses.PostData
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityRecommendedFollowersBinding
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SomeOneProfileActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class RecommendedFollowersActivity : AppCompatActivity(), KodeinAware,
    RecommendedFollowersEventListener, RecommendedFollowersAdapter.RecommendedFollowersListener,
    DiscoverMemberStaggeredAdapter.OwnPostSelectedListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val factory: RecommendedFollowersViewModelFactory by instance()
    val homeRepositry: HomeRepository by instance()
    lateinit var binding: ActivityRecommendedFollowersBinding
    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recommended_followers)
        val viewmodel =
            ViewModelProvider(this, factory).get(RecommendedFolowersViewModel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.recommendedFollowersEventListener = this
        viewmodel.getLoggedInUser().observe(this, Observer { user ->
            userId = user.users_id!!
            getMembers()
        })
        getImageStaggered()

    }

    private fun getImageStaggered() {
        lifecycleScope.launch {
            try {
                var postList: MutableList<PostData> = ArrayList<PostData>()

                binding.rvmembersImages.also {
                    it.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
                    it.setHasFixedSize(true)
                    it.adapter =
                        DiscoverMemberStaggeredAdapter(postList, this@RecommendedFollowersActivity)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    private fun getMembers() {

        lifecycleScope.launch {
            try {
                val allUsersRes = homeRepositry.getAllUsers(1, 100, userId)
                if (allUsersRes.status) {
                    BASE_IMAGE = allUsersRes.image
                    val allUsersData = allUsersRes.data
                    binding.rvmembers.also {
                        it.layoutManager = LinearLayoutManager(
                            this@RecommendedFollowersActivity,
                            RecyclerView.VERTICAL,
                            false
                        )
                        it.setHasFixedSize(true)
                        it.adapter = RecommendedFollowersAdapter(
                            allUsersData,
                            this@RecommendedFollowersActivity
                        )
                    }
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }

    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onFollowClick(allUsersData: AllUsersData, position: Int) {
        lifecycleScope.launch {
            try {
                val res = homeRepositry.addUserFollow(userId, allUsersData.usersId)
                if (res.status) {
                    getString(R.string.following).toast(this@RecommendedFollowersActivity)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    override fun onViewProfile(allUsersData: AllUsersData) {
        val intent = Intent(this, SomeOneProfileActivity::class.java)
        intent.putExtra(SomeOneProfileActivity.SOME_ONES_USER_ID, allUsersData.usersId)
        startActivity(intent)
    }

    override fun onUnfollowUser(allUsersData: AllUsersData, position: Int) {
        lifecycleScope.launch {
            try {
                val unfollowUserId = allUsersData.usersId
                val res = homeRepositry.unFollowUser(userId, unfollowUserId)
                if (res.status) {
                    "User unfollowed".toast(this@RecommendedFollowersActivity)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    override fun onPostSelected(postData: PostData) {

    }


}