package com.petruskambala.namcovidcontacttracer.ui.visit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.petruskambala.namcovidcontacttracer.model.Visit
import com.petruskambala.namcovidcontacttracer.repository.VisitRepo
import com.petruskambala.namcovidcontacttracer.ui.AbstractViewModel
import com.petruskambala.namcovidcontacttracer.utils.Results

class VisitViewModel : AbstractViewModel<Visit>() {


    private val _placesVisited: MutableLiveData<ArrayList<Visit>> = MutableLiveData()
    var placesVisited: LiveData<ArrayList<Visit>> = _placesVisited


    val visitRepo = VisitRepo()


    fun loadPlaceVisited(personId: String){
        visitRepo.loadPlacesVisited(personId){visits: ArrayList<Visit>, results: Results ->

        }
    }

    fun recordVisit(visit: Visit) {
        visitRepo.recordVisit(visit){results: Results->
            _repoResults.postValue(Pair(null,results))
        }
    }

}