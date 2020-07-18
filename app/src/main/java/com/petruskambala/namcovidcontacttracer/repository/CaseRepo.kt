package com.petruskambala.namcovidcontacttracer.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.petruskambala.namcovidcontacttracer.model.AbstractModel
import com.petruskambala.namcovidcontacttracer.model.CovidCase
import com.petruskambala.namcovidcontacttracer.utils.Docs
import com.petruskambala.namcovidcontacttracer.utils.Results

class CaseRepo {

    private val DB = FirebaseFirestore.getInstance()

    fun registerNewCase(case: CovidCase, callback: (result: Results) -> Unit) {
        DB.collection(Docs.CASES.name).document(case.personId)
            .set(case)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    callback(Results.Success(Results.Success.CODE.WRITE_SUCCESS))
                else callback(Results.Error(it.exception))
            }
    }

    fun loadCases(callback: (ArrayList<CovidCase>?, Results) -> Unit) {
        DB.collection(Docs.CASES.name).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.let {
                        val stat = if (it.isEmpty) null else
                            it.documents.mapNotNull { doc -> doc.toObject(CovidCase::class.java) }
                        callback(
                            stat as ArrayList<CovidCase>?,
                            Results.Success(Results.Success.CODE.LOAD_SUCCESS)
                        )
                    }
                } else callback(null, Results.Error(task.exception))
            }
    }

    fun findCase(
        email: String? = null,
        phoneNumber: String? = null,
        nationalId: String? = null,
        callback: (CovidCase?, Results) -> Unit
    ) {
        val query = if (!email.isNullOrEmpty())
            DB.collection(Docs.CASES.name).whereEqualTo("email", email)
        else if (!phoneNumber.isNullOrEmpty())
            DB.collection(Docs.CASES.name).whereEqualTo("cellphone", phoneNumber)
        else
            DB.collection(Docs.CASES.name).whereEqualTo("nationalId", nationalId)
        query.get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val docs = it.result!!.documents
                    val case =
                        if (docs.isEmpty()) null else docs.mapNotNull { acc ->
                            acc.toObject(
                                CovidCase::class.java
                            )
                        }
                            .first()
                    val results =
                        if (case == null) Results.Error(AbstractModel.NoEntityException()) else Results.Success(
                            Results.Success.CODE.LOAD_SUCCESS
                        )
                    callback(case, results)
                } else callback(null, Results.Error(it.exception))
            }
    }

    fun updateCase(case: CovidCase, callback: (Results) -> Unit) {
        DB.collection(Docs.CASES.name).document(case.personId)
            .update(
                "caseState", case.caseState!!.name,
                "inQuarantine", case.inQuarantine
            ).addOnCompleteListener {
                if (it.isSuccessful)
                    callback(Results.Success(Results.Success.CODE.UPDATE_SUCCESS))
                else callback(Results.Error(it.exception))
            }
    }
}