package com.wiesoftware.spine.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.AuthRepository
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 8/7/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory(
    private val authRepository: AuthRepository,
    private val homeRepository: HomeRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(authRepository,homeRepository) as T
    }
}