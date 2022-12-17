package com.wiesoftware.spine.data.net.reponses

import java.io.Serializable

data class CurrencyData(
    val code: String,
    val country: String,
    val currency: String,
    val id: String,
    val symbol: String
): Serializable