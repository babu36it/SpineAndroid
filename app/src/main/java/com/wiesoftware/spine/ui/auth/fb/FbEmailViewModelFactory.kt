package com.wiesoftware.spine.ui.auth.fb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.AuthRepository

/**
 * Created by Vivek kumar on 8/10/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class FbEmailViewModelFactory(
    private val authRepositry: AuthRepository
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FbEmailViewModel(authRepositry) as T
    }
}