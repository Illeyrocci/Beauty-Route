package com.illeyrocci.beautyroute.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illeyrocci.beautyroute.domain.model.Appointment
import com.illeyrocci.beautyroute.domain.usecase.GetAppointmentByIdUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetMyDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AppointmentListViewModel(
    private val getMyDataUseCase: GetMyDataUseCase,
    private val getAppointmentByIdUseCase: GetAppointmentByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(arrayListOf<Appointment>())

    val state: StateFlow<ArrayList<Appointment>>
        get() = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val userFlow = getMyDataUseCase()

            userFlow.collectLatest { user ->
                Log.d("GAT", "user collected")
                _state.value = user.appointments.map {
                    getAppointmentByIdUseCase(it)
                } as ArrayList<Appointment>
            }
        }
    }
}