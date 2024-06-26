package com.illeyrocci.beautyroute.domain.model

data class Service(
    val name: String="",
    val duration: Int=0,
    val price: Int? = null,
    val description: String? = null,
    val photos: ArrayList<String>? = null
)
