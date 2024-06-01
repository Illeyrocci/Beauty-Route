package com.illeyrocci.beautyroute.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illeyrocci.beautyroute.domain.model.Service
import com.illeyrocci.beautyroute.domain.usecase.GetUserDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserProfileViewModel(
    private val getUserDataUseCase: GetUserDataUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(UserProfileUiState())

    val state: StateFlow<UserProfileUiState>
        get() = _state.asStateFlow()

    fun setUserId(uid: String) {
        Log.d("TAGGG", "user sett: $uid")
        _state.update {
            it.copy(userId = uid)
        }
        viewModelScope.launch(Dispatchers.IO) {
            val userFlow = getUserDataUseCase(uid)

            userFlow.collectLatest { user ->
                Log.d("GAT", "user collected in userprof")
                _state.update {
                    UserProfileUiState(
                        it.userId,
                        user.name,
                        user.phone,
                        user.address,
                        user.description,
                        user.services
                    )
                }
            }
        }
    }
}

data class UserProfileUiState(
    val userId: String = "$",
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val description: String? = null,
    val services: ArrayList<Service> = arrayListOf()
)