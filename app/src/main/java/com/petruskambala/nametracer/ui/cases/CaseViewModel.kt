package com.petruskambala.nametracer.ui.cases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.petruskambala.nametracer.model.Cases
import com.petruskambala.nametracer.model.CovidStat
import com.petruskambala.nametracer.model.Person
import com.petruskambala.nametracer.repository.CaseRepo
import com.petruskambala.nametracer.ui.AbstractViewModel
import com.petruskambala.nametracer.ui.Event
import com.petruskambala.nametracer.utils.Results
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class CaseViewModel : AbstractViewModel<Cases>() {

    private val caseRepo = CaseRepo()

    private var _caseList = MutableLiveData<ArrayList<Cases>>()
    val caseList: LiveData<ArrayList<Cases>>
        get() {
            return if (_caseList.value == null) MutableLiveData<ArrayList<Cases>>(ArrayList())
            else _caseList
        }

    private var _covidStat = MutableLiveData<CovidStat>()
    val covidStat: LiveData<CovidStat> = _covidStat

    init {
        loadCases()
        loadStat()
    }

    fun registerNewCase(aCase: Cases) {
        caseRepo.registerNewCase(aCase) { result ->
            if (result is Results.Success)
                _caseList.postValue(caseList.value?.apply { add(aCase) })
            _repoResults.postValue(Event(Pair(null, result)))
        }
    }

    private fun loadCases() {
        caseRepo.loadCases { cases, results ->
            if (results is Results.Success)
                _caseList.value = cases
        }
    }

    fun findCase(email: String? = null, cellphone: String? = null) {
        caseRepo.findCase(email, cellphone) { case, results ->
            _repoResults.postValue(Event( Pair(case?.also { it.person = Person() }, results)))
        }
    }

    fun updateCase(aCase: Cases) {
        caseRepo.updateCase(aCase) {
            if (it is Results.Success) {
                val pos = caseList.value!!.indexOfFirst { aCase.personId == it.personId }
                _caseList.postValue(_caseList.value!!.apply { set(pos, aCase) })
            }
            _repoResults.postValue(Event(Pair(null, it)))
        }
    }

    fun loadStat() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://coronavirus-19-api.herokuapp.com/countries/Namibia")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                _repoResults.postValue(Event(Pair(null, Results.Error(e))))
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
            }
        })
    }
}