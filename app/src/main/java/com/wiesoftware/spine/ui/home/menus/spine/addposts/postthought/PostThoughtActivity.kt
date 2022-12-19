package com.wiesoftware.spine.ui.home.menus.spine.addposts.postthought

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.databinding.ActivityPostThoughtBinding
import com.wiesoftware.spine.ui.home.menus.spine.addposts.hashtags.HashtagActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

var COLOR_ID: String = "color_id"
var THOUGHT: String = "thought"

class PostThoughtActivity : AppCompatActivity(), KodeinAware, PostThoughtEventListener,
    PostOptionsAdapter.ThoughtEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val factory: PostThoughtViewModelFactory by instance()
    lateinit var binding: ActivityPostThoughtBinding

    var bg_color = ArrayList<String>()
    var activated_bg: String? = null
    var colors = ArrayList<ThoughtsData>()
    lateinit var adapter: PostOptionsAdapter
    lateinit var viewadapter: ViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_thought)
        val viewmodel = ViewModelProvider(this, factory).get(PostThoughtViewModel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.postThoughtEventListener = this

        bg_color.addAll(arrayOf("#B89A8A", "#E42E11", "#008000", "#2196F3"))
        colors.addAll(
            arrayOf(
                ThoughtsData(R.color.colorPrimaryDark, ""),
                ThoughtsData(R.color.color_red, ""),
                ThoughtsData(R.color.green, ""),
                ThoughtsData(R.color.blue, "")
            )
        )
        activated_bg = bg_color[0]
        adapter = PostOptionsAdapter(colors, this, this)
        binding.vp2.adapter = adapter
        binding.indicator.setViewPager(binding.vp2)

        binding.rgBtn.setOnCheckedChangeListener { radioGroup, i ->
            val rb: RadioButton = radioGroup.findViewById(i)
            val index: Int = radioGroup.indexOfChild(rb)
            binding.vp2.setCurrentItem(index)
        }

/*
        binding.vp2.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

                activated_bg = bg_color[position]

                */
/*val rBtnId = binding.rgBtn.getChildAt(position).id
                binding.rgBtn.check(rBtnId)*//*

            }
        })
*/

        binding.vp2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                activated_bg = bg_color[position]

                val rBtnId = binding.rgBtn.getChildAt(position).id
                binding.rgBtn.check(rBtnId)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onThoughtNext(thought: String) {
        val intent = Intent(this, HashtagActivity::class.java)
        intent.putExtra(COLOR_ID, activated_bg)
        intent.putExtra(THOUGHT, thought)
        startActivity(intent)
    }

    override fun onThoughtChanged(thought: String) {
        colors.forEach { thoughtsData ->
            thoughtsData.thought = thought
        }
        adapter.notifyDataSetChanged()
    }

    override fun onbackpressed() {
        finish()
    }

}

data class ThoughtsData(
    val color: Int,
    var thought: String
) {}