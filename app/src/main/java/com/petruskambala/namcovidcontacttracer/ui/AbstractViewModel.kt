package com.petruskambala.namcovidcontacttracer.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.petruskambala.namcovidcontacttracer.model.AbstractModel
import com.petruskambala.namcovidcontacttracer.utils.Results

abstract class AbstractViewModel<K : AbstractModel> : ViewModel() {

    enum class LoadState {
        LOADED, NO_LOAD, LOADING
    }

    protected var _repoResults: MutableLiveData<Pair<K?, Results>> = MutableLiveData()

    val repoResults: LiveData<Pair<K?, Results>>
        get() = _repoResults

    var modelLoadState = MutableLiveData<Pair<LoadState, Results?>>(Pair(LoadState.NO_LOAD,null))
    var modelLoad = MutableLiveData<K>()

    open fun clearModelLoad() {
        modelLoad.value = null
        modelLoadState.value = Pair(LoadState.NO_LOAD,null)
    }

    open fun getModelLoad(): LiveData<K> {
        return modelLoad
    }

    open fun clearRepoResults(owner: LifecycleOwner) {
        repoResults.removeObservers(owner)
        _repoResults.postValue(null)
    }
}