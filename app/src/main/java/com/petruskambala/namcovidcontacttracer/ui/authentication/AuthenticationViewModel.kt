package com.petruskambala.namcovidcontacttracer.ui.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.petruskambala.namcovidcontacttracer.model.Account
import com.petruskambala.namcovidcontacttracer.repository.AccountRepo
import com.petruskambala.namcovidcontacttracer.ui.AbstractViewModel
import com.petruskambala.namcovidcontacttracer.utils.Results

enum class AuthState {
    AUTHENTICATED, UNAUTHENTICATED
}

class AuthenticationViewModel : AbstractViewModel<Account>() {


    private val userId = MutableLiveData<String>()
    private val _currentAccount: MutableLiveData<Account> =
        Transformations.switchMap(userId) {
            loadUserProfile(it)
            MutableLiveData<Account>()
        } as MutableLiveData<Account>

    val currentAccount: LiveData<Account> = _currentAccount

    private var mAuthListener: FirebaseAuth.AuthStateListener
    private var mAccountRepo: AccountRepo = AccountRepo()

    private val _mAuthState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _mAuthState

    init {
        //listen for auth changes
        mAuthListener = FirebaseAuth.AuthStateListener { auth ->
            //if auth.currentUser is not null
            auth.currentUser?.let {
                userId.postValue(it.uid)
                _currentAccount.postValue(Account(user = it))
                _mAuthState.postValue(AuthState.AUTHENTICATED)
                return@AuthStateListener
            }
            _currentAccount.value = null
            _mAuthState.postValue(AuthState.UNAUTHENTICATED)
        }
        Firebase.auth.addAuthStateListener(mAuthListener)
    }

    private fun loadUserProfile(userId: String) {
        mAccountRepo.loadAccountInfo(userId) { user, mResult ->
            if (mResult is Results.Success)
                _currentAccount.postValue(user)
        }
    }

    fun authenticate(email: String, password: String) {
        mAccountRepo.authenticateWithEmailAndPassword(email, password) { mResults ->
            _repoResults.postValue(Pair(null, mResults))
        }
    }

    fun createNewUser(account: Account, password: String) {
        mAccountRepo.createNewUserWithEmailAndPassword(account, password) { obj, mResults ->
            if (mResults is Results.Success) {
                _currentAccount.postValue(obj)
                _mAuthState.value = AuthState.AUTHENTICATED

            } else _mAuthState.value = AuthState.UNAUTHENTICATED
            _repoResults.postValue(Pair(obj, mResults))
        }
    }

    fun signOut() {
        Firebase.auth.signOut()
//            signInProviderID.value = null
    }

}