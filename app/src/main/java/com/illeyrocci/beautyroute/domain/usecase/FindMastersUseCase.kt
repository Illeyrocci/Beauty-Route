package com.illeyrocci.beautyroute.domain.usecase

import com.illeyrocci.beautyroute.domain.repository.UserRepository

class FindMastersUseCase (
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(query: String) = userRepository.searchUsers(query)
}