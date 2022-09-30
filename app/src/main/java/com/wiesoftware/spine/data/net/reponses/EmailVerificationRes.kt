package com.wiesoftware.spine.data.net.reponses

data class EmailVerificationRes(
    val message: String,
    val otp: String,
    val status: Boolean
)