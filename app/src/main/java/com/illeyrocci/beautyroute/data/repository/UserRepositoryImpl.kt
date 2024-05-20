package com.illeyrocci.beautyroute.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.illeyrocci.beautyroute.domain.model.Resource
import com.illeyrocci.beautyroute.domain.model.User
import com.illeyrocci.beautyroute.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    //TODO("INJECTION FIREBASE AUTH. VAL")
    private var db: FirebaseFirestore = Firebase.firestore
) : BaseFirebaseRepository(), UserRepository {

    private val COLLECTION_USERS_PATH = "users"

    override suspend fun addUserToDB(
        name: String,
        phone: String,
        address: String
    ): Resource<Unit> = doRequest {
        db.collection(COLLECTION_USERS_PATH)
            .add(User(name = name, phone = phone, address = address)).await()
    }

    override suspend fun deleteUserFromDB(uid: String): Resource<Unit> = doRequest {
        db.collection(COLLECTION_USERS_PATH).document(uid).delete().await()
    }
}