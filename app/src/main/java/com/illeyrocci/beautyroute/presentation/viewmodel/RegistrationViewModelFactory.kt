package com.illeyrocci.beautyroute.presentation.viewmodel

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.illeyrocci.beautyroute.data.repository.AuthRepositoryImpl
import com.illeyrocci.beautyroute.domain.usecase.CreateUserWithEmailAndPasswordUseCase
import com.illeyrocci.beautyroute.domain.usecase.SendVerificationEmailUseCase
import com.illeyrocci.beautyroute.domain.usecase.SignOutUseCase

class RegistrationViewModelFactory(
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

        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistrationViewModel(
                CreateUserWithEmailAndPasswordUseCase(authRepository),
                SendVerificationEmailUseCase(authRepository),
                SignOutUseCase(authRepository),
                handle
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}