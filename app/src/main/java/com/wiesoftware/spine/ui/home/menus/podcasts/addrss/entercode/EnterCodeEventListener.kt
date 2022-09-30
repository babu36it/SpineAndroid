package com.wiesoftware.spine.ui.home.menus.podcasts.addrss.entercode

/**
 * Created by Vivek kumar on 4/6/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
interface EnterCodeEventListener {
    fun onBack()
    fun onNext(code: String)
    fun onResendCode()
}