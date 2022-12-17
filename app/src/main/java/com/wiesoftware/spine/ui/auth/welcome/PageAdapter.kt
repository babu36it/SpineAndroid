package com.wiesoftware.spine.ui.auth.welcome

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.wiesoftware.spine.data.net.reponses.welcompageresponse.WelcomePageReponse
import com.wiesoftware.spine.databinding.WelcomeScreenBinding


class PageAdapter(var pages: List<WelcomePageReponse.Data>, val baseImgUrl: String) :
    PagerAdapter() {


    override fun getCount(): Int {
        if (pages == null)
            return 0
        return pages.size + 1
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding: WelcomeScreenBinding =
            WelcomeScreenBinding.inflate(LayoutInflater.from(container.context))

        if (position == 0) {
            binding.ivLogo.visibility = View.VISIBLE
            binding.tvProjectTitle.visibility = View.VISIBLE
            binding.tvTitle.visibility = View.GONE
            binding.tvDesc.visibility = View.GONE
            binding.tvBottomText.visibility = View.VISIBLE

        } else if (position == pages.size) {
            binding.ivLogo.visibility = View.VISIBLE
            binding.tvProjectTitle.visibility = View.VISIBLE
            binding.tvTitle.visibility = View.GONE
            binding.tvDesc.visibility = View.GONE
            binding.tvBottomText.visibility = View.GONE
        } else {
            binding.ivLogo.visibility = View.GONE
            binding.tvProjectTitle.visibility = View.GONE
            binding.tvBottomText.visibility = View.GONE
        }

        if(position > 0 && position < pages.size) {
            Glide.with(container.context).asBitmap().load(baseImgUrl + pages[position - 1].backgroundImage)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .into(object : SimpleTarget<Bitmap>(720, 1020) {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        var drawable = BitmapDrawable(container.resources, resource)
                        binding.relContainer.background = drawable
                    }
                });

            binding.tvTitle.text = pages[position - 1].title
            binding.tvDesc.text = Html.fromHtml(pages[position - 1].description)
        }


        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }


}