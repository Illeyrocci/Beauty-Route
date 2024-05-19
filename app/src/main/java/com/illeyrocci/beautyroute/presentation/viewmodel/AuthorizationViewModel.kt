package com.illeyrocci.beautyroute.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illeyrocci.beautyroute.domain.usecase.CheckIfAuthorizedAndGetStatusUseCase
import com.illeyrocci.beautyroute.domain.usecase.SignInAndGetStatusUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AuthorizationViewModel(
    private val signInAndGetStatusUseCase: SignInAndGetStatusUseCase,
    private val checkIfAuthorizedAndGetStatusUseCase: CheckIfAuthorizedAndGetStatusUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AuthorizationUiState())
    val state: StateFlow<AuthorizationUiState>
        get() = _state.asStateFlow()

    init {
        val initStatus = runBlocking(viewModelScope.coroutineContext) { checkIfAuthorizedAndGetStatusUseCase() }
        val initEmail: String = savedStateHandle[EMAIL] ?: DEFAULT
        val initPassword: String = savedStateHandle[PASSWORD] ?: DEFAULT

        _state.value = AuthorizationUiState(
            initEmail,
            initPassword,
            initStatus
        )
    }

    fun updateFormState(
        email: String,
        password: String
    ) {
        Log.d("TAGGG", "updateFormState call")
        _state.update {
            AuthorizationUiState(email, password, AuthStatus.DEFAULT)
        }
    }

    fun signIn() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update {
                it.copy(authStatus = AuthStatus.LOADING)
            }

            _state.update {
                it.copy(
                    authStatus = signInAndGetStatusUseCase(it.email, it.password)
                )
            }
        }
    }

    override fun onCleared() {
        savedStateHandle[EMAIL] = state.value.email
        savedStateHandle[PASSWORD] = state.value.password
        super.onCleared()
    }
}

data class AuthorizationUiState(
    val email: String = DEFAULT,
    val password: String = DEFAULT,
    val authStatus: AuthStatus = AuthStatus.DEFAULT
)

enum class AuthStatus {
    LOADING,
    SUCCESS,
    DEFAULT,
    WRONG_PASSWORD,
    EMAIL_TOO_MANY_REQUESTS,
    WRONG_CREDENTIALS,
    EMAIL_SENT,
    EMAIL_BAD_FORMAT,
    USER_NOT_FOUND,
    POOR_NETWORK,
    POOR_WEB,
    TOO_MANY_REQUESTS,
    API_NOT_AVAILABLE,
    EMPTY_INPUTS,
    SOMETHING_WRONG
}

private const val EMAIL: String = "user_email"
private const val PASSWORD: String = "user_password"
private const val DEFAULT: String = ""