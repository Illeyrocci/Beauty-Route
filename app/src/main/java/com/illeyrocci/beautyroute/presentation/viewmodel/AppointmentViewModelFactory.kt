package com.illeyrocci.beautyroute.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.illeyrocci.beautyroute.data.repository.AuthRepositoryImpl
import com.illeyrocci.beautyroute.data.repository.UserRepositoryImpl
import com.illeyrocci.beautyroute.domain.usecase.DeleteAppointmentUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetAppointmentByIdUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetUserByIdUseCase

class AppointmentViewModelFactory() :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppointmentViewModel::class.java)) {
            val authRepository = AuthRepositoryImpl()
            val userRepository = UserRepositoryImpl()
            @Suppress("UNCHECKED_CAST")
            return AppointmentViewModel(
                GetAppointmentByIdUseCase(userRepository),
                DeleteAppointmentUseCase(userRepository, authRepository),
                GetUserByIdUseCase(userRepository)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}