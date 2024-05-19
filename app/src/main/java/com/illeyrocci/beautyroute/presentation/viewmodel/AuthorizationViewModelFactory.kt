package com.illeyrocci.beautyroute.presentation.viewmodel

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.illeyrocci.beautyroute.data.repository.AuthRepositoryImpl
import com.illeyrocci.beautyroute.domain.usecase.CheckIfAuthorizedAndGetStatusUseCase
import com.illeyrocci.beautyroute.domain.usecase.SignInAndGetStatusUseCase

class AuthorizationViewModelFactory (
    //TODO("INJECTION REPOS")
    owner: SavedStateRegistryOwner
) :
    AbstractSavedStateViewModelFactory(owner, null) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {

        val authRepository = AuthRepositoryImpl()

        if (modelClass.isAssignableFrom(AuthorizationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthorizationViewModel(
                SignInAndGetStatusUseCase(authRepository),
                CheckIfAuthorizedAndGetStatusUseCase(authRepository),
                handle
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}