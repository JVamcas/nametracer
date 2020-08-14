package com.petruskambala.namcovidcontacttracer.ui

import androidx.lifecycle.*
import com.petruskambala.namcovidcontacttracer.model.AbstractModel
import com.petruskambala.namcovidcontacttracer.utils.Results

abstract class AbstractViewModel<K : AbstractModel> : ViewModel() {

    enum class LoadState {
        LOADED, NO_LOAD, LOADING
    }

    protected var _repoResults: MutableLiveData<Event<K>> = MutableLiveData()

    val repoResults: LiveData<Event<K>>
        get() = _repoResults

    var modelLoadState = MutableLiveData<Pair<LoadState, Results?>>(Pair(LoadState.NO_LOAD, null))
    var modelLoad = MutableLiveData<K>()

    open fun clearModelLoad() {
        modelLoad.value = null
        modelLoadState.value = Pair(LoadState.NO_LOAD, null)
    }

    open fun getModelLoad(): LiveData<K> {
        return modelLoad
    }

    open fun clearRepoResults(owner: LifecycleOwner) {
        repoResults.removeObservers(owner)
        _repoResults.postValue(null)
    }
}

class ObserveOnce<T : AbstractModel>(private val onEventUnhandledContent: (Pair<T?, Results>) -> Unit) :
    Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let { it -> onEventUnhandledContent(it) }
    }
}


open class Event<K : AbstractModel>(private val content: Pair<K?, Results>) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): Pair<K?, Results>? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): Pair<K?, Results> = content
}