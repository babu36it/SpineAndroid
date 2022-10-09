package com.wiesoftware.spine.ui.home.menus.spine.addposts.postthought

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.databinding.ThoughtOptionsItemBinding
import kotlinx.android.synthetic.main.thought_options_item.view.*

/**
 * Created by Vivek kumar on 11/5/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class PostOptionsAdapter(
    val colors: ArrayList<ThoughtsData>,
    val context: Context,
    val listener: ThoughtEventListener
) : RecyclerView.Adapter<PostOptionsAdapter.OptionsHolder>() {

    interface ThoughtEventListener {
        fun onThoughtNext(thought: String)
        fun onThoughtChanged(thought: String)
        fun onbackpressed()

    }

    class OptionsHolder(thoughtOptionsItemBinding: ThoughtOptionsItemBinding) :
        RecyclerView.ViewHolder(thoughtOptionsItemBinding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        OptionsHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.thought_options_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: OptionsHolder, position: Int) {
        holder.itemView.etThought.setBackgroundColor(getColor(context, colors[position].color))
        holder.itemView.etThought.setText(colors[position].thought)
        holder.itemView.ivBack.setOnClickListener {
            listener.onbackpressed()
        }
        holder.itemView.etThought.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val thoughts = holder.itemView.etThought.text.toString()
                listener.onThoughtChanged(thoughts)
            }
        }
        val mNameTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length <= 460) {
                    val str = "(" + (s.length) + "/460 characters)"
                    holder.itemView.tvCounter.setText(str)
                }

                //This sets a textview to the current length
//                binding.tvNameCounter.setText(40-s.length);
            }

            override fun afterTextChanged(s: Editable) {
            }
        }

        holder.itemView.etThought.addTextChangedListener(mNameTextWatcher)


        holder.itemView.nextbtn.setOnClickListener {
            val thoughts = holder.itemView.etThought.text.toString()
            if (!thoughts.isEmpty()) {
                listener.onThoughtNext(thoughts)
            }
        }
    }

    override fun getItemCount() = colors.size
}