package com.illeyrocci.beautyroute.domain.model

data class User(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val photo: String? = null,
    val address: String = "",
    val description: String? = null,
    val services: ArrayList<Service> = arrayListOf(),
    val schedule: ArrayList<ScheduleDay> = arrayListOf(),
    val appointments: ArrayList<String> = arrayListOf(),
    val favouriteUsers: ArrayList<String> = arrayListOf()
)