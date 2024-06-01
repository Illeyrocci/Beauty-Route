package com.illeyrocci.beautyroute.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illeyrocci.beautyroute.domain.model.ScheduleDay
import com.illeyrocci.beautyroute.domain.usecase.AddScheduleDayUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetMyDataUseCase
import com.illeyrocci.beautyroute.domain.usecase.MakeAppointmentUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

class UserScheduleViewModel(
    private val getMyDataUseCase: GetMyDataUseCase,
    private val makeAppointmentUseCase: MakeAppointmentUseCase,
    private val addScheduleDayUseCase: AddScheduleDayUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(UserScheduleUiState())
    val state: StateFlow<UserScheduleUiState>
        get() = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val userFlow = getMyDataUseCase()

            userFlow.collectLatest { user ->
                Log.d("GAT", "user collected my schedule")
                _state.update {
                    it.copy(
                        schedule = user.schedule
                    )
                }

            }
        }
    }

    fun getDate(): Date = state.value.date

    fun setDate(newDate: Date) {
        _state.update {
            it.copy(date = newDate)
        }
    }

    fun makeAppointment(uid: String, servicePosition: Int, startTime: Long, endTime: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            makeAppointmentUseCase(
                servicePosition,
                startTime,
                endTime,
                uid
            )
        }
    }

    suspend fun getCurrentDayIndex(uid: String): Int {

        state.value.schedule.forEachIndexed { index, it ->
            Log.d(
                "TAGGG", "dayStartUnitTime=${it.dayStartUnixTime}, ${Date(it.dayStartUnixTime)}  " +
                        "       stateTime=${(state.value.date.time / 86400000 + 1) * 86400000}, ${Date((state.value.date.time / 86400000 + 1) * 86400000)}"
            )
            if (it.dayStartUnixTime == (state.value.date.time / 86400000 + 1) * 86400000) {
                return index
            }
        }

        return addScheduleDayUseCase.invoke((state.value.date.time / 86400000 + 1) * 86400000, uid)
    }
}

data class UserScheduleUiState(
    val date: Date = Date(),
    val schedule: ArrayList<ScheduleDay> = arrayListOf()
)
