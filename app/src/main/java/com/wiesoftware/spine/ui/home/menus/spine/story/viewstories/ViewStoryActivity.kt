package com.wiesoftware.spine.ui.home.menus.spine.story.viewstories

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseIntArray
import android.widget.PopupMenu
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope

import com.bumptech.glide.Glide
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheUtil
import com.google.android.exoplayer2.util.Util
import com.wiesoftware.spine.BuildConfig
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.SpineApplication
import com.wiesoftware.spine.data.net.reponses.FollowingStoriesData
import com.wiesoftware.spine.data.net.reponses.FollwingData
import com.wiesoftware.spine.data.net.reponses.StoryMothData
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityViewStoryBinding
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE
import com.wiesoftware.spine.ui.home.menus.spine.story.viewstories.customstoryview.StoryPagerAdapter
import com.wiesoftware.spine.ui.home.menus.spine.story.viewstories.storyscreen.PageChangeListener
import com.wiesoftware.spine.ui.home.menus.spine.story.viewstories.storyscreen.PageViewOperator
import com.wiesoftware.spine.ui.home.menus.spine.story.viewstories.storyscreen.StoryDisplayFragment
import com.wiesoftware.spine.util.*
import kotlinx.android.synthetic.main.activity_view_story.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ViewStoryActivity : AppCompatActivity(),
   KodeinAware, ViewStoryEventListener, PageViewOperator {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    private lateinit var pagerAdapter: StoryPagerAdapter
    private var currentPage: Int = 0
    private var prevDragPosition = 0


    override val kodein by kodein()
    val factory: ViewStoryViewmodelFactory by instance()
    val homeRepositry: HomeRepository by instance()
    lateinit var binding: ActivityViewStoryBinding
    lateinit var user_id: String
    lateinit var storyList:ArrayList<FollowingStoriesData>
    lateinit var storyUser: ArrayList<FollwingData>

    var newStoryUsers: MutableList<FollwingData> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_view_story)
        val viewmodel=ViewModelProvider(this,factory).get(ViewStoryViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.viewStoryEventListener=this

        viewmodel.getLoggedInUser().observe(this, Observer { user->
            user_id=user.users_id!!
            var isSelected = false
            try {
                isSelected=intent.getBooleanExtra(StoryDisplayFragment.IS_STORY_TIME_SELECTED,false)
            }catch (e: Exception){
                e.printStackTrace()
            }
            if (isSelected){
                getStoriesByTime()
            }else{
                getStoriesData()
            }
        })

    }

    private fun getStoriesByTime() {
        val storyMonthData= intent.getSerializableExtra(StoryDisplayFragment.STORY_TIME_DATA) as StoryMothData
        val monthId=storyMonthData.month_id
        val year=storyMonthData.year
        lifecycleScope.launch {
         try {
             val res=homeRepositry.getStoryListByMonthWithYear(user_id,monthId,year)
             if (res.status){
                 STORY_IMAGE=res.image
                 storyUser = res.data
                 //preLoadStories(storyUser)
                 setUpPager(storyUser)
             }
         }catch (e: Exception){
             e.printStackTrace()
         } catch (e: NoInternetException){
             e.printStackTrace()
         }
        }
    }

    private fun setUpPager(storyUser: ArrayList<FollwingData>) {
        //val storyUserList = StoryGenerator.generateStories()
        preLoadStories(storyUser)

        pagerAdapter = StoryPagerAdapter(
            supportFragmentManager,
            storyUser
        )
        viewPager.adapter = pagerAdapter
        viewPager.currentItem = currentPage
        viewPager.setPageTransformer(
            true,
            CubeOutTransformer()
        )
        viewPager.addOnPageChangeListener(object : PageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
            }

            override fun onPageScrollCanceled() {
                currentFragment()?.resumeCurrentStory()
            }
        })
    }
    private fun currentFragment(): StoryDisplayFragment? {
        return pagerAdapter.findFragmentByPosition(viewPager, currentPage) as StoryDisplayFragment
    }
    private fun preLoadStories(storyUserList: ArrayList<FollwingData>) {
        val imageList = mutableListOf<String>()
        val videoList = mutableListOf<String>()

        storyUserList.forEach { storyUsers ->
            storyList=storyUsers.stories_data
            storyUsers.stories_data.forEach { story ->

                if(story.isVideo()){
                    videoList.add(STORY_IMAGE+story.media_file)
                } else {
                    imageList.add(STORY_IMAGE+story.media_file)
                }


            }

        }


        preLoadVideos(videoList)
        preLoadImages(imageList)
        Log.e("imgList:::",""+imageList)

    }

   private fun isVideo(media_file:String) =  media_file?.contains(".mp4",true) ?: false ||
            media_file?.contains(".mov",true) ?: false ||
            media_file?.contains(".3gp",true) ?: false ||
            media_file?.contains(".avi",true) ?: false


    private fun preLoadVideos(videoList: MutableList<String>) {
        videoList.map { data ->
            GlobalScope.async {
                val dataUri = Uri.parse(data)
                val dataSpec = DataSpec(dataUri, 0, 500 * 1024, null)
                val dataSource: DataSource =
                    DefaultDataSourceFactory(
                        applicationContext,
                        Util.getUserAgent(applicationContext, getString(R.string.app_name))
                    ).createDataSource()

                val listener =
                    CacheUtil.ProgressListener { requestLength: Long, bytesCached: Long, _: Long ->
                        val downloadPercentage = (bytesCached * 100.0
                                / requestLength)
                        Log.e("preLoadVideos", "downloadPercentage: $downloadPercentage")
                    }

                try {
                    CacheUtil.cache(
                        dataSpec,
                        SpineApplication.simpleCache,
                        CacheUtil.DEFAULT_CACHE_KEY_FACTORY,
                        dataSource,
                        listener,
                        null
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun preLoadImages(imageList: MutableList<String>) {
        imageList.forEach { imageStory ->
            Glide.with(this).load(imageStory).preload()
        }
    }

    private fun getStoriesData() {
        storyUser = ArrayList()
        var list : ArrayList<FollowingStoriesData> = ArrayList()
        list.add(FollowingStoriesData("","","","","https://demonuts.com/Demonuts/SampleImages/W-03.JPG","","Awesome","","1",12,5,1,1))
        list.add(FollowingStoriesData("","","","","https://tableforchange.com/wp-content/uploads/2017/06/yoga-quotes-11-min.png","","Sky","","1",1,25,1,1))
        lifecycleScope.launch {

            try {
                storyUser.add(FollwingData("","",list,"",""))
                setUpPager(storyUser)
            /*    val storyRes=homeRepositry.getFollowingUsersStoryList(1,100,user_id)//homeRepositry.getYourStories(userId)
                if (storyRes.status){
                    STORY_IMAGE=storyRes.image
                    storyUser = storyRes.data
                    //preLoadStories(storyUser)
                    setUpPager(storyUser)
                } Temparary commented MT*/

            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onStorySave() {
        onStorySave("")
    }

    fun onStorySave(storyid: String) {

            lifecycleScope.launch {
                try {
                    val res=homeRepositry.saveStory(user_id,storyid)
                    if (res.status){
                        "Story saved successfully".toast(this@ViewStoryActivity)
                    }
                }catch (e: ApiException){
                    e.printStackTrace()
                }catch (e: NoInternetException){
                    e.printStackTrace()
                }
            }


    }

    override fun onStoryMore() {
        onShowPopupMenu()
    }

    fun onShowPopupMenu() {
        val popupMenu: PopupMenu = PopupMenu(this,binding.StoryMore)
        popupMenu.menuInflater.inflate(R.menu.story_menu,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.action_share ->
                   shareStory("","")
            }
            true
        })
        popupMenu.show()
    }

    private fun shareStory(name: String,storyImg: String) {
        val appId= BuildConfig.APPLICATION_ID
        val uName=name
        val textToShare="Hey! $uName has shared you their story from Spine.\n \n" +
                "$title  \n \n $storyImg \n \n  Check your spine app for details. If you don't have spine app click below link to download->" +
                "\n\n https://play.google.com/store/apps/details?id=$appId"
        ShareCompat.IntentBuilder
            .from(this)
            .setText(textToShare)
            .setType("text/plain")
            .setChooserTitle("Share Spine Content")
            .startChooser();
    }


    override fun onDestroy() {
        super.onDestroy()
    }


    override fun onPause() {
        super.onPause()

    }

    override fun onStop() {
        super.onStop()

    }

    companion object{
        val progressState = SparseIntArray()
    }

    override fun backPageView() {
        if (viewPager.currentItem > 0) {
            try {
                fakeDrag(false)
            } catch (e: Exception) {
                //NO OP
            }
        }
    }

    override fun nextPageView() {
        if (viewPager.currentItem + 1 < viewPager.adapter?.count ?: 0) {
            try {
                fakeDrag(true)
            } catch (e: Exception) {
                //NO OP
            }
        } else {
            onBackPressed()
            //Toast.makeText(this, "All stories displayed.", Toast.LENGTH_LONG).show()
        }
    }

    private fun fakeDrag(forward: Boolean) {
        if (prevDragPosition == 0 && viewPager.beginFakeDrag()) {
            ValueAnimator.ofInt(0, viewPager.width).apply {
                duration = 400L
                interpolator = FastOutSlowInInterpolator()
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {}

                    override fun onAnimationEnd(animation: Animator?) {
                        removeAllUpdateListeners()
                        if (viewPager.isFakeDragging) {
                            viewPager.endFakeDrag()
                        }
                        prevDragPosition = 0
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                        removeAllUpdateListeners()
                        if (viewPager.isFakeDragging) {
                            viewPager.endFakeDrag()
                        }
                        prevDragPosition = 0
                    }

                    override fun onAnimationStart(p0: Animator?) {}
                })
                addUpdateListener {
                    if (!viewPager.isFakeDragging) return@addUpdateListener
                    val dragPosition: Int = it.animatedValue as Int
                    val dragOffset: Float =
                        ((dragPosition - prevDragPosition) * if (forward) -1 else 1).toFloat()
                    prevDragPosition = dragPosition
                    viewPager.fakeDragBy(dragOffset)
                }
            }.start()
        }
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }

    fun currentTimeToLong(): Long {
        return System.currentTimeMillis()
    }

    fun convertDateToLong(date: String): Long {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault())//2021-02-25 12:40:55
        return df.parse(date)!!.time
    }
}