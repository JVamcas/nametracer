package com.petruskambala.namcovidcontacttracer.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.petruskambala.namcovidcontacttracer.model.Account
import com.petruskambala.namcovidcontacttracer.repository.AccountRepo
import com.petruskambala.namcovidcontacttracer.ui.AbstractViewModel
import com.petruskambala.namcovidcontacttracer.utils.Results

class AccountViewModel() : AbstractViewModel<Account>() {


    private var _account = MutableLiveData<Account>()
    val account: LiveData<Account> = _account

    private val accountRepo = AccountRepo()

    fun findPerson(email: String? = null,phoneNumber: String? = null,nationalId: String? = null){
        accountRepo.findPerson(email,phoneNumber,nationalId){account, results ->
            if(results is Results.Success)
                _account.postValue(account)
            repoResults = MutableLiveData(results)
        }
    }
}