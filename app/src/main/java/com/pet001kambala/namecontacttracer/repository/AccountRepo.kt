package com.pet001kambala.namecontacttracer.repository

import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.pet001kambala.namecontacttracer.model.AbstractModel
import com.pet001kambala.namecontacttracer.model.AbstractModel.NoEntityException
import com.pet001kambala.namecontacttracer.model.Account
import com.pet001kambala.namecontacttracer.model.AccountType
import com.pet001kambala.namecontacttracer.model.Person
import com.pet001kambala.namecontacttracer.utils.Docs
import com.pet001kambala.namecontacttracer.utils.ParseUtil
import com.pet001kambala.namecontacttracer.utils.Results.Success
import com.pet001kambala.namecontacttracer.utils.Results
import com.pet001kambala.namecontacttracer.utils.Results.Success.CODE.*

class AccountRepo {
    private val DB = FirebaseFirestore.getInstance()
    private val AUTH = Firebase.auth

    fun createNewUserWithEmailAndPassword(
        person: Person,
        password: String,
        callback: (Person?, Results) -> Unit
    ) {
        AUTH.createUserWithEmailAndPassword(person.email!!, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    person.apply { id = AUTH.currentUser!!.uid }
                    DB.collection(Docs.ACCOUNTS.name).document(person.id)
                        .set(person)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful)
                                callback(person, Success(WRITE_SUCCESS))
                            else callback(person, Results.Error(task.exception))
                        }
                } else callback(null, Results.Error(it.exception))
            }
    }


    fun createUserWithPhone(account: Person, callback: (Person?, Results) -> Unit) {
        account.let {
            it.id = AUTH.currentUser?.uid ?: ""
            it.cellphone = ParseUtil.formatPhone(it.cellphone!!)
        }

        DB.collection(Docs.ACCOUNTS.name).document(account.id)
            .set(account)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    callback(account, Success(WRITE_SUCCESS))
                else callback(null, Results.Error(task.exception))
            }

    }

    fun loadAccountInfo(userId: String, callback: (Person?, Results) -> Unit) {
        DB.collection(Docs.ACCOUNTS.name).document(userId).get()
            .addOnSuccessListener {
                val account = it.toObject(Person::class.java)
                val mResults = Success(LOAD_SUCCESS)
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
            .addOnCompleteListener {
                callback(
                    when {
                        it.isSuccessful -> Success(AUTH_SUCCESS)
                        it.exception is FirebaseAuthInvalidCredentialsException ->
                            Results.Error(AbstractModel.InvalidPasswordEmailException())
                        else -> Results.Error(it.exception)
                    }
                )
            }
    }

    fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        callback: (Results) -> Unit
    ) {
        AUTH.signInWithCredential(credential)
            .addOnCompleteListener {
                callback(
                    when {
                        it.isSuccessful -> Success(AUTH_SUCCESS)
                        it.exception is FirebaseAuthInvalidCredentialsException ->
                            Results.Error(
                                AbstractModel.InvalidPhoneAuthCodeException()
                            )
                        else -> Results.Error(it.exception)
                    }
                )
            }
    }

    fun findAccount(
        email: String? = null,
        phoneNumber: String? = null,
        accountType: AccountType,
        callback: (Person?, Results) -> Unit
    ) {
        val query = if (!email.isNullOrEmpty())
            DB.collection(Docs.ACCOUNTS.name).whereEqualTo("email", email)
        else
            DB.collection(Docs.ACCOUNTS.name).whereEqualTo("cellphone", ParseUtil.formatPhone(phoneNumber!!))
        query.whereEqualTo("accountType", accountType).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val docs = it.result!!.documents
                    val account =
                        if (docs.isEmpty()) null else docs.mapNotNull { acc -> acc.toObject(Person::class.java) }
                            .first()
                    val results =
                        if (account == null) Results.Error(NoEntityException()) else Success(LOAD_SUCCESS)
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
                            "birthDate", it.birthDate
                        )
                    }
                task.addOnCompleteListener {
                    val results =
                        if (it.isSuccessful) Success(UPDATE_SUCCESS) else Results.Error(
                            it.exception
                        )
                    callback(results)
                }
            }
    }

    fun sendVerificationEmail(callback: (Results) -> Unit) {
        val user = Firebase.auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener {
            callback(
                if (it.isSuccessful)
                    Success(VERIFICATION_EMAIL_SENT)
                else Results.Error(it.exception)
            )
        }
    }

    fun resetPassword(email: String, callback: (Results) -> Unit) {
        Firebase.auth.sendPasswordResetEmail(email).addOnCompleteListener {
            val results = if (it.isSuccessful) Success(PASSWORD_RESET_LINK_SENT)
            else Results.Error(it.exception)
            callback(results)
        }
    }

    fun verifyPhoneNumber(
        phone: String,
        activity: FragmentActivity,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone, // Phone number to verify
            120, // Timeout duration
            java.util.concurrent.TimeUnit.SECONDS, // Unit of timeout
            activity, // Activity (for callback binding)
            callback
        ) // OnVerificationStateChangedCallbacks
    }
}