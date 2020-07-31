package com.petruskambala.namcovidcontacttracer.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.petruskambala.namcovidcontacttracer.model.*
import com.petruskambala.namcovidcontacttracer.model.AbstractModel.NoEntityException
import com.petruskambala.namcovidcontacttracer.utils.Docs
import com.petruskambala.namcovidcontacttracer.utils.Results
import com.petruskambala.namcovidcontacttracer.utils.Results.Success

class AccountRepo {
    private val DB = FirebaseFirestore.getInstance()
    private val AUTH = Firebase.auth

    fun createNewUserWithEmailAndPassword(
        mAccount: Account,
        password: String,
        callback: (Person?, Results) -> Unit
    ) {
        val person = Person(account = mAccount)
        AUTH.createUserWithEmailAndPassword(mAccount.email!!, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    person.apply { id = AUTH.currentUser!!.uid }
                    DB.collection(Docs.ACCOUNTS.name).document(person.id)
                        .set(person)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful)
                                callback(person, Success(Success.CODE.WRITE_SUCCESS))
                            else callback(person, Results.Error(task.exception))
                        }
                } else callback(null, Results.Error(it.exception))
            }
    }

    fun loadAccountInfo(userId: String, callback: (Person?, Results) -> Unit) {
        DB.collection(Docs.ACCOUNTS.name).document(userId).get()
            .addOnSuccessListener {
                val account = it.toObject(Person::class.java)
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
        accountType: AccountType,
        callback: (Person?, Results) -> Unit
    ) {

        val query = if (!email.isNullOrEmpty())
            DB.collection(Docs.ACCOUNTS.name).whereEqualTo("email", email)
        else if (!phoneNumber.isNullOrEmpty())
            DB.collection(Docs.ACCOUNTS.name).whereEqualTo("cellphone", phoneNumber)
        else
            DB.collection(Docs.ACCOUNTS.name).whereEqualTo("nationalId", nationalId)
        query.whereEqualTo(
            "accountType", accountType.name
        ).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val docs = it.result!!.documents
                    val account =
                        if (docs.isEmpty()) null else docs.mapNotNull { acc -> acc.toObject(Person::class.java) }
                            .first()
                    val results =
                        if (account == null) Results.Error(NoEntityException()) else Success(Success.CODE.LOAD_SUCCESS)
                    callback(account, results)
                } else {
                    callback(null, Results.Error(it.exception))
                }
            }
    }

    fun updateAccount(account: Account, callback: (Results) -> Unit) {
        DB.collection(Docs.ACCOUNTS.name).document(account.id)
            .apply {
                val task = if (account.accountType == AccountType.BUSINESS)
                    update(
                        "name", account.name,
                        "accountType", account.accountType?.name,
                        "address_1", account.address_1,
                        "town", account.town,
                        "gender", null,
                        "nationalId", null,
                        "birthDate", null
                    )
                else
                    (account as Person).let {
                        update(
                            "name", it.name,
                            "accountType", it.accountType?.name,
                            "address_1", it.address_1,
                            "town", it.town,
                            "gender", it.gender?.name,
                            "nationalId", it.nationalId,
                            "birthDate", it.birthDate
                        )
                    }
                task.addOnCompleteListener {
                    val results =
                        if (it.isSuccessful) Success(Success.CODE.UPDATE_SUCCESS) else Results.Error(
                            it.exception
                        )
                    callback(results)
                }
            }
    }
}