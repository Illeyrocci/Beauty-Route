package com.illeyrocci.beautyroute.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.illeyrocci.beautyroute.domain.model.Resource
import com.illeyrocci.beautyroute.domain.model.Service
import com.illeyrocci.beautyroute.domain.model.User
import com.illeyrocci.beautyroute.domain.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    //TODO("INJECTION FIREBASE AUTH. VAL")
    private var db: FirebaseFirestore = Firebase.firestore
) : BaseFirebaseRepository(), UserRepository {

    private val COLLECTION_USERS_PATH = "users"

    override suspend fun addUserToDB(
        uid: String,
        name: String,
        phone: String,
        address: String
    ): Resource<Unit> = doRequest {
        db.collection(COLLECTION_USERS_PATH).document(uid)
            .set(User(name = name, phone = phone, address = address)).await()
    }

    override suspend fun deleteUserFromDB(uid: String): Resource<Unit> = doRequest {
        db.collection(COLLECTION_USERS_PATH).document(uid).delete().await()
    }

    override suspend fun addService(uid: String): Resource<Unit> = doRequest {

            val userRef = db.collection("users").document(uid)
            val userSnapshot = userRef.get().await()

            if (userSnapshot.exists()) {
                val user = userSnapshot.toObject(User::class.java)!!
                val arrLst = user.services + Service("", 0)
                userRef.update("services", arrLst).await()
                Log.d("GAT", "exists")
            }
    }

    override fun getUserData(uid: String): Flow<User> = callbackFlow {
        val listener =
            db.collection(COLLECTION_USERS_PATH).document(uid).addSnapshotListener { snapshot, e ->
                if (snapshot != null && snapshot.exists()) {
                    trySend(snapshot.toObject(User::class.java)!!).isSuccess
                }
            }

        awaitClose { listener.remove() }
    }

}