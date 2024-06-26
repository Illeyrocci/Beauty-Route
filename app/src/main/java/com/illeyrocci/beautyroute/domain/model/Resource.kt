package com.illeyrocci.beautyroute.domain.model

sealed class Resource<T>(
    val data: T? = null,
    val exception: ResourceException? = null
) {
    class Success<T>(data: T) : Resource<T>(data = data)
    class Failure<T>(exception: ResourceException) : Resource<T>(exception = exception)
}