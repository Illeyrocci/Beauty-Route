package com.illeyrocci.beautyroute.domain.model

sealed class ResourceException(val exception: Exception) {

    class AuthUserCollision(exception: Exception) : ResourceException(exception = exception)

    class AuthWeakPassword(exception: Exception) : ResourceException(exception = exception)

    class Unauthenticated(exception: Exception) : ResourceException(exception = exception)

    class Other(exception: Exception) : ResourceException(exception = exception)

    companion object {
        const val UNAUTHENTICATEDCODE = "UNAUTHENTICATED"
    }
}
