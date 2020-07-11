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
    protected var _repoResults: MutableLiveData<Pair<K?,Results>?> = MutableLiveData()
    var repoResults: LiveData<Pair<K?,Results>?> = _repoResults
        set(value) {
            field = value
            _repoResults.postValue(value.value)
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
        _repoResults.postValue(null)
    }
}