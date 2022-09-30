package com.wiesoftware.spine.ui.home.menus.spine.featuredpost

import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * Created by Vivek kumar on 5/13/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class SpineWebViewClient(val listener: SpineWebEventListener) : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        url?.let { view?.loadUrl(it) }
        return true
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        listener.onPageLoaded()
    }
    interface SpineWebEventListener{
        fun onPageLoaded()
    }
}