package com.petruskambala.namcovidcontacttracer.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.petruskambala.namcovidcontacttracer.model.User
import com.petruskambala.namcovidcontacttracer.utils.Docs
import com.petruskambala.namcovidcontacttracer.utils.Results
import com.petruskambala.namcovidcontacttracer.utils.Results.Success

class UserRepo {
    private val DB = FirebaseFirestore.getInstance()
    private val AUTH = Firebase.auth

    fun createNewUserWithEmailAndPassword(mUser: User, password: String, callback: (User?, Results) -> Unit) {
        AUTH.createUserWithEmailAndPassword(mUser.email!!, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = AUTH.currentUser
                    DB.collection(Docs.USERS.name).document(user!!.uid)
                        .set(mUser)
                        .addOnCompleteListener { task1: Task<Void?> ->
                            if (task1.isSuccessful)
                                callback(mUser, Success(Success.CODE.WRITE_SUCCESS))
                            else callback(mUser, Results.Error(task1.exception))
                        }
                } else callback(null, Results.Error(task.exception))
            }
    }

    fun loadUserProfile(userId: String, callback: (User?, Results) -> Unit) {
        DB.collection(Docs.USERS.name).document(userId).get()
            .addOnSuccessListener { shot: DocumentSnapshot ->
                val mUser = shot.toObject(User::class.java)
                val mResults = Success(Success.CODE.LOAD_SUCCESS)
                callback(mUser, mResults)
            }
            .addOnFailureListener { exception ->
                callback(null, Results.Error(exception))
            }
    }

    fun authenticateWithEmailAndPassword(email: String, password: String, callback: (Results) -> Unit) {
        AUTH.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful)
                    callback(Results.Error(task.exception))
                else callback(Success(Success.CODE.AUTH_SUCCESS))
            }
    }
}