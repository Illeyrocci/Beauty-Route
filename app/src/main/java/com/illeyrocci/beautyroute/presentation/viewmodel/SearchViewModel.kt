package com.illeyrocci.beautyroute.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illeyrocci.beautyroute.domain.model.User
import com.illeyrocci.beautyroute.domain.usecase.FindMastersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val findMastersUseCase: FindMastersUseCase,
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
}

data class SearchUiState(
    val nearestAppointmentId: String? = null,
    val searchText: String = DEFAULT,
    val favouriteUsers: ArrayList<String> = arrayListOf(),
    val searchResult: List<User> = emptyList()
)

private const val SEARCH: String = "search_text"
private const val DEFAULT: String = ""