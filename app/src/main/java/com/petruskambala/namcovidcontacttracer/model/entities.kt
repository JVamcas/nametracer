package com.petruskambala.namcovidcontacttracer.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.petruskambala.namcovidcontacttracer.BR
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Exclude
import com.petruskambala.namcovidcontacttracer.utils.DateUtil

enum class AccountType {
    PERSONAL, BUSINESS
}

abstract class AbstractModel(
    open var id: String = "",
    open var photoUrl: String? = null
) : BaseObservable() {
    class EntityExistException : Exception()
    class NoEntityException : Exception()
    class NoAccountException: Exception()
}

/***
 * Represent a visit to a place e.g. office, taxi or house
 */
data class Visit(
    var time: String = "",
    var person: Account? = null,
    var place: Account? = null,
    var personId: String = person?.id?:"",
    var placeId: String = place?.id?:"",
    private var _temperature: String? = null
) : AbstractModel(id = "") {
    var temperature: String?
        @Bindable
        get() = _temperature
        set(value) {
            if (_temperature != value) {
                _temperature = value
                notifyPropertyChanged(BR.temperature)
            }
        }
}

enum class CaseState {
    ACTIVE, RECOVERED, DEAD
}

data class CovidCase(
    var time: String = DateUtil.today(),
    private var person: Account? = null,
    private var _inQuarantine: Boolean = false,
    private var _caseState: CaseState? = null,
    override var id: String = person?.id ?: "",
    override var photoUrl: String? = person?.photoUrl,
    @get:Exclude override var accountType: AccountType? = null,
    @get:Exclude override var admin: Boolean = false
) : Account(
    _email = person?.email,
    _cellphone = person?.cellphone,
    _name = person?.name ?: "",
    _nationalId = person?.nationalId,
    _address_1 = person?.address_1 ?: "",
    _birthDate = person?.birthDate,
    _gender = person?.gender,
    _town = person?.town ?: "",
    _placeVisted = person?.placeVisited ?: 0
) {
    var caseState: CaseState?
        @Bindable get() = _caseState
        set(value) {
            if (_caseState != value) {
                _caseState = value
                notifyPropertyChanged(BR.caseState)
            }
        }
    var inQuarantine: Boolean
        @Bindable get() = _inQuarantine
        set(value) {
            if (_inQuarantine != value) {
                _inQuarantine = value
                notifyPropertyChanged(BR.inQuarantine)
            }
        }

    override fun toString(): String {
        return "$address_1 | $town"
    }
}

data class Alert(
    var id: String
)

/***
 * Represent app user e.g. a place or person
 */
open class Account(
    @get: Exclude val user: FirebaseUser? = null,
    private var _name: String = user?.displayName ?: "",
    private var _cellphone: String? = user?.phoneNumber,
    private var _email: String? = user?.email,
    private var _address_1: String = "",
    private var _town: String = "",
    open var admin: Boolean = false,
    private var _accountType: AccountType? = null,
    private var _nationalId: String? = null,
    private var _gender: Gender? = null,
    private var _birthDate: String? = null,
    private var _placeVisted: Int = 0
) : AbstractModel(id = user?.uid ?: "") {

    open var accountType: AccountType?
        @Bindable get() = _accountType
        set(value) {
            if (_accountType != value) {
                _accountType = value
                notifyPropertyChanged(BR.accountType)
            }
        }

    var placeVisited: Int
        @Bindable get() = _placeVisted
        set(value) {
            _placeVisted = value
            notifyPropertyChanged(BR.placeVisited)
        }

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

    var nationalId: String?
        @Bindable get() = _nationalId
        set(value) {
            if (_nationalId != value) {
                _nationalId = value
                notifyPropertyChanged(BR.nationalId)
            }
        }

    var gender: Gender?
        @Bindable get() = _gender
        set(value) {
            if (_gender != value) {
                _gender = value
                notifyPropertyChanged(BR.gender)
            }
        }

    var birthDate: String?
        @Bindable get() = _birthDate
        set(value) {
            if (_birthDate != value) {
                _birthDate = value
                notifyPropertyChanged(BR.birthDate)
            }
        }

    override fun toString(): String {
        return "$address_1 | $town"
    }
}

data class Auth(
    private var _idEmailCell: String = "",
    private var _password: String = ""
) : AbstractModel() {
    var idMailCell: String
        @Bindable get() = _idEmailCell
        set(value) {
            if (_idEmailCell != value) {
                _idEmailCell = value
                notifyPropertyChanged(BR.idMailCell)
            }
        }
    var password: String
        @Bindable get() = _password
        set(value) {
            if (_password != value) {
                _password = value
                notifyPropertyChanged(BR.password)
            }
        }
}

enum class Gender {
    MALE, FEMALE, UNISEX
}
