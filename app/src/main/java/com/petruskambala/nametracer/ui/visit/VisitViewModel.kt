package com.petruskambala.nametracer.ui.visit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.petruskambala.nametracer.model.Visit
import com.petruskambala.nametracer.repository.VisitRepo
import com.petruskambala.nametracer.ui.AbstractViewModel
import com.petruskambala.nametracer.ui.Event
import com.petruskambala.nametracer.utils.Results

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
            loadPersonVisits(it)
            MutableLiveData<ArrayList<Visit>>()
        } as MutableLiveData<ArrayList<Visit>>
    val personVisits: LiveData<ArrayList<Visit>> = _personVisits

    private val visitRepo = VisitRepo()

    fun recordVisit(visit: Visit) {
        visitRepo.recordVisit(visit) { result ->
            if (result is Results.Success)
                _visitorsList.postValue(
                    if (visitorsList.value == null) arrayListOf(visit)
                    else visitorsList.value?.also { it.add(visit) })
            _repoResults.postValue(Event(Pair(null, result)))
        }
    }

    /***
     * Load visits for a specific person only
     * @param personId id of the visitor
     */
    private fun loadPersonVisits(personId: String) {
        modelLoadState.postValue(Pair(LoadState.LOADING, null))
        visitRepo.loadPersonVisits(personId) { visits, results ->
            if (results is Results.Success)
                _personVisits.postValue(visits)
            modelLoadState.postValue(Pair(LoadState.LOADED, results))
        }
    }

    fun switchPerson(id: String) {
        if (personId.value == null || personId.value != id) {
            _personVisits.postValue(null)
            modelLoadState.postValue(null)
            personId.postValue(id)
        }
    }

    fun switchPlace(id: String) {
        if (placeId.value == null || placeId.value != id) {
            _visitorsList.postValue(null)
            modelLoadState.postValue(null)
            placeId.postValue(id)
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