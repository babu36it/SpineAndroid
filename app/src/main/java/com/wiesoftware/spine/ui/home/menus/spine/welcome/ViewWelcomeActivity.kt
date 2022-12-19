package com.wiesoftware.spine.ui.home.menus.spine.welcome

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.exoplayer2.SimpleExoPlayer
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.net.reponses.WelcomeData
import com.wiesoftware.spine.data.repo.AuthRepository
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityViewWelcomeBinding
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.Coroutines
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class ViewWelcomeActivity : AppCompatActivity(),KodeinAware, ViewWelcomeEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val factory: ViewWelcomeViewModelFactory by instance()
    val homeRepositry: HomeRepository by instance()
    val authRepository: AuthRepository by instance()
    lateinit var binding: ActivityViewWelcomeBinding
    lateinit var viewmodel: ViewWelcomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_view_welcome)
        viewmodel=ViewModelProvider(this, factory).get(ViewWelcomeViewModel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.viewWelcomeEventListener=this
        getWelcomeList()
    }

    private fun getWelcomeList(){
        Coroutines.main {
            try {
                val welcomeResponse=authRepository.getWelcomeData()
                if (welcomeResponse.status!!){
                    BASE_IMAGE =welcomeResponse.image
                    val welcomeList:List<WelcomeData> = welcomeResponse.data!!
                    binding.vp2.adapter=ViewWelcomeAdapter(welcomeList, this)
                    binding.vp2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                        override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                        ) {
                            super.onPageScrolled(position, positionOffset, positionOffsetPixels)

                        }

                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)

                        }

                        override fun onPageScrollStateChanged(state: Int) {
                            super.onPageScrollStateChanged(state)

                        }
                    })
                }else{
                    welcomeResponse.message!!.toast(this)
                }

            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }

    }


    fun enterPipMode() {

    }
    override fun onBack() {
        onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer(ViewWelcomeAdapter.VidHolder.simpleExoplayer)
    }

    override fun onStop() {
        super.onStop()
        pausePlayer(ViewWelcomeAdapter.VidHolder.simpleExoplayer)
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseExoPlayer(ViewWelcomeAdapter.VidHolder.simpleExoplayer)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        pausePlayer(ViewWelcomeAdapter.VidHolder.simpleExoplayer)
        releaseExoPlayer(ViewWelcomeAdapter.VidHolder.simpleExoplayer)
    }
    fun pausePlayer(exoPlayer: SimpleExoPlayer?) {
        if (exoPlayer != null) {
            exoPlayer.playWhenReady = false
        }
    }
    fun releaseExoPlayer(exoPlayer: SimpleExoPlayer?) {
        exoPlayer?.release()
    }

}