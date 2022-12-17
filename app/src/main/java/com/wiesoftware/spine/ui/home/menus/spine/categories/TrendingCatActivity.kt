package com.wiesoftware.spine.ui.home.menus.spine.categories

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.net.reponses.HashtagData
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityTrendingCatBinding
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class TrendingCatActivity : AppCompatActivity(),KodeinAware, TrendingCatEventListener,
    CategoryAdapter.HashtagEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val factory: TrendingCatViewmodelFactory by instance()
    val homeRepositry: HomeRepository by instance()
    lateinit var binding: ActivityTrendingCatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_trending_cat)
        val viewmodel=ViewModelProvider(this,factory).get(TrendingCatViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.trendingCatEventListener=this
        setCategories()

    }

    private fun setCategories() {

        lifecycleScope.launch {
            val hastagRes= homeRepositry.getHashtagList()
            if (hastagRes.status){
                val hashtagDataList=hastagRes.data
                binding.rvCategories.also {
                    it.layoutManager=LinearLayoutManager(this@TrendingCatActivity,RecyclerView.VERTICAL,false)
                    it.setHasFixedSize(true)
                    it.adapter=CategoryAdapter(hashtagDataList,this@TrendingCatActivity)
                }
            }
        }
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun allCategory() {
        binding.cv.setCardBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark))
        binding.cv1.setCardBackgroundColor(ContextCompat.getColor(this,R.color.white))
        binding.textView28.setTextColor(ContextCompat.getColor(this,R.color.text_white))
        binding.textView29.setTextColor(ContextCompat.getColor(this,R.color.text_black))
    }

    override fun trendingCategory() {
        binding.cv1.setCardBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark))
        binding.cv.setCardBackgroundColor(ContextCompat.getColor(this,R.color.white))
        binding.textView28.setTextColor(ContextCompat.getColor(this,R.color.text_black))
        binding.textView29.setTextColor(ContextCompat.getColor(this,R.color.text_white))
    }

    override fun onTrendingFollow(hashtag: HashtagData) {
        "wait".toast(this)
    }
}