package com.illeyrocci.beautyroute.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illeyrocci.beautyroute.domain.model.Service
import com.illeyrocci.beautyroute.domain.usecase.AddServiceUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetMyDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyProfileViewModel(
    private val getMyDataUseCase: GetMyDataUseCase,
    private val addServiceUseCase: AddServiceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MyProfileUiState())
    val state: StateFlow<MyProfileUiState>
        get() = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val userFlow = getMyDataUseCase()

            userFlow.collectLatest { user ->
                Log.d("GAT", "user collected")
                _state.update {
                    MyProfileUiState(
                        user.name,
                        user.phone,
                        user.services
                    )
                }
            }
        }
    }


    fun addNewService() {
        viewModelScope.launch(Dispatchers.IO) { addServiceUseCase() }
    }
}

data class MyProfileUiState(
    val name: String = "",
    val phone: String = "",
    val services: ArrayList<Service> = arrayListOf()
)