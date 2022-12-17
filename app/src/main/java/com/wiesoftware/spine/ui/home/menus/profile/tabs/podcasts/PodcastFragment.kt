package com.wiesoftware.spine.ui.home.menus.profile.tabs.podcasts


import android.app.ProgressDialog
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
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.ListPodcastAdapter
import com.wiesoftware.spine.data.net.reponses.PodDatas
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.data.repo.PodcastRepository
import com.wiesoftware.spine.databinding.FragmentPodcastBinding
import com.wiesoftware.spine.ui.home.menus.podcasts.addrss.AddRssActivity
import com.wiesoftware.spine.ui.home.menus.podcasts.podcastdetails.PodcastDetailActivity
import com.wiesoftware.spine.ui.home.menus.podcasts.watch.WatchPodcastsFragment
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class PodcastFragment : Fragment(), KodeinAware, PodcastsEventListner,
    ListPodcastAdapter.OnPodEveListener {

    override val kodein by kodein()
    val homeRepositry: HomeRepository by instance()
    val podcastRepository: PodcastRepository by instance()
    lateinit var binding: FragmentPodcastBinding
    lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_podcast, container, false)
        val viewmodel = ViewModelProvider(this).get(PodcastsViewmodel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.podcastsEventListner = this
        progressDialog = ProgressDialog(context)
        homeRepositry.getUser().observe(viewLifecycleOwner, Observer { user ->
            val userId = user.users_id!!
            getOwnPodcasts(userId)
        })



        return binding.root
    }

    private fun getOwnPodcasts(userId: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = podcastRepository.getAllPodcasts()
                if (res.status) {
                    dismissProgressDailog()
                    val dataList = res.data
                    if (dataList.isNotEmpty()) {
                        binding.tvNoPodcast.visibility = View.GONE
                    } else {
                        binding.tvNoPodcast.visibility = View.VISIBLE
                    }
                    setPodData(dataList)
                } else {
                    dismissProgressDailog()
                    binding.tvNoPodcast.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), res.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                dismissProgressDailog()
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
                dismissProgressDailog()
            }
        }
    }

    private fun setPodData(dataList: List<PodDatas>) {
        /*if (dataList.size > 0) {
            //   binding.button36.visibility=View.GONE
        }*/
        binding.rvOwnPods.also {
            it.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            it.setHasFixedSize(true)
            it.adapter = ListPodcastAdapter(dataList, this)
            it.adapter!!.notifyDataSetChanged()
        }
    }

    override fun onAddPodCast() {
        startActivity(Intent(requireContext(), AddRssActivity::class.java))
    }

    override fun onPodDetails(podcastData: PodDatas) {
        val intent = Intent(requireContext(), PodcastDetailActivity::class.java)
        intent.putExtra(WatchPodcastsFragment.POD_ID, podcastData.id)
        startActivity(intent)
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