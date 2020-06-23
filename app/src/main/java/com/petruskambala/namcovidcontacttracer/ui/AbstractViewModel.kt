package com.petruskambala.namcovidcontacttracer.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.petruskambala.namcovidcontacttracer.model.AbstractModel
import com.petruskambala.namcovidcontacttracer.utils.Results

abstract class AbstractViewModel<K: AbstractModel> : ViewModel() {

    enum class LoadState {
        LOADED, NO_LOAD, LOADING, LOAD_FAIL
    }
    private var _repoResults: MutableLiveData<Results> = MutableLiveData<Results>()
    var repoResults: LiveData<Results> = _repoResults
        set(value) {
            _repoResults.postValue(value.value)
            field = value
        }
    var modelLoadState = MutableLiveData<LoadState>()
    var modelLoad = MutableLiveData<K>()

    open fun clearModelLoad() {
        modelLoad.value = null
        modelLoadState.value = LoadState.NO_LOAD
    }

    open fun getModelLoad(): LiveData<K> {
        return modelLoad
    }

    open fun clearRepoResults(owner: LifecycleOwner) {
        repoResults.removeObservers(owner)
        _repoResults = MutableLiveData()
    }
}