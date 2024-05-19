package com.illeyrocci.beautyroute.domain.usecase

import com.illeyrocci.beautyroute.domain.model.Resource
import com.illeyrocci.beautyroute.domain.repository.AuthRepository
import com.illeyrocci.beautyroute.presentation.viewmodel.AuthStatus

class CheckIfAuthorizedAndGetStatusUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): AuthStatus {
        val checkIfAuthorizedStatus =
            authRepository.checkIfAuthorized()
        return if (checkIfAuthorizedStatus is Resource.Success && checkIfAuthorizedStatus.data!!) {
            AuthStatus.SUCCESS

        } else {
            AuthStatus.DEFAULT
        }
    }
}