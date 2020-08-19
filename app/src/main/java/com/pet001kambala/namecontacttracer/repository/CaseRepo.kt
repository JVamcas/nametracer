package com.pet001kambala.namecontacttracer.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pet001kambala.namecontacttracer.model.AbstractModel
import com.pet001kambala.namecontacttracer.model.Cases
import com.pet001kambala.namecontacttracer.utils.Docs
import com.pet001kambala.namecontacttracer.utils.ParseUtil
import com.pet001kambala.namecontacttracer.utils.Results

class CaseRepo {

    private val DB = FirebaseFirestore.getInstance()

    fun registerNewCase(aCase: Cases, callback: (result: Results) -> Unit) {
        DB.collection(Docs.CASES.name).document(aCase.personId)
            .set(aCase)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    callback(Results.Success(Results.Success.CODE.WRITE_SUCCESS))
                else callback(Results.Error(it.exception))
            }
    }

    fun loadCases(callback: (ArrayList<Cases>?, Results) -> Unit) {
        DB.collection(Docs.CASES.name).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.let {
                        val stat = if (it.isEmpty) null else
                            it.documents.mapNotNull { doc -> doc.toObject(Cases::class.java) }
                        callback(
                            stat as ArrayList<Cases>?,
                            Results.Success(Results.Success.CODE.LOAD_SUCCESS)
                        )
                    }
                } else callback(null, Results.Error(task.exception))
            }
    }

    fun findCase(
        email: String? = null,
        phoneNumber: String? = null,
        callback: (Cases?, Results) -> Unit
    ) {
        val phone = phoneNumber?.let { ParseUtil.formatPhone(phoneNumber) }
        val query = if (!email.isNullOrEmpty())
            DB.collection(Docs.CASES.name).whereEqualTo("email", email)
        else
            DB.collection(Docs.CASES.name).whereEqualTo("cellphone", phone)
        query.get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val docs = it.result!!.documents
                    val case =
                        if (docs.isEmpty()) null else docs.mapNotNull { acc ->
                            acc.toObject(
                                Cases::class.java
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

    fun updateCase(aCase: Cases, callback: (Results) -> Unit) {
        DB.collection(Docs.CASES.name).document(aCase.personId)
            .update(
                "caseState", aCase.caseState!!.name,
                "inQuarantine", aCase.inQuarantine
            ).addOnCompleteListener {
                if (it.isSuccessful)
                    callback(Results.Success(Results.Success.CODE.UPDATE_SUCCESS))
                else callback(Results.Error(it.exception))
            }
    }
}