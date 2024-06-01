package com.illeyrocci.beautyroute.domain.usecase

import com.illeyrocci.beautyroute.domain.repository.UserRepository

class GetUserByIdUseCase (
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: String?) = userRepository.getUserDataSnapshot(id)
}