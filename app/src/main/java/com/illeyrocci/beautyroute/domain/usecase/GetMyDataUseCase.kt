package com.illeyrocci.beautyroute.domain.usecase

import com.illeyrocci.beautyroute.domain.repository.AuthRepository

class GetMyDataUseCase(
    private val authRepository: AuthRepository,
    private val getUserDataUseCase: GetUserDataUseCase
) {
    suspend operator fun invoke() =
        getUserDataUseCase(authRepository.getMyUID().data!!)

}