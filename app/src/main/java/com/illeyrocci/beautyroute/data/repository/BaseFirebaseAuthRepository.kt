package com.illeyrocci.beautyroute.data.repository

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.illeyrocci.beautyroute.domain.model.Resource
import com.illeyrocci.beautyroute.domain.model.ResourceException

abstract class BaseFirebaseAuthRepository {

    //dedicated to reduce code duplication
    suspend fun <T> doRequest(request: suspend () -> Resource.Success<T>): Resource<T> {
        return try {
            request.invoke()
        } catch (e: FirebaseAuthUserCollisionException) {
            Resource.Failure(ResourceException.AuthUserCollision(e))
        } catch (e: FirebaseAuthException) {
            if (e.errorCode == ResourceException.UNAUTHENTICATEDCODE) {
                Resource.Failure(ResourceException.Unauthenticated(e))
            } else {
                Resource.Failure(ResourceException.Other(e))
            }
        } catch (e: Exception) {
            Resource.Failure(ResourceException.Other(e))
        }
    }
}