package com.illeyrocci.beautyroute.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.illeyrocci.beautyroute.domain.model.Appointment
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
import kotlin.math.max


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
            .set(User(uid = uid, name = name, phone = phone, address = address)).await()
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
            val day = (date.time / 86400000L) * 86400000
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

    override suspend fun searchUsers(query: String): List<User> {
        return try {

            val allUsers = db.collection("users").get().await().toObjects(User::class.java)

            val allUsersToMark: MutableMap<User, Int> = mutableMapOf()

            val lcs: (s1: String, s2: String) -> Int = { s1, s2 ->
                val m = s1.length
                val n = s2.length
                val lcsTable = Array(m + 1) {
                    IntArray(
                        n + 1
                    )
                }

                // Building the matrix in bottom-up way
                for (i in 0..m) {
                    for (j in 0..n) {
                        if (i == 0 || j == 0) lcsTable[i][j] = 0
                        else if (s1[i - 1] == s2[j - 1]) lcsTable[i][j] = lcsTable[i - 1][j - 1] + 1
                        else lcsTable[i][j] =
                            max(lcsTable[i - 1][j].toDouble(), lcsTable[i][j - 1].toDouble())
                                .toInt()
                    }
                }

                var index = lcsTable[m][n]
                val temp = index

                val lcs = CharArray(index + 1)
                lcs[index] = '\u0000'

                var i = m
                var j = n
                while (i > 0 && j > 0) {
                    if (s1[i - 1] == s2[j - 1]) {
                        lcs[index - 1] = s1[i - 1]

                        i--
                        j--
                        index--
                    } else if (lcsTable[i - 1][j] > lcsTable[i][j - 1]) i--
                    else j--
                }
                temp
            }

            allUsers.forEach {
                allUsersToMark += it to 0
                allUsersToMark[it] = allUsersToMark[it]!! + lcs(it.description ?: "", query) * 100
                it.services.forEach { service ->
                    allUsersToMark[it] = allUsersToMark[it]!! + lcs(service.name, query) * 10
                    allUsersToMark[it] =
                        allUsersToMark[it]!! + lcs(service.description ?: "", query)
                }
            }

            val list = allUsersToMark.toList().toMutableList()
            list.sortWith { o1, o2 -> o2.second.compareTo(o1.second) }

            val res = list.map { pair -> pair.first }
            res
        } catch (e: Exception) {
            Log.d("TAGGG", "!!!!!!!!EXCEPTION search users: $e")
            listOf(User())
        }
    }

    override suspend fun makeAppointment(
        clientId: String,
        masterId: String,
        servicePosition: Int,
        startTime: Long,
        endTime: Long
    ) {

        Log.d("TAGGG", "stTime: $startTime endTime: $endTime")

        val userRef = db.collection("users").document(clientId)
        val userSnapshot = userRef.get().await()

        if (userSnapshot.exists()) {
            val user = userSnapshot.toObject(User::class.java)!!

            val appointment = Appointment(
                id = "",
                clientId = clientId,
                salonId = masterId,
                startTime = startTime,
                endTime = endTime,
                service = user.services[servicePosition]
            )

            val ref = db.collection("appointments").add(appointment).await()
            val newAppointment = appointment.copy(id = ref.id)
            ref.update("id", ref.id).await()

            val arrLst = user.appointments + newAppointment.id
            userRef.update("appointments", arrLst).await()
            Log.d("GAT", "exists")

            val masterRef = db.collection("users").document(masterId)
            val masterSnapshot = masterRef.get().await()
            val master = masterSnapshot.toObject(User::class.java)
            val schedule = master!!.schedule

            val newSchedule = schedule

            var found = false
            schedule.forEach { scheduleDay ->
                if ((startTime / 86400000) * 86400000 == scheduleDay.dayStartUnixTime) {
                    found = true
                }
            }
            if (!found) {
                val sections: ArrayList<CustomPair> = arrayListOf()
                repeat(96) { sections.add(CustomPair(false, null)) }
                newSchedule.add(
                    ScheduleDay(
                        dayStartUnixTime = (startTime / 86400000L) * 86400000,
                        sections = sections
                    )
                )
            }
            Log.d("TAGGG", "imhere: $startTime, $endTime")

            schedule.forEachIndexed { dayIndex, day ->
                if (startTime >= day.dayStartUnixTime && endTime < day.dayStartUnixTime + 86400000) {
                    day.sections.forEachIndexed { index, _ ->
                        if (startTime - day.dayStartUnixTime <= ((6L * 60 + index * 15) * 60000) % 86400000
                            && ((6L * 60 + (index + 1) * 15) * 60000) % 86400000 <= endTime - day.dayStartUnixTime
                        ) {
                            Log.d("TAGGG", "imhere: $startTime, $endTime")
                            newSchedule[dayIndex].sections[index] = CustomPair(true, ref.id)
                        }
                    }
                }
            }

            userRef.update("schedule", newSchedule).await()

        }
    }

    override suspend fun addScheduleDay(unixTime: Long, uid: String): Int {
        val userRef = db.collection("users").document(uid)
        val userSnapshot = userRef.get().await()

        val user = userSnapshot.toObject(User::class.java)!!

        val oldSchedule = user.schedule
        val sections: ArrayList<CustomPair> = arrayListOf()
        repeat(96) { sections.add(CustomPair(false, null)) }
        val newSchedule = oldSchedule + ScheduleDay(unixTime, sections)
        userRef.update("schedule", newSchedule).await()
        return oldSchedule.size
    }

    override suspend fun getAppointmentById(id: String): Appointment {
        val appointmentRef = db.collection("appointments").document(id)
        val appointmentSnapshot = appointmentRef.get().await()

        return appointmentSnapshot.toObject(Appointment::class.java)!!
    }
}