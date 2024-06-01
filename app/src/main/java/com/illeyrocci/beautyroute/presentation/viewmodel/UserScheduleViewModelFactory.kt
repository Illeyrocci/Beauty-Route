package com.illeyrocci.beautyroute.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.illeyrocci.beautyroute.data.repository.AuthRepositoryImpl
import com.illeyrocci.beautyroute.data.repository.MediaRepositoryImpl
import com.illeyrocci.beautyroute.data.repository.UserRepositoryImpl
import com.illeyrocci.beautyroute.domain.usecase.AddScheduleDayUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetMyDataUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetUserDataUseCase
import com.illeyrocci.beautyroute.domain.usecase.MakeAppointmentUseCase

class UserScheduleViewModelFactory() :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserScheduleViewModel::class.java)) {
            val authRepository = AuthRepositoryImpl()
            val userRepository = UserRepositoryImpl()
            val mediaRepository = MediaRepositoryImpl()
            @Suppress("UNCHECKED_CAST")
            return UserScheduleViewModel(
                GetMyDataUseCase(authRepository, GetUserDataUseCase(userRepository)),
                MakeAppointmentUseCase(
                    authRepository,
                    userRepository
                ),
                AddScheduleDayUseCase(userRepository)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}