package com.petruskambala.namcovidcontacttracer.ui.cases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.petruskambala.namcovidcontacttracer.model.CovidCase
import com.petruskambala.namcovidcontacttracer.model.CovidStat
import com.petruskambala.namcovidcontacttracer.model.Person
import com.petruskambala.namcovidcontacttracer.repository.CaseRepo
import com.petruskambala.namcovidcontacttracer.ui.AbstractViewModel
import com.petruskambala.namcovidcontacttracer.utils.Results
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class CaseViewModel : AbstractViewModel<CovidCase>() {

    private val caseRepo = CaseRepo()

    private var _caseList = MutableLiveData<ArrayList<CovidCase>>()
    val caseList: LiveData<ArrayList<CovidCase>> = _caseList

    private var _covidStat = MutableLiveData<CovidStat>()
    val covidStat: LiveData<CovidStat> = _covidStat

    init {
        loadCases()
        loadStat()
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
            if (it is Results.Success) {
                val pos = caseList.value!!.indexOfFirst { case.personId == it.personId }
                _caseList.postValue(_caseList.value!!.apply { set(pos, case) })
            }
            _repoResults.postValue(Pair(null, it))
        }
    }

    fun loadStat() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://coronavirus-19-api.herokuapp.com/countries/Namibia")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                _repoResults.postValue(Pair(null, Results.Error(e)))
            }

            override fun onResponse(call: Call, response: Response) {
                val result = response.body()?.string()
                val obj = JSONObject(result)
                val stats =
                    CovidStat(
                        total = obj.getString("cases"),
                        active = obj.getString("active"),
                        deaths = obj.getString("deaths"),
                        recovered = obj.getString("recovered"),
                        tests = obj.getString("totalTests"),
                        newCases = obj.getString("todayCases")
                    )
                _covidStat.postValue(stats)
                _repoResults.postValue(Pair(null,Results.Success(Results.Success.CODE.LOAD_SUCCESS)))
            }
        })
    }
}