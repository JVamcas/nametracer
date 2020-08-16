package com.petruskambala.nametracer.utils

enum class AccessType(private val value: String) {

    VIEW_CASE("View recorded cases."),
    RECORD_CASE("Record new case."),
    UPDATE_CASE("Update case details."),
    DELETE_CASE("Delete case from database.");

    override fun toString(): String {
        return value
    }
}