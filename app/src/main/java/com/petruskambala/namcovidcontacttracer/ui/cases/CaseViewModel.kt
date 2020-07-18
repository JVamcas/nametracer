package com.petruskambala.namcovidcontacttracer.ui.cases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.petruskambala.namcovidcontacttracer.model.CovidCase
import com.petruskambala.namcovidcontacttracer.model.Person
import com.petruskambala.namcovidcontacttracer.repository.CaseRepo
import com.petruskambala.namcovidcontacttracer.ui.AbstractViewModel
import com.petruskambala.namcovidcontacttracer.utils.Results

class CaseViewModel : AbstractViewModel<CovidCase>() {

    private val caseRepo = CaseRepo()

    private var _caseList = MutableLiveData<ArrayList<CovidCase>>()
    val caseList: LiveData<ArrayList<CovidCase>> = _caseList

    init {
        loadCases()
    }

    fun registerNewCase(case: CovidCase) {
        caseRepo.registerNewCase(case) { result ->
            if (result is Results.Success)
                _caseList.postValue(_caseList.value?.apply { add(case) })
            _repoResults.postValue(Pair(null, result))
        }
    }

    private fun loadCases() {
        caseRepo.loadCases { cases, results ->
            if (results is Results.Success)
                _caseList.value = cases
        }
    }

    fun findCase(email: String? = null, cellphone: String? = null, nationalId: String? = null) {
        caseRepo.findCase(email, cellphone, nationalId) { case, results ->
            _repoResults.postValue(Pair(case?.also { it.person = Person() }, results))
        }
    }

    fun updateCase(case: CovidCase) {
        caseRepo.updateCase(case) {
            if (it is Results.Success){
                val pos = caseList.value!!.indexOfFirst { case.personId == it.personId }
                _caseList.postValue(_caseList.value!!.apply { set(pos,case) })
            }
            _repoResults.postValue(Pair(null, it))
        }
    }
}