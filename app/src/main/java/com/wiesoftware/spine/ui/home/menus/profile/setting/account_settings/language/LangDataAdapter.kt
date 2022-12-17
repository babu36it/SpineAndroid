package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.language

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.databinding.ItemLangBinding

/**
 * Created by Vivek kumar on 2/11/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class LangDataAdapter(val langDataList: List<LanguageData>,val listener: LanguageSelectedListener):RecyclerView.Adapter<LangDataAdapter.LangDataHolder>() {
    class LangDataHolder(val itemLangBinding: ItemLangBinding): RecyclerView.ViewHolder(itemLangBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LangDataHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_lang,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: LangDataHolder, position: Int) {
        holder.itemLangBinding.model=langDataList[position]
        holder.itemLangBinding.tvlang.setOnClickListener {
            listener.onLanguageSelected(langDataList[position])
        }
    }

    override fun getItemCount() = langDataList.size

    interface LanguageSelectedListener{
        fun onLanguageSelected(langData: LanguageData)
    }
}