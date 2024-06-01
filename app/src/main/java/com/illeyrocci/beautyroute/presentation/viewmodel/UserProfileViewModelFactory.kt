package com.illeyrocci.beautyroute.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.illeyrocci.beautyroute.data.repository.AuthRepositoryImpl
import com.illeyrocci.beautyroute.data.repository.MediaRepositoryImpl
import com.illeyrocci.beautyroute.data.repository.UserRepositoryImpl
import com.illeyrocci.beautyroute.domain.usecase.AddToFavouritesUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetUserDataUseCase

class UserProfileViewModelFactory() :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserProfileViewModel::class.java)) {
            val authRepository = AuthRepositoryImpl()
            val userRepository = UserRepositoryImpl()
            MediaRepositoryImpl()
            @Suppress("UNCHECKED_CAST")
            return UserProfileViewModel(
                GetUserDataUseCase(userRepository),
                AddToFavouritesUseCase(authRepository, userRepository)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}