package com.wiesoftware.spine.data.net.reponses

data class CurrencyRes(
    val `data`: List<CurrencyData>,
    val message: String,
    val status: Boolean
)