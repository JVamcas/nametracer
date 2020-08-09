package com.petruskambala.namcovidcontacttracer.ui.visit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.petruskambala.namcovidcontacttracer.model.Visit
import com.petruskambala.namcovidcontacttracer.repository.VisitRepo
import com.petruskambala.namcovidcontacttracer.ui.AbstractViewModel
import com.petruskambala.namcovidcontacttracer.utils.Results

class VisitViewModel : AbstractViewModel<Visit>() {

    private val personId = MutableLiveData<String>()
    private val placeId = MutableLiveData<String>()

    private val _visitorsList: MutableLiveData<ArrayList<Visit>> =
        Transformations.switchMap(placeId) {
            loadPlaceVisitors(it)
            MutableLiveData<ArrayList<Visit>>()
        } as MutableLiveData<ArrayList<Visit>>

    val visitorsList: LiveData<ArrayList<Visit>> = _visitorsList

    private val _personVisits: MutableLiveData<ArrayList<Visit>> =
        Transformations.switchMap(personId) {
            loadVisits(it)
            MutableLiveData<ArrayList<Visit>>()
        } as MutableLiveData<ArrayList<Visit>>
    val personVisits: LiveData<ArrayList<Visit>> = _personVisits

    private val _allVisits = MutableLiveData<ArrayList<Visit>>()
    val allVisits: LiveData<ArrayList<Visit>>
        get() {
            if (_allVisits.value == null)
                loadVisits()
            return _allVisits
        }

    private val visitRepo = VisitRepo()

    fun recordVisit(visit: Visit) {
        visitRepo.recordVisit(visit) { result ->
            if (result is Results.Success)
                _visitorsList.postValue(
                    if (visitorsList.value == null) arrayListOf(visit)
                    else visitorsList.value?.also { it.add(visit) })
            _repoResults.postValue(Pair(null, result))
        }
    }

    /***
     * Load visits for a specific person only
     * @param personId id of the visitor
     */
    private fun loadVisits(personId: String) {
        modelLoadState.postValue(Pair(LoadState.LOADING, null))
        visitRepo.loadVisits(personId) { visits, results ->
            if (results is Results.Success)
                _personVisits.postValue(visits)
            modelLoadState.postValue(Pair(LoadState.LOADED, results))
        }
    }

    fun switchPerson(id: String) {
        if (personId.value == null || personId.value != id) {
            personId.postValue(id)
            _personVisits.postValue(null)
        }
    }

    fun switchPlace(id: String) {
        if (placeId.value == null || placeId.value != id) {
            placeId.postValue(id)
            _visitorsList.postValue(null)
        }
    }

    /***
     * Load all visits on the database
     */
    private fun loadVisits() {
        visitRepo.loadVisits { visits, results ->
            if (results is Results.Success)
                _allVisits.postValue(visits)
            _repoResults.postValue(Pair(null, results))
        }
    }

    private fun loadPlaceVisitors(placeId: String) {
        modelLoadState.postValue(Pair(LoadState.LOADING, null))
        visitRepo.loadPlaceVisitors(placeId) { visits, results ->
            if (results is Results.Success)
                _visitorsList.postValue(visits)
            modelLoadState.postValue(Pair(LoadState.LOADED, results))
        }
    }
}