package com.wiesoftware.spine.ui.home.menus.profile.setting.my_ads.Inprogress

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.databinding.AdpInprogressBinding
import com.wiesoftware.spine.databinding.ItemLangBinding
import com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.language.LangDataAdapter
import com.wiesoftware.spine.ui.home.menus.profile.setting.my_ads.InProgressModel
import com.wiesoftware.spine.ui.home.menus.profile.setting.my_ads.MyAdsActivity

class InProgressAdapter(
    val myAdsActivity: MyAdsActivity,
    val inProgressModel: ArrayList<InProgressModel>
) :
    RecyclerView.Adapter<InProgressAdapter.InProgressDataHolde>() {

    class InProgressDataHolde(val itemLangBinding: AdpInprogressBinding) :
        RecyclerView.ViewHolder(itemLangBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
       InProgressDataHolde(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.adp_inprogress,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: InProgressAdapter.InProgressDataHolde, position: Int) {
        holder.itemLangBinding.model=inProgressModel[position]

    }

    override fun getItemCount() = inProgressModel.size
}