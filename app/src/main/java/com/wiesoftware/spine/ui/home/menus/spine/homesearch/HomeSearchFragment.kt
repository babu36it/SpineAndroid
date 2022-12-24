package com.wiesoftware.spine.ui.home.menus.spine.homesearch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.HashtagAutocompleteAdapter
import com.wiesoftware.spine.data.adapter.SearchForUContentAdapter
import com.wiesoftware.spine.data.net.reponses.AllUsersData
import com.wiesoftware.spine.data.net.reponses.HashtagData
import com.wiesoftware.spine.data.net.reponses.PostData
import com.wiesoftware.spine.data.net.reponses.SpineImpulseData
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.*
import com.wiesoftware.spine.ui.home.menus.events.event_details.EventDetailActivity
import com.wiesoftware.spine.ui.home.menus.profile.someonesprofile.SomeOneProfileActivity
import com.wiesoftware.spine.ui.home.menus.profile.tabs.posts.PostsFragment
import com.wiesoftware.spine.ui.home.menus.spine.SpineFragment
import com.wiesoftware.spine.ui.home.menus.spine.categories.CategoryAdapter
import com.wiesoftware.spine.ui.home.menus.spine.foryou.POST_BASE_IMG_FILE
import com.wiesoftware.spine.ui.home.menus.spine.foryou.POST_BASE_IMG_PRO
import com.wiesoftware.spine.ui.home.menus.spine.foryou.SpineForYouFragment
import com.wiesoftware.spine.ui.home.menus.spine.postdetails.PostDetailsActivity
import com.wiesoftware.spine.ui.home.menus.spine.practicioners.PractSelectlistAdapter
import com.wiesoftware.spine.ui.home.menus.spine.rec_followers.RecommendedFollowersAdapter
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.BaseAdapter
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

/**
 * By vivek kumar
 * Email: vivekpcst.kumar@gmail.com
 */
