package com.petruskambala.namcovidcontacttracer.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.petruskambala.namcovidcontacttracer.model.CovidCase
import com.petruskambala.namcovidcontacttracer.utils.Docs
import com.petruskambala.namcovidcontacttracer.utils.Results

class CaseRepo {

    private val DB = FirebaseFirestore.getInstance()

    fun registerNewCase(case: CovidCase, callback: (result: Results) -> Unit) {
        DB.collection(Docs.CASES.name).document(case.id)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful)
                    callback(Results.Success(Results.Success.CODE.WRITE_SUCCESS))
                else callback(Results.Error(it.exception))
            }
    }
}