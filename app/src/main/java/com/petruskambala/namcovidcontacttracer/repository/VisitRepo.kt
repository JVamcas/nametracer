package com.petruskambala.namcovidcontacttracer.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.petruskambala.namcovidcontacttracer.model.Visit
import com.petruskambala.namcovidcontacttracer.utils.Docs
import com.petruskambala.namcovidcontacttracer.utils.Results

class VisitRepo {
    private val DB = FirebaseFirestore.getInstance()

    fun loadPlacesVisited(personId: String, function: (ArrayList<Visit>, Results) -> Unit) {
        DB.collection(Docs.VISITS.name).whereEqualTo("personId", personId)

    }

    fun recordVisit(visit: Visit, callback: (Results) -> Unit) {
        DB.collection(Docs.VISITS.name).document().apply {
            visit.id = id
            set(visit)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                        callback(Results.Success(Results.Success.CODE.WRITE_SUCCESS))
                    else
                        callback(Results.Error(it.exception))
                }
        }
    }
}