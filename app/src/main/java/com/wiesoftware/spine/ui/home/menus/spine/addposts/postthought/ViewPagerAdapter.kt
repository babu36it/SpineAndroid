package com.wiesoftware.spine.ui.home.menus.spine.addposts.postthought

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.wiesoftware.spine.databinding.ThoughtOptionsItemBinding
import kotlinx.android.synthetic.main.thought_options_item.view.*


class ViewPagerAdapter(
    val colors: ArrayList<ThoughtsData>,
    val context: Context,
    val listner: PostThoughtActivity
) : PagerAdapter() {
    override fun getCount(): Int {
        return colors.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    class OptionsHolder(thoughtOptionsItemBinding: ThoughtOptionsItemBinding) :
        RecyclerView.ViewHolder(thoughtOptionsItemBinding.root) {


    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val thoughtOptionsItemBinding = OptionsHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(container.context),
                com.wiesoftware.spine.R.layout.thought_options_item,
                container,
                false
            )
        )
        thoughtOptionsItemBinding.itemView.etThought.setBackgroundColor(
            ContextCompat.getColor(
                context,
                colors[position].color
            )
        )
        thoughtOptionsItemBinding.itemView.etThought.setText(colors[position].thought)
        thoughtOptionsItemBinding.itemView.etThought.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                val thoughts= thoughtOptionsItemBinding.itemView.etThought.text.toString()
                listner.onThoughtChanged(thoughts)
            }
        }

        thoughtOptionsItemBinding.itemView.nextbtn.setOnClickListener {
            val thoughts= thoughtOptionsItemBinding.itemView.etThought.text.toString()
            if (!thoughts.isEmpty()){
                listner.onThoughtNext(thoughts)
            }
        }
        return thoughtOptionsItemBinding;

    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val viewPager = container as ViewPager
        val view = `object` as View
        viewPager.removeView(view)
    }

    interface ThoughtEventListener {
        fun onThoughtNext(thought: String)
        fun onThoughtChanged(thought: String)
    }

}