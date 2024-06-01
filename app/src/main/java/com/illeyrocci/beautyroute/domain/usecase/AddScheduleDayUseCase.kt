package com.illeyrocci.beautyroute.domain.usecase

import com.illeyrocci.beautyroute.domain.repository.UserRepository

class AddScheduleDayUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(unixTime: Long, uid: String) =
        userRepository.addScheduleDay(unixTime, uid)
}