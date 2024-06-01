package com.illeyrocci.beautyroute.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.illeyrocci.beautyroute.data.repository.AuthRepositoryImpl
import com.illeyrocci.beautyroute.data.repository.MediaRepositoryImpl
import com.illeyrocci.beautyroute.data.repository.UserRepositoryImpl
import com.illeyrocci.beautyroute.domain.usecase.AddMyScheduleDayUseCase
import com.illeyrocci.beautyroute.domain.usecase.AddScheduleDayUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetMyDataUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetUserDataUseCase
import com.illeyrocci.beautyroute.domain.usecase.SwitchSlotByPositionUseCase

class MyScheduleViewModelFactory() :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyScheduleViewModel::class.java)) {
            val authRepository = AuthRepositoryImpl()
            val userRepository = UserRepositoryImpl()
            val mediaRepository = MediaRepositoryImpl()
            @Suppress("UNCHECKED_CAST")
            return MyScheduleViewModel(
                GetMyDataUseCase(authRepository, GetUserDataUseCase(userRepository)),
                SwitchSlotByPositionUseCase(userRepository, authRepository),
                AddMyScheduleDayUseCase(AddScheduleDayUseCase(userRepository), authRepository)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}