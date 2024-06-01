package com.illeyrocci.beautyroute.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illeyrocci.beautyroute.domain.model.ScheduleDay
import com.illeyrocci.beautyroute.domain.usecase.AddMyScheduleDayUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetMyDataUseCase
import com.illeyrocci.beautyroute.domain.usecase.SwitchSlotByPositionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

class MyScheduleViewModel(
    private val getMyDataUseCase: GetMyDataUseCase,
    private val switchSlotByPositionUseCase: SwitchSlotByPositionUseCase,
    private val addMyScheduleDayUseCase: AddMyScheduleDayUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MyScheduleUiState())
    val state: StateFlow<MyScheduleUiState>
        get() = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val userFlow = getMyDataUseCase()

            userFlow.collectLatest { user ->
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

    fun updateSlotByPosition(pos: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            //false switchim na true i naoborot u esg dnya i u pos sekcii

            switchSlotByPositionUseCase(getDate(), pos)
        }
    }

    fun getCurrentDay(): ScheduleDay? {
        _state.value.schedule.forEach {
            if (it.dayStartUnixTime == state.value.date.time) {
                return it
            }
        }

        return null
    }

    suspend fun getCurrentDayIndex(): Int {

        state.value.schedule.forEachIndexed { index, it ->
            Log.d(
                "TAGGG", "dayStartUnitTime=${it.dayStartUnixTime}, ${Date(it.dayStartUnixTime)}  " +
                        "   stateTime=${getDate().time}, ${getDate()}"
            )
            if (it.dayStartUnixTime == getDate().time) {
                return index
            }
        }

        Log.d("TAGGG", "ADDEDNEWDAY")
        return addMyScheduleDayUseCase.invoke(getDate().time)
    }
}

data class MyScheduleUiState(
    val date: Date = Date((Date().time / 86400000) * 86400000),
    val schedule: ArrayList<ScheduleDay> = arrayListOf()
)
