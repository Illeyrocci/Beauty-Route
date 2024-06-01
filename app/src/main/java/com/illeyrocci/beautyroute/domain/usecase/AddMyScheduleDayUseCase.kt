package com.illeyrocci.beautyroute.domain.usecase

import com.illeyrocci.beautyroute.domain.repository.AuthRepository

class AddMyScheduleDayUseCase(
    private val addScheduleDayUseCase: AddScheduleDayUseCase,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(unixTime: Long) =
        addScheduleDayUseCase(unixTime, authRepository.getMyUID().data!!)
}