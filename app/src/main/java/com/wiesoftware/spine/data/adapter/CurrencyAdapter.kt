package com.wiesoftware.spine.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.CurrencyData
import com.wiesoftware.spine.databinding.ItemCurrencyBinding

/**
 * Created by Vivek kumar on 2/11/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
class CurrencyAdapter(val currencyList: List<CurrencyData>, val listener: CurrencyEventListener) :
    RecyclerView.Adapter<CurrencyAdapter.CurrencyHolder>() {
    class CurrencyHolder(val itemCurrencyBinding: ItemCurrencyBinding) :
        RecyclerView.ViewHolder(itemCurrencyBinding.root) {

    }

    interface CurrencyEventListener {
        fun onCurencySelected(currencyData: CurrencyData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CurrencyHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_currency,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: CurrencyHolder, position: Int) {
        holder.itemCurrencyBinding.model = currencyList[position]
        holder.itemCurrencyBinding.tvlang.setOnClickListener {
            listener.onCurencySelected(currencyList[position])
        }

//        if (position == itemCount - 1) {
//            holder.itemCurrencyBinding.view41.visibility = View.GONE
//        } else {
//            holder.itemCurrencyBinding.view41.visibility = View.VISIBLE
//        }
    }

    override fun getItemCount() = currencyList.size
}