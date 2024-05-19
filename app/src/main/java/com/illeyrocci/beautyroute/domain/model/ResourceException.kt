package com.illeyrocci.beautyroute.domain.model

sealed class ResourceException(val exception: Exception) {

    class AuthUserCollision(exception: Exception) : ResourceException(exception = exception)

    class AuthWeakPassword(exception: Exception) : ResourceException(exception = exception)

    class Unauthenticated(exception: Exception) : ResourceException(exception = exception)

    class EmailBadFormat(exception: Exception) : ResourceException(exception = exception)

    class Web(exception: Exception) : ResourceException(exception = exception)

    class Network(exception: Exception) : ResourceException(exception = exception)

    class UserNotFound(exception: Exception) : ResourceException(exception = exception)

    class ApiNotAvailable(exception: Exception) : ResourceException(exception = exception)

    class EmailAddressRequired(exception: Exception) : ResourceException(exception = exception)

    class WrongPassword(exception: Exception) : ResourceException(exception = exception)

    class TooManyRequests(exception: Exception) : ResourceException(exception = exception)

    class OtherInvalidCredentials(exception: Exception) : ResourceException(exception = exception)

    class EmptyArguments(exception: Exception) : ResourceException(exception = exception)

    class Other(exception: Exception) : ResourceException(exception = exception)
}
