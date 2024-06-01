package com.illeyrocci.beautyroute.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illeyrocci.beautyroute.domain.model.Appointment
import com.illeyrocci.beautyroute.domain.model.User
import com.illeyrocci.beautyroute.domain.usecase.DeleteAppointmentUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetAppointmentByIdUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetUserById
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppointmentViewModel(
    private val getAppointmentByIdUseCase: GetAppointmentByIdUseCase,
    private val deleteAppointmentUseCase: DeleteAppointmentUseCase,
    private val getUserById: GetUserById
) : ViewModel() {

    private val _state = MutableStateFlow(Pair(Appointment(), User()))

    val state: StateFlow<Pair<Appointment, User>>
        get() = _state.asStateFlow()

    fun setAppointment(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val appointment = getAppointmentByIdUseCase(id)
            val user = getUserById(appointment.salonId)
            _state.value = Pair(appointment, user)
        }
    }

    fun deleteAppointment() {
        viewModelScope.launch(Dispatchers.IO) {
            deleteAppointmentUseCase(state.value.first.id)
        }
    }
}