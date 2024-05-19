package com.illeyrocci.beautyroute.domain.usecase

import com.illeyrocci.beautyroute.domain.repository.AuthRepository

class CreateUserWithEmailAndPasswordUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) =
        authRepository.createUserWithEmailAndPassword(email, password)
}