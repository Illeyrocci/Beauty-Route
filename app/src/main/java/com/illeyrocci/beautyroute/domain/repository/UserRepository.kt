package com.illeyrocci.beautyroute.domain.repository

import com.illeyrocci.beautyroute.domain.model.Resource

interface UserRepository {

    suspend fun addUserToDB(name: String, phone: String, address: String) : Resource<Unit>

    suspend fun deleteUserFromDB(uid: String): Resource<Unit>
}