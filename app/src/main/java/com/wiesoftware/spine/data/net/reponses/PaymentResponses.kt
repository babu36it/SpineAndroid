package com.wiesoftware.spine.data.net.reponses

data class PaymentResponses(
    var status: Boolean,
    var message: String,
    var data: PaymentData
)

data class PaymentData(
    var client_secret: String,
    var secret_key: String
)