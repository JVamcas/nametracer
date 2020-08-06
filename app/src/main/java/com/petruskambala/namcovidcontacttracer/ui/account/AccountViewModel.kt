package com.petruskambala.namcovidcontacttracer.ui.account

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.FirebaseException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.petruskambala.namcovidcontacttracer.model.*
import com.petruskambala.namcovidcontacttracer.repository.AccountRepo
import com.petruskambala.namcovidcontacttracer.ui.AbstractViewModel
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil
import com.petruskambala.namcovidcontacttracer.utils.Results

class AccountViewModel : AbstractViewModel<Account>() {
    enum class AuthState {
        AUTHENTICATED, UNAUTHENTICATED, EMAIL_NOT_VERIFIED
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
    val authState: LiveData<AuthState> = _mAuthState

    init {
        //listen for auth changes
        mAuthListener = FirebaseAuth.AuthStateListener { auth ->
            //if auth.currentUser is not null
            auth.currentUser?.let {

                if (it.providerId == EmailAuthProvider.PROVIDER_ID && !it.isEmailVerified)
                    _mAuthState.postValue(AuthState.EMAIL_NOT_VERIFIED)
                else {
                    userId.postValue(it.uid)
                    _currentAccount.postValue(Person(account = Account(user = it)))
                    _mAuthState.postValue(AuthState.AUTHENTICATED)
                }
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

    fun authenticateWithEmail(email: String, password: String) {
        accountRepo.authenticateWithEmailAndPassword(email, password) { mResults ->

            _repoResults.postValue(Pair(null, mResults))
        }
    }

    fun authenticateWithPhoneNumber(phoneCredential: PhoneAuthCredential) {
        accountRepo.signInWithPhoneAuthCredential(phoneCredential) { results ->

            _repoResults.postValue(Pair(null, results))
        }
    }

    fun verifyPhoneNumber(phoneNumber: String, activity: FragmentActivity) {
        accountRepo.verifyPhoneNumber(
            phoneNumber,
            activity,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    val accountCred = PhoneAuthCred(p0)
                    _repoResults.postValue(
                        Pair(
                            accountCred,
                            Results.Success(Results.Success.CODE.PHONE_VERIFY_SUCCESS)
                        )
                    )
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    _repoResults.postValue(Pair(null, Results.Error(p0)))
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    _repoResults.postValue(
                        Pair(
                            PhoneAuthCred(verificationId = verificationId),
                            Results.Success(Results.Success.CODE.PHONE_VERIFY_CODE_SENT)
                        )
                    )
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    super.onCodeAutoRetrievalTimeOut(p0)
                    _repoResults.postValue(
                        Pair(
                            PhoneAuthCred(verificationId = p0), Results.Error(
                                AbstractModel.PhoneVerificationCodeExpired()
                            )
                        )
                    )
                }
            })
    }

    fun createNewUser(account: Account, password: String) {
        accountRepo.createNewUserWithEmailAndPassword(account, password) { obj, mResults ->
            _repoResults.postValue(Pair(obj, mResults))
        }
    }

    fun sendVerificationEmail() {
        accountRepo.sendVerificationEmail {
            _repoResults.postValue(Pair(null, it))
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

    fun resetPassword(email: String) {

        accountRepo.resetPassword(email = email) {
            _repoResults.postValue(Pair(null, it))
        }
    }
}