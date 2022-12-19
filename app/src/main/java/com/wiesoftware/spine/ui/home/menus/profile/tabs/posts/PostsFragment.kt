package com.wiesoftware.spine.ui.home.menus.profile.tabs.posts

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.OwnPostAdapter
import com.wiesoftware.spine.data.net.reponses.PostData
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.FragmentPostsBinding
import com.wiesoftware.spine.ui.home.menus.spine.addposts.AddPostActivity
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.postdetails.PostDetailsActivity
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

var profileImgBase = ""

class PostsFragment : Fragment(), KodeinAware, PostEventListner,
    OwnPostAdapter.OwnPostSelectedListener {

    override val kodein by kodein()
    private val factory: PostViewModelFactory by instance()
    private val homeRepositry: HomeRepository by instance()
    lateinit var binding: FragmentPostsBinding
    var userId: String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_posts, container, false)
        val viewmodel = ViewModelProvider(this, factory).get(PostViewModel::class.java)
        binding.model = viewmodel
        viewmodel.postEventListner = this
        viewmodel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            userId = user.users_id!!
            getOwnPost()
        })

        return binding.root
    }

    private fun getOwnPost() {
        lifecycleScope.launch {
            try {
                val postRes = homeRepositry.getAllPosts(1, 200, userId, 0, 1)
                if (postRes.status) {
                    profileImgBase = postRes.profilImage
                    val postList: List<PostData> = postRes.data
                    if (postList.size > 0) {
                        binding.tvNoPost.visibility = View.GONE
                    } else {
                        binding.tvNoPost.visibility = View.VISIBLE
                    }
                    binding.rvPost.also {
                        it.layoutManager = LinearLayoutManager(activity)
                        it.setHasFixedSize(false)
                        it.adapter = OwnPostAdapter(postList, this@PostsFragment)
                        it.adapter!!.notifyDataSetChanged()
                    }
                } else {
                    binding.tvNoPost.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), postRes.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    override fun createPost() {
        startActivity(Intent(requireContext(), AddPostActivity::class.java))
    }

    override fun onPostSelected(postData: PostData) {
        /*val profileImg=BASE_IMAGE+postData.files
        if (!(postData.files).isNullOrEmpty()){
            val type= if (isVideo(profileImg)){"1"}else{"0"}
            val intent=Intent(requireContext(), ViewMediaInLargeActivity::class.java)
            intent.putExtra(ViewMediaInLargeActivity.MEDIA_URL,profileImg)
            intent.putExtra(ViewMediaInLargeActivity.MEDIA_TYPE,type)
            startActivity(intent)
        }*/
        val intent = Intent(requireContext(), PostDetailsActivity::class.java)
        intent.putExtra(POST_DATA, postData)
        intent.putExtra(BASE_POST_IMG, BASE_IMAGE)
        intent.putExtra(PROFILE_IMG_BASE, profileImgBase)
        startActivity(intent)

    }


    companion object {
        val POST_DATA = "postData"
        val BASE_POST_IMG = "basePostImg"
        val PROFILE_IMG_BASE = "profileImgBase"
    }

}