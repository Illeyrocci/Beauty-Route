package com.illeyrocci.beautyroute.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.illeyrocci.beautyroute.data.repository.AuthRepositoryImpl
import com.illeyrocci.beautyroute.data.repository.MediaRepositoryImpl
import com.illeyrocci.beautyroute.data.repository.UserRepositoryImpl
import com.illeyrocci.beautyroute.domain.usecase.AddServiceImageUseCase
import com.illeyrocci.beautyroute.domain.usecase.AddServiceUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetMyDataUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetUserDataUseCase
import com.illeyrocci.beautyroute.domain.usecase.UpdateServiceDataUseCase

class MyProfileViewModelFactory() :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyProfileViewModel::class.java)) {
            val authRepository = AuthRepositoryImpl()
            val userRepository = UserRepositoryImpl()
            val mediaRepository = MediaRepositoryImpl()
            @Suppress("UNCHECKED_CAST")
            return MyProfileViewModel(
                GetMyDataUseCase(authRepository, GetUserDataUseCase(userRepository)),
                AddServiceUseCase(
                    authRepository,
                    userRepository
                ),
                AddServiceImageUseCase(authRepository, userRepository, mediaRepository),
                UpdateServiceDataUseCase(authRepository, userRepository)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}