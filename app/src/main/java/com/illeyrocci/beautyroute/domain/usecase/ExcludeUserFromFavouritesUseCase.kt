package com.illeyrocci.beautyroute.domain.usecase

import com.illeyrocci.beautyroute.domain.repository.AuthRepository
import com.illeyrocci.beautyroute.domain.repository.UserRepository

class ExcludeUserFromFavouritesUseCase (
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: String) {
        userRepository.excludeUserFromFavourites(id, authRepository.getMyUID().data!!)
    }

}