package com.illeyrocci.beautyroute.domain.usecase

import android.util.Log
import com.illeyrocci.beautyroute.domain.model.Resource
import com.illeyrocci.beautyroute.domain.model.ResourceException
import com.illeyrocci.beautyroute.domain.repository.AuthRepository
import com.illeyrocci.beautyroute.domain.repository.UserRepository
import com.illeyrocci.beautyroute.presentation.viewmodel.EditProfileStatus

class SubmitProfileChangesUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        description: String,
        name: String,
        phone: String,
        email: String,
        lastPassword: String,
        password: String,
        address: String
    ): EditProfileStatus {
        val uid = authRepository.getMyUID().data!!
        Log.d("TAGGG", "${authRepository.getEmail().data!!}, ${lastPassword}, ${authRepository.signInWithEmailAndPassword(authRepository.getEmail().data!!, lastPassword).exception}")

        val changeCredentialsResource = authRepository.changeCredentials(email, password)

        val changeUserDataResource =
            userRepository.changeUserData(
                uid,
                name,
                phone,
                address,
                description
            )

        if (changeUserDataResource is Resource.Failure) return convertResourceExceptionToEditaProfileStatus(
            changeUserDataResource.exception!!
        )

        return if (changeCredentialsResource is Resource.Success) {
            EditProfileStatus.SUCCESS
        } else {
            convertResourceExceptionToEditaProfileStatus(changeCredentialsResource.exception!!)
        }
    }

    private fun convertResourceExceptionToEditaProfileStatus(exception: ResourceException): EditProfileStatus {
        return when (exception) {
            is ResourceException.EmailBadFormat -> EditProfileStatus.EMAIL_BAD_FORMAT
            is ResourceException.AuthUserCollision -> EditProfileStatus.EMAIL_COLLISION
            is ResourceException.AuthWeakPassword -> EditProfileStatus.WEAK_PASSWORD
            is ResourceException.Web -> EditProfileStatus.POOR_WEB
            is ResourceException.Network -> EditProfileStatus.POOR_NETWORK
            is ResourceException.ApiNotAvailable -> EditProfileStatus.API_NOT_AVAILABLE
            is ResourceException.TooManyRequests -> EditProfileStatus.TOO_MANY_REQUESTS
            is ResourceException.EmptyArguments -> EditProfileStatus.EMPTY_INPUTS
            else -> EditProfileStatus.SOMETHING_WRONG
        }
    }
}