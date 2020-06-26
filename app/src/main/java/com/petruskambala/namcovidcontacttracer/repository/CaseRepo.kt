package com.petruskambala.namcovidcontacttracer.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.petruskambala.namcovidcontacttracer.model.CovidCase
import com.petruskambala.namcovidcontacttracer.utils.Docs
import com.petruskambala.namcovidcontacttracer.utils.Results

class CaseRepo {


    private val DB = FirebaseFirestore.getInstance()
    private val AUTH = Firebase.auth

    fun registerNewCase(case: CovidCase, callback:(result: Results)->Unit){
        DB.collection(Docs.CASES.name).document(case.id)
    }
}