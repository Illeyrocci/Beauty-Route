package com.illeyrocci.beautyroute.data.repository

import android.util.Log
import com.google.firebase.FirebaseApiNotAvailableException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWebException
import com.illeyrocci.beautyroute.domain.model.Resource
import com.illeyrocci.beautyroute.domain.model.ResourceException

abstract class BaseFirebaseRepository {

    suspend fun <T> doRequest(request: suspend () -> T): Resource<T> {
        return try {
            Resource.Success(request.invoke())
        } catch (e: FirebaseNetworkException) {
            Resource.Failure(ResourceException.Network(e))
        } catch (e: FirebaseAuthWebException) {
            Resource.Failure(ResourceException.Web(e))
        } catch (e: FirebaseAuthUserCollisionException) {
            when (e.errorCode) {
                "ERROR_EMAIL_ALREADY_IN_USE" -> Resource.Failure(
                    ResourceException.AuthUserCollision(e)
                )

                else -> {
                    Log.d("TAGGG", "${e}, ${e.message}")
                    Resource.Failure(ResourceException.Other(e))
                }
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            when (e.errorCode) {
                "ERROR_USER_NOT_FOUND" -> Resource.Failure(ResourceException.UserNotFound(e))
                else -> {
                    Log.d("TAGGG", "${e}, ${e.message}")
                    Resource.Failure(ResourceException.Unauthenticated(e))
                }
            }
        } catch (e: FirebaseTooManyRequestsException) {
            Resource.Failure(ResourceException.TooManyRequests(e))
        } catch (e: FirebaseApiNotAvailableException) {
            Resource.Failure(ResourceException.ApiNotAvailable(e))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            when (e.errorCode) {
                "ERROR_USER_NOT_FOUND" -> Resource.Failure(ResourceException.UserNotFound(e))
                "ERROR_WRONG_PASSWORD" -> Resource.Failure(ResourceException.WrongPassword(e))
                "ERROR_WEAK_PASSWORD" -> Resource.Failure(ResourceException.AuthWeakPassword(e))
                "ERROR_INVALID_EMAIL" -> Resource.Failure(ResourceException.EmailBadFormat(e))
                "ERROR_INVALID_CREDENTIAL" -> when (e.message) {
                    "The supplied auth credential is incorrect, malformed or has expired." -> Resource.Failure(
                        ResourceException.WrongCredentials(e)
                    )

                    else -> Resource.Failure(ResourceException.Other(e))
                }

                else -> {
                    Log.d("TAGGG", "${e}, ${e.message}")
                    Resource.Failure(ResourceException.Other(e))
                }
            }
        } catch (e: java.lang.IllegalArgumentException) {
            if (e.message == "Given String is empty or null") {
                Resource.Failure(ResourceException.EmptyArguments(e))
            } else {
                Log.d("TAGGG", "${e}, ${e.message}")
                Resource.Failure(ResourceException.Other(e))
            }
        } catch (e: Exception) {
            Log.d("TAGGG", "${e}, ${e.message}")
            Resource.Failure(ResourceException.Other(e))
        }
    }
}