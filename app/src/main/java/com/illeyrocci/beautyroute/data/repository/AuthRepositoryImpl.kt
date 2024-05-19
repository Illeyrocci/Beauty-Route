package com.illeyrocci.beautyroute.data.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth
import com.illeyrocci.beautyroute.domain.model.Resource
import com.illeyrocci.beautyroute.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    //TODO("INJECTION FIREBASE AUTH. VAL")
    private var firebaseAuth: FirebaseAuth = Firebase.auth
) : BaseFirebaseAuthRepository(), AuthRepository {

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Resource<Unit> = doRequest {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        Resource.Success(Unit)
    }

    override suspend fun checkEmailVerification(): Resource<Boolean> = doRequest {
        firebaseAuth.currentUser?.let {
            Resource.Success(it.isEmailVerified)
        } ?: throw FirebaseAuthInvalidUserException("currentUser is null", "checkEmailVerification")
    }

    override suspend fun sendVerificationEmail(): Resource<Unit> = doRequest {
        firebaseAuth.currentUser?.let {
            it.sendEmailVerification().await()
            Resource.Success(Unit)
        } ?: throw FirebaseAuthInvalidUserException("currentUser is null", "sendVerificationEmail")
    }

    override suspend fun checkIfAuthenticated(): Resource<Boolean> = doRequest {
        Resource.Success(firebaseAuth.currentUser != null)
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Resource<Unit> = doRequest {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
        Resource.Success(Unit)
    }

    override suspend fun sendPasswordResetEmail(email: String): Resource<Unit> = doRequest {
        firebaseAuth.sendPasswordResetEmail(email).await()
        Resource.Success(Unit)
    }

    override suspend fun signOut(): Resource<Unit> = doRequest {
        Resource.Success(firebaseAuth.signOut())
    }
}