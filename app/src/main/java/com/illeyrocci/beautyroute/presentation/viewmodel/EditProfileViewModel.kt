package com.illeyrocci.beautyroute.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illeyrocci.beautyroute.domain.usecase.GetEmailUseCase
import com.illeyrocci.beautyroute.domain.usecase.GetMyDataUseCase
import com.illeyrocci.beautyroute.domain.usecase.SubmitProfileChangesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel (
private val submitProfileChangesUseCase: SubmitProfileChangesUseCase,
private val getMyDataUseCase: GetMyDataUseCase,
private val getEmailUseCase: GetEmailUseCase,
private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    /**
     * Stream of immutable states representative of the UI.
     */
    private val _state = MutableStateFlow(EditProfileUiState())
    val state: StateFlow<EditProfileUiState>
    get() = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val userFlow = getMyDataUseCase()

            userFlow.collectLatest { user ->
                Log.d("GAT", "user collected")
                _state.update {
                    EditProfileUiState(
                        user.description ?: DEFAULT,
                        user.name,
                        user.phone,
                        getEmailUseCase().data!!,
                        if (it.lastPassword != DEFAULT) it.lastPassword else savedStateHandle[LAST_PASSWORD] ?: DEFAULT,
                        if (it.password != DEFAULT) it.password else savedStateHandle[PASSWORD] ?: DEFAULT,
                        user.address,
                        savedStateHandle[EDIT_PROFILE_STATUS] ?: EditProfileStatus.DEFAULT
                    )
                }
            }
        }
    }

    fun updateFormState(
        description: String,
        name: String,
        phone: String,
        email: String,
        lastPassword: String,
        password: String,
        address: String
    ) {
        _state.update {
            EditProfileUiState(description, name, phone, email, lastPassword, password, address, EditProfileStatus.DEFAULT)
        }
        Log.d("TAGGG", "updateFormState call $")
    }

    fun submitChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update {
                it.copy(editProfileStatus = EditProfileStatus.LOADING)
            }

            _state.update {
                it.copy(editProfileStatus = submitProfileChangesUseCase(it.description, it.name, it.phone, it.email, it.lastPassword, it.password, it.address))
            }
        }
    }

    override fun onCleared() {
        savedStateHandle[EDIT_PROFILE_STATUS] = state.value.editProfileStatus
        super.onCleared()
    }
}

data class EditProfileUiState(
    val description: String = DEFAULT,
    val name: String = DEFAULT,
    val phone: String = DEFAULT,
    val email: String = DEFAULT,
    val lastPassword: String = DEFAULT,
    val password: String = DEFAULT,
    val address: String = DEFAULT,
    val editProfileStatus: EditProfileStatus = EditProfileStatus.DEFAULT
)

enum class EditProfileStatus {
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

private const val DEFAULT: String = ""
private const val EDIT_PROFILE_STATUS: String = "edit_status"
private const val LAST_PASSWORD: String = "last_password"
private const val PASSWORD: String = "password"
