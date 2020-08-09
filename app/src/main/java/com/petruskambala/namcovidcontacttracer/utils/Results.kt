package com.petruskambala.namcovidcontacttracer.utils

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
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
            VERIFICATION_EMAIL_SENT,
            PASSWORD_RESET_LINK_SENT,
            PHONE_VERIFY_CODE_SENT,
            PHONE_VERIFY_SUCCESS
        }
    }

    class Error(error: Exception?) : Results() {
        enum class CODE {
            NETWORK,
            PERMISSION_DENIED,
            UNKNOWN,
            ENTITY_EXISTS,
            AUTH,
            NO_RECORD,
            NO_ACCOUNT,
            INVALID_AUTH_CODE,
            PHONE_VERIFICATION_CODE_EXPIRED,
            NO_SUCH_USER,
            DUPLICATE_ACCOUNT,
            INCORRECT_EMAIL_PASSWORD_COMBO
        }

        val code: CODE = when (error) {
            is EntityExistException -> CODE.ENTITY_EXISTS
            is FirebaseAuthInvalidUserException -> CODE.NO_SUCH_USER
            is AbstractModel.InvalidPhoneAuthCodeException -> CODE.INVALID_AUTH_CODE
            is FirebaseAuthUserCollisionException -> CODE.DUPLICATE_ACCOUNT
            is FirebaseAuthException -> CODE.AUTH
            is FirebaseNetworkException -> CODE.NETWORK
            is AbstractModel.NoEntityException -> CODE.NO_RECORD
            is AbstractModel.PhoneVerificationCodeExpired -> CODE.PHONE_VERIFICATION_CODE_EXPIRED
            is AbstractModel.InvalidPasswordEmailException -> CODE.INCORRECT_EMAIL_PASSWORD_COMBO
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