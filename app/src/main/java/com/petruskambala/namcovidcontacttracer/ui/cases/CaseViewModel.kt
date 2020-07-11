package com.petruskambala.namcovidcontacttracer.ui.cases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.petruskambala.namcovidcontacttracer.model.CovidCase
import com.petruskambala.namcovidcontacttracer.repository.CaseRepo
import com.petruskambala.namcovidcontacttracer.ui.AbstractViewModel
import com.petruskambala.namcovidcontacttracer.utils.Results

class CaseViewModel : AbstractViewModel<CovidCase>() {

    private val caseRepo = CaseRepo()

    private var _caseList = MutableLiveData<ArrayList<CovidCase>>(ArrayList())
    val caseList: LiveData<ArrayList<CovidCase>> = _caseList

    fun registerNewCase(case: CovidCase) {
        caseRepo.registerNewCase(case) { result ->
            if (result is Results.Success) {
                _caseList.postValue(_caseList.value?.apply { add(case) })
            }
            _repoResults.postValue(Pair(null,result))
        }
    }
}