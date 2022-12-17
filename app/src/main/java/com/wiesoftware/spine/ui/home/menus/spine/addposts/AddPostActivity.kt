 package com.wiesoftware.spine.ui.home.menus.spine.addposts

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.databinding.ActivityAddPostBinding
import com.wiesoftware.spine.ui.home.menus.events.addevents.AddEventActivity
import com.wiesoftware.spine.ui.home.menus.events.addordup.AddOrDupEventActivity
import com.wiesoftware.spine.ui.home.menus.podcasts.addpodcasts.AddPodcastActivity
import com.wiesoftware.spine.ui.home.menus.podcasts.addrss.AddRssActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postmedia.PostMediaActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.poststory.AddStoryActivity
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postthought.PostThoughtActivity
import com.wiesoftware.spine.util.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

 class AddPostActivity : AppCompatActivity(),KodeinAware, AddPostEventListener {
     override fun attachBaseContext(base: Context?) {
         super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
     }

     override val kodein by kodein()
     lateinit var binding: ActivityAddPostBinding
     lateinit var viewmodel: AddPostViewmodel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_add_post)
        viewmodel=ViewModelProvider(this).get(AddPostViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.addPostEventListener=this

    }

     override fun onBack() {
         onBackPressed()
     }

     override fun onPostThought() {
        startActivity(Intent(this,PostThoughtActivity::class.java))
     }

     override fun onPostPictureOrVideo() {
         startActivity(Intent(this,PostMediaActivity::class.java))
     }

     override fun onPostStory() {
         startActivity(Intent(this,AddStoryActivity::class.java))
     }

     override fun onPostEvent() {
         startActivity(Intent(this,AddOrDupEventActivity::class.java))
     }

     override fun onPostPodcast() {
         startActivity(Intent(this,AddRssActivity::class.java))
     }
 }