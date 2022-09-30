package com.wiesoftware.spine.ui.auth.number

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.AuthRepositry

/**
 * Created by Vivek kumar on 8/5/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class NumberViewModelFactory(
    private val authRepositry: AuthRepositry
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NumberViewModel(authRepositry) as T
    }
}