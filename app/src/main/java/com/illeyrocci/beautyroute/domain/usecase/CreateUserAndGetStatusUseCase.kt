package com.illeyrocci.beautyroute.domain.usecase

import android.util.Log
import com.illeyrocci.beautyroute.domain.model.Resource
import com.illeyrocci.beautyroute.domain.model.ResourceException
import com.illeyrocci.beautyroute.domain.repository.AuthRepository
import com.illeyrocci.beautyroute.domain.repository.UserRepository
import com.illeyrocci.beautyroute.presentation.viewmodel.RegistrationStatus

class CreateUserAndGetStatusUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        name: String,
        phone: String,
        email: String,
        password: String,
        address: String
    ): RegistrationStatus {
        val signUpResource = authRepository.createUserWithEmailAndPassword(email, password)

        val saveUserResource = userRepository.addUserToDB(authRepository.getMyUID().data!!, name, phone, address)

        if (saveUserResource is Resource.Failure) return convertResourceExceptionToRegisterStatus(
            saveUserResource.exception!!
        )

        return if (signUpResource is Resource.Success) {
            Log.d("TAGGG", "signedUp, ${authRepository.checkIfAuthorized().data!!}")
            val sendVerificationEmailResource = authRepository.sendVerificationEmail()
            if (sendVerificationEmailResource is Resource.Success) {
                authRepository.signOut()
                RegistrationStatus.SUCCESS
            } else {
                try {
                    if (sendVerificationEmailResource.exception is ResourceException.Other) {
                        userRepository.deleteUserFromDB(authRepository.getMyUID().data!!)
                        authRepository.deleteUser()
                    }
                    convertResourceExceptionToRegisterStatus(sendVerificationEmailResource.exception!!)
                } catch (e: Exception) {
                    Log.d("TAGGG", "SEND_EM_BRUH$$")
                    RegistrationStatus.SOMETHING_WRONG
                }
            }
        } else {
            try {
                if (signUpResource.exception is ResourceException.Other) {
                    userRepository.deleteUserFromDB(authRepository.getMyUID().data!!)
                    authRepository.deleteUser()
                }
                convertResourceExceptionToRegisterStatus(signUpResource.exception!!)

            } catch (e: Exception) {
                Log.d("TAGGG", "SEND_EM_BRUH")

                RegistrationStatus.SOMETHING_WRONG
            }
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