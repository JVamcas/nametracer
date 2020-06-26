package com.petruskambala.namcovidcontacttracer.ui.cases

import androidx.lifecycle.ViewModel
import com.petruskambala.namcovidcontacttracer.model.CovidCase
import com.petruskambala.namcovidcontacttracer.repository.CaseRepo
import com.petruskambala.namcovidcontacttracer.ui.AbstractViewModel
import com.petruskambala.namcovidcontacttracer.utils.Results

class CaseViewModel : AbstractViewModel<CovidCase>() {


    val caseRepo = CaseRepo()
    fun registerNewCase(case: CovidCase){
        caseRepo.registerNewCase(case){result ->
            if(result is Results.Success){

            }
        }
    }
}