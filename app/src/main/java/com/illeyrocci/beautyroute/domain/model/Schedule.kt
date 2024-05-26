package com.illeyrocci.beautyroute.domain.model

data class ScheduleDay(
    val dayStartUnixTime: Long = 0,
    val sections: ArrayList<CustomPair> = arrayListOf(CustomPair(false, null))
)

data class CustomPair(
    val first: Boolean = false,
    val second: String? = null
)

