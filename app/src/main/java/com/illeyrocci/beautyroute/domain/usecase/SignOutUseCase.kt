package com.illeyrocci.beautyroute.domain.usecase

import com.illeyrocci.beautyroute.domain.repository.AuthRepository

class SignOutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke() = authRepository.signOut()
}