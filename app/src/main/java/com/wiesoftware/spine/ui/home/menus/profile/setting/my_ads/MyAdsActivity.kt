package com.wiesoftware.spine.ui.home.menus.profile.setting.my_ads

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityMyAdsBinding
import com.wiesoftware.spine.ui.home.menus.profile.setting.my_ads.Inprogress.InProgressAdapter
import com.wiesoftware.spine.ui.home.menus.profile.setting.my_ads.current.CurrentAdapter
import com.wiesoftware.spine.ui.home.menus.profile.setting.my_ads.current.CurrentModel
import com.wiesoftware.spine.ui.home.menus.profile.setting.my_ads.past.PastAdapter
import com.wiesoftware.spine.ui.home.menus.profile.setting.my_ads.past.PastModel
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MyAdsActivity : AppCompatActivity(), KodeinAware, MyAdsEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    lateinit var binding: ActivityMyAdsBinding
    val homeRepositry: HomeRepository by instance()
    lateinit var userId: String
    lateinit var inProgressAdapter: InProgressAdapter
    lateinit var currentAdapter: CurrentAdapter
    lateinit var pastAdapter: PastAdapter
    var inProgressModel: ArrayList<InProgressModel> = arrayListOf()
    var currentModel: ArrayList<CurrentModel> = arrayListOf()
    var pastModel: java.util.ArrayList<PastModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_ads)
        val viewModel = ViewModelProvider(this).get(MyAdsViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.myAdsEventListener = this
        homeRepositry.getUser().observe(this, Observer { user ->
            userId = user.users_id!!
            getMyAds()
        })
    }

    private fun setInprogressModel() {
        inProgressModel.clear()
        inProgressModel.add(
            InProgressModel(
                "Herbal Essence",
                "Made in Germany",
                "Lorem ipsum dolor sit amet dolor sit amet ipsum \n" +
                        "at de lorem ipsum dolor sit amet dolor sit amet."
            )
        )
    }

    private fun setCurrentModel() {
        currentModel.clear()
        currentModel.add(
            CurrentModel(
                "09\n May",
                "Yoga Weekend Retreat - \n" +
                        "Reclaiming Your Center",
                "Madrid, Spain | 2 days",
                "Lorem ipsum dolor sit amet dolor sit amet ipsum \n" +
                        "at de lorem ipsum dolor sit amet dolor sit amet.",
                "Duration: 1 week",
                "Start date: 12 May 2020",
                "Time: 16:00",
                "Cost: 100 €"
            )
        )


    }

    private fun setPastModel() {
        pastModel.clear()
        pastModel.add(PastModel("Lorem ipsum dolor sit amet dolor sit amet ipsum \n" +
                "at de lorem ipsum dolor sit amet dolor sit amet.",
        "Duration: 1 week","Start date: 12 January 2020",
        "Time: 16:00","Cost: 100 €"));
    }

    private fun getMyAds() {
        setInprogressModel()
        setCurrentModel()
        setPastModel()
        lifecycleScope.launch {
            try {
                binding.recyclerView8.layoutManager = LinearLayoutManager(this@MyAdsActivity)
                binding.recyclerView8.setHasFixedSize(true)
                binding.recyclerView8.isNestedScrollingEnabled = false
                inProgressAdapter = InProgressAdapter(this@MyAdsActivity, inProgressModel)
                binding.recyclerView8.adapter = inProgressAdapter



                binding.recyclerView10.layoutManager = LinearLayoutManager(this@MyAdsActivity)
                binding.recyclerView10.setHasFixedSize(true)
                binding.recyclerView8.isNestedScrollingEnabled = false
                currentAdapter = CurrentAdapter(this@MyAdsActivity, currentModel)
                binding.recyclerView10.adapter = currentAdapter


                binding.rvPast.layoutManager = LinearLayoutManager(this@MyAdsActivity)
                binding.rvPast.setHasFixedSize(true)
                binding.rvPast.isNestedScrollingEnabled = false
                pastAdapter = PastAdapter(this@MyAdsActivity, pastModel)
                binding.rvPast.adapter = pastAdapter

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onBack() {
        onBackPressed()
    }
}