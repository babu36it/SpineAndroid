package com.wiesoftware.spine.data.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.PodcastSubCategoryData
import com.wiesoftware.spine.databinding.PodSubcatListBinding

/**
 * Created by Vivek kumar on 4/14/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class PodcastSubcategoryAdapter(
    var context: Context,
    val dataList: List<PodcastSubCategoryData>,
    val listener: OnPodSubCatSelectedListener
) : RecyclerView.Adapter<PodcastSubcategoryAdapter.SubcategoryHolder>() {
    class SubcategoryHolder(val podSubcatListBinding: PodSubcatListBinding, val context: Context) :
        RecyclerView.ViewHolder(podSubcatListBinding.root) {

    }

    var list = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SubcategoryHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.pod_subcat_list,
                parent,
                false
            ), parent.context
        )

    val checkedItemList: MutableMap<String, Boolean> = mutableMapOf()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: SubcategoryHolder, position: Int) {
        holder.podSubcatListBinding.model = dataList[position]
        if (checkedItemList.containsKey(dataList[position].id)) {
            if (checkedItemList.get(dataList[position].id)!!) {
                holder.podSubcatListBinding.itemLayout.background =
                    ContextCompat.getDrawable(holder.context, R.drawable.round_button_bg)
                holder.podSubcatListBinding.textView28.setTextAppearance(R.style.newspinnerText)

            } else {
                holder.podSubcatListBinding.itemLayout.background =
                    ContextCompat.getDrawable(holder.context, R.drawable.boarder_round_btn_bg)
                holder.podSubcatListBinding.textView28.setTextAppearance(R.style.newspinnerText)

            }
        } else {
            holder.podSubcatListBinding.itemLayout.background =
                ContextCompat.getDrawable(holder.context, R.drawable.boarder_round_btn_bg)
            holder.podSubcatListBinding.textView28.setTextAppearance(R.style.newspinnerText)

        }

        holder.podSubcatListBinding.itemLayout.setOnClickListener {

            if (list.contains(dataList[position].id)) {
                list.remove(dataList[position].id)
                listener.onPodSubCatSelectedList(list)
                holder.podSubcatListBinding.itemLayout.background =
                    ContextCompat.getDrawable(holder.context, R.drawable.boarder_round_btn_bg)
                holder.podSubcatListBinding.textView28.setTextAppearance(R.style.newspinnerText)

            } else {
                if (list.size > 2) {
                    Toast.makeText(
                        context,
                        "User can choose only up to 3 sub-categories",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    list.add(dataList[position].id)
                    holder.podSubcatListBinding.itemLayout.background =
                        ContextCompat.getDrawable(holder.context, R.drawable.round_button_bg)
                    holder.podSubcatListBinding.textView28.setTextAppearance(R.style.newspinnerText)
                    listener.onPodSubCatSelectedList(list)
                }

            }


            /* if (checkedItemList.containsKey(dataList[position].id)){
                 if(checkedItemList.get(dataList[position].id)!!){
                     listener.onPodSubCatSelected(dataList[position],false)
                     checkedItemList.put(dataList[position].id,false)
                     Log.e("data1",checkedItemList.toString())
                     holder.podSubcatListBinding.itemLayout.background = ContextCompat.getDrawable(holder.context,R.drawable.boarder_round_btn_bg)
                 }else{
                     listener.onPodSubCatSelected(dataList[position],true)
                     checkedItemList.put(dataList[position].id,true)
                     Log.e("data",checkedItemList.toString())
                     holder.podSubcatListBinding.itemLayout.background = ContextCompat.getDrawable(holder.context,R.drawable.round_button_bg)
                 }
             }else{
                 listener.onPodSubCatSelected(dataList[position],true)
                 checkedItemList.put(dataList[position].id,true)
                 Log.e("data2",checkedItemList.toString())
                 holder.podSubcatListBinding.itemLayout.background = ContextCompat.getDrawable(holder.context,R.drawable.round_button_bg)
             }*/
        }


    }

    override fun getItemCount() = dataList.size

    interface OnPodSubCatSelectedListener {
        fun onPodSubCatSelected(subCategoryData: PodcastSubCategoryData, isSelected: Boolean)
        fun onPodSubCatSelectedList(subCategoryData: ArrayList<String>)
    }

}