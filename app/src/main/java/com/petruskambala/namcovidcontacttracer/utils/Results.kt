package com.petruskambala.namcovidcontacttracer.utils

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import java.lang.Exception
import com.google.firebase.firestore.FirebaseFirestoreException.Code.*
import com.petruskambala.namcovidcontacttracer.model.AbstractModel
import com.petruskambala.namcovidcontacttracer.model.AbstractModel.EntityExistException

/**
 * Represent results of an async operation
 */
sealed class Results {
    class Success(val code: CODE) : Results() {
        enum class CODE {
            WRITE_SUCCESS,
            UPDATE_SUCCESS,
            LOAD_SUCCESS,
            AUTH_SUCCESS,
            LOGOUT_SUCCESS,
            DELETE_SUCCESS,
        }
    }

    class Error(error: Exception?) : Results() {
        enum class CODE {
            NETWORK,
            PERMISSION_DENIED,
            UNKNOWN,
            ENTITY_EXISTS,
            AUTH,
            NO_RECORD
        }

        val code: CODE = when (error) {
            is EntityExistException -> CODE.ENTITY_EXISTS
            is FirebaseAuthException -> CODE.AUTH
            is FirebaseNetworkException -> CODE.NETWORK
            is AbstractModel.NoEntityException -> CODE.NO_RECORD
             is FirebaseFirestoreException -> {
                when (error.code) {
                    PERMISSION_DENIED -> CODE.PERMISSION_DENIED
                    ALREADY_EXISTS -> CODE.ENTITY_EXISTS
                    else -> CODE.UNKNOWN
                }
            }
            else -> CODE.UNKNOWN
        }
    }
}