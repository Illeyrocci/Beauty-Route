package com.illeyrocci.beautyroute.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illeyrocci.beautyroute.domain.model.Appointment
import com.illeyrocci.beautyroute.domain.model.User
import com.illeyrocci.beautyroute.domain.usecase.ExcludeUserFromFavouritesUseCase
import com.illeyrocci.beautyroute.domain.usecase.FindMastersUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetFavouritesUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetNearestAppointmentUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetUserByIdUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val findMastersUseCase: FindMastersUseCase,
    private val excludeUserFromFavouritesUseCase: ExcludeUserFromFavouritesUseCase,
    private val getFavouritesUseCase: GetFavouritesUseCase,
    private val getNearestAppointmentUseCase: GetNearestAppointmentUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState>
        get() = _state.asStateFlow()

    init {
        val initSearchText: String = savedStateHandle[SEARCH] ?: DEFAULT

        _state.value = SearchUiState(
            searchText = initSearchText
        )
    }

    fun getSearchResult(query: String) {
        viewModelScope.launch {
            Log.d("TAGGG", "finded")
            _state.update { it.copy(searchResult = findMastersUseCase(query)) }
        }
    }

    override fun onCleared() {
        savedStateHandle[SEARCH] = state.value.searchText
        super.onCleared()
    }

    fun clearSearch() {
        _state.update { it.copy(searchResult = emptyList()) }
    }

    fun excludeUserFromFavourites(it: String) {
        viewModelScope.launch(Dispatchers.IO) {
            excludeUserFromFavouritesUseCase(it)
        }
    }

    fun updateFavourites() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { state1 ->
                state1.copy(favouriteUsers = getFavouritesUseCase().map {
                    getUserByIdUseCase(it)!!
                })
            }
        }
    }

    fun updateNearestAppointment() {
        viewModelScope.launch(Dispatchers.IO) {
            val appointment = getNearestAppointmentUseCase()
            _state.update {
                it.copy(
                    nearestAppointment = getNearestAppointmentUseCase(),
                    masterOfNearest = getUserByIdUseCase(appointment?.salonId)
                )
            }
        }
    }

}

data class SearchUiState(
    val nearestAppointment: Appointment? = null,
    val masterOfNearest: User? = null,
    val searchText: String = DEFAULT,
    val favouriteUsers: List<User> = arrayListOf(),
    val searchResult: List<User> = emptyList()
)

private const val SEARCH: String = "search_text"
private const val DEFAULT: String = ""