package com.illeyrocci.beautyroute.domain.usecase

import com.illeyrocci.beautyroute.domain.repository.UserRepository

class GetUserDataUseCase(private val userRepository: UserRepository) {
    operator fun invoke(uid: String) = userRepository.getUserData(uid)
}