package com.petruskambala.namcovidcontacttracer.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.petruskambala.namcovidcontacttracer.BR
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Exclude
import com.petruskambala.namcovidcontacttracer.utils.AccessType
import com.petruskambala.namcovidcontacttracer.utils.DateUtil
import java.util.*
import kotlin.collections.ArrayList

enum class AccountType {
    PERSONAL, BUSINESS
}

abstract class AbstractModel(
    open var id: String = "",
    open var photoUrl: String? = null
) : BaseObservable() {
    class EntityExistException : Exception()
    class NoEntityException : Exception()
}

/***
 * Represent a visit to a place e.g. office, taxi or house
 */
data class Visit(
    var time: String = "",
    var person: Person? = null,
    var place: Account? = null,
    var personId: String = person?.id ?: "",
    var placeId: String = place?.id ?: "",
    @get:Exclude override var photoUrl: String? = null,
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

data class RecordVisit(
    private var _visitorMailCellId: String = "",
    private var _visitorTemperature: String = ""
) : AbstractModel() {
    var visitorMailCellId: String
        @Bindable get() = _visitorMailCellId
        set(value) {
            if (_visitorMailCellId != value) {
                _visitorMailCellId = value.toLowerCase(Locale.ROOT)
                notifyPropertyChanged(BR.visitorMailCellId)
            }
        }
    var visitorTemperature: String
        @Bindable get() = _visitorTemperature
        set(value) {
            if (_visitorTemperature != value) {
                _visitorTemperature = value
                notifyPropertyChanged(BR.visitorTemperature)
            }
        }
}

data class CovidCase(
    var time: String = DateUtil.today(),
    private var _person: Person? = null,
    private var _inQuarantine: Boolean = false,
    private var _caseState: CaseState? = null,
    var tested: Boolean = false,
    var personId: String = _person?.id?:"",
    @get: Exclude @Transient override var permission: ArrayList<AccessType>? = null,
    @get:Exclude @Transient override var id: String = "",
    @get: Exclude @Transient override var placeVisited: Int = 0,
    @get: Exclude @Transient override var photoUrl: String? = null,
    @get:Exclude @Transient override var accountType: AccountType? = null,
    @get:Exclude @Transient override var admin: Boolean = false
) : Person(
    account = _person,
    _gender = _person?.gender,
    _nationalId = _person?.nationalId ?: "",
    _placeVisited = _person?.placeVisited ?: 0,
    _birthDate = _person?.birthDate ?: ""
) {
    @get: Exclude
    var person: Person?
        @Bindable get() = _person
        set(value) {
            _person = value
            notifyPropertyChanged(BR.person)
        }

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

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is CovidCase)
            return false
        return (!other.email.isNullOrEmpty() && email == other.email)
                || (!other.cellphone.isNullOrEmpty() && cellphone == other.cellphone)
                || (other.nationalId.isNotEmpty() && nationalId == other.nationalId)
    }
}

data class Alert(
    var id: String
)

data class CovidStat(
    var total: String  = "0",
    var newCases: String = "0",
    var deaths: String = "0",
    var recovered: String = "0",
    var active: String = "0",
    var tests: String = "0"
)

/***
 * Represent app user e.g. a place or person
 */
open class Account(
    @get: Exclude @Transient val user: FirebaseUser? = null,
    private var _name: String = user?.displayName ?: "",
    private var _cellphone: String? = user?.phoneNumber,
    private var _email: String? = user?.email?.toLowerCase(Locale.ROOT),
    private var _address_1: String = "",
    private var _town: String? = "",
    open var admin: Boolean = false,
    private var _accountType: AccountType? = null,
    open var permission: ArrayList<AccessType>? = null
) : AbstractModel(id = user?.uid ?: "") {
    open var accountType: AccountType?
        @Bindable get() = _accountType
        set(value) {
            if (_accountType != value) {
                _accountType = value
                notifyPropertyChanged(BR.accountType)
            }
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
                if (value != null) {
                    _email = value.toLowerCase(Locale.ROOT)
                }
                notifyPropertyChanged(BR.email)
            }
        }
    var town: String?
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

open class Person(
    account: Account? = null,
    private var _birthDate: String = "",
    private var _nationalId: String = "",
    private var _gender: Gender? = null,
    private var _placeVisited: Int = 0
) : Account(
    _name = account?.name ?: "",
    _town = account?.town,
    _address_1 = account?.address_1 ?: "",
    _cellphone = account?.cellphone,
    _email = account?.email?.toLowerCase(Locale.ROOT),
    _accountType = account?.accountType
) {
    var birthDate: String
        @Bindable get() = _birthDate
        set(value) {
            if (_birthDate != value) {
                _birthDate = value
                notifyPropertyChanged(BR.birthDate)
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
    open var placeVisited: Int
        @Bindable get() = _placeVisited
        set(value) {
            _placeVisited = value
            notifyPropertyChanged(BR.placeVisited)
        }
    var nationalId: String
        @Bindable get() = _nationalId
        set(value) {
            _nationalId = value
            notifyPropertyChanged(BR.nationalId)
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
                _idEmailCell = value.toLowerCase(Locale.ROOT)
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
