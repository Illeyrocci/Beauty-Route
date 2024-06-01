package com.illeyrocci.beautyroute.presentation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.illeyrocci.beautyroute.data.repository.AuthRepositoryImpl
import com.illeyrocci.beautyroute.data.repository.MediaRepositoryImpl
import com.illeyrocci.beautyroute.data.repository.UserRepositoryImpl
import com.illeyrocci.beautyroute.domain.usecase.AddServiceImageUseCase
import com.illeyrocci.beautyroute.domain.usecase.AddServiceUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetAppointmentByIdUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetMyDataUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetUserDataUseCase
import com.illeyrocci.beautyroute.domain.usecase.UpdateServiceDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppointmentListViewModelFactory() :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppointmentListViewModel::class.java)) {
            val authRepository = AuthRepositoryImpl()
            val userRepository = UserRepositoryImpl()
            @Suppress("UNCHECKED_CAST")
            return AppointmentListViewModel(
                GetMyDataUseCase(authRepository, GetUserDataUseCase(userRepository)),
                GetAppointmentByIdUseCase(userRepository)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}