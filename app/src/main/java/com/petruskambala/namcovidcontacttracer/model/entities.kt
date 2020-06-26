package com.petruskambala.namcovidcontacttracer.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.petruskambala.namcovidcontacttracer.BR
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Exclude
import com.petruskambala.namcovidcontacttracer.utils.DateUtil

enum class UserType {
    PERSONAL, BUSINESS
}

abstract class AbstractModel(
    var id: String="",
    var photoUrl: String? = null
) : BaseObservable() {
    class EntityExistException : Exception()
}

/***
 * Represent a visit to a place e.g. office, taxi or house
 */
data class Visit(
    var time: String = "",
    var personId: String,
    var placeId: String,
    var person: User,
    var place: User
) : AbstractModel(id = "")

enum class CaseState {
    NON_QUARANTINE, QUARANTINE, ACTIVE, RECOVERED, DEAD
}

data class CovidCase(
    var time: String = DateUtil.today(),
    var personId: String = "",
    var person: User? = null,
    private var _caseState: CaseState = CaseState.NON_QUARANTINE
) : AbstractModel() {
    var caseState: CaseState
        @Bindable get() = _caseState
        set(value) {
            if(_caseState != value){
                _caseState = value
                notifyPropertyChanged(BR.caseState)
            }
        }
}

data class Alert(
    var id: String
)

/***
 * Represent app user e.g. a place or person
 */
data class User(
    @get: Exclude val user: FirebaseUser? = null

) : AbstractModel(id = user?.uid ?: "") {
    private var _name: String = user?.displayName ?: ""
    var userType: UserType = UserType.PERSONAL
    private var _cellphone: String? = user?.phoneNumber
    private var _email: String? = user?.email
    private var _address_1: String = ""
    private var _town: String = ""
    var admin: Boolean = false

    var name: String
        @Bindable get() = _name
        set(value) {
            if (_name != value) {
                _name = value
                notifyPropertyChanged(BR.name)
            }
        }
    var cellphone: String?
        @Bindable get() = _cellphone
        set(value) {
            if (_cellphone != value) {
                _cellphone = value
                notifyPropertyChanged(BR.cellphone)
            }
        }
    var email: String?
        @Bindable get() = _email
        set(value) {
            if (_email != value) {
                _email = value
                notifyPropertyChanged(BR.email)
            }
        }
    var town: String
        @Bindable get() = _town
        set(value) {
            if (_town != value) {
                _town = value
                notifyPropertyChanged(BR.town)
            }
        }
    var address_1: String
        @Bindable get() = _address_1
        set(value) {
            if (_address_1 != value) {
                _address_1 = value
                notifyPropertyChanged(BR.address_1)
            }
        }

    override fun toString(): String {
        return "$address_1 | $town"
    }
}