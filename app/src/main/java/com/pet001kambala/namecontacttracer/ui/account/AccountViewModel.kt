package com.pet001kambala.namecontacttracer.ui.account

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pet001kambala.namecontacttracer.model.*
import com.pet001kambala.namecontacttracer.repository.AccountRepo
import com.pet001kambala.namecontacttracer.ui.AbstractViewModel
import com.pet001kambala.namecontacttracer.ui.Event
import com.pet001kambala.namecontacttracer.utils.ParseUtil
import com.pet001kambala.namecontacttracer.utils.Results

class AccountViewModel : AbstractViewModel<Account>() {
    enum class AuthState {
        AUTHENTICATED, UNAUTHENTICATED, EMAIL_NOT_VERIFIED, ACCOUNT_INFO_MISSING

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

    private val _mAuthState = MutableLiveData<AuthState>(AuthState.UNAUTHENTICATED)
    val authState: LiveData<AuthState>
        get() {
            val userTask = Firebase.auth
            userTask.currentUser?.apply {
                this.reload().addOnSuccessListener {
                    if (userTask.currentUser!!.isEmailVerified) {
                        _mAuthState.postValue(AuthState.AUTHENTICATED)
                        userId.postValue(userTask.currentUser!!.uid)//trigger account data load
                    }
                }
            }
            return _mAuthState
        }

    init {
        //listen for auth changes
        mAuthListener = FirebaseAuth.AuthStateListener { auth ->
            //if auth.currentUser is not null
            auth.currentUser?.let {
                when {
                    isEmailAuth() && !it.isEmailVerified -> _mAuthState.postValue(AuthState.EMAIL_NOT_VERIFIED)
                    else -> {
                        _currentAccount.postValue(Person(account = Account(user = it)))
                        userId.postValue(it.uid)//trigger account data load
                        _mAuthState.postValue(AuthState.AUTHENTICATED)
                    }
                }
                return@AuthStateListener
            }
            _currentAccount.value = null
            _mAuthState.postValue(AuthState.UNAUTHENTICATED)
        }
        Firebase.auth.addAuthStateListener(mAuthListener)
    }

    private fun isAccountInfoMissing(account: Person?): Boolean {
        return account?.name.isNullOrEmpty() || account?.accountType == null
    }

    private fun isEmailAuth(): Boolean {
        val user = Firebase.auth.currentUser
        return user?.providerData?.get(1)?.providerId == EmailAuthProvider.PROVIDER_ID
    }

    private fun isPhoneAuth(): Boolean {
        val user = Firebase.auth.currentUser
        return user?.providerData?.get(1)?.providerId == PhoneAuthProvider.PROVIDER_ID
    }

    private fun loadUserProfile(userId: String) {
        accountRepo.apply {
            loadAccountInfo(userId) { mUser, mResult ->
                if (mResult is Results.Success) {
                    mUser?.let { _currentAccount.postValue(it) }
                    if (isPhoneAuth()) {
                        when {
                            mUser == null -> {
                                createUserWithPhone(phoneAuthAccount!!) { account, results ->
                                    if (results is Results.Success) {
                                        _currentAccount.postValue(account)
                                        if (isAccountInfoMissing(account))
                                            _mAuthState.postValue(AuthState.ACCOUNT_INFO_MISSING)
                                    }
//                                    _repoResults.postValue(Event(Pair(null, results)))
                                }
                            }
                            isAccountInfoMissing(mUser) -> _mAuthState.postValue(AuthState.ACCOUNT_INFO_MISSING)
                        }
                    }
                }
            }
        }
    }

    fun authenticateWithEmail(email: String, password: String) {
        accountRepo.authenticateWithEmailAndPassword(email, password) { mResults ->
            _repoResults.postValue(Event(Pair(null, mResults)))
        }
    }

    /**
     * Account passed via phone auth to be created
     * */
    private var phoneAuthAccount: Person? = null
    fun signInWithPhoneAuthCredential(
        account: Person? = null,
        phoneCredential: PhoneAuthCredential
    ) {
        phoneAuthAccount = account
        accountRepo.signInWithPhoneAuthCredential(
            credential = phoneCredential
        ) { results ->
            _repoResults.postValue(Event(Pair(null, results)))
        }
    }

    fun verifyPhoneNumber(phoneNumber: String, activity: FragmentActivity) {
        val phone = ParseUtil.formatPhone(phoneNumber)
        accountRepo.verifyPhoneNumber(
            phone,
            activity,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    val accountCred = PhoneAuthCred(p0)
                    _repoResults.postValue(
                        Event(Pair(accountCred, Results.Success(Results.Success.CODE.PHONE_VERIFY_SUCCESS)))
                    )
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    _repoResults.postValue(Event(Pair(null, Results.Error(p0))))
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    _repoResults.postValue(
                        Event(Pair(
                            PhoneAuthCred(verificationId = verificationId),
                            Results.Success(Results.Success.CODE.PHONE_VERIFY_CODE_SENT)
                        ))
                    )
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    super.onCodeAutoRetrievalTimeOut(p0)
                    _repoResults.postValue(
                        Event(Pair(
                            PhoneAuthCred(verificationId = p0), Results.Error(
                                AbstractModel.PhoneVerificationCodeExpired()
                            ))
                        )
                    )
                }
            })
    }

    fun createNewUser(account: Person, password: String) {
        accountRepo.createNewUserWithEmailAndPassword(account, password) { obj, mResults ->
            _repoResults.postValue(Event(Pair(obj, mResults)))
        }
    }

    fun sendVerificationEmail() {
        accountRepo.sendVerificationEmail {
            _repoResults.postValue(Event(Pair(null, it)))
        }
    }

    fun updateAccount(account: Account) {
        accountRepo.updateAccount(account) { results ->
            if (results is Results.Success)
                _mAuthState.postValue(AuthState.AUTHENTICATED)
            _currentAccount.postValue(
                if (account.accountType == AccountType.BUSINESS)
                    Person(account = account)
                else account as Person
            )
            _repoResults.postValue(Event(Pair(null, results)))
        }
    }

    fun signOut() {
        Firebase.auth.signOut()
    }

    fun findAccount(
        email: String? = null,
        phoneNumber: String? = null,
        accountType: AccountType = AccountType.PERSONAL
    ) {
        accountRepo.findAccount(
            email,
            phoneNumber,
            accountType
        ) { account, results ->
            _repoResults.postValue(Event(Pair(account, results)))
        }
    }

    fun resetPassword(email: String) {

        accountRepo.resetPassword(email = email) {
            _repoResults.postValue(Event(Pair(null, it)))
        }
    }
}