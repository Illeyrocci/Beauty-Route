package com.illeyrocci.beautyroute.domain.repository

import com.illeyrocci.beautyroute.domain.model.Resource

interface AuthRepository {

    suspend fun createUserWithEmailAndPassword(email: String, password: String): Resource<Unit>

    suspend fun checkEmailVerification(): Resource<Boolean>

    suspend fun sendVerificationEmail(): Resource<Unit>

    suspend fun checkIfAuthorized(): Resource<Boolean>

    suspend fun signInWithEmailAndPassword(email: String, password: String): Resource<Unit>

    suspend fun sendPasswordResetEmail(email: String): Resource<Unit>

    suspend fun signOut(): Resource<Unit>
}