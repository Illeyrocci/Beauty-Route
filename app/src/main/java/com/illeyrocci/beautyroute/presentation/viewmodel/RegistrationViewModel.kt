package com.illeyrocci.beautyroute.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illeyrocci.beautyroute.domain.model.Resource
import com.illeyrocci.beautyroute.domain.model.ResourceException
import com.illeyrocci.beautyroute.domain.usecase.CreateUserWithEmailAndPasswordUseCase
import com.illeyrocci.beautyroute.domain.usecase.SendVerificationEmailUseCase
import com.illeyrocci.beautyroute.domain.usecase.SignOutUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val createUserWithEmailAndPasswordUseCase: CreateUserWithEmailAndPasswordUseCase,
    private val sendVerificationEmailUseCase: SendVerificationEmailUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    /**
     * Stream of immutable states representative of the UI.
     */
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState>
        get() = _state.asStateFlow()

    init {
        val initName: String = savedStateHandle[NAME] ?: DEFAULT
        val initPhone: String = savedStateHandle[PHONE] ?: DEFAULT
        val initEmail: String = savedStateHandle[EMAIL] ?: DEFAULT
        val initPassword: String = savedStateHandle[PASSWORD] ?: DEFAULT
        val initAddress: String = savedStateHandle[ADDRESS] ?: DEFAULT
        val initStatus: RegisterStatus =
            savedStateHandle[REGISTER_STATUS] ?: RegisterStatus.DEFAULT

        _state.value = UiState(
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
            UiState(name, phone, email, password, address, RegisterStatus.DEFAULT)
        }
    }

    fun signUp(
        name: String,
        phone: String,
        email: String,
        password: String,
        address: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update {
                it.copy(registerStatus = RegisterStatus.LOADING)
            }
            val signUpResource =
                createUserWithEmailAndPasswordUseCase(email, password)
            val status = if (signUpResource is Resource.Success) {
                val sendVerificationEmailResource = sendVerificationEmailUseCase()
                if (sendVerificationEmailResource is Resource.Success) {
                    RegisterStatus.SUCCESS
                } else {
                    convertResourceExceptionToRegisterStatus(sendVerificationEmailResource.exception!!)
                }
            } else convertResourceExceptionToRegisterStatus(signUpResource.exception!!)
            signOutUseCase()
            _state.update {
                it.copy(registerStatus = status)
            }
        }
    }

    private fun convertResourceExceptionToRegisterStatus(exception: ResourceException): RegisterStatus {
        return when (exception) {
            is ResourceException.EmailBadFormat -> RegisterStatus.EMAIL_BAD_FORMAT
            is ResourceException.AuthUserCollision -> RegisterStatus.EMAIL_COLLISION
            is ResourceException.AuthWeakPassword -> RegisterStatus.WEAK_PASSWORD
            is ResourceException.Web -> RegisterStatus.POOR_WEB
            is ResourceException.Network -> RegisterStatus.POOR_NETWORK
            is ResourceException.ApiNotAvailable -> RegisterStatus.API_NOT_AVAILABLE
            is ResourceException.TooManyRequests -> RegisterStatus.TOO_MANY_REQUESTS
            is ResourceException.EmptyArguments -> RegisterStatus.EMPTY_INPUTS
            else -> RegisterStatus.SOMETHING_WRONG
        }
    }

    override fun onCleared() {
        savedStateHandle[NAME] = state.value.name
        savedStateHandle[PHONE] = state.value.phone
        savedStateHandle[EMAIL] = state.value.email
        savedStateHandle[PASSWORD] = state.value.password
        savedStateHandle[ADDRESS] = state.value.address
        savedStateHandle[REGISTER_STATUS] = state.value.registerStatus
        super.onCleared()
    }
}

data class UiState(
    val name: String = DEFAULT,
    val phone: String = DEFAULT,
    val email: String = DEFAULT,
    val password: String = DEFAULT,
    val address: String = DEFAULT,
    val registerStatus: RegisterStatus = RegisterStatus.DEFAULT
)

enum class RegisterStatus {
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
