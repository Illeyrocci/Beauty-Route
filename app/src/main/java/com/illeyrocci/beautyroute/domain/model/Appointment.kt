package com.illeyrocci.beautyroute.domain.model

import java.util.UUID

data class Appointment(
    val id: String = UUID.randomUUID().toString(),
    val clientId: String="",
    val salonId: String="",
    val service: Service,
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