package com.illeyrocci.beautyroute.domain.model

import java.util.Random

data class Service(
    val uid: Long = Random().nextLong(),
    val name: String="",
    val duration: Int=0,
    val price: Int? = null,
    val description: String? = null,
    val photos: ArrayList<String>? = null
)
