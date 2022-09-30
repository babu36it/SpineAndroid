package com.cometchat.pro.uikit

import adapter.TabAdapter
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.cometchat.pro.uikit.SharedMedia.SharedFilesFragment
import com.cometchat.pro.uikit.SharedMedia.SharedImagesFragment
import com.cometchat.pro.uikit.SharedMedia.SharedVideosFragment
import com.google.android.material.tabs.TabLayout
import utils.Utils

class SharedMediaView : RelativeLayout {
    private var c: Context? = null
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var Id: String? = null
    private var type: String? = null
    private var attrs: AttributeSet? = null
    private var adapter: TabAdapter? = null

    constructor(context: Context) : super(context) {
        this.c = context
        initViewComponent(context, null, -1, -1)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.attrs = attrs
        this.c = context
        initViewComponent(context, attrs, -1, -1)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.c = context
        this.attrs = attrs
        initViewComponent(context, attrs, defStyleAttr, -1)
    }

    private fun initViewComponent(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val view = View.inflate(context, R.layout.cometchat_shared_media, null)
        val a = getContext().theme.obtainStyledAttributes(attributeSet, R.styleable.SharedMediaView, 0, 0)
        addView(view)
        val bundle = Bundle()
        bundle.putString("Id", Id)
        bundle.putString("type", type)
        if (type != null) {
            viewPager = findViewById(R.id.viewPager)
            tabLayout = view.findViewById(R.id.tabLayout)
            adapter = TabAdapter((context as FragmentActivity).supportFragmentManager)
            val images = SharedImagesFragment()
            images.arguments = bundle
            adapter!!.addFragment(images, resources.getString(R.string.images))
            val videos = SharedVideosFragment()
            videos.arguments = bundle
            adapter!!.addFragment(videos, resources.getString(R.string.videos))
            val files = SharedFilesFragment()
            files.arguments = bundle
            adapter!!.addFragment(files, resources.getString(R.string.files))
            viewPager!!.setAdapter(adapter)
            viewPager!!.setOffscreenPageLimit(3)
            tabLayout!!.setupWithViewPager(viewPager)
            if (Utils.isDarkMode(context)) {
                tabLayout!!.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.grey)))
                tabLayout!!.setTabTextColors(resources.getColor(R.color.light_grey), resources.getColor(R.color.textColorWhite))
            } else {
                tabLayout!!.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.textColorWhite)))
                tabLayout!!.setTabTextColors(resources.getColor(R.color.primaryTextColor), resources.getColor(R.color.textColorWhite))
            }
        }
    }

    fun setRecieverId(uid: String?) {
        Id = uid
    }

    fun setRecieverType(receiverTypeUser: String?) {
        type = receiverTypeUser
    }

    fun reload() {
        initViewComponent(context, null, -1, -1)
    }
}