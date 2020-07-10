package com.petruskambala.namcovidcontacttracer.utils

enum class Docs(private val value: String) {
    ACCOUNTS("Accounts"),
    VISITS("Visits"),
    CASES("cases");

    override fun toString(): String {
        return value
    }
}