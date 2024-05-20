package com.illeyrocci.beautyroute.domain.model

data class Appointment(
    val clientId: String="",
    val salonId: String="",
    val service: Service=Service(),
    val startTime: Long=0,
    val endTime: Long=0
    //val status: AppointmentStatus = AppointmentStatus.PENDING
)
//{
//    enum class AppointmentStatus {
//        PENDING,
//        CONFIRMED,
//        CANCELLED,
//        COMPLETED
//    }
//}