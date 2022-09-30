package com.wiesoftware.spine.ui.home.menus.profile.setting.my_ads.current

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.databinding.AdpCurrentadsBinding
import com.wiesoftware.spine.databinding.AdpInprogressBinding
import com.wiesoftware.spine.ui.home.menus.profile.setting.my_ads.Inprogress.InProgressAdapter
import com.wiesoftware.spine.ui.home.menus.profile.setting.my_ads.MyAdsActivity

class CurrentAdapter(val myAdsActivity: MyAdsActivity, val currentModel: ArrayList<CurrentModel>)
    :  RecyclerView.Adapter<CurrentAdapter.CurrentDataHolder>(){

    class CurrentDataHolder(val itemLangBinding: AdpCurrentadsBinding) :
        RecyclerView.ViewHolder(itemLangBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CurrentAdapter.CurrentDataHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.adp_currentads,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CurrentAdapter.CurrentDataHolder, position: Int) {
        holder.itemLangBinding.model=currentModel[position]

    }

    override fun getItemCount() = currentModel.size
}