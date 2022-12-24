package com.wiesoftware.spine.ui.home.menus.spine.rec_followers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.DiscoverMemberStaggeredAdapter
import com.wiesoftware.spine.data.net.reponses.AllUsersData
import com.wiesoftware.spine.data.net.reponses.PostData
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityRecommendedFollowersBinding
import com.wiesoftware.spine.databinding.OwnPostItemBinding
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SomeOneProfileActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.BaseAdapter
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
    val factory: RecommendedFollowersViewmodelFactory by instance()
    val homeRepositry: HomeRepository by instance()
    lateinit var binding: ActivityRecommendedFollowersBinding
    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recommended_followers)
        val viewmodel =
            ViewModelProvider(this, factory).get(RecommendedFolowersViewmodel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.recommendedFollowersEventListener = this
        viewmodel.getLoggedInUser().observe(this, Observer { user ->
            userId = user.users_id!!
          //  getMembers() temparary commented
        })
        getImageStaggered()

    }

    private fun getImageStaggered() {
        lifecycleScope.launch {
            try {
                var postList: MutableList<String> = ArrayList<String>()//PostData
                postList.add("https://img.rawpixel.com/private/static/images/website/2022-05/366-mj-7703-fon-jj.jpg?w=800&dpr=1&fit=default&crop=default&q=65&vib=3&con=3&usm=15&bg=F4F4F3&ixlib=js-2.2.1&s=f23359a06a626e56bedb45dac2809feb")
                postList.add("https://img.freepik.com/free-vector/nature-scene-with-river-hills-forest-mountain-landscape-flat-cartoon-style-illustration_1150-37326.jpg?w=2000")
                postList.add("https://tableforchange.com/wp-content/uploads/2017/06/yoga-quotes-11-min.png")
                postList.add("https://demonuts.com/Demonuts/SampleImages/W-03.JPG")
                postList.add("https://demonuts.com/Demonuts/SampleImages/W-08.JPG")
                postList.add("https://i.pinimg.com/736x/69/27/6c/69276ccea9ec5464b9f4c7c69a85390f.jpg")
                postList.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSsISEKeeDKAl0gAMw0MMVi3gggbna6F62OvA&usqp=CAU")
                postList.add("https://streetbounty.com/wp-content/uploads/2018/03/100-Street-Photography-Quotes-HelenLevitt.jpg")
                var mAdapter = BaseAdapter<String>(this@RecommendedFollowersActivity)
                mAdapter!!.listOfItems = postList
                mAdapter!!.expressionViewHolderBinding = { data, viewBinding, context ->
                    var holder = viewBinding as OwnPostItemBinding

                    holder.rrPostVideo.visibility= View.VISIBLE
                    holder.ivPostImageVideo.visibility= View.VISIBLE
                    Glide.with(this@RecommendedFollowersActivity)
                        .load( data)
                        .into( holder.ivPostImageVideo)
                }
                mAdapter!!.expressionOnCreateViewHolder = { viewGroup ->
                    OwnPostItemBinding.inflate(
                        LayoutInflater.from(viewGroup.context),
                        viewGroup,
                        false
                    )
                }
                binding.rvmembersImages.also {
                    it.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
                    it.setHasFixedSize(false)
                    it.adapter =
                        mAdapter//DiscoverMemberStaggeredAdapter(postList, this@RecommendedFollowersActivity)
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