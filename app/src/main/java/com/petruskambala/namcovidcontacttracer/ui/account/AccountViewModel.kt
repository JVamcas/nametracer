package com.petruskambala.namcovidcontacttracer.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.petruskambala.namcovidcontacttracer.model.Account
import com.petruskambala.namcovidcontacttracer.model.AccountType
import com.petruskambala.namcovidcontacttracer.model.Person
import com.petruskambala.namcovidcontacttracer.repository.AccountRepo
import com.petruskambala.namcovidcontacttracer.ui.AbstractViewModel
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil
import com.petruskambala.namcovidcontacttracer.utils.Results

class AccountViewModel : AbstractViewModel<Account>() {
    enum class AuthState {
        AUTHENTICATED, UNAUTHENTICATED
    }

    private val accountRepo = AccountRepo()

    private val userId = MutableLiveData<String>()
    private val _currentAccount: MutableLiveData<Person> =
        Transformations.switchMap(userId) {
            loadUserProfile(it)
            MutableLiveData<Person>()
        } as MutableLiveData<Person>

    val currentAccount: LiveData<Person> = _currentAccount

    private var mAuthListener: FirebaseAuth.AuthStateListener

    private val _mAuthState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _mAuthState

    init {
        //listen for auth changes
        mAuthListener = FirebaseAuth.AuthStateListener { auth ->
            //if auth.currentUser is not null
            auth.currentUser?.let {
                userId.postValue(it.uid)
                _currentAccount.postValue(Person(account = Account(user = it)))
                _mAuthState.postValue(AuthState.AUTHENTICATED)
                return@AuthStateListener
            }
            _currentAccount.value = null
            _mAuthState.postValue(AuthState.UNAUTHENTICATED)
        }
        Firebase.auth.addAuthStateListener(mAuthListener)
    }

    private fun loadUserProfile(userId: String) {
        accountRepo.loadAccountInfo(userId) { user, mResult ->
            if (mResult is Results.Success)
                _currentAccount.postValue(user)
        }
    }

    fun authenticate(email: String, password: String) {
        accountRepo.authenticateWithEmailAndPassword(email, password) { mResults ->
            _repoResults.postValue(Pair(null, mResults))
        }
    }

    fun createNewUser(account: Account, password: String) {
        accountRepo.createNewUserWithEmailAndPassword(account, password) { obj, mResults ->
            if (mResults is Results.Success) {
                _currentAccount.postValue(obj)
                _mAuthState.value = AuthState.AUTHENTICATED

            } else _mAuthState.value = AuthState.UNAUTHENTICATED
            _repoResults.postValue(Pair(obj, mResults))
        }
    }

    fun updateAccount(account: Account) {
        accountRepo.updateAccount(account) { results ->
            if (results is Results.Success)
                _currentAccount.postValue(
                    if (account.accountType == AccountType.BUSINESS)
                        Person(
                            account = ParseUtil.copyOf(
                                account,
                                Account::class.java
                            )
                        ) else account as Person
                )
            _repoResults.postValue(Pair(null, results))
        }
    }

    fun signOut() {
        Firebase.auth.signOut()
//            signInProviderID.value = null
    }

    fun findAccount(
        email: String? = null,
        phoneNumber: String? = null,
        nationalId: String? = null,
        accountType: AccountType = AccountType.PERSONAL
    ) {
        accountRepo.findPerson(email, phoneNumber, nationalId, accountType) { account, results ->
            _repoResults.postValue(Pair(account, results))
        }
    }
}