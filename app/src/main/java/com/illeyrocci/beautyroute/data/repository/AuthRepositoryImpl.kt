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
) : BaseFirebaseRepository(), AuthRepository {

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Resource<Unit> = doRequest {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun checkEmailVerification(): Resource<Boolean> = doRequest {
        if (firebaseAuth.currentUser == null) {
            throw FirebaseAuthInvalidUserException(
                "currentUser is null",
                "checkEmailVerification"
            )
        } else {
            firebaseAuth.currentUser!!.isEmailVerified
        }
    }

    override suspend fun sendVerificationEmail(): Resource<Unit> = doRequest {
        if (firebaseAuth.currentUser == null) {
            throw FirebaseAuthInvalidUserException(
                "currentUser is null",
                "sendVerificationEmail"
            )
        } else {
            firebaseAuth.currentUser!!.sendEmailVerification().await()
        }
    }

    override suspend fun checkIfAuthorized(): Resource<Boolean> = doRequest {
        firebaseAuth.currentUser != null
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Resource<Unit> = doRequest {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun sendPasswordResetEmail(email: String): Resource<Unit> = doRequest {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    override suspend fun signOut(): Resource<Unit> = doRequest {
        firebaseAuth.signOut()
    }

    override suspend fun getMyUID(): Resource<String> = doRequest {
        firebaseAuth.currentUser?.uid ?: throw FirebaseAuthInvalidUserException(
            "currentUser is null",
            "getUserUID"
        )
    }

    override suspend fun deleteUser(): Resource<Unit> = doRequest {
        firebaseAuth.currentUser?.delete()?.await()
            ?: throw FirebaseAuthInvalidUserException(
                "currentUser is null",
                "deleteUser"
            )
    }
}