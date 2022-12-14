package com.wiesoftware.spine.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.data.repo.AuthRepository
import com.wiesoftware.spine.ui.auth.register.RegisterViewModel

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(
    private val authRepositry: AuthRepository
) :ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RegisterViewModel(authRepositry) as T
    }

}