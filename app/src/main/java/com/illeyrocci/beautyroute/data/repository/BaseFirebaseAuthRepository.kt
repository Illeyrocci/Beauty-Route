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

abstract class BaseFirebaseAuthRepository {

    //dedicated to reduce code duplication
    suspend fun <T> doRequest(request: suspend () -> Resource.Success<T>): Resource<T> {
        return try {
            request.invoke()
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
            Resource.Failure(ResourceException.Unauthenticated(e))
        } catch (e: FirebaseTooManyRequestsException) {
            Resource.Failure(ResourceException.TooManyRequests(e))
        } catch (e: FirebaseApiNotAvailableException) {
            Resource.Failure(ResourceException.ApiNotAvailable(e))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            when (e.errorCode) {
                "ERROR_USER_NOT_FOUND" -> Resource.Failure(ResourceException.UserNotFound(e))
                "ERROR_MISSING_EMAIL" -> Resource.Failure(ResourceException.EmailAddressRequired(e))
                "ERROR_WRONG_PASSWORD" -> Resource.Failure(ResourceException.WrongPassword(e))
                "ERROR_WEAK_PASSWORD" -> Resource.Failure(ResourceException.AuthWeakPassword(e))
                "ERROR_INVALID_EMAIL" -> Resource.Failure(ResourceException.EmailBadFormat(e))
                else -> {
                    Log.d("TAGGG", "${e}, ${e.message}")
                    Resource.Failure(ResourceException.OtherInvalidCredentials(e))
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