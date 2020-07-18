package com.petruskambala.namcovidcontacttracer.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.petruskambala.namcovidcontacttracer.model.Visit
import com.petruskambala.namcovidcontacttracer.utils.Docs
import com.petruskambala.namcovidcontacttracer.utils.Results

class VisitRepo {
    private val DB = FirebaseFirestore.getInstance()

    fun loadPlacesVisited(personId: String, function: (ArrayList<Visit>, Results) -> Unit) {
        DB.collection(Docs.VISITS.name).whereEqualTo("personId", personId)

    }

    fun recordVisit(visit: Visit, callback: (Results) -> Unit) {
        val personRef = DB.collection(Docs.ACCOUNTS.name).document(visit.personId)
        val visitRef = DB.collection(Docs.VISITS.name).document()
        DB.batch().apply {
            update(
                personRef, "placeVisited", visit.person!!.placeVisited
            )
            visitRef.apply { visit.id = id }
            set(visitRef, visit)
        }.commit()
            .addOnCompleteListener {
                if (it.isSuccessful)
                    callback(Results.Success(Results.Success.CODE.WRITE_SUCCESS))
                else
                    callback(Results.Error(it.exception))
            }
    }

    fun loadPlaceVisited(personId: String, callback: (ArrayList<Visit>?, Results) -> Unit) {
        DB.collection(Docs.VISITS.name).whereEqualTo("personId", personId)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result?.let {
                        val stat = if (it.isEmpty) null else
                            it.documents.mapNotNull { doc -> doc.toObject(Visit::class.java) }
                        callback(
                            stat as ArrayList<Visit>?,
                            Results.Success(Results.Success.CODE.LOAD_SUCCESS)
                        )
                    }
                } else callback(null, Results.Error(it.exception))
            }
    }
}