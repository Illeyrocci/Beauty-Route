package com.illeyrocci.beautyroute.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illeyrocci.beautyroute.domain.usecase.CreateUserAndGetStatusUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val createUserAndGetStatusUseCase: CreateUserAndGetStatusUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    /**
     * Stream of immutable states representative of the UI.
     */
    private val _state = MutableStateFlow(RegistrationUiState())
    val state: StateFlow<RegistrationUiState>
        get() = _state.asStateFlow()

    init {
        val initName: String = savedStateHandle[NAME] ?: DEFAULT
        val initPhone: String = savedStateHandle[PHONE] ?: DEFAULT
        val initEmail: String = savedStateHandle[EMAIL] ?: DEFAULT
        val initPassword: String = savedStateHandle[PASSWORD] ?: DEFAULT
        val initAddress: String = savedStateHandle[ADDRESS] ?: DEFAULT
        val initStatus: RegistrationStatus =
            savedStateHandle[REGISTER_STATUS] ?: RegistrationStatus.DEFAULT

        _state.value = RegistrationUiState(
            initName,
            initPhone,
            initEmail,
            initPassword,
            initAddress,
            initStatus
        )
    }

    fun updateFormState(
        name: String,
        phone: String,
        email: String,
        password: String,
        address: String
    ) {
        Log.d("TAGGG", "updateFormState call")
        _state.update {
            RegistrationUiState(name, phone, email, password, address, RegistrationStatus.DEFAULT)
        }
    }

    fun signUp() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update {
                it.copy(registrationStatus = RegistrationStatus.LOADING)
            }

            _state.update {
                it.copy(registrationStatus = createUserAndGetStatusUseCase(it.name, it.phone, it.email, it.password, it.address))
            }
        }
    }

    override fun onCleared() {
        savedStateHandle[NAME] = state.value.name
        savedStateHandle[PHONE] = state.value.phone
        savedStateHandle[EMAIL] = state.value.email
        savedStateHandle[PASSWORD] = state.value.password
        savedStateHandle[ADDRESS] = state.value.address
        savedStateHandle[REGISTER_STATUS] = state.value.registrationStatus
        super.onCleared()
    }
}

data class RegistrationUiState(
    val name: String = DEFAULT,
    val phone: String = DEFAULT,
    val email: String = DEFAULT,
    val password: String = DEFAULT,
    val address: String = DEFAULT,
    val registrationStatus: RegistrationStatus = RegistrationStatus.DEFAULT
)

enum class RegistrationStatus {
    LOADING,
    SUCCESS,
    DEFAULT,
    WEAK_PASSWORD,
    EMAIL_COLLISION,
    EMAIL_BAD_FORMAT,
    POOR_NETWORK,
    POOR_WEB,
    TOO_MANY_REQUESTS,
    API_NOT_AVAILABLE,
    EMPTY_INPUTS,
    SOMETHING_WRONG
}

private const val NAME: String = "user_name"
private const val PHONE: String = "user_phone"
private const val EMAIL: String = "user_email"
private const val PASSWORD: String = "user_password"
private const val ADDRESS: String = "user_address"
private const val DEFAULT: String = ""
private const val REGISTER_STATUS: String = "register_status"
