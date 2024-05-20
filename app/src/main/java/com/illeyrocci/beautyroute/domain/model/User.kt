package com.illeyrocci.beautyroute.domain.model

data class User(
    val name: String = "",
    val phone: String = "",
    val photo: String? = null,
    val address: String = "",
    val description: String? = null,
    val services: ArrayList<Service> = arrayListOf(),
    val schedule: Schedule = Schedule(arrayListOf()),
    val appointments: ArrayList<Appointment> = arrayListOf(),
    val favouriteUsers: ArrayList<String> = arrayListOf()
)