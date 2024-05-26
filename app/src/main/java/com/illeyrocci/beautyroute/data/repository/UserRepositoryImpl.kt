package com.illeyrocci.beautyroute.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.illeyrocci.beautyroute.domain.model.CustomPair
import com.illeyrocci.beautyroute.domain.model.Resource
import com.illeyrocci.beautyroute.domain.model.ScheduleDay
import com.illeyrocci.beautyroute.domain.model.Service
import com.illeyrocci.beautyroute.domain.model.User
import com.illeyrocci.beautyroute.domain.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

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
            val arrLst = user.services + Service(name = "", duration = 0)
            userRef.update("services", arrLst).await()
            Log.d("GAT", "exists")
        }
    }

    override fun getUserData(uid: String): Flow<User> = callbackFlow {
        val listener =
            db.collection(COLLECTION_USERS_PATH).document(uid).addSnapshotListener { snapshot, e ->
                if (snapshot != null && snapshot.exists()) {
                    Log.d("TAGGG", "repoiMpil ${snapshot.data}")
                    trySend(snapshot.toObject(User::class.java)!!).isSuccess
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getUserDataSnapshot(uid: String): User {
        return db.collection(COLLECTION_USERS_PATH).document(uid).get().await()
            .toObject(User::class.java)!!
    }

    override suspend fun updateServicePhotos(position: Int, urls: ArrayList<String>, uid: String) {
        val userRef = db.collection(COLLECTION_USERS_PATH).document(uid)
        val oldArr = userRef.get().await().toObject(User::class.java)!!.services
        oldArr[position] = oldArr[position].copy(photos = urls)
        db.collection(COLLECTION_USERS_PATH).document(uid).update("services", oldArr)

        Log.d("TAGGG", oldArr.toString())

    }

    override suspend fun updateServiceData(
        position: Int,
        name: String,
        duration: String,
        cost: String,
        description: String,
        uid: String
    ) {
        try {

            val userRef = db.collection(COLLECTION_USERS_PATH).document(uid)
            val oldArr = userRef.get().await().toObject(User::class.java)!!.services
            oldArr[position] = oldArr[position].copy(
                name = name,
                duration = try {
                    duration.toInt()
                } catch (e: Exception) {
                    0
                },
                price = try {
                    cost.toInt()
                } catch (e: Exception) {
                    0
                },
                description = description
            )
            db.collection(COLLECTION_USERS_PATH).document(uid).update("services", oldArr)
        } catch (e: Exception) {
            Log.d("TAGGG", "$e")
        }
    }

    override suspend fun changeUserData(
        uid: String,
        name: String,
        phone: String,
        address: String,
        description: String
    ): Resource<Unit> = doRequest {
        db.collection("users").document(uid)
            .update(
                mapOf(
                    "name" to name,
                    "phone" to phone,
                    "address" to address,
                    "description" to description
                )
            )


    }

    override suspend fun changeUserScheduleSection(
        uid: String,
        date: Date,
        sectionPos: Int,
    ) {

        try {
            val userRef = db.collection("users").document(uid)
            val userSnapshot = userRef.get().await()
            val user = userSnapshot.toObject(User::class.java)!!
            val lastArr = user.schedule
            var dayPos: Int? = null
            val day = (date.time/86400000L + 1) * 86400000
            lastArr.forEachIndexed { index, it ->
                Log.d("TAGGG", "${it.dayStartUnixTime} $day")

                if (it.dayStartUnixTime == day) {
                    dayPos = index
                }
            }

            if (dayPos == null) {
                val sections: ArrayList<CustomPair> = arrayListOf()
                repeat(96) { sections.add(CustomPair(false, null)) }
                lastArr.add(ScheduleDay(dayStartUnixTime = day, sections = sections))
                dayPos = lastArr.size - 1
            }
            lastArr[dayPos!!].sections[sectionPos] =
                lastArr[dayPos!!].sections[sectionPos].copy(first = !lastArr[dayPos!!].sections[sectionPos].first)

            db.collection("users").document(uid).update(
                mapOf(
                    "schedule" to lastArr
                )
            )
        } catch (e: Exception) {
            Log.d("TAGGG", "$e")
        }

    }
}