class HomeSearchFragment : Fragment(), KodeinAware, HomeSearchEventListener,
    SpineFragment.OnSearchHomeListener, HashtagAutocompleteAdapter.OnHashtagSelectedListener,
    /*RecommendedFollowersAdapter.RecommendedFollowersListener,*/
    SearchForUContentAdapter.SearchForUContentListener /*CategoryAdapter.HashtagEventListener */ {

    override val kodein by kodein()
    lateinit var binding: FragmentHomeSearchBinding
    val homeRepositry: HomeRepository by instance()
    var mContext: Context? = null
    var dataList: MutableList<HashtagData> = mutableListOf()
    var adapter: HashtagAutocompleteAdapter =
        HashtagAutocompleteAdapter(dataList, this@HomeSearchFragment)
    var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_search, container, false)
        val viewmodel = ViewModelProvider(this).get(HomeSearchViewmodel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.homeSearchEventListener = this

        homeRepositry.getUser().observe(viewLifecycleOwner, Observer { user ->
            userId = user.users_id!!
        })

        getUserHashtags()

        return binding.root
    }

    override fun onPosts() {
        getAllPosts()
        dataList.clear()
        adapter.notifyDataSetChanged()
        binding.button102.setBackgroundResource(R.drawable.black_round_background)
        binding.button103.setBackgroundResource(0)
        binding.button104.setBackgroundResource(0)
        binding.button102.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_white))
        binding.button103.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
        binding.button104.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
    }

    private fun getAllPosts() {

        lifecycleScope.launch {
            try {
                val postRes = homeRepositry.getAllPosts(1, 200, userId, 0, 0)
                if (postRes.status) {
                    POST_BASE_IMG_FILE = postRes.image
                    POST_BASE_IMG_PRO = postRes.profilImage
                    val postList: List<PostData> = postRes.data
                    binding.rvHomeSearch.also {
                        it.layoutManager = LinearLayoutManager(
                            requireContext(),
                            RecyclerView.VERTICAL,
                            false
                        )
                        it.setHasFixedSize(true)
                        it.adapter = SearchForUContentAdapter(postList, this@HomeSearchFragment)
                    }

                }

            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    override fun onTags() {
        dataList.clear()
        getUserHashtags()
        binding.button102.setBackgroundResource(0)
        binding.button103.setBackgroundResource(R.drawable.black_round_background)
        binding.button104.setBackgroundResource(0)
        binding.buttonPracticioners.setBackgroundResource(0)
        binding.button102.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
        binding.button103.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_white))
        binding.button104.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
        binding.buttonPracticioners.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.text_black
            )
        )
    }

    override fun onPeople() {
        getMembers()
        dataList.clear()
        adapter.notifyDataSetChanged()
        binding.button104.setBackgroundResource(0)
        binding.button103.setBackgroundResource(0)
        binding.buttonPracticioners.setBackgroundResource(0)
        binding.button102.setBackgroundResource(R.drawable.black_round_background)
        binding.button104.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
        binding.button103.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
        binding.button102.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_white))
        binding.buttonPracticioners.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.text_black
            )
        )
    }

    override fun onPracticioner() {
        dataList.clear()
        lifecycleScope.launch {
            var listString = ArrayList<String>(10)
            listString.add("1")
            listString.add("1")
            listString.add("1")
            listString.add("1")
            listString.add("1")
            listString.add("1")
            listString.add("1")

            var mAdapter = BaseAdapter<String>(requireContext())
            mAdapter!!.listOfItems = listString
            mAdapter!!.expressionViewHolderBinding = { data, viewBinding, context ->
                var holder = viewBinding as PractSelectedResultListitemBinding
            }
            mAdapter!!.expressionOnCreateViewHolder = { viewGroup ->
                PractSelectedResultListitemBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false
                )
            }
            binding.rvHomeSearch.also {
                it.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                it.setHasFixedSize(true)
                it.adapter = mAdapter
            }

            binding.button102.setBackgroundResource(0)
            binding.button103.setBackgroundResource(0)
            binding.button104.setBackgroundResource(0)
            binding.buttonPracticioners.setBackgroundResource(R.drawable.black_round_background)
            binding.button102.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_black
                )
            )
            binding.button103.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_black
                )
            )
            binding.button104.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_black
                )
            )
            binding.buttonPracticioners.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_white
                )
            )

        }
    }

    override fun onCategories() {
        dataList.clear()
        dataList.add(HashtagData("", "Boxing", "", "", "306,325"))
        dataList.add(HashtagData("", "Yoga", "", "", "306,325"))
        dataList.add(HashtagData("", "Life Transformation", "", "", "306,325"))
        dataList.add(HashtagData("", "Soccer", "", "", "306,325"))
        var mAdapter = BaseAdapter<HashtagData>(requireContext())
        mAdapter!!.listOfItems = dataList
        mAdapter!!.expressionViewHolderBinding = { data, viewBinding, context ->
            var holder = viewBinding as CatItemBinding
            holder.model = data
            holder.follow.setOnClickListener {
                //listener.
                onTrendingFollow(data)
            }
        }
        mAdapter!!.expressionOnCreateViewHolder = { viewGroup ->
            CatItemBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        }
        binding.rvHomeSearch.also {
            it.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            it.setHasFixedSize(true)
            it.adapter = mAdapter //CategoryAdapter(dataList, this)
        }
        binding.button102.setBackgroundResource(0)
        binding.button103.setBackgroundResource(0)
        binding.buttonPracticioners.setBackgroundResource(0)
        binding.button104.setBackgroundResource(R.drawable.black_round_background)
        binding.button102.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.text_black
            )
        )
        binding.button103.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.text_black
            )
        )
        binding.buttonPracticioners.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.text_black
            )
        )
        binding.button104.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.text_white
            )
        )

    }


    private fun getMembers() {

        lifecycleScope.launch {
            try {
                /*val allUsersRes=homeRepositry.getAllUsers(1,100,userId)   Temp commented MT
                if (allUsersRes.status){
                    BASE_IMAGE =allUsersRes.image
                    val allUsersData=allUsersRes.data
                    binding.rvHomeSearch.also {
                        it.layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
                        it.setHasFixedSize(true)
                        it.adapter= RecommendedFollowersAdapter(allUsersData,this@HomeSearchFragment)
                    }
                }*/
                var allUsersData: ArrayList<AllUsersData> = ArrayList()
                allUsersData.add(
                    AllUsersData(
                        "",
                        "Lorem Ipsome",
                        "Craig Warner",
                        "1",
                        "",
                        "",
                        "",
                        "Craig Warner",
                        ""
                    )
                )
                allUsersData.add(
                    AllUsersData(
                        "",
                        "Lorem Ipsome",
                        "Craig Warner",
                        "1",
                        "",
                        "",
                        "",
                        "Craig Warner",
                        ""
                    )
                )
                allUsersData.add(
                    AllUsersData(
                        "",
                        "Lorem Ipsome",
                        "Craig Warner",
                        "1",
                        "",
                        "",
                        "",
                        "Craig Warner",
                        ""
                    )
                )
                allUsersData.add(
                    AllUsersData(
                        "",
                        "Lorem Ipsome",
                        "Craig Warner",
                        "1",
                        "",
                        "",
                        "",
                        "Craig Warner",
                        ""
                    )
                )
                allUsersData.add(
                    AllUsersData(
                        "",
                        "Lorem Ipsome",
                        "Craig Warner",
                        "1",
                        "",
                        "",
                        "",
                        "Craig Warner",
                        ""
                    )
                )

                var mAdapter = BaseAdapter<AllUsersData>(requireContext())
                mAdapter!!.listOfItems = allUsersData
                mAdapter!!.expressionViewHolderBinding = { data, viewBinding, context ->
                    val holder = viewBinding as RecMembersItemBinding
                    holder.textView84.text = data.displayName
                    holder.textView85.text = data.town
                    if (data.followStatus.equals("1")) {
                        holder.followBtn.text = getString(R.string.following)
                    } else {
                        holder.followBtn.text = getString(R.string.follows)
                    }

                    holder.followBtn.setOnClickListener {

                        if (data.followStatus.equals("1")) {
                            //listener.
                            onUnfollowUser(data, 0)
                            holder.followBtn.text = getString(R.string.follows)
                        } else {
                            //listener.
                            onFollowClick(data, 0)
                            holder.followBtn.text = getString(R.string.following)
                        }
                    }
                    holder.imageView9.setOnClickListener {
                        //listener.
                        onViewProfile(data)
                    }
                    holder.textView84.setOnClickListener {
                        //listener.
                        onViewProfile(data)
                    }
                }
                mAdapter!!.expressionOnCreateViewHolder = { viewGroup ->
                    RecMembersItemBinding.inflate(
                        LayoutInflater.from(viewGroup.context),
                        viewGroup,
                        false
                    )
                }
                binding.rvHomeSearch.also {
                    it.layoutManager =
                        LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                    it.setHasFixedSize(true)
                    it.adapter =
                        mAdapter//RecommendedFollowersAdapter(allUsersData,this@HomeSearchFragment)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }

    }

    private fun getUserHashtags() {
        lifecycleScope.launch {
            try {
                // val res=homeRepositry.getUserHashtags()   MT comment for prototype
                /* if (!res.status){
                     dataList=res.data
                     adapter= HashtagAutocompleteAdapter(dataList,this@HomeSearchFragment)
                     binding.rvHomeSearch.also {
                         it.layoutManager=
                             LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
                         it.setHasFixedSize(true)
                         it.adapter=adapter
                         adapter.notifyDataSetChanged()
                     }
                 }
 */
                dataList.add(HashtagData("", "#lamborgini", "", "", "306,325"))
                dataList.add(HashtagData("", "#lamborgini", "", "", "306,325"))
                dataList.add(HashtagData("", "#lamborgini", "", "", "306,325"))
                dataList.add(HashtagData("", "#lamborgini", "", "", "306,325"))
                var mAdapter = BaseAdapter<HashtagData>(requireContext())
                mAdapter!!.listOfItems = dataList
                mAdapter!!.expressionViewHolderBinding = { data, viewBinding, context ->
                    var holder = viewBinding as HashtagAutoItemBinding
                    holder.model = data
                    holder.textView268.setOnClickListener {
                        //listener.
                        onHashtagSelected(data)
                    }
                    /*temp code*/
                    holder.textView268.text = data.hash_title
                    holder.textView271.text = data.totalCount
                }
                //adapter= HashtagAutocompleteAdapter(dataList,this@HomeSearchFragment)
                mAdapter!!.expressionOnCreateViewHolder = { viewGroup ->
                    HashtagAutoItemBinding.inflate(
                        LayoutInflater.from(viewGroup.context),
                        viewGroup,
                        false
                    )
                }
                binding.rvHomeSearch.also {
                    it.layoutManager =
                        LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                    it.setHasFixedSize(true)
                    it.adapter = mAdapter
                    // adapter.notifyDataSetChanged()
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = requireContext()
    }


    override fun onSearch(context: Context, searchTeaxt: String) {
        // mContext=context
        if (mContext != null) {
            searchTeaxt.toast(requireContext())
            adapter.filter.filter(searchTeaxt)
        }

    }

    override fun onHashtagSelected(hashtagData: HashtagData) {

    }

    fun onFollowClick(allUsersData: AllUsersData, position: Int) {
        lifecycleScope.launch {
            try {
                val res = homeRepositry.addUserFollow(userId, allUsersData.usersId)
                if (res.status) {
                    getString(R.string.following).toast(requireContext())
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    fun onViewProfile(allUsersData: AllUsersData) {
        val intent = Intent(requireContext(), SomeOneProfileActivity::class.java)
        intent.putExtra(SomeOneProfileActivity.SOME_ONES_USER_ID, allUsersData.usersId)
        startActivity(intent)
    }

    fun onUnfollowUser(allUsersData: AllUsersData, position: Int) {
        lifecycleScope.launch {
            try {
                val unfollowUserId = allUsersData.usersId
                val res = homeRepositry.unFollowUser(userId, unfollowUserId)
                if (res.status) {
                    "User unfollowed".toast(requireContext())
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

    override fun onPostDetails(postData: PostData) {
        if (postData.eventPost.equals("1")) {
            val intent = Intent(requireContext(), EventDetailActivity::class.java)
            intent.putExtra(SpineForYouFragment.IS_EVE_FROM_POST, true)
            intent.putExtra(SpineForYouFragment.EVENT_POST_ID, postData.id)
            intent.putExtra("event_id", postData.id)
            startActivity(intent)


        } else {
            val intent = Intent(requireContext(), PostDetailsActivity::class.java)
            intent.putExtra(PostsFragment.POST_DATA, postData)
            intent.putExtra(PostsFragment.BASE_POST_IMG, POST_BASE_IMG_FILE)
            intent.putExtra(PostsFragment.PROFILE_IMG_BASE, POST_BASE_IMG_PRO)
            startActivity(intent)
        }
    }

    fun onTrendingFollow(hashtag: HashtagData) {

    }
}