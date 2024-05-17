package com.illeyrocci.beautyroute.domain.model

data class Appointment(
    val clientId: String,
    val salonId: String,
    val service: Service,
    val startTime: Long,
    val endTime: Long
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