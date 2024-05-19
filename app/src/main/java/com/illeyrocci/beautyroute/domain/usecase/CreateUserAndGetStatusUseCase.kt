package com.illeyrocci.beautyroute.domain.usecase

import android.util.Log
import com.illeyrocci.beautyroute.domain.model.Resource
import com.illeyrocci.beautyroute.domain.model.ResourceException
import com.illeyrocci.beautyroute.domain.repository.AuthRepository
import com.illeyrocci.beautyroute.presentation.viewmodel.RegistrationStatus

class CreateUserAndGetStatusUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): RegistrationStatus {
        val signUpResource =
            authRepository.createUserWithEmailAndPassword(email, password)
        return if (signUpResource is Resource.Success) {
            val sendVerificationEmailResource = authRepository.sendVerificationEmail()
            if (sendVerificationEmailResource is Resource.Success) {
                RegistrationStatus.SUCCESS
            } else {
                convertResourceExceptionToRegisterStatus(sendVerificationEmailResource.exception!!)
            }
        } else {
            convertResourceExceptionToRegisterStatus(signUpResource.exception!!)
        }.also {
            authRepository.signOut()
            Log.d("TAGGG", "SIGNED OUT")
        }
    }

    private fun convertResourceExceptionToRegisterStatus(exception: ResourceException): RegistrationStatus {
        return when (exception) {
            is ResourceException.EmailBadFormat -> RegistrationStatus.EMAIL_BAD_FORMAT
            is ResourceException.AuthUserCollision -> RegistrationStatus.EMAIL_COLLISION
            is ResourceException.AuthWeakPassword -> RegistrationStatus.WEAK_PASSWORD
            is ResourceException.Web -> RegistrationStatus.POOR_WEB
            is ResourceException.Network -> RegistrationStatus.POOR_NETWORK
            is ResourceException.ApiNotAvailable -> RegistrationStatus.API_NOT_AVAILABLE
            is ResourceException.TooManyRequests -> RegistrationStatus.TOO_MANY_REQUESTS
            is ResourceException.EmptyArguments -> RegistrationStatus.EMPTY_INPUTS
            else -> RegistrationStatus.SOMETHING_WRONG
        }
    }
}