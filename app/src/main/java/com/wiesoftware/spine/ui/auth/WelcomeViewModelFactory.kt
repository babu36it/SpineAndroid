package com.wiesoftware.spine.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.AuthRepositry

/**
 * Created by Vivek kumar on 8/7/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class WelcomeViewModelFactory(
    private val authRepositry: AuthRepositry
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WelcomeViewModel(authRepositry) as T
    }
}