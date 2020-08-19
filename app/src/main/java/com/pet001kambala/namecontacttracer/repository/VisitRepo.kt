package com.pet001kambala.namecontacttracer.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pet001kambala.namecontacttracer.model.Visit
import com.pet001kambala.namecontacttracer.utils.DateUtil
import com.pet001kambala.namecontacttracer.utils.Docs
import com.pet001kambala.namecontacttracer.utils.Results
import kotlin.collections.ArrayList

class VisitRepo {
    private val DB = FirebaseFirestore.getInstance()

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

    fun loadPersonVisits(personId: String, callback: (ArrayList<Visit>?, Results) -> Unit) {
        DB.collection(Docs.VISITS.name)
            .whereEqualTo("personId",personId)
            .whereGreaterThan("expiryDate",DateUtil.today())
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

    fun loadPlaceVisitors(placeId: String, callback: (ArrayList<Visit>?, Results) -> Unit) {
        DB.collection(Docs.VISITS.name)
            .whereEqualTo("placeId",placeId)
            .whereGreaterThan("expiryDate",DateUtil.today())
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result?.let {
                        val stat = if (it.isEmpty) null else
                            it.documents.mapNotNull {
                                    doc -> doc.toObject(Visit::class.java)
                            }.distinctBy { it.person?.id }
                        callback(
                            stat as ArrayList<Visit>?,
                            Results.Success(Results.Success.CODE.LOAD_SUCCESS)
                        )
                    }
                } else callback(null, Results.Error(it.exception))
            }
    }
}