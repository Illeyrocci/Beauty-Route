package com.illeyrocci.beautyroute.domain.model

data class User(
    val uid: String,
    val name: String,
    val phone: String,
    val photo: String,
    val address: String,
    val description: String? = null,
    val services: ArrayList<Service>,
    val schedule: Schedule,
    val appointments: ArrayList<Appointment>,
    val favouriteUsers: ArrayList<String>
)