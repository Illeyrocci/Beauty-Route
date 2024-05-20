package com.illeyrocci.beautyroute.domain.usecase

import com.illeyrocci.beautyroute.domain.repository.AuthRepository
import com.illeyrocci.beautyroute.domain.repository.UserRepository

class UpdateServiceDataUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        position: Int,
        name: String,
        duration: String,
        cost: String,
        descroption: String
    ) {
        userRepository.updateServiceData(
            position,
            name,
            duration,
            cost,
            descroption,
            authRepository.getMyUID().data!!
        )
    }
}