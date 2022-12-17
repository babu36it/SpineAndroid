package com.wiesoftware.spine.data.net.reponses.adsmanagment

import com.wiesoftware.spine.data.net.reponses.AdDurationData

data class AdsTypeModel
    (
    val `data`: ArrayList<AdsTypeData>,
    val message: String,
    val status: Boolean
)

data class AdsTypeData(

    val id: String,
    val type: String
)
