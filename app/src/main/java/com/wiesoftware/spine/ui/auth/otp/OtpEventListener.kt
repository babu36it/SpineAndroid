package com.wiesoftware.spine.ui.auth.otp

interface OtpEventListener {
    fun onBack()
    fun getCodeViaSMS()
    fun onOtpVerified(otp: String)
    fun getCodeAgain()
}