package com.illeyrocci.beautyroute.domain.usecase

import com.illeyrocci.beautyroute.domain.repository.AuthRepository
import com.illeyrocci.beautyroute.domain.repository.UserRepository

class DeleteAppointmentUseCase(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(id: String) =
        userRepository.deleteAppointment(id, authRepository.getMyUID().data!!)
}