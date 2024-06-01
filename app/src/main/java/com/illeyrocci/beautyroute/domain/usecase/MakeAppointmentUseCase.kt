package com.illeyrocci.beautyroute.domain.usecase

import com.illeyrocci.beautyroute.domain.repository.AuthRepository
import com.illeyrocci.beautyroute.domain.repository.UserRepository

class MakeAppointmentUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        servicePosition: Int,
        startTime: Long,
        endTime: Long,
        masterId: String
    ) {
        userRepository.makeAppointment(
            authRepository.getMyUID().data!!,
            masterId,
            servicePosition,
            startTime,
            endTime
        )
    }
}