package com.illeyrocci.beautyroute.domain.repository

import com.illeyrocci.beautyroute.domain.model.Resource
import com.illeyrocci.beautyroute.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun addUserToDB(
        uid: String,
        name: String,
        phone: String,
        address: String
    ): Resource<Unit>

    suspend fun deleteUserFromDB(uid: String): Resource<Unit>

    suspend fun addService(uid: String): Resource<Unit>

    fun getUserData(uid: String): Flow<User>

    suspend fun getUserDataSnapshot(uid: String): User

    suspend fun updateServicePhotos(position: Int, urls: ArrayList<String>, uid: String)

    suspend fun updateServiceData(
        position: Int,
        name: String,
        duration: String,
        cost: String,
        description: String,
        uid: String
    )

    suspend fun changeUserData(
        uid: String,
        name: String,
        phone: String,
        address: String,
        description: String
    ): Resource<Unit>
}