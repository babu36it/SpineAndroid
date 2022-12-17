package com.wiesoftware.spine.ui.auth.number

interface NumberEventListener {
    fun onBack()
    fun onNext(phoneNumber: String?)
    fun onCodeFailed(msg: String?)
}