package com.wiesoftware.spine.ui.home.menus.profile.setting.my_ads.past

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.databinding.AdpInprogressBinding
import com.wiesoftware.spine.databinding.AdpPastBinding
import com.wiesoftware.spine.ui.home.menus.profile.setting.my_ads.Inprogress.InProgressAdapter
import com.wiesoftware.spine.ui.home.menus.profile.setting.my_ads.MyAdsActivity
import java.util.ArrayList

class PastAdapter(val myAdsActivity: MyAdsActivity, val pastModel: ArrayList<PastModel>) :
    RecyclerView.Adapter<PastAdapter.PastDataHolder>() {

    class PastDataHolder(val itemLangBinding: AdpPastBinding) :
        RecyclerView.ViewHolder(itemLangBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PastAdapter.PastDataHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.adp_past,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: PastAdapter.PastDataHolder, position: Int) {
        holder.itemLangBinding.model=pastModel[position]

    }

    override fun getItemCount() = pastModel.size
}