package com.petruskambala.namcovidcontacttracer.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.petruskambala.namcovidcontacttracer.model.Account
import com.petruskambala.namcovidcontacttracer.model.AccountType
import com.petruskambala.namcovidcontacttracer.model.Visit
import com.petruskambala.namcovidcontacttracer.repository.AccountRepo
import com.petruskambala.namcovidcontacttracer.ui.AbstractViewModel
import com.petruskambala.namcovidcontacttracer.utils.Results

class AccountViewModel : AbstractViewModel<Account>() {


    private var _account = MutableLiveData<Account>()
    val acc: LiveData<Account> = _account

    private val accountRepo = AccountRepo()

    fun findAccount(
        email: String? = null,
        phoneNumber: String? = null,
        nationalId: String? = null,
        accountType: AccountType  = AccountType.PERSONAL
    ) {
        accountRepo.findPerson(email, phoneNumber, nationalId,accountType) { account, results ->
//            if (results is Results.Success)
//                _account.postValue(account)
            _repoResults.postValue(Pair(account, results))
        }
    }
}