package com.petruskambala.namcovidcontacttracer.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.petruskambala.namcovidcontacttracer.model.Account
import com.petruskambala.namcovidcontacttracer.utils.Docs
import com.petruskambala.namcovidcontacttracer.utils.Results
import com.petruskambala.namcovidcontacttracer.utils.Results.Success

class AccountRepo {
    private val DB = FirebaseFirestore.getInstance()
    private val AUTH = Firebase.auth

    fun createNewUserWithEmailAndPassword(
        mAccount: Account,
        password: String,
        callback: (Account?, Results) -> Unit
    ) {
        AUTH.createUserWithEmailAndPassword(mAccount.email!!, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    mAccount.apply { id = AUTH.currentUser!!.uid }
                    DB.collection(Docs.ACCOUNTS.name).document(mAccount.id)
                        .set(mAccount)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful)
                                callback(mAccount, Success(Success.CODE.WRITE_SUCCESS))
                            else callback(mAccount, Results.Error(task.exception))
                        }
                } else callback(null, Results.Error(it.exception))
            }
    }

    fun loadAccountInfo(userId: String, callback: (Account?, Results) -> Unit) {
        DB.collection(Docs.ACCOUNTS.name).document(userId).get()
            .addOnSuccessListener {
                val account = it.toObject(Account::class.java)
                val mResults = Success(Success.CODE.LOAD_SUCCESS)
                callback(account, mResults)
            }
            .addOnFailureListener { exception ->
                callback(null, Results.Error(exception))
            }
    }

    fun authenticateWithEmailAndPassword(
        email: String,
        password: String,
        callback: (Results) -> Unit
    ) {
        AUTH.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful)
                    callback(Results.Error(task.exception))
                else callback(Success(Success.CODE.AUTH_SUCCESS))
            }
    }

    fun findPerson(
        email: String? = null,
        phoneNumber: String? = null,
        nationalId: String? = null,
        callback: (Account?, Results) -> Unit
    ) {

        val query = if (email.isNullOrEmpty())
            DB.collection(Docs.ACCOUNTS.name).whereEqualTo("email", email)
        else if (phoneNumber.isNullOrEmpty())
            DB.collection(Docs.ACCOUNTS.name).whereEqualTo("cellphone", phoneNumber)
        else
            DB.collection(Docs.ACCOUNTS.name).whereEqualTo("nationalId", nationalId)
        query.get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val account =
                        it.result!!.documents.mapNotNull { acc -> acc.toObject(Account::class.java) }
                            .first()
                    callback(account, Success(Success.CODE.LOAD_SUCCESS))
                } else callback(null, Results.Error(it.exception))
            }

    }
}