package com.illeyrocci.beautyroute.presentation.viewmodel

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.illeyrocci.beautyroute.data.repository.AuthRepositoryImpl
import com.illeyrocci.beautyroute.data.repository.UserRepositoryImpl
import com.illeyrocci.beautyroute.domain.usecase.GetEmailUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetMyDataUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetUserDataUseCase
import com.illeyrocci.beautyroute.domain.usecase.SubmitProfileChangesUseCase

class EditProfileViewModelFactory(
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
        val userRepository = UserRepositoryImpl()

        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditProfileViewModel(
                SubmitProfileChangesUseCase(authRepository, userRepository),
                GetMyDataUseCase(authRepository, GetUserDataUseCase(userRepository)),
                GetEmailUseCase(authRepository),
                handle
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}