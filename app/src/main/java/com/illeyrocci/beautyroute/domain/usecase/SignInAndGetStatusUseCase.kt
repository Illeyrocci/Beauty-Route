package com.illeyrocci.beautyroute.domain.usecase

import com.google.firebase.FirebaseTooManyRequestsException
import com.illeyrocci.beautyroute.domain.model.Resource
import com.illeyrocci.beautyroute.domain.model.ResourceException
import com.illeyrocci.beautyroute.domain.repository.AuthRepository
import com.illeyrocci.beautyroute.presentation.viewmodel.AuthStatus

class SignInAndGetStatusUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): AuthStatus {
        val signInResource =
            authRepository.signInWithEmailAndPassword(email, password)
        return if (signInResource is Resource.Success) {
            val checkEmailVerificationResource = authRepository.checkEmailVerification()
            if (checkEmailVerificationResource is Resource.Success) {
                if (checkEmailVerificationResource.data!!) {
                    AuthStatus.SUCCESS
                } else {
                    val sendVerificationEmailResource = authRepository.sendVerificationEmail()
                    if (sendVerificationEmailResource is Resource.Success) {
                        AuthStatus.EMAIL_SENT
                    } else {
                        if (sendVerificationEmailResource.exception!!.exception is FirebaseTooManyRequestsException) {
                            AuthStatus.EMAIL_TOO_MANY_REQUESTS
                        } else {
                            convertResourceExceptionToAuthStatus(sendVerificationEmailResource.exception)
                        }
                    }.also { authRepository.signOut() }
                }
            } else {
                authRepository.signOut()
                convertResourceExceptionToAuthStatus(checkEmailVerificationResource.exception!!)
            }
        } else {
            authRepository.signOut()
            convertResourceExceptionToAuthStatus(signInResource.exception!!)
        }
    }

    private fun convertResourceExceptionToAuthStatus(exception: ResourceException): AuthStatus {
        return when (exception) {
            is ResourceException.EmailBadFormat -> AuthStatus.EMAIL_BAD_FORMAT
            is ResourceException.UserNotFound -> AuthStatus.USER_NOT_FOUND
            is ResourceException.WrongPassword -> AuthStatus.WRONG_PASSWORD
            is ResourceException.WrongCredentials -> AuthStatus.WRONG_CREDENTIALS
            is ResourceException.Web -> AuthStatus.POOR_WEB
            is ResourceException.Network -> AuthStatus.POOR_NETWORK
            is ResourceException.ApiNotAvailable -> AuthStatus.API_NOT_AVAILABLE
            is ResourceException.TooManyRequests -> AuthStatus.TOO_MANY_REQUESTS
            is ResourceException.EmptyArguments -> AuthStatus.EMPTY_INPUTS
            else -> AuthStatus.SOMETHING_WRONG
        }
    }
